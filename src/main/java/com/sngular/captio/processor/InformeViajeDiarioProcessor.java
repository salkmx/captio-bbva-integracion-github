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
import com.sngular.captio.dto.DotacionDTO;
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
import com.sngular.captio.util.DateUtils;
import com.sngular.captio.util.ObjetosUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class InformeViajeDiarioProcessor implements ItemProcessor<ViajeDTO, InformeDTO> {

	private final GastoService gastoService;

	private final InformeService informeService;

	private final Properties properties;

	private final EmailService emailService;

	private final UsuarioService usuarioService;

	@Override
	public InformeDTO process(ViajeDTO item) throws Exception {
		log.info("process");
		Integer usuarioId = item.getUser().getId();

		if (item.getCustomFields().isEmpty()) {
			escribirErrorViaje(item, "Sin información de tipo de viaje");
			return null;
		}

		// 1. Obtener gastos nuevos sin informe (Report_Id = null)
		List<GastoDTO> gastosNuevos = gastoService.obtenerGastosPorUsuario(item);

		//Dejamos comentada esta sección porqué no importa que el viaje no tenga gastos de todos modos debe de validar los gastos
		/*if (gastosNuevos.isEmpty()) {
			escribirErrorViaje(item, "Viaje sin gastos");
			return null;
		}*/

		// 2. Buscar el informe por nombre
		List<InformeDTO> informes = informeService.obtenerInformes(String.format("{\"Name\":\"%s\"}",
				buscarPorId(item.getCustomFields(), CustomFieldsEnum.NOMBRE_INFORME.getIdCustomField()).getValue()));

		if (informes.isEmpty())
			return null;

		InformeDTO informe = informes.get(0);

		// 3. Obtener gastos que ya están en el informe
		List<GastoDTO> gastosExistentes = gastoService.obtenerGastosPorFiltro(
				"{\"Report_Id\": " + informe.getId() + "}");

		// 4. Combinar gastos existentes + nuevos (sin duplicados por ID)
		List<GastoDTO> todosLosGastos = new ArrayList<>();
		if (gastosExistentes != null && !gastosExistentes.isEmpty()) {
			todosLosGastos.addAll(gastosExistentes);
		}

		for (GastoDTO nuevo : gastosNuevos) {
			boolean yaExiste = todosLosGastos.stream()
					.anyMatch(g -> g.getId().equals(nuevo.getId()));
			if (!yaExiste) {
				todosLosGastos.add(nuevo);
			}
		}

		// 5. Validar TODOS los gastos (existentes + nuevos)
		List<ErrorDTO> errores = gastoService.validacionesGastos(todosLosGastos, item);

		// 6. Solo enviar los gastos NUEVOS al informe (los existentes ya están
		// asociados)
		if (!gastosNuevos.isEmpty()) {
			informe.setGastos(gastosNuevos);

			List<InformeDTO> informesAAgregar = new ArrayList<>();
			informesAAgregar.add(informe);

			if (!informeService.agregarGastosInforme(informesAAgregar))
				return null;
		}

		if (errores != null && !errores.isEmpty()) {
			UsuarioDTO usuarioBase = usuarioService.obtenerUsuarioByFiltro(String.format("{\"Id\": %d}", usuarioId))
					.stream().findFirst().orElse(null);
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
			informe.setCustomFields(fields);
			ObjetosUtils.limpiarCamposExcepto(informe, List.of("id", "customFields"));

			informeService.actualizarInforme(informe);

			if (!errores.isEmpty()) {
				escribirErrorGasto(errores, informes.get(0), usuarioBase != null ? usuarioBase.getEmail() : null);

				manejarCorreoYDotaciones(item, errores, informe, usuarioId);

			}
		}

		return informe;
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

	public boolean compararFechas(LocalDateTime fechaOriginal) {

		LocalDateTime fechaMas10Dias = fechaOriginal.plusDays(10);

		LocalDateTime hoy = LocalDateTime.now();

		if (fechaMas10Dias.isEqual(hoy) || fechaMas10Dias.isAfter(hoy)) {
			return true;
		} else {
			return false;
		}
	}

	public CustomFieldDTO buscarPorId(List<CustomFieldDTO> customFields, Integer idBuscado) {
		if (customFields == null || idBuscado == null) {
			return null;
		}

		return customFields.stream().filter(cf -> idBuscado.equals(cf.getId())).findFirst().orElse(null);
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

				// Extraer solo el monto de descripcionObjeto (última parte después de la última
				// coma)
				String monto = "";
				if (error.getDescripcionObjeto() != null) {
					String[] partes = error.getDescripcionObjeto().split(",");
					if (partes.length > 0) {
						monto = partes[partes.length - 1].trim();
					}
				}

				sb.append(error.getDescripcion());
				if (!monto.isEmpty()) {
					sb.append(", ").append(monto);
				}
				sb.append('\n');

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

			// Extraer solo el monto de descripcionObjeto (última parte después de la última
			// coma)
			String monto = "";
			if (error.getDescripcionObjeto() != null) {
				String[] partes = error.getDescripcionObjeto().split(",");
				if (partes.length > 0) {
					monto = partes[partes.length - 1].trim();
				}
			}

			sb.append(error.getDescripcion());
			if (!monto.isEmpty()) {
				sb.append(", ").append(monto);
			}
			sb.append('\n');

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
