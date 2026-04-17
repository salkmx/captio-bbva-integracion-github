package com.sngular.captio.tasklet;

import com.google.common.base.Stopwatch;
import com.sngular.captio.dto.OpcionesUsuarioDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.mapper.UsuarioSonarMapper;
import com.sngular.captio.model.UsuarioSonar;
import com.sngular.captio.repository.UsuarioSonarRepository;
import com.sngular.captio.services.EmpleadoCaptioService;
import com.sngular.captio.services.EmpleadoSonarService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.util.ObjetosUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Tasklet para sincronización de usuarios con Captio.
 * <p>
 * Lógica:
 * 1. Para usuarios nuevos: crear en Captio (POST)
 * 2. Para usuarios existentes: actualizar datos (PUT) y sincronizar:
 * - Workflows (ya manejado por FlujoAprobacionTasklet)
 * - Grupos de usuario
 * - Tarjetas de crédito (payments)
 * 3. Para usuarios dados de baja: desactivar en Captio
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmpleadosSyncTasklet implements Tasklet {

    private final UsuarioSonarRepository usuarioSonarRepository;

    private final UsuarioService usuarioService;
    private final EmpleadoCaptioService empleadoCaptioService;
    private final EmpleadoSonarService empleadoSonarService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("========== INICIANDO SINCRONIZACIÓN DE USUARIOS ==========");
        log.info(chunkContext.toString());
        var sp = Stopwatch.createStarted();

        UsuarioSonarMapper usuarioMapper = Mappers.getMapper(UsuarioSonarMapper.class);

        // Obtener usuarios activos (estadoEmpleado = 'ACT') y de baja (estadoEmpleado =
        // 'BAJ')
        List<UsuarioSonar> usuariosActivosSonar = usuarioSonarRepository.obtenerUsuariosActivos();
        List<UsuarioSonar> usuariosActivosBaja = usuarioSonarRepository.obtenerUsuariosBaja();

        if (usuariosActivosSonar != null && !usuariosActivosSonar.isEmpty()) {
            List<UsuarioDTO> usuariosDTO = usuarioMapper.toDtoList(usuariosActivosSonar);

            // Establecer campos adicionales para cada usuario
            for (UsuarioDTO usuario : usuariosDTO) {
                // TDC ya viene del archivo VIBASONAR.txt (leído en UsuarioSonarItemReader)

                // Establecer Active basándose en el estado del archivo (ACT = true, otros =
                // false)
                // Aplica tanto para altas (POST) como updates (PUT)
                // IMPORTANTE: Este campo debe enviarse en updates, o Captio lo borra
                boolean isActive = "ACT".equals(usuario.getEstadoEmpleado());
                usuario.setActive(isActive);
            }

            // Separar usuarios nuevos de existentes
            procesarUsuarios(usuariosDTO);
        }

        if (usuariosActivosBaja != null && !usuariosActivosBaja.isEmpty()) {
            bajaUsuario(usuarioMapper.toDtoList(usuariosActivosBaja));
        }

        log.info("========== SINCRONIZACIÓN DE USUARIOS COMPLETADA ==========");
        log.info("[{}][execute] took {}sg", this.getClass().getSimpleName(), sp.elapsed(TimeUnit.SECONDS));
        return RepeatStatus.FINISHED;
    }

    /**
     * Procesa usuarios separándolos en nuevos y existentes.
     * - Usuarios nuevos: se crean con POST
     * - Usuarios existentes: se actualizan con PUT y se sincronizan sus
     * configuraciones
     */
    private void procesarUsuarios(List<UsuarioDTO> usuarios) throws Exception {
        List<UsuarioDTO> usuariosNuevos = new ArrayList<>();
        List<UsuarioDTO> usuariosExistentes = new ArrayList<>();

        // Obtener mapa de usuarios existentes en Captio por email
        Map<String, UsuarioDTO> usuariosCaptioMap = obtenerUsuariosCaptioMap(usuarios);

        for (UsuarioDTO usuario : usuarios) {
            String emailLower = usuario.getEmail() != null ? usuario.getEmail().toLowerCase() : null;

            if (emailLower == null || emailLower.isBlank()) {
                log.warn("Usuario sin email válido, ignorando: {}", usuario.getNombreEmpleado());
                continue;
            }

            UsuarioDTO usuarioCaptio = usuariosCaptioMap.get(emailLower);

            if (usuarioCaptio != null) {
                // Usuario existe en Captio - actualizar
                usuario.setId(usuarioCaptio.getId());
                usuario.setUserId(usuarioCaptio.getUserId());
                usuariosExistentes.add(usuario);
                log.debug("Usuario existente: {} (ID: {})", emailLower, usuarioCaptio.getId());
            } else {
                // Usuario nuevo - crear
                usuariosNuevos.add(usuario);
                log.debug("Usuario nuevo: {}", emailLower);
            }
        }

        log.info("Usuarios a crear: {}, Usuarios a actualizar: {}",
                usuariosNuevos.size(), usuariosExistentes.size());

        // Procesar usuarios nuevos (crear)
        if (!usuariosNuevos.isEmpty()) {
            log.info("Creando {} usuarios nuevos...", usuariosNuevos.size());
            altaUsuario(usuariosNuevos);
        }

        // Procesar usuarios existentes (actualizar)
        if (!usuariosExistentes.isEmpty()) {
            log.info("Actualizando {} usuarios existentes...", usuariosExistentes.size());
            actualizarUsuariosExistentes(usuariosExistentes);
        }
    }

    /**
     * Obtiene un mapa de usuarios existentes en Captio indexados por email
     * (lowercase).
     */
    private Map<String, UsuarioDTO> obtenerUsuariosCaptioMap(List<UsuarioDTO> usuarios) {
        Map<String, UsuarioDTO> mapa = new HashMap<>();
        try {

//            List<UsuarioDTO> foundUsers = usuarioService.obtenerUsuarioByFiltro("");
            List<UsuarioDTO> foundUsers = empleadoSonarService.findAll();

            for (UsuarioDTO usuario : usuarios) {
                if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                    continue;
                }

                var user = foundUsers.stream().filter(usr ->
                                usr.getEmail().equalsIgnoreCase(usuario.getEmail()))
                        .findFirst();
                user.ifPresent(usuarioDTO -> mapa.put(usuario.getEmail().toLowerCase(), usuarioDTO));
            }
        } catch (Exception e) {
            log.error("Ocurrió un error al buscar los usuario: {}", e.getMessage());
        }

        log.info("Encontrados {} usuarios existentes en Captio", mapa.size());
        return mapa;
    }

    /**
     * Actualiza usuarios existentes en Captio.
     * Actualiza estos campos:
     * - Nombre completo (Name) - IMPORTANTE: debe enviarse siempre o se borra
     * - Estado activo (Active) - IMPORTANTE: debe enviarse siempre o se borra
     * Se establece basándose en estadoEmpleado del archivo (ACT = true, BAJ =
     * false)
     * - Centro de coste (Options.CostCentre)
     * - Código de empresa (Options.CompanyCode)
     * - Código de usuario (Options.EmployeeCode)
     * - Tarjeta de crédito (Payments)
     * <p>
     * Los grupos (28, 13) y workflows se sincronizan después del update.
     */
    private void actualizarUsuariosExistentes(List<UsuarioDTO> usuarios) throws Exception {
        // 1. Preparar usuarios para actualización (solo campos específicos)
        List<UsuarioDTO> userToUpdate = new ArrayList<>();

        for (UsuarioDTO usuario : usuarios) {
            UsuarioDTO usuarioUpdate = getUsuarioDTO(usuario);
            userToUpdate.add(usuarioUpdate);
        }

        // 2. Actualizar campos en Captio (Centro coste, Código empresa, Código usuario)
        try {
            empleadoCaptioService.updateUsers(userToUpdate);
        } catch (Exception e) {
            log.error("Error al actualizar usuarios: {}", e.getMessage());
        }

        // 3. Sincronizar grupos de usuario
        sincronizarGrupos(usuarios);

        // 4. Sincronizar tarjetas de crédito (payments)
        try {
            empleadoCaptioService.sincronizarPayments(usuarios);
        } catch (Exception e) {
            log.error("Error al sincronizar payments: {}", e.getMessage());
        }

        // Nota: Los workflows se sincronizan en FlujoAprobacionTasklet
        log.info(
                "Usuarios existentes actualizados: Nombre, Centro coste, Código empresa, Código usuario, Grupos y Tarjetas.");
    }

    private UsuarioDTO getUsuarioDTO(UsuarioDTO usuario) {
        // Campos obligatorios para el PUT de Captio
        return new UsuarioDTO()
                .setId(usuario.getId())
                .setEmail(usuario.getEmail()) // Requerido por la API aunque no se actualice
                .setName(usuario.getName()) // Nombre completo - DEBE enviarse o se borra
                .setActive(usuario.getActive()) // Estado activo - DEBE enviarse o se borra
                // Campos de TDC (Tarjeta de Crédito) para sincronización de métodos de pago
                .setTdc(usuario.getTdc())
                .setTdcStatus(usuario.getTdcStatus())
                .setAuthenticationType(3) // Tipo de autenticación (3 = SSO) - DEBE enviarse o se borra
                // Crear objeto Options con los 3 campos a actualizar
                .setOptions(usuario.getOptions() == null ? null : new OpcionesUsuarioDTO()
                        .setCompanyCode(usuario.getOptions().getCompanyCode())
                        .setCostCentre(usuario.getOptions().getCostCentre())
                        .setEmployeeCode(usuario.getOptions().getEmployeeCode())
                );
    }

    /**
     * Sincroniza grupos de usuario.
     * Asigna grupo de viajes (TravelGroups) y grupo de KM (13).
     * Nota: El grupo general (28) se asigna en GrupoTasklet.
     */
    private void sincronizarGrupos(List<UsuarioDTO> usuarios) {
        if (usuarios == null || usuarios.isEmpty()) {
            return;
        }

        try {
            // 1. Preparar usuarios para grupo de viajes (activeTravelGroup)
            List<UsuarioDTO> usuariosViajes = new ArrayList<>();
            usuarios.stream().filter(usr -> usr.getId() != null)
                    .toList()
                    .forEach(usr -> {
                        UsuarioDTO usuarioViaje = new UsuarioDTO()
                                .setId(usr.getId())
                                .setActiveTravelGroup(true);
                        ObjetosUtils.limpiarCamposExcepto(usuarioViaje, Arrays.asList("id", "activeTravelGroup"));
                        usuariosViajes.add(usuarioViaje);
                    });

            // 2. Sincronizar grupo de viajes
            if (!usuariosViajes.isEmpty()) {
                log.info("Sincronizando grupo de viajes para {} usuarios...", usuariosViajes.size());
                empleadoCaptioService.sincronizarGrupoViajes(usuariosViajes);
            }

            // 3. Sincronizar grupo KM (grupo 13)
            // Nota: El grupo general (28) se asigna en GrupoTasklet
            log.info("Sincronizando grupo KM para {} usuarios...", usuarios.size());
            empleadoCaptioService.sincronizarGrupoKm(usuarios);

        } catch (Exception e) {
            log.error("Error al sincronizar grupos: {}", e.getMessage());
        }
    }

    private void altaUsuario(List<UsuarioDTO> usuarios) throws Exception {
        log.info("altaUsuario - {} usuarios", usuarios.size());
        empleadoCaptioService.uploadUser(usuarios);
    }

    private void bajaUsuario(List<UsuarioDTO> usuarios) throws Exception {
        log.info("bajaUsuario - {} usuarios", usuarios.size());
        empleadoCaptioService.deleteUser(usuarios);
    }

}
