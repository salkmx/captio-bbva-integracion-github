package com.sngular.captio.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.CustomFieldDTO;
import com.sngular.captio.dto.ErrorDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.ViajeDTO;
import com.sngular.captio.dto.WorkFlowDTO;
import com.sngular.captio.enums.CustomFieldsEnum;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.EmailService;
import com.sngular.captio.services.GastoService;
import com.sngular.captio.services.InformeService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.services.ViajeService;
import com.sngular.captio.util.DateUtils;
import com.sngular.captio.util.ObjetosUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class InformeViajeValidacionProcessor implements ItemProcessor<InformeDTO, InformeDTO> {

	private final GastoService gastoService;

	private final InformeService informeService;

	private final Properties properties;

	private final EmailService emailService;

	private final UsuarioService usuarioService;

	private final ViajeService viajeService;

	@Override
	public InformeDTO process(InformeDTO item) throws Exception {
		log.info("process");

		Integer usuarioId = item.getUser().getId();

		List<ViajeDTO> viajes = viajeService.obtenerViajesAprobados("{\"User_Id\": " + usuarioId + " }");

		ViajeDTO viaje = buscarViajePorValorCustomField(viajes, item.getName());

		if (viaje == null) {
			escribirErrorInforme(item, "Informe no asociado a viaje");
			return null;
		}

		List<GastoDTO> gastos = gastoService
				.obtenerGastosPorFiltro((String.format("{\"Report_Id\": %d}", item.getId())));

		if (gastos.isEmpty()) {
			escribirErrorViaje(viaje, "Viaje sin gastos");
			return null;
		}

		UsuarioDTO usuarioBase = usuarioService.obtenerUsuarioByFiltro(String.format("{\"Id\": %d}", usuarioId))
				.stream().findFirst().orElse(null);

		List<InformeDTO> informes = new ArrayList<>();
		informes.add(item);

		if (compararFechas(viaje.getEndDate()) || item.getStatus() == 2) {

			List<GastoDTO> gastosValidacion = gastoService.obtenerGastosPorFiltro("{Report_Id: " + item.getId() + "}");

			List<ErrorDTO> errores = gastoService.validacionesGastos(gastosValidacion, viaje);
			StringBuilder sb = new StringBuilder();
			for (ErrorDTO error : errores) {
				sb.append(error.getDescripcion()).append("; ");
			}
			String comment = sb == null ? "" : sb.toString();
			int maxLen = 400;
			comment = comment.substring(0, Math.min(maxLen, comment.length()));
			List<CustomFieldDTO> fields = new ArrayList<>();
			CustomFieldDTO customFieldDTO = new CustomFieldDTO();
			customFieldDTO.setValue(comment);
			customFieldDTO.setId(CustomFieldsEnum.ERRORES_INFORME_GASTOS.getIdCustomField());
			fields.add(customFieldDTO);
			item.setCustomFields(fields);
			ObjetosUtils.limpiarCamposExcepto(item, List.of("id", "customFields", "status"));

			informeService.actualizarInforme(item);

			if (!errores.isEmpty()) {
				escribirErrorGasto(errores, informes.get(0), usuarioBase != null ? usuarioBase.getEmail() : null);

				manejarCorreoYDotaciones(viaje, errores, item, usuarioId);

			}
		}

		return item;
	}

	public ViajeDTO buscarViajePorValorCustomField(List<ViajeDTO> viajes, String textoBuscado) {
		if (viajes == null || textoBuscado == null) {
			return null;
		}

		return viajes.stream()
				.filter(viaje -> viaje.getCustomFields() != null
						&& viaje.getCustomFields().stream().anyMatch(cf -> textoBuscado.equals(cf.getValue())))
				.findFirst().orElse(null);
	}

	public boolean compararFechas(LocalDateTime fechaOriginal) {

		LocalDateTime fechaMas10Dias = fechaOriginal.plusDays(10);

		LocalDateTime hoy = LocalDateTime.now();

		if (fechaMas10Dias.isEqual(hoy) || fechaMas10Dias.isAfter(hoy)) {
			return true;
		} else {
			return false;
		}
	}

	private void manejarCorreoYDotaciones(ViajeDTO item, List<ErrorDTO> errores, InformeDTO informe,
			Integer usuarioId) {
		try {
			List<UsuarioDTO> usuarios = usuarioService
					.obtenerUsuarioByFiltro(String.format("{\"Id\":\"%d\"}", usuarioId));

			String correo = (usuarios != null && !usuarios.isEmpty()) ? usuarios.get(0).getEmail() : null;
			enviarCorreoInforme(errores, informe, correo);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public void enviarCorreoInforme(List<ErrorDTO> errores, InformeDTO informe, String correo) {
		List<String> erroresCadenas = new ArrayList<>();
		if (errores != null) {
			for (ErrorDTO error : errores) {
				StringBuilder sb = new StringBuilder();

				sb.append(error.getDescripcion()).append(',').append(error.getDescripcionObjeto()).append('\n');

				erroresCadenas.add(sb.toString());
			}
		}
		emailService
				.enviarCorreo(emailService.crearMailDTOErrores(correo, "Se encontraron errores al generar el informe",
						erroresCadenas, informe.getName(), "email/error_gastos_viaje_informe", informe.getId().toString()));
	}

	public void escribirErrorGasto(List<ErrorDTO> errores, InformeDTO informe, String correoAprobador) {
		String ruta = properties.getRutaArchivoErrorViajes() + DateUtils.obtenerFechaActual() + ".csv";
		List<String> erroresCadenas = new ArrayList<>();
		for (ErrorDTO error : errores) {
			StringBuilder sb = new StringBuilder();

			sb.append(error.getDescripcion()).append(',').append(error.getDescripcionObjeto()).append('\n');

			erroresCadenas.add(sb.toString());
		}

		if (informe != null) {

			emailService.enviarCorreo(emailService.crearMailDTOErrores(correoAprobador,
					"Se encontraron errores al generar el informe", erroresCadenas, informe.getName(),
					"email/error_gastos_viaje_informe", informe.getId().toString()));
		}

		try {
			File archivo = new File(ruta);

			archivo.getParentFile().mkdirs();

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
				for (ErrorDTO error : errores) {

					writer.write(error.getDescripcion());
					writer.write(",");
					writer.write(error.getDescripcionObjeto());

				}
			}
		} catch (IOException e) {
			log.error("Error al escribir registro: " + e.getMessage(), e);
		}
	}

	public void escribirErrorViaje(ViajeDTO item, String error) {
		String ruta = properties.getRutaArchivoErrorGastos() + DateUtils.obtenerFechaActual() + ".csv";

		try {
			File archivo = new File(ruta);

			archivo.getParentFile().mkdirs();

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
				writer.write(item.getId().toString());
				writer.write(",");
				writer.write(error);
				writer.newLine();
			}
		} catch (IOException e) {
			log.error("Error al escribir registro: " + e.getMessage(), e);
		}
	}

	public void escribirErrorInforme(InformeDTO item, String error) {
		String ruta = properties.getRutaArchivoErrorGastos() + DateUtils.obtenerFechaActual() + ".csv";

		try {
			File archivo = new File(ruta);

			archivo.getParentFile().mkdirs();

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
				writer.write(item.getId().toString());
				writer.write(",");
				writer.write(error);
				writer.newLine();
			}
		} catch (IOException e) {
			log.error("Error al escribir registro: " + e.getMessage(), e);
		}
	}

}
