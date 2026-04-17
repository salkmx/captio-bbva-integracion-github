package com.sngular.captio.processor;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.AcecDTO;
import com.sngular.captio.dto.CfdiResumenDTO;
import com.sngular.captio.dto.DotacionDTO;
import com.sngular.captio.dto.FlightDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.dto.ShipDTO;
import com.sngular.captio.dto.TrainDTO;
import com.sngular.captio.dto.TravelServiceDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.ViajeDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.CfdiXmlParserService;
import com.sngular.captio.services.GastoService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.util.AcecPipeExportUtil;
import com.sngular.captio.util.DateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReporteACECProcessor implements ItemProcessor<InformeDTO, AcecDTO> {

	private final GastoService gastoService;

	private final Properties properties;

	private final CfdiXmlParserService cfdiXmlParserService;

	private final UsuarioService usuarioService;

	private Integer consecutivo = 0;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy", new Locale("es", "MX"));

	DateTimeFormatter formatterDDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "MX"));

	@Override
	public AcecDTO process(InformeDTO item) throws Exception {
		log.debug("process BalanzaContable");

		List<GastoDTO> gastos = gastoService
				.obtenerGastosPorFiltro("{\"Report_Id\": " + safe(item != null ? item.getId() : null) + "}");

		List<AcecDTO> gastosBalanza = new ArrayList<>();

		for (GastoDTO gasto : gastos) {
			consecutivo++;

			Integer userId = (gasto != null && gasto.getUser() != null) ? gasto.getUser().getId() : null;
			UsuarioDTO usuarioDTO = null;

			if (userId != null) {
				List<UsuarioDTO> usuarios = usuarioService.obtenerUsuarioByFiltro("{\"Id\":" + userId + "}");
				usuarioDTO = firstOrNull(usuarios);
			}

			if (usuarioDTO == null) {
				return null;
			}

			CfdiResumenDTO cfdi = new CfdiResumenDTO();
			try {
				String xml = Files.readString(Path.of("/temp/fspmv_167107.xml"), StandardCharsets.UTF_8);
				cfdi = cfdiXmlParserService.parse(xml);
			} catch (Exception ex) {
				log.warn("No se pudo leer/parsear CFDI XML, se continúa con valores vacíos. {}", ex.getMessage());
			}

			AcecDTO acecDTO = new AcecDTO();
			acecDTO.setEmpresa(usuarioDTO.getUserOptions().getCompanyCode());
			acecDTO.setNumeroSecuencia(consecutivo.longValue());
			acecDTO.setFechaContable(safeDate(() -> gasto.getDate().format(formatterDDMMYYYY)));
			acecDTO.setFechaOperacion(safeDate(() -> gasto.getCreationDate().format(formatterDDMMYYYY)));
			acecDTO.setDivisa1(gasto.getExpenseAmount().getCurrency().getCode());
			acecDTO.setIva(cfdi.totalImpuestosTrasladados);
			acecDTO.setImporte1(new BigDecimal(gasto.getExpenseAmount().getValue()));
			acecDTO.setTimestamp(LocalDateTime.now().toString());
			acecDTO.setNumeroTarjeta(null);

			gastosBalanza.add(acecDTO);
		}

		agregaRegistro(gastosBalanza);
		return null;
	}

	private void agregaRegistro(List<AcecDTO> gastos) {
		final Path path = Paths.get(
				properties.getAcecLocalDir() + DateUtils.obtenerFechaActual() + "_" + properties.getAcecLocalName());
		if (path.getParent() != null) {
			try {
				Files.createDirectories(path.getParent());
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}

		String block = AcecPipeExportUtil.toPipe(gastos, false);

		try {
			Files.writeString(path, block, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		log.info("Escritas {} registros balanza en {}", gastos.size(), path);

	}

	public static void filtrarEnSitio(List<DotacionDTO> dotaciones, Integer idViaje) {
		if (dotaciones == null)
			return;
		final String esperado = String.valueOf(idViaje);

		dotaciones.removeIf(d -> {
			if (d == null)
				return true;
			var cfs = d.getCustomFields();
			if (cfs == null || cfs.isEmpty())
				return true;
			var cf0 = cfs.get(0);
			if (cf0 == null || cf0.getValue() == null)
				return true;
			return !esperado.equals(String.valueOf(cf0.getValue()));
		});
	}

	public static List<ViajeDTO> viajeAsociadoaInforme(List<ViajeDTO> viajes, String nombreInforme) {
		if (viajes == null || viajes.isEmpty())
			return List.of();

		final String esperado = nombreInforme == null ? null : nombreInforme;

		return viajes.stream().filter(Objects::nonNull).filter(v -> {
			var cfs = v.getCustomFields();
			if (cfs == null || cfs.isEmpty())
				return false;
			var cf0 = cfs.get(0);
			if (cf0 == null || cf0.getValue() == null)
				return false;
			return Objects.equals(esperado, String.valueOf(cf0.getValue()));
		}).toList();
	}

	public String obtenerEndDate(TravelServiceDTO travel) {

		LocalDateTime dep = firstNonBlankLocalDateTime(travel.getFlights(), FlightDTO::getEndDate);
		if (dep != null)
			return dep.format(formatterDDMMYYYY);

		dep = firstNonBlankLocalDateTime(travel.getShips(), ShipDTO::getEndDate);
		if (dep != null)
			return dep.format(formatterDDMMYYYY);

		dep = firstNonBlankLocalDateTime(travel.getTrains(), TrainDTO::getEndDate);
		if (dep != null)
			return dep.format(formatterDDMMYYYY);

		return null;
	}

	public String obtenerStartDate(TravelServiceDTO travel) {

		LocalDateTime dep = firstNonBlankLocalDateTime(travel.getFlights(), FlightDTO::getStartDate);
		if (dep != null)
			return dep.format(formatterDDMMYYYY);

		dep = firstNonBlankLocalDateTime(travel.getShips(), ShipDTO::getStartDate);
		if (dep != null)
			return dep.format(formatterDDMMYYYY);

		dep = firstNonBlankLocalDateTime(travel.getTrains(), TrainDTO::getStartDate);
		if (dep != null)
			return dep.format(formatterDDMMYYYY);

		return null;
	}

	public String obtenerDestination(TravelServiceDTO travel) {

		String dep = firstNonBlank(travel.getFlights(), FlightDTO::getDestination);
		if (dep != null)
			return dep;

		dep = firstNonBlank(travel.getShips(), ShipDTO::getDestination);
		if (dep != null)
			return dep;

		dep = firstNonBlank(travel.getTrains(), TrainDTO::getDestination);
		if (dep != null)
			return dep;

		return null;
	}

	public String obtenerDeparture(TravelServiceDTO travel) {

		String dep = firstNonBlank(travel.getFlights(), FlightDTO::getDeparture);
		if (dep != null)
			return dep;

		dep = firstNonBlank(travel.getShips(), ShipDTO::getDeparture);
		if (dep != null)
			return dep;

		dep = firstNonBlank(travel.getTrains(), TrainDTO::getDeparture);
		if (dep != null)
			return dep;

		return null;
	}

	private <T> LocalDateTime firstNonBlankLocalDateTime(List<T> list, Function<T, LocalDateTime> getter) {
		if (list == null || list.isEmpty())
			return null;

		for (T item : list) {
			if (item == null)
				continue;
			LocalDateTime value = getter.apply(item);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	private <T> String firstNonBlank(List<T> list, Function<T, String> getter) {
		if (list == null || list.isEmpty())
			return null;

		for (T item : list) {
			if (item == null)
				continue;
			String value = getter.apply(item);
			if (value != null && !value.trim().isEmpty()) {
				return value.trim();
			}
		}
		return null;
	}

	private <T> T firstOrNull(List<T> list) {
		return (list != null && !list.isEmpty()) ? list.get(0) : null;
	}

	private String safeDate(Supplier<String> supplier) {
		try {
			String v = supplier.get();
			return v != null ? v : "";
		} catch (Exception e) {
			return "";
		}
	}

	private String safe(Object value) {
		return value != null ? String.valueOf(value) : "";
	}

}
