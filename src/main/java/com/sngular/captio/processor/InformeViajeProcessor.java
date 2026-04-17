package com.sngular.captio.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sngular.captio.dto.CustomFieldDTO;
import com.sngular.captio.dto.DotacionDTO;
import com.sngular.captio.dto.ErrorDTO;
import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.dto.TravelServiceDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.ViajeDTO;
import com.sngular.captio.dto.WorkFlowDTO;
import com.sngular.captio.enums.CustomFieldsEnum;
import com.sngular.captio.enums.NombreInformeEnum;
import com.sngular.captio.enums.WorkFlowEnum;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.DotacionService;
import com.sngular.captio.services.EmailService;
import com.sngular.captio.services.InformeService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.services.ViajeService;
import com.sngular.captio.util.CaptioUtils;
import com.sngular.captio.util.CustomFieldsUtils;
import com.sngular.captio.util.DateUtils;
import com.sngular.captio.util.ObjetosUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class InformeViajeProcessor implements ItemProcessor<ViajeDTO, InformeDTO> {

	private final InformeService informeService;

	private final Properties properties;

	private final DotacionService dotacionService;

	private final EmailService emailService;

	private final UsuarioService usuarioService;

	private final ViajeService viajeService;

	@Override
	public InformeDTO process(ViajeDTO item) throws Exception {
		log.info("process");
		CustomFieldDTO tipoViaje = CaptioUtils.obtieneTipoViaje(item);

		if (item.getCustomFields().isEmpty()) {
			escribirErrorViaje(item, "Sin información de tipo de viaje");
			return null;
		}

		CustomFieldDTO custom = CustomFieldsUtils.buscarPorId(item.getCustomFields(),
				CustomFieldsEnum.NOMBRE_INFORME.getIdCustomField());

		if (custom != null) {
			return null;
		}

		Integer usuarioId = item.getUser().getId();

		UsuarioDTO usuarioDTO = usuarioService.obtenerFlujoUsuarioByFiltro(String.format("{\"Id\": %d}", usuarioId));

		List<WorkFlowDTO> workflows = filtrarTipo1(usuarioDTO.getWorkflows());
		if (workflows.isEmpty())
			return null;

		UsuarioDTO usuarioBase = usuarioService.obtenerUsuarioByFiltro(String.format("{\"Id\": %d}", usuarioId))
				.stream().findFirst().orElse(null);

		InformeDTO informeDTO = crearInforme(usuarioBase, workflows.get(0), null, obtenerTipoInforme(tipoViaje, item));

		if (informeDTO == null)
			return null;

		Thread.sleep(2000);

		List<InformeDTO> informes = informeService
				.obtenerInformes(String.format("{\"Name\":\"%s\"}", informeDTO.getName()));

		if (informes.isEmpty())
			return null;

		agregarCustomFieldsViaje(item, informeDTO.getName());

		InformeDTO informe = informes.get(0);
		prepararInforme(informe);

		manejarCorreoYDotaciones(item, informe);

		return informe;
	}

	private String obtenerTipoInforme(CustomFieldDTO tipoDeViaje, ViajeDTO viajeDTO) {
		NombreInformeEnum nombreInformeEnum = null;

		List<TravelServiceDTO> travels = viajeService.obtenerServiciosViajes("{\"Id\":" + viajeDTO.getId() + "}");

		if ((travels.get(0).getFlights() != null || !travels.get(0).getFlights().isEmpty())
				&& travels.get(0).getFlights().size() > 2) {
			if (tipoDeViaje.getValue().equals("Nacional")) {
				nombreInformeEnum = NombreInformeEnum.MULTIDESTINO_NACIONAL;
			} else if (tipoDeViaje.getValue().equals("Extranjero")) {
				nombreInformeEnum = NombreInformeEnum.MULTIDESTINO_INTERNACIONAL;
			} else {
				nombreInformeEnum = NombreInformeEnum.MULTIDESTINO_INTERCONTINENTAL;
			}
		} else {
			if (tipoDeViaje.getValue().equals("Nacional")) {
				nombreInformeEnum = NombreInformeEnum.NACIONAL;
			} else if (tipoDeViaje.getValue().equals("Extranjero")) {
				nombreInformeEnum = NombreInformeEnum.INTERNACIONAL;
			} else {
				nombreInformeEnum = NombreInformeEnum.INTERCONTINENTAL;
			}
		}

		return nombreInformeEnum.getNombreInforme();

	}

	private void agregarCustomFieldsViaje(ViajeDTO viaje, String nombre) {
		List<CustomFieldDTO> fields = new ArrayList<>();
		CustomFieldDTO field = new CustomFieldDTO();
		field.setId(CustomFieldsEnum.NOMBRE_INFORME.getIdCustomField());
		field.setValue(nombre);
		fields.add(field);
		viaje.setCustomFields(fields);
		viajeService.actualizarViajeCustomFields(viaje);
	}

	private InformeDTO crearInforme(UsuarioDTO usuario, WorkFlowDTO workflow, List<ErrorDTO> errores, String nombre) {
		StringBuilder sb = new StringBuilder();
		if (errores != null) {
			for (ErrorDTO error : errores) {
				sb.append(error.getDescripcion()).append("; ");
			}
		}
		String comment = sb == null ? "" : sb.toString();
		int maxLen = 400;
		comment = comment.substring(0, Math.min(maxLen, comment.length()));

		return informeService.crearInforme(usuario, workflow, comment, nombre);
	}

	private void prepararInforme(InformeDTO informe) {
		informe.setSendEmail(true);
		informe.setSkipAlertsPreview(true);
	}

	private void manejarCorreoYDotaciones(ViajeDTO item, InformeDTO informe) {
		try {

			List<DotacionDTO> dotaciones = dotacionService
					.obtenerDotacion(String.format("{\"User_Id\":\"%d\"}", item.getUser().getId()));

			filtrarEnSitio(dotaciones, item.getId());

			ObjetosUtils.limpiarCamposExcepto(dotaciones, List.of("id"));
			ObjetosUtils.limpiarCamposExcepto(List.of(informe), List.of("id"));

			informe.setAnticipos(dotaciones);

			dotaciones.forEach(d -> {
				d.setDeliveryDate(DateUtils.obtenerFechaInicialServicios(item.getStartDate()));
				try {
					dotacionService.entregarDotacion(d);
				} catch (JsonProcessingException e) {
					log.error(e.getMessage());
				}
			});

			informeService.agregarAnticipoInforme(List.of(informe));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
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

	public static List<WorkFlowDTO> filtrarTipo1(List<WorkFlowDTO> lista) {
		if (lista == null)
			return List.of();
		return lista.stream().filter(Objects::nonNull).filter(wf -> Objects.equals(wf.getType(), 1)).filter(wf -> !Objects.equals(wf.getId(), WorkFlowEnum.GASTOS_MEDICOS.getIdworkFlow())).toList();
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

}
