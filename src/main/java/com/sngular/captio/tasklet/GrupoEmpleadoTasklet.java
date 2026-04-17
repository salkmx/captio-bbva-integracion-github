package com.sngular.captio.tasklet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sngular.captio.config.GrupoEquivalenciasConfig;
import com.sngular.captio.dto.GrupoDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.enums.GrupoEnum;
import com.sngular.captio.mapper.EmpleadoSonarMapper;
import com.sngular.captio.model.UsuarioSonar;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.repository.UsuarioSonarRepository;
import com.sngular.captio.services.EmailService;
import com.sngular.captio.services.EmpleadoSonarService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrupoEmpleadoTasklet implements Tasklet {

    private final UsuarioSonarRepository usuarioSonarRepository;
    private final UsuarioService usuarioService;
    private final Properties properties;
    private final EmailService emailService;

    private final EmpleadoSonarService empleadoSonarService;
    EmpleadoSonarMapper mapper = Mappers.getMapper(EmpleadoSonarMapper.class);

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("[execute] Ejecutando alta de flujos");

        List<UsuarioSonar> usuariosActivosSonar = Optional.ofNullable(usuarioSonarRepository.obtenerUsuariosActivos())
                .orElse(Collections.emptyList());

        if (usuariosActivosSonar.isEmpty()) {
            log.info("No hay usuarios activos en Sonar para procesar");
            return RepeatStatus.FINISHED;
        }

        List<UsuarioDTO> usuarios = new ArrayList<>();
        List<Map<String, Object>> usuariosNoEncontrados = new ArrayList<>();


        for (UsuarioSonar usuarioSonar : usuariosActivosSonar) {

            UsuarioDTO usuarioDTO = obtenerUsuarioPorEmail(usuarioSonar.getEmail());
            if (usuarioDTO == null) {
                var errorEntry = handleUserNotFound(usuarioSonar);
                usuariosNoEncontrados.add(errorEntry);
                continue;
            }

            List<GrupoDTO> grupos = new ArrayList<>();
            grupos.add(createGroupByLevel(usuarioSonar.getNivelEstructura()));

            // Agregar grupo general para todos los usuarios
            grupos.add(new GrupoDTO()
                    .setGroupId(GrupoEnum.GENERAL.getId()));

            GrupoDTO grupoPuesto = asignarGrupoPorPuesto(usuarioSonar);
            if (grupoPuesto != null) {
                grupos.add(grupoPuesto);
            }

            GrupoDTO grupoDireccion = asignarGrupoPorDireccion(usuarioSonar);
            if (grupoDireccion != null) {
                grupos.add(grupoDireccion);
            }
            usuarioDTO.setGrupos(grupos);
            usuarios.add(usuarioDTO);
        }

        // Escribir errores de usuarios no encontrados
        if (!usuariosNoEncontrados.isEmpty()) {
            escribirErrorUsuarios(usuariosNoEncontrados);
            log.warn("{} usuarios no encontrados en Captio - registrados en archivo de errores",
                    usuariosNoEncontrados.size());
        }

        if (!usuarios.isEmpty()) {
            usuarioService.joinGroup(usuarios);
        }

        return RepeatStatus.FINISHED;
    }

    private Map<String, Object> handleUserNotFound(UsuarioSonar usuarioSonar) {
        String emailInfo = (usuarioSonar.getEmail() == null || usuarioSonar.getEmail().isBlank())
                ? "(email vacio)"
                : usuarioSonar.getEmail();
        log.warn("No se encontró usuario en BBVA/Captio para el email: {}", emailInfo);

        // Crear error en formato JSON consistente con el resto del sistema
        Map<String, Object> errorEntry = new HashMap<>();
        errorEntry.put("Key", "Email");
        errorEntry.put("Value", emailInfo);

        // Construir mensaje de error con campos del archivo VIBASONAR para identificar
        // el registro
        String mensajeError = String.format(
                "Usuario no encontrado en Captio - CodRegistro: %s | Nombre: %s %s %s | CodEmpleado: %s",
                obtenerValorONA(usuarioSonar.getCodigoRegistro()),
                obtenerValorONA(usuarioSonar.getNombreEmpleado()),
                obtenerValorONA(usuarioSonar.getApellidoPaterno()),
                obtenerValorONA(usuarioSonar.getApellidoMaterno()),
                obtenerValorONA(usuarioSonar.getCodigoEmpleado()));

        Map<String, Object> validation = new HashMap<>();
        validation.put("Message", mensajeError);
        validation.put("Code", "USER_NOT_FOUND_IN_CAPTIO");
        validation.put("Key", "stepGrupo");
        validation.put("Value", null);

        errorEntry.put("Validations", List.of(validation));
        errorEntry.put("Status", 1);
        return errorEntry;
    }

    /**
     * Escribe los errores de usuarios no encontrados en el archivo CSV de errores
     * en formato JSON y envia email de notificacion.
     */
    private void escribirErrorUsuarios(List<Map<String, Object>> errores) {
        String ruta = properties.getRutaArchivoErrorUsuarios() + DateUtils.obtenerFechaActual() + ".csv";

        try {
            File archivo = new File(ruta);
            archivo.getParentFile().mkdirs();

            // Convertir a JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonError = mapper.writeValueAsString(errores);

            // Construir mensaje para email
            StringBuilder sb = new StringBuilder();
            sb.append("[stepGrupo] Usuarios no encontrados en Captio:").append(System.lineSeparator());
            sb.append(System.lineSeparator());
            for (Map<String, Object> error : errores) {
                sb.append(error.get("Value"));
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> validations = (List<Map<String, Object>>) error.get("Validations");
                if (validations != null && !validations.isEmpty()) {
                    sb.append(" - ").append(validations.get(0).get("Message"));
                }
                sb.append(System.lineSeparator());
            }

            // Enviar email
            for (String errorMail : properties.getErrorMails()) {
                emailService.enviarCorreo(
                        emailService.crearMailDTO(errorMail, "Usuario error - stepGrupo", sb.toString(),
                                "email/error_usuarios"));
            }

            // Escribir en archivo CSV con UTF-8
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(archivo, true), StandardCharsets.UTF_8))) {
                writer.write(jsonError);
                writer.newLine();
            }
            log.debug("Errores de usuarios escritos en: {}", ruta);
        } catch (IOException e) {
            log.error("Error al escribir archivo de errores de usuarios: {}", e.getMessage(), e);
        }
    }

    private GrupoDTO createGroupByLevel(String nivelEstructuraStr) {
        int nivel;
        try {
            nivel = Integer.parseInt(nivelEstructuraStr);
        } catch (NumberFormatException e) {
            nivel = Integer.MAX_VALUE;
        }

        GrupoEnum grupoEnum = (nivel <= 29) ? GrupoEnum.NIVEL_2_O_INFERIOR : GrupoEnum.NIVEL_3_O_SUPERIOR;

        return new GrupoDTO()
                .setGroupId(grupoEnum.getId());
    }

    private UsuarioDTO obtenerUsuarioPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        var empleado = empleadoSonarService.findOneByEmail(email);
        return mapper.toDto(empleado);

