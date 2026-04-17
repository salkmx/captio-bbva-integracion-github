package com.sngular.captio.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.sngular.captio.enums.NombreInformeEnum;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.EmailService;
import com.sngular.captio.services.GastoService;
import com.sngular.captio.services.GrupoService;
import com.sngular.captio.services.InformeService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.util.DateUtils;
import com.sngular.captio.util.ObjetosUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class InformeOtrosGastosProcessor implements ItemProcessor<InformeDTO, InformeDTO> {

	private final GastoService gastoService;

	private final Properties properties;

	private final EmailService emailService;

	private final UsuarioService usuarioService;

	private final GrupoService grupoService;

	private final InformeService informeService;

	@Override
	public InformeDTO process(InformeDTO item) throws Exception {
		if (item != null && Arrays.stream(NombreInformeEnum.values())
				.noneMatch(t -> item.getName().startsWith(t.getNombreInforme()))) {
			List<GastoDTO> gastos = gastoService.obtenerGastosPorFiltro("{\"Report_Id\":" + item.getId() + "}");

			List<UsuarioDTO> usuarios = grupoService.obtenerGrupos("{\"Id\":" + item.getUser().getId() + "}");

			if (usuarios != null && !usuarios.isEmpty()) {
				List<ErrorDTO> errores = gastoService.validacionesGastosOtros(gastos, usuarios.get(0).getGrupos());

				if (errores != null && !errores.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					for (ErrorDTO error : errores) {
						sb.append(error.getDescripcion()).append("; ");
					}
					String comment = sb == null ? "" : sb.toString();
					int maxLen = 400;
					comment = comment.substring(0, Math.min(maxLen, comment.length()));
					UsuarioDTO usuarioDTO = usuarioService
							.obtenerFlujoUsuarioByFiltro(String.format("{\"Id\": %d}", item.getUser().getId()));
					manejarCorreo(errores, item, usuarioDTO.getId());
					List<CustomFieldDTO> fields = new ArrayList<>();
					CustomFieldDTO customFieldDTO = new CustomFieldDTO();
					customFieldDTO.setValue(comment);
					customFieldDTO.setId(CustomFieldsEnum.ERRORES_INFORME_GASTOS.getIdCustomField());
					fields.add(customFieldDTO);
					item.setCustomFields(fields);
					ObjetosUtils.limpiarCamposExcepto(item, List.of("id", "customFields"));
					item.setGastos(gastos);
					informeService.actualizarInforme(item);
				}
			}

		}
		return item;
	}

	private void manejarCorreo(List<ErrorDTO> errores, InformeDTO informeDTO, Integer usuarioId) {
		try {
			List<UsuarioDTO> usuarios = usuarioService
					.obtenerUsuarioByFiltro(String.format("{\"Id\":\"%d\"}", usuarioId));

			String correo = (usuarios != null && !usuarios.isEmpty()) ? usuarios.get(0).getEmail() : null;
			enviarCorreoInforme(errores, informeDTO, correo);

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
						erroresCadenas, informe.getName(), "email/error_otros_gastos_informe", informe.getId().toString()));
	}

	public static List<WorkFlowDTO> filtrarTipo1(List<WorkFlowDTO> lista) {
		if (lista == null)
			return List.of();
		return lista.stream().filter(Objects::nonNull).filter(wf -> Objects.equals(wf.getType(), 1)).toList();
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
