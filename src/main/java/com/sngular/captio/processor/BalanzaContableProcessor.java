package com.sngular.captio.processor;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.AdjuntoDTO;
import com.sngular.captio.dto.BalanzaDTO;
import com.sngular.captio.dto.CfdiResumenDTO;
import com.sngular.captio.dto.CuentaContableDTO;
import com.sngular.captio.dto.CuentaContableSearchRequestDTO;
import com.sngular.captio.dto.CustomFieldDTO;
import com.sngular.captio.dto.DotacionDTO;
import com.sngular.captio.dto.FlightDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.dto.MetodoPagoDTO;
import com.sngular.captio.dto.ShipDTO;
import com.sngular.captio.dto.StepDTO;
import com.sngular.captio.dto.TrainDTO;
import com.sngular.captio.dto.TravelServiceDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.ViajeDTO;
import com.sngular.captio.dto.WorkFlowDTO;
import com.sngular.captio.enums.CategoriasEnum;
import com.sngular.captio.enums.CustomFieldsEnum;
import com.sngular.captio.enums.MetodoPagoEnum;
import com.sngular.captio.enums.NombreInformeEnum;
import com.sngular.captio.enums.TipoGastoEnum;
import com.sngular.captio.mapper.GastoBalanzaMapper;
import com.sngular.captio.model.UsuarioSonar;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.repository.UsuarioSonarRepository;
import com.sngular.captio.services.CfdiXmlParserService;
import com.sngular.captio.services.CuentaContableService;
import com.sngular.captio.services.DotacionService;
import com.sngular.captio.services.FlujoAprobacionService;
import com.sngular.captio.services.GastoService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.services.ViajeService;
import com.sngular.captio.util.BalanzaPipeExportUtil;
import com.sngular.captio.util.CustomFieldsUtils;
import com.sngular.captio.util.DateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class BalanzaContableProcessor implements ItemProcessor<InformeDTO, BalanzaDTO> {

	private final GastoService gastoService;

	private final Properties properties;

	private final GastoBalanzaMapper gastoBalanzaMapper;

	private final CfdiXmlParserService cfdiXmlParserService;

	private final CuentaContableService cuentaContableService;

	private final UsuarioService usuarioService;

	private final ViajeService viajeService;

	private final DotacionService dotacionService;

	private final UsuarioSonarRepository usuarioSonarRepository;

	private final FlujoAprobacionService flujoAprobacionService;

	private Integer consecutivo = 0;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy", new Locale("es", "MX"));

	DateTimeFormatter formatterDDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "MX"));

	DateTimeFormatter formatterMesAnio = DateTimeFormatter.ofPattern("MM-yyyy", new Locale("es", "MX"));

	DecimalFormat df = new DecimalFormat("#,##0.00");

	private static final List<String> PREFIJOS_VIA = Arrays.stream(NombreInformeEnum.values())
			.map(NombreInformeEnum::getNombreInforme).filter(p -> p.startsWith("VIA")).toList();

	@Override
	public BalanzaDTO process(InformeDTO item) throws Exception {
		log.debug("process BalanzaContable");
		List<GastoDTO> gastos = gastoService
				.obtenerGastosPorFiltro("{\"Report_Id\": " + safe(item != null ? item.getId() : null) + "}");
		List<BalanzaDTO> gastosBalanza = new ArrayList<>();

		WorkFlowDTO wf = flujoAprobacionService.obtenerFlujoByFiltro("{\"Id\":" + item.getWorkflow().getId() + "}");

		StepDTO stepDTO = firstOrNull(wf.getSteps());

		List<UsuarioDTO> usuariosSupervisores = usuarioService
				.obtenerUsuarioByFiltro("{\"Id\":" + stepDTO.getSupervisorId() + "}");

		UsuarioDTO usuarioSupervisorDTO = firstOrNull(usuariosSupervisores);

		for (GastoDTO gasto : gastos) {
			String kilometraje = null;
			CfdiResumenDTO cfdi = obtenerXml(gasto.getId());
			CustomFieldDTO propinaCustom = CustomFieldsUtils.buscarPropina(gasto.getCustomFields());
//			CustomFieldDTO propinaMetodoCustom = CustomFieldsUtils.buscarPorId(gasto.getCustomFields(),
//					MetodoPagoEnum.PROPINA_COMIDA.getIdMetodo());
			if (gasto.getCategory().getId().equals(CategoriasEnum.GASTO_KILOMETRAJE.getClave())) {
				kilometraje = gasto.getMileageInfo().getDistance().toString();
			}

			consecutivo++;

			Integer userId = (gasto != null && gasto.getUser() != null) ? gasto.getUser().getId() : null;
			List<UsuarioDTO> usuarios = userId != null
					? usuarioService.obtenerUsuarioByFiltro("{\"Id\":" + userId + "}")
					: List.of();

			UsuarioDTO usuarioDTO = firstOrNull(usuarios);

			Integer usuarioId = usuarioDTO != null ? usuarioDTO.getId() : null;

			if (usuarioDTO == null)
				return null;
			UsuarioSonar usuarioSonar = usuarioSonarRepository
					.obtenerUsuariosActivosByCorreo(usuarioDTO.getEmail().trim());

			List<ViajeDTO> viajes = null;
			ViajeDTO viajeDTO = null;
			DotacionDTO dotacionDTO = null;
			TravelServiceDTO travelServiceDTO = null;
			CustomFieldDTO tipoViaje = null;
			CustomFieldDTO motivoViaje = null;
			if (usuarioId != null && empiezaConPrefijo(item.getName())) {
				log.debug("Es un informe de vaijes");
				List<DotacionDTO> dotaciones = usuarioId != null
						? dotacionService.obtenerDotacion(String.format("{\"Report_Id\": %s}", item.getId()))
						: List.of();
				dotacionDTO = firstOrNull(dotaciones);

				if (dotacionDTO != null) {

					CustomFieldDTO viajeCustom = CustomFieldsUtils.buscarPorId(dotacionDTO.getCustomFields(),
							CustomFieldsEnum.ID_VIAJE.getIdCustomField());

					viajes = viajeService.obtenerViajesAprobados(
							String.format("{\"\":6, \"Report_Id\": %s}", viajeCustom.getValue()));

					if (viajes != null && !viajes.isEmpty()) {
						viajeDTO = firstOrNull(viajes);

						Integer viajeId = (viajeDTO != null) ? viajeDTO.getId() : null;
						List<TravelServiceDTO> serviciosViaje = viajeId != null
								? viajeService.obtenerServiciosViajes(String.format("{\"Id\": %s}", viajeId))
								: List.of();

						travelServiceDTO = firstOrNull(serviciosViaje);

						tipoViaje = (viajeDTO != null)
								? CustomFieldsUtils.buscarPorId(viajeDTO.getCustomFields(),
										CustomFieldsEnum.TIPO_VIAJE.getIdCustomField())
								: null;

						motivoViaje = (viajeDTO != null) ? CustomFieldsUtils.buscarPorId(viajeDTO.getCustomFields(),
								CustomFieldsEnum.MOTIVO_VIAJE.getIdCustomField()) : null;
					}
				}

			}

			BalanzaDTO balanzaDTO = llenarBalanzaDTO(item, gasto, usuarioDTO, usuarioSupervisorDTO, usuarioSonar,
					viajeDTO, dotacionDTO, travelServiceDTO, tipoViaje, motivoViaje, cfdi, propinaCustom, kilometraje,
					consecutivo);

			if (dotacionDTO != null) {
				BalanzaDTO balanzaDTODotacion = llenarBalanzaDTO(item, gasto, usuarioDTO, usuarioSupervisorDTO,
						usuarioSonar, null, dotacionDTO, travelServiceDTO, tipoViaje, motivoViaje, cfdi, propinaCustom,
						kilometraje, consecutivo);
				gastosBalanza.add(balanzaDTODotacion);
			}

			if (propinaCustom != null) {
				BalanzaDTO balanzaPropina = llenarBalanzaDTO(item, null, usuarioDTO, usuarioSupervisorDTO, usuarioSonar,
						null, dotacionDTO, travelServiceDTO, tipoViaje, motivoViaje, cfdi, propinaCustom, kilometraje,
						consecutivo);
				gastosBalanza.add(balanzaPropina);
			}

			gastosBalanza.add(balanzaDTO);
		}

		agregaRegistro(gastosBalanza);

		return null;
	}

	private CfdiResumenDTO obtenerXml(Integer idGasto) {
		CfdiResumenDTO cfdi = null;
		List<GastoDTO> adjuntos = gastoService.obtenerAdjuntos("{\"Id\": [" + idGasto + "]}");
		String xml = null;
		if (!adjuntos.isEmpty()) {
			cfdi = new CfdiResumenDTO();
			for (GastoDTO adjunto : adjuntos) {
				List<AdjuntoDTO> attachments = adjunto.getAttachments();
				for (AdjuntoDTO attachment : attachments) {
					if (attachment.getFileName().endsWith(".xml")) {
						cfdi.setExisteXml(true);
						try {
							byte[] xmlByteContent = gastoService.obtenerXmlAdjunto(attachment.getUrl());
							xml = xmlReaderWithoutBOM(xmlByteContent);
							cfdi = cfdiXmlParserService.parse(xml);
						} catch (Exception ex) {
							log.warn("No se pudo leer/parsear CFDI XML, se continúa con valores vacíos. {}",
									ex.getMessage());
						}
					} else if (attachment.getFileName().endsWith(".pdf")) {
						cfdi.setExistePDF(true);
					}
				}
			}
		}
		return cfdi;
	}

	private String xmlReaderWithoutBOM(byte[] xmlByte) {

		String xmlContent = new String(xmlByte, StandardCharsets.UTF_8);
		String xmlContentWithoutBom = null;

		if (xmlContent.indexOf("<?xml") != -1) {
			xmlContentWithoutBom = xmlContent.substring(xmlContent.indexOf("<?xml"), xmlContent.length());
		} else {
			xmlContentWithoutBom = xmlContent;
		}

		return xmlContentWithoutBom;

	}

	private BalanzaDTO llenarBalanzaDTO(InformeDTO item, GastoDTO gasto, UsuarioDTO usuarioDTO,
			UsuarioDTO usuarioSupervisorDTO, UsuarioSonar usuarioSonar, ViajeDTO viajeDTO, DotacionDTO dotacionDTO,
			TravelServiceDTO travelServiceDTO, CustomFieldDTO tipoViaje, CustomFieldDTO motivoViaje,
			CfdiResumenDTO cfdi, CustomFieldDTO propinaCustom, String kilometraje, int consecutivo) {

		BalanzaDTO balanzaDTO = null;
		String employeeCode = safe(() -> usuarioDTO.getUserOptions().getEmployeeCode());

		if (gasto == null) {
			balanzaDTO = new BalanzaDTO();
		} else {
			balanzaDTO = gastoBalanzaMapper.toBalanza(gasto);
			balanzaDTO.setFechaContable(safeDate(() -> gasto.getDate().format(formatter)).toUpperCase());
			balanzaDTO.setOperacionLig(employeeCode + " " + safe(() -> String.valueOf(gasto.getId())));
			balanzaDTO.setFechaRegistro(safeDate(() -> gasto.getCreationDate().format(formatter)).toUpperCase());
			balanzaDTO.setConceptoGto(safe(() -> gasto.getCategory().getId() + "-" + gasto.getCategory().getName()));
			balanzaDTO.setNomComercial(safe(() -> gasto.getMerchant()));
			balanzaDTO.setGasto(safe(gasto.getExpenseAmount().getValue()));
			balanzaDTO.setOp(obtenerOp(gasto.getPaymentMethod(), item, gasto.getCategory().getId()));
			balanzaDTO.setDeducible(
					gasto.getPaymentMethod().getId().equals(MetodoPagoEnum.TARJETA_EMPRESARIAL.getIdMetodo()) ? "SI"
							: "NO");
		}

		if (viajeDTO != null) {
			balanzaDTO.setFchDep(safe(viajeDTO.getStatusDate().format(formatterDDMMYYYY)));
			balanzaDTO.setMotivo(motivoViaje != null ? safe(motivoViaje.getValue()) : "");

			balanzaDTO.setProyecto(safe(viajeDTO.getComments()));
			balanzaDTO.setDescripcion(safe(viajeDTO.getComments()));
			balanzaDTO.setMes(safe(viajeDTO.getStatusDate().format(formatterMesAnio)));
			balanzaDTO.setDd(safe(viajeDTO.getStatusDate().getDayOfMonth()));
			balanzaDTO.setAa(safe(viajeDTO.getStatusDate().getYear()));
		}

		balanzaDTO.setDif1(String.valueOf(consecutivo));

		balanzaDTO.setRegistro(employeeCode);

		balanzaDTO.setViajero(safe(() -> usuarioDTO.getName()));
		balanzaDTO.setNombreViajero(safe(() -> usuarioDTO.getName()));
		balanzaDTO.setFolio(safe(() -> item.getName()));

		if (usuarioSonar != null) {

			balanzaDTO.setCr(usuarioSonar.getCodigoCr());
			balanzaDTO.setCrNom(usuarioSonar.getNombreCr());
			balanzaDTO.setCo(usuarioSonar.getCodigoDireccionGeneral());
			balanzaDTO.setCoNom(usuarioSonar.getNombreDireccionGeneral());
			balanzaDTO.setAutorizador(usuarioSonar.getLoginSupervisor());
			balanzaDTO.setDga(usuarioSonar.getNombreDireccionGeneral());
			balanzaDTO.setDireccion(usuarioSonar.getNombreDireccion());
			balanzaDTO.setSubdireccion(usuarioSonar.getNombreSubDireccion());
			balanzaDTO.setDep(usuarioSonar.getNombreDepartamento());
			balanzaDTO.setPuesto(usuarioSonar.getNombrePuesto());
			balanzaDTO.setCvePuesto(usuarioSonar.getCodigoPuesto());
			balanzaDTO.setBanca2(usuarioSonar.getCodigoEmpresa());
		}

		if (dotacionDTO != null) {
			balanzaDTO.setDotDit(dotacionDTO != null ? safe(String.valueOf(dotacionDTO.getId())) : "");

		}

		balanzaDTO.setTpoViaje(tipoViaje != null ? safe(tipoViaje.getValue()) : "");

		balanzaDTO.setOrigenPlaza(safe(obtenerDeparture(travelServiceDTO)));
		balanzaDTO.setDestino(safe(obtenerDestination(travelServiceDTO)));

		if (cfdi != null) {
			balanzaDTO.setRazonSocial(safe(cfdi.getEmisorNombre()));
			balanzaDTO.setRfcProveedor(validaRFC(cfdi.getEmisorRfc()));
			balanzaDTO.setImpTot(safe(cfdi.getEmisorRfc()));
			balanzaDTO.setFchFactura(safe(cfdi.getFecha()));

			balanzaDTO.setXml(cfdi.isExisteXml() ? "SI" : "NO");
			balanzaDTO.setPdf(cfdi.isExistePDF() ? "SI" : "NO");

			balanzaDTO.setImpTot(cfdi.getTotal() != null ? formatCantidad(cfdi.getTotal()) : "");

			balanzaDTO.setTasaIva(cfdi.getTasa() != null ? formatCantidad(cfdi.getTasa()) : "");

			balanzaDTO.setImpNeto(cfdi.getSubTotal() != null ? formatCantidad(cfdi.getSubTotal()) : "");

			balanzaDTO.setImpIva(
					cfdi.getTotalImpuestosTrasladados() != null ? formatCantidad(cfdi.getTotalImpuestosTrasladados())
							: "");

			balanzaDTO.setImpPorConcepto(cfdi.getConceptos() != null
					? cfdi.getConceptos().stream().filter(Objects::nonNull)
							.map(c -> c.descripcion + "-" + df.format(c.importe)).collect(Collectors.joining(";"))
					: "");

			balanzaDTO.setUuid(cfdi.getUuid());

		}else{
			balanzaDTO.setRfcProveedor("XAXX010101000");
		}

		balanzaDTO.setTipoRfc("Proveedor");

        if(gasto!=null && gasto.getPaymentMethod()!=null)
			balanzaDTO.setRegistroContable(gasto.getPaymentMethod().getId().equals(
				MetodoPagoEnum.TARJETA_EMPRESARIAL.getIdMetodo()) ? "Resultados" : "No deducible");

		balanzaDTO.setImpPropina(propinaCustom != null ? propinaCustom.getValue() : "");

		balanzaDTO.setImpPropina2(propinaCustom != null ? propinaCustom.getValue() : "");

		balanzaDTO.setCcImpNeto(creaerCuentaContableSearchRequestDTO(gasto, cfdi, usuarioDTO, TipoGastoEnum.GASTOS));

		balanzaDTO.setCcImpIva(creaerCuentaContableSearchRequestDTO(gasto, cfdi, usuarioDTO, TipoGastoEnum.IVAS));

		balanzaDTO.setCcPropina(
				creaerCuentaContableSearchRequestDTO(gasto, cfdi, usuarioDTO, TipoGastoEnum.OTROS_GASTOS));

		balanzaDTO.setNota("");

		balanzaDTO.setFechaTramite(safeDate(() -> item.getStatusDate().format(formatterDDMMYYYY)));

		balanzaDTO.setOperPagaduria(
				usuarioSupervisorDTO.getUserOptions() != null ? usuarioSupervisorDTO.getUserOptions().getEmployeeCode()
						: "");

		balanzaDTO.setKilometraje(safe(kilometraje));
		balanzaDTO.setPais("1MEXICO");
		balanzaDTO.setPaisCamelCase("1MEXICO");
		return balanzaDTO;
	}

	private String formatCantidad(BigDecimal cantidad) {
		try {
			return df.format(cantidad);
		} catch (Exception e) {
			return "";
		}
	}

	private boolean empiezaConPrefijo(String cadena) {

		if (cadena == null)
			return false;
		return PREFIJOS_VIA.stream().anyMatch(cadena::startsWith);
	}

	private String creaerCuentaContableSearchRequestDTO(GastoDTO gasto, CfdiResumenDTO cfdi, UsuarioDTO usuarioDTO,
			TipoGastoEnum tipoGasto) {
		List<CuentaContableDTO> cuentas = new ArrayList<>();
		CuentaContableSearchRequestDTO cuentaContableSearchRequestDTO = new CuentaContableSearchRequestDTO();
		try {
			cuentaContableSearchRequestDTO.setIdCategoria(Integer.valueOf(gasto.getCategory().getCode()));
			cuentaContableSearchRequestDTO.setIdEmpresa(!usuarioDTO.getUserOptions().getCompanyCode().isBlank()
					? Integer.valueOf(usuarioDTO.getUserOptions().getCompanyCode())
					: 0);
			cuentaContableSearchRequestDTO.setIdMedioPago(gasto.getPaymentMethod().getId());
			cuentaContableSearchRequestDTO.setIdTipoGasto(tipoGasto.getId());

			cuentas = cuentaContableService.buscar(cuentaContableSearchRequestDTO);

		} catch (NumberFormatException e) {
			log.error("El código de empresa no es numerico " + e.getMessage());
		} catch (Exception e) {
			return "";
		}

		return !cuentas.isEmpty() ? cuentas.get(0).getCuentaContable() : "";

	}

	private void agregaRegistro(List<BalanzaDTO> gastos) {
		final Path path = Paths.get(properties.getBalanzaLocalDir() + DateUtils.obtenerFechaActual() + "_"
				+ properties.getBalanzaLocalName());
		if (path.getParent() != null) {
			try {
				Files.createDirectories(path.getParent());
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		String block = null;
		if (Files.exists(path)) {
			block = BalanzaPipeExportUtil.toPipe(gastos, false);
		} else {
			block = BalanzaPipeExportUtil.toPipe(gastos, true);
		}
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
			List<CustomFieldDTO> cfs = v.getCustomFields();
			if (cfs == null || cfs.isEmpty()) {
				return false;
			}

			return cfs.stream().anyMatch(cf -> cf != null && cf.getValue() != null
					&& Objects.equals(esperado, String.valueOf(cf.getValue())));
		}).toList();
	}

	public String obtenerEndDate(TravelServiceDTO travel) {
		if (travel == null) {
			return null;
		}
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

		if (travel == null) {
			return null;
		}

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

		if (travel == null) {
			return null;
		}

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

		if (travel == null) {
			return null;
		}

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

	private String safe(String value) {
		return value != null ? value : "";
	}

	private String validaRFC(String rfc) {
		if(rfc==null){
			return "XAXX010101000";
		}else{
			if(rfc.length()<2)
				return "XAXX010101000";
			else return rfc;
		}
	}

	private String safe(Object value) {
		return value != null ? String.valueOf(value) : "";
	}

	private String safe(Supplier<String> supplier) {
		try {
			String v = supplier.get();
			return v != null ? v : "";
		} catch (Exception e) {
			return "";
		}
	}

	private String safeDate(Supplier<String> supplier) {
		try {
			String v = supplier.get();
			return v != null ? v : "";
		} catch (Exception e) {
			return "";
		}
	}

	private String obtenerOp(MetodoPagoDTO metodo, InformeDTO informe, Integer idCategoria) {
		String oP = "";

		if (informe.getName().startsWith("RMED_")) {
			oP = "REM";
		} else if (idCategoria.equals(CategoriasEnum.CAJA_CHICA_COMIDA_HORARIO_EXTRAORDINARIO.getClave())) {
			oP = "RCC ";
		} else {
			if (metodo.getId().equals(MetodoPagoEnum.TARJETA_EMPRESARIAL.getIdMetodo())) {
				oP = "LIQ";
			} else if (metodo.getId().equals(MetodoPagoEnum.EFECTIVO.getIdMetodo())) {
				oP = "REE";
			}
		}
		return oP;
	}

}