//		String filtro = "{\"Email\":\"" + email + "\"}";
//		List<UsuarioDTO> encontrados = null;
//		try {
//			encontrados = usuarioService.obtenerUsuarioByFiltro(filtro);
//		} catch (Exception e) {
//			log.error(e.getMessage());
//		}
//
//		if (encontrados == null || encontrados.isEmpty()) {
//			return null;
//		}
//		return encontrados.get(0);
    }

    private GrupoDTO asignarGrupoPorPuesto(UsuarioSonar usuarioSonar) {
        if (usuarioSonar == null || usuarioSonar.getNombrePuesto() == null) return null;

        String puestoNormalizado = normalizar(usuarioSonar.getNombrePuesto()).trim();
        String puestoTruncado = puestoNormalizado.length() > 40
                ? puestoNormalizado.substring(0, 40)
                : puestoNormalizado;

        // Coincidencias exactas contra listas de puestos (0 falsos positivos)
        GrupoDTO grupo = crearGrupoSiCoincide(puestoTruncado,
                GrupoEquivalenciasConfig.PUESTOS_CIB_VALIDOS,
                GrupoEnum.EJECUTIVOS_CIB);
        if (grupo != null)
            return grupo;

        grupo = crearGrupoSiCoincide(puestoTruncado,
                GrupoEquivalenciasConfig.PUESTOS_CASH_MANAGEMENT_VALIDOS,
                GrupoEnum.EJECUTIVOS_CASH_MANAGEMENT);
        if (grupo != null)
            return grupo;

        grupo = crearGrupoSiCoincide(puestoTruncado,
                GrupoEquivalenciasConfig.PUESTOS_EJECUTIVOS_BEYG_VALIDOS,
                GrupoEnum.EJECUTIVOS_BEYG);
        if (grupo != null)
            return grupo;

        grupo = crearGrupoSiCoincide(puestoTruncado,
                GrupoEquivalenciasConfig.PERSONAL_DE_RETAIL_VALIDOS,
                GrupoEnum.PERSONAL_RETAIL);
        if (grupo != null)
            return grupo;

        // Caso especial: requiere dos palabras específicas
        if (puestoNormalizado.contains("asesor") && puestoNormalizado.contains("inversion")) {
            GrupoDTO grupoDTO = new GrupoDTO();
            grupoDTO.setId(GrupoEnum.ASESORES_INVERSION.getId());
            return grupoDTO;
        }

        return null;
    }

    private GrupoDTO asignarGrupoPorDireccion(UsuarioSonar usuarioSonar) {
        if (usuarioSonar == null) {
            return null;
        }

        String nombreDir = usuarioSonar.getNombreDireccionGeneral();
        String codigoDir = usuarioSonar.getCodigoDireccionGeneral();

        String base = (nombreDir != null && !nombreDir.isEmpty()) ? nombreDir : codigoDir;

        if (base == null) {
            return null;
        }

        String direccionNormalizada = normalizar(base).trim();

        // Coincidencias exactas contra listas de direcciones (0 falsos positivos)
        GrupoDTO grupo = crearGrupoSiCoincide(direccionNormalizada,
                GrupoEquivalenciasConfig.DIRECCIONES_BCA_PATRIMONIAL_PRIVADA_VALIDAS,
                GrupoEnum.DIR_BCA_PATRIMONIAL_Y_PRIVADA);
        if (grupo != null)
            return grupo;

        grupo = crearGrupoSiCoincide(direccionNormalizada,
                GrupoEquivalenciasConfig.DIRECCIONES_DIVISIONAL_VALIDAS,
                GrupoEnum.DIR_DIVISIONAL);
        if (grupo != null)
            return grupo;

        grupo = crearGrupoSiCoincide(direccionNormalizada,
                GrupoEquivalenciasConfig.DIRECCIONES_REGIONAL_VALIDAS,
                GrupoEnum.DIR_REGIONAL);
        if (grupo != null)
            return grupo;

        grupo = crearGrupoSiCoincide(direccionNormalizada,
                GrupoEquivalenciasConfig.DIRECCIONES_ZONA_VALIDAS,
                GrupoEnum.DIR_ZONA);
        return grupo;
    }

    /**
     * Crea un GrupoDTO si el valor normalizado está en el conjunto de valores
     * válidos.
     *
     * @param valorNormalizado Valor ya normalizado a validar
     * @param valoresValidos   Conjunto de valores válidos
     * @param grupoEnum        Enum del grupo a asignar
     * @return GrupoDTO con el ID del grupo si coincide, null si no coincide
     */
    private GrupoDTO crearGrupoSiCoincide(
            String valorNormalizado,
            java.util.Set<String> valoresValidos,
            GrupoEnum grupoEnum) {

        if (!valoresValidos.contains(valorNormalizado)) return null;
        return new GrupoDTO()
                .setId(grupoEnum.getId());
    }

    /**
     * Normaliza texto: minúsculas, sin acentos, sin puntos, espacios compactados.
     */
    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String s = texto.trim().toLowerCase();
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("\\p{M}+", "");
        s = s.replace('.', ' ');
        s = s.replace(',', ' ');
        s = s.replaceAll("\\s+", " ");
        s = " " + s + " ";
        return s;
    }

    /**
     * Retorna el valor o "N/A" si es null o vacío.
     */
    private String obtenerValorONA(String valor) {
        return (valor != null && !valor.isBlank()) ? valor : "N/A";
    }

}
