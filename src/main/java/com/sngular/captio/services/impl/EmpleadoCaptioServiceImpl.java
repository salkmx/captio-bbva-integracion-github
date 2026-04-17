package com.sngular.captio.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sngular.captio.dto.*;
import com.sngular.captio.enums.MetodoPagoEnum;
import com.sngular.captio.enums.ResultadoAPIEnum;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.repository.UsuarioSonarRepository;
import com.sngular.captio.services.ApiRequest;
import com.sngular.captio.services.EmailService;
import com.sngular.captio.services.EmpleadoCaptioService;
import com.sngular.captio.util.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmpleadoCaptioServiceImpl extends ApiRequest<UsuarioDTO> implements EmpleadoCaptioService {

    private final RestTemplate restTemplate;
    private final Properties properties;
    private final EmailService emailService;
    private final UsuarioSonarRepository usuarioSonarRepository;

    @Override
    public void uploadUser(List<UsuarioDTO> usuarios) throws Exception {
//        log.debug(CaptioJsonUtils.convertirUsuariosAJson(usuarios));
        // Guardar mapa de email -> TDC y email -> TDC Status antes del POST (la
        // respuesta no incluye estos datos)
        Map<String, String> emailToTdc = new HashMap<>();
        Map<String, String> emailToTdcStatus = new HashMap<>();
        for (UsuarioDTO u : usuarios) {
            if (u.getEmail() != null && u.getTdc() != null && !u.getTdc().isBlank()) {
                emailToTdc.put(u.getEmail().toLowerCase(), u.getTdc());
                emailToTdcStatus.put(u.getEmail().toLowerCase(), u.getTdcStatus());
            }
        }

        var entity = setupEntity(usuarios, properties.getCustomerKey());

        try {
            ResponseEntity<GenericResponseDTO<UsuarioDTO>[]> response = restTemplate.exchange(
                    properties.getUrlUsuarios(), HttpMethod.POST, entity,
                    new ParameterizedTypeReference<GenericResponseDTO<UsuarioDTO>[]>() {
                    });
            GenericResponseDTO<UsuarioDTO>[] body = response.getBody();

            if (body == null) return;
            if (body.length == 0) return;

            List<UsuarioDTO> usuariosViajes = new ArrayList<>();
            List<GrupoDTO> gruposKm = new ArrayList<>();
            for (GenericResponseDTO<UsuarioDTO> dto : response.getBody()) {
                UsuarioDTO usuario = dto.getResult();
                GrupoDTO grupoKm = new GrupoDTO();
//					log.info(usuario.getUserId().toString());
                usuario.setActiveTravelGroup(true);
                usuario.setId(usuario.getUserId());
                ObjetosUtils.limpiarCamposExcepto(usuario, Arrays.asList("id", "activeTravelGroup"));
                grupoKm.setId(usuario.getId());
                grupoKm.setGroupId(13);
                usuariosViajes.add(usuario);
                gruposKm.add(grupoKm);

                if (usuario.getUserId() == null) continue;
                if (usuario.getEmail() == null) continue;

                // Crear método de pago (TDC) para usuario nuevo (solo si NO es BAJ)
                String emailKey = usuario.getEmail().toLowerCase();
                String tdc = emailToTdc.get(emailKey);
                String tdcStatus = emailToTdcStatus.get(emailKey);
                // Si el estatus es BAJ, no crear método de pago
                if (tdcStatus != null && "BAJ".equalsIgnoreCase(tdcStatus.trim())) {
                    log.info("Usuario nuevo {} tiene TDC con estatus BAJ, no se crea método de pago",
                            usuario.getEmail());
                } else {
                    createPaymentMethod(usuario.getUserId(), tdc, tdcStatus);
                    log.info("[uploadUser] Método de pago creado para usuario nuevo {}", usuario.getEmail());
                }
            }

            uploadUserTravels(usuariosViajes);
            uploadUsersGroupKM(gruposKm);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.error("Error 400 - Bad Request: ", e);
                message(e.getMessage());
                updateUserStatus(e.getMessage());
            }
        } catch (Exception e) {
            log.error("Error 400 - Bad Request: ", e.getMessage());
        }
    }

    @Transactional
    private void updateUserStatus(String error) {
        List<UsuarioDTO> usuarios = new ArrayList<>();
        List<GenericResponseDTO<UsuarioDTO>> resultados = new ArrayList<>();
        String jsonFormateado = CaptioJsonUtils.obtenerJsonError(error);
        ObjectMapper mapper = new ObjectMapper();
        try {
            resultados = mapper.readValue(jsonFormateado, new TypeReference<List<GenericResponseDTO<UsuarioDTO>>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Error 400 - Bad Request: ", e);
        }

        for (GenericResponseDTO<UsuarioDTO> resultado : resultados) {
            if (!resultado.getStatus().equals(ResultadoAPIEnum.CORRECTO.getEstatus())) {
                usuarioSonarRepository.actualizaEstatusAPI(resultado.getValue());
            } else {
                if (resultado.getResult() != null) {
                    UsuarioDTO usuarioDTO = resultado.getResult();
                    usuarioDTO.setActiveTravelGroup(true);
                    usuarioDTO.setId(usuarioDTO.getUserId());
                    usuarios.add(usuarioDTO);
                }
            }
        }

        if (!usuarios.isEmpty()) {
            try {
                uploadUserTravels(usuarios);
            } catch (Exception e) {
                log.error("Error 400 - Bad Request: ", e);
            }
        }
    }


    @Override
    public void updateUsers(List<UsuarioDTO> usuarios) {
        if (usuarios == null || usuarios.isEmpty()) return;

        log.info("Actualizando {} usuarios existentes", usuarios.size());
        // Primero sincronizar métodos de pago (crear, actualizar o eliminar) para cada
        // usuario
        try {
//            log.debug(CaptioJsonUtils.convertirUsuariosAJson(usuarios));

            // Obtener métodos de pago actuales del usuario
            List<MetodoPagoDTO> paymentsList = getPaymentMethod("");

            //TODO Refactor second attempt
            for (UsuarioDTO usuario : usuarios) {
                if (usuario.getId() == null) continue;

                String tdc = usuario.getTdc();
                String tdcStatus = usuario.getTdcStatus();

                var actualPayments = paymentsList.stream()
                        .filter(payment -> payment.getId().equals(usuario.getId()))
                        .toList();

                // Caso 1: TDC con estatus BAJ - eliminar metodo de pago si existe
                if (tdcStatus != null && "BAJ".equalsIgnoreCase(tdcStatus.trim())) {
                    actualPayments.forEach(payment ->
                            deletePaymentMethods(usuario.getId(), payment.getId()));

                    if (actualPayments.isEmpty()) {
                        log.debug("Usuario {} tiene TDC BAJ pero no tiene métodos de pago registrados",
                                usuario.getEmail());
                    }
                    continue; // No procesar más este usuario
                }

                // Caso 2: Validar TDC antes de procesar (solo para ACT o casos válidos)
                if (!TdcUtil.validarTdc(tdc, tdcStatus, usuario.getId())) {
                    // Si es null/vacío se omite silenciosamente, si es inválido ya se registró
                    // error
                    continue;
                }

                // Caso 3: TDC válida y activa - crear o actualizar método de pago
                boolean tieneTarjeta = actualPayments.stream()
                        .anyMatch(p -> p != null && TdcUtil.tdcCoincide(tdc, p.getValue()));
                if (tieneTarjeta) {
                    continue; // ya tiene la TDC correcta
                }

                if (!actualPayments.isEmpty()) {
                    MetodoPagoDTO pagoExistente = actualPayments.get(0);
                    pagoExistente.setValue(tdc);
                    updatePaymentMethod(usuario.getId(), pagoExistente);
                    log.info("Método de pago actualizado para usuario {}", usuario.getEmail());

                } else {
                    createPaymentMethod(usuario.getId(), tdc, tdcStatus);
                    log.info("Método de pago creado para usuario {}", usuario.getEmail());
                }
            }
        } catch (Exception e) {
            log.error("Error sincronizando payments", e);
        }

        var entity = setupEntity(usuarios, properties.getCustomerKey());
        try {
            restTemplate.exchange(properties.getUrlUsuarios(), HttpMethod.PUT, entity,
                    new ParameterizedTypeReference<GenericResponseDTO<UsuarioDTO>[]>() {
                    });
            log.info("Usuarios actualizados correctamente");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.error("Error 400 - Bad Request al actualizar usuarios: ", e);
                message(e.getMessage());
            }
        }
    }

    private List<MetodoPagoDTO> getPaymentMethod(String filtro) throws Exception {
        ResponseEntity<UsuarioDTO[]> response = null;
        URI uri = UriComponentsBuilder.fromUriString(properties.getUrlGetPaymentsUsuarios())
                .queryParam("filters", filtro)
                .build()
                .encode().toUri();
        var entity = setupEntity(properties.getCustomerKey());

        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, entity, UsuarioDTO[].class);
            for (UsuarioDTO usuarioDTO : response.getBody()) {
                return usuarioDTO.getPayments();
            }
//            return Arrays.stream(response.getBody()).map(dto -> (MetodoPagoDTO) dto.getPayments())
//                    .toList();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.error("Error 400 - Bad Request: ", e);
                message(e.getMessage());
            }
        }
        return List.of();
    }

    /**
     * Elimina un método de pago de un usuario mediante DELETE.
     * Formato del payload: [{"Id": usuarioId, "Payments": [{"Id": paymentId}]}]
     *
     * @param usuarioId ID del usuario en Captio (usuario.getId(), NO
     *                  usuario.getUserId())
     * @param paymentId ID del método de pago a eliminar (pago.getId())
     * @throws Exception si hay error en la petición
     */
    private void deletePaymentMethods(Integer usuarioId, Integer paymentId) {
        if (usuarioId == null || paymentId == null) {
            log.warn("[deletePaymentMethods] No se puede eliminar método de pago: usuarioId={}, paymentId={}", usuarioId, paymentId);
            return;
        }

        // Construir payload según formato Captio: [{"Id": 5685, "Payments": [{"Id": 5959}]}]
        List<Map<String, Object>> payload = new ArrayList<>();
        Map<String, Object> userPayment = new HashMap<>();
        userPayment.put("Id", usuarioId); // ID del usuario en Captio

        List<Map<String, Object>> payments = new ArrayList<>();
        Map<String, Object> payment = new HashMap<>();
        payment.put("Id", paymentId); // ID del método de pago
        payments.add(payment);

        userPayment.put("Payments", payments);
        payload.add(userPayment);

        log.info("=== ELIMINANDO MÉTODO DE PAGO ===");
        log.info("Usuario ID: {}, Payment ID: {}", usuarioId, paymentId);

        // Log del JSON exacto que se enviará
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
            log.info("Payload DELETE Payments JSON:\n{}", jsonPayload);
        } catch (Exception e) {
            log.warn("No se pudo serializar payload para log: {}", e.getMessage());
        }

        var entity = setupEntityPayload(payload, properties.getCustomerKey());

        try {
            if (properties.getUrlDeletePaymentsUsuarios() == null
                    || properties.getUrlDeletePaymentsUsuarios().isBlank()) {
                throw new IllegalStateException(
                        "URL DELETE Payments no configurada (captio.api.usuario.payments.delete.url)");
            }
            log.info("Ejecutando DELETE URL: {}", properties.getUrlDeletePaymentsUsuarios());
            restTemplate.exchange(properties.getUrlDeletePaymentsUsuarios(), HttpMethod.DELETE, entity, Void.class);
            log.info("✅ DELETE método de pago EXITOSO - Usuario ID: {}, Payment ID: {}", usuarioId, paymentId);
        } catch (HttpClientErrorException e) {
            log.error("❌ Error eliminando método de pago - Usuario ID: {}, Payment ID: {}", usuarioId, paymentId);
            log.error("Status Code: {}", e.getStatusCode());
            log.error("Error Message: {}", e.getMessage());
            log.error("Response Body: {}", e.getResponseBodyAsString());
            message(e.getMessage());
            throw e;
        }
    }

    /**
     * Actualiza un método de pago: elimina el existente y crea uno nuevo con el
     * valor actualizado.
     * La API de Captio no soporta PUT para Payments, por lo que se hace
     * DELETE + POST.
     */
    private void updatePaymentMethod(Integer userId, MetodoPagoDTO pagoExistente) throws Exception {
        if (userId == null || pagoExistente == null || pagoExistente.getId() == null) {
            throw new IllegalArgumentException("userId y pago.id son requeridos para actualizar método de pago");
        }

        String nuevoValor = pagoExistente.getValue();

        // 1. Eliminar el método de pago existente
        deletePaymentMethods(userId, pagoExistente.getId());
        log.info("Método de pago anterior eliminado para userId {} (paymentId {})", userId, pagoExistente.getId());

        // 2. Crear uno nuevo con el valor actualizado
        createPaymentMethod(userId, nuevoValor, "ACT");
        log.info("Nuevo método de pago creado para userId {} con valor {}", userId, nuevoValor);
    }

    /**
     * Crea un nuevo método de pago para un usuario (POST).
     * Envía payload con Id del usuario y array Payments.
     */
    private void createPaymentMethod(Integer userId, String valorTdc, String tdcStatus) {
        if (userId == null) {
            throw new IllegalArgumentException("userId es requerido para crear método de pago");
        }

        // Validar TDC
        if (!TdcUtil.validarTdc(valorTdc, tdcStatus, userId)) {
            return; // Si es null/vacío se ignora, si es inválido ya se registró el error
        }

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("Id", userId);
        userMap.put("Payments", List.of(new MetodoPagoDTO()
                .setValue(valorTdc)
                .setPaymentId(MetodoPagoEnum.TARJETA_EMPRESARIAL.getIdMetodo())
                .setIdentifierType(1) // 1 = Número de tarjeta
                .setName("Tarjeta de Crédito Empresarial")
                .setIsReimbursable(false) //La forma de pago Tarjeta de Crédito no es reembolsable.
                .setIsReconcilable(true)
        ));

        List<Map<String, Object>> payload = List.of(userMap);

        log.info("Creando método de pago para userId {}: TDC={}", userId, valorTdc);
        log.debug("Payload POST Payments: {}", payload);

        var entity = setupEntityPayload(payload, properties.getCustomerKey());

        try {
            if (properties.getUrlPostPaymentsUsuarios() == null || properties.getUrlPostPaymentsUsuarios().isBlank()) {
                throw new IllegalStateException(
                        "URL POST Payments no configurada (captio.api.usuario.payments.post.url)");
            }
            restTemplate.exchange(properties.getUrlPostPaymentsUsuarios(), HttpMethod.POST, entity, Void.class);
            log.info("POST método de pago exitoso para usuarioId {}", userId);
        } catch (HttpClientErrorException e) {
            log.error("Error creando método de pago para userId {}: {}", userId, e.getMessage());
            message(e.getMessage());
            throw e;
        }
    }


    @Override
    public void sincronizarGrupoViajes(List<UsuarioDTO> usuarios) throws Exception {
        if (usuarios == null || usuarios.isEmpty()) return;
        log.info("Sincronizando grupo de viajes para {} usuarios", usuarios.size());
        uploadUserTravels(usuarios);
    }

    private void uploadUserTravels(List<UsuarioDTO> usuarios) throws Exception {
//        log.debug(CaptioJsonUtils.convertirUsuariosAJson(usuarios));
        var entity = setupEntity(usuarios, properties.getCustomerKey());

        try {
            restTemplate.exchange(properties.getUrlUsuariosViajes(), HttpMethod.PUT, entity, Void.class);
            log.info("Grupos de viaje sincronizados correctamente");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.error("Error 400 - Bad Request altaUsuarioViajes: ", e);
                message(e.getMessage());
            }
        }
    }

    @Override
    public void deleteUser(List<UsuarioDTO> usuarios) throws Exception {
//        log.debug(CaptioJsonUtils.convertirUsuariosAJson(usuarios));
        var entity = setupEntity(usuarios, properties.getCustomerKey());
        try {
            //TODO BAD REQUEST
            restTemplate.exchange(properties.getUrlDeleteUsuarios(), HttpMethod.DELETE, entity,
                    GenericResponseDTO[].class);
            log.debug("Exito bajaUsuario");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.error("Error 400 - Bad Request: ", e);
                message(e.getMessage());
            }
        }
    }

    @Override
    public void sincronizarGrupoKm(List<UsuarioDTO> usuarios) throws Exception {
        if (usuarios == null || usuarios.isEmpty()) return;
        log.info("Sincronizando grupo KM para {} usuarios", usuarios.size());
        List<GrupoDTO> gruposKm = usuarios.stream()
                .filter(usr -> usr.getId() != null).toList()
                .stream().map(usr -> new GrupoDTO()
                        .setId(usr.getId())
                        .setGroupId(13))
                .toList();

        if (!gruposKm.isEmpty()) {
            uploadUsersGroupKM(gruposKm);
        }
    }

    private void uploadUsersGroupKM(List<GrupoDTO> grupos) throws Exception {
//        log.debug(CaptioJsonUtils.convertirGruposAJson(grupos));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("customerKey", properties.getCustomerKey());
        HttpEntity<List<GrupoDTO>> entity = new HttpEntity<>(grupos, headers);

        try {
            restTemplate.exchange(properties.getUrlUsuarioskmgroups(), HttpMethod.PUT, entity, Void.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.error("Error 400 - Bad Request: ", e);
            }
        }
    }

    @Override
    public void sincronizarPayments(List<UsuarioDTO> usuarios) throws Exception {
        if (usuarios == null || usuarios.isEmpty()) return;

        List<MetodoPagoDTO> paymentsList = getPaymentMethod("");
        for (UsuarioDTO usuario : usuarios) {
            if (usuario.getId() != null) {
                String tdc = usuario.getTdc();
                String tdcStatus = usuario.getTdcStatus();

                try {
                    // Obtener los métodos de pago actuales del usuario
                    var actualPayments = paymentsList.stream()
                            .filter(payment -> payment.getId().equals(usuario.getId()))
                            .toList();

                    // Caso 1: TDC con estatus BAJ - eliminar método de pago si existe
                    if (tdcStatus != null && "BAJ".equalsIgnoreCase(tdcStatus.trim())) {
                        actualPayments.forEach(payment ->
                                deletePaymentMethods(usuario.getId(), payment.getId()));

                        if (actualPayments.isEmpty()) {
                            log.debug("[sincronizarPayments] Usuario {} tiene TDC BAJ pero no tiene métodos de pago registrados",
                                    usuario.getEmail());
                        }
                        continue; // No procesar más este usuario
                    }

                    // Caso 2: Validar TDC antes de procesar (solo para ACT o casos válidos)
                    if (!TdcUtil.validarTdc(tdc, tdcStatus, usuario.getId())) {
                        // Si es null/vacío se omite silenciosamente, si es inválido ya se registró
                        // error
                        continue;
                    }

                    // Caso 3: TDC válida y activa - crear o actualizar método de pago
                    boolean tieneTarjeta = actualPayments.stream()
                            .anyMatch(p -> p != null && TdcUtil.tdcCoincide(tdc, p.getValue()));

                    if (!tieneTarjeta) {
                        log.info("Usuario {} necesita sincronizar tarjeta de crédito: {}", usuario.getEmail(), tdc);

                        if (actualPayments != null && !actualPayments.isEmpty()) {
                            // Ya tiene un método de pago, actualizar el valor
                            MetodoPagoDTO pagoExistente = actualPayments.get(0);
                            pagoExistente.setValue(tdc);
                            updatePaymentMethod(usuario.getId(), pagoExistente);
                            log.info("[sincronizarPayments] Método de pago actualizado para usuario {}", usuario.getEmail());
                        } else {
                            // No tiene método de pago, crear uno nuevo
                            createPaymentMethod(usuario.getId(), tdc, tdcStatus);
                            log.info("[sincronizarPayments] Método de pago creado para usuario {}", usuario.getEmail());
                        }
                    }
                } catch (Exception e) {
                    log.error("Error al sincronizar payments para usuario {}: {}", usuario.getEmail(), e.getMessage());
                }
            }
        }
    }


    @Override
    protected void writeError(String error, List<GenericResponseDTO<UsuarioDTO>> result) {
        String ruta = properties.getRutaArchivoErrorUsuarios() + DateUtils.obtenerFechaActual() + ".csv";
        try {
            if (result != null) {
                StringBuilder sb = new StringBuilder();
                for (GenericResponseDTO<UsuarioDTO> errorUsuario : result) {
                    sb.append(errorUsuario.getValue());
                    sb.append(System.lineSeparator());
                    if (errorUsuario.getValidations() != null) {
                        sb.append(errorUsuario.getValidations().stream().map(ValidationDTO::getMessage)
                                .filter(Objects::nonNull).collect(Collectors.joining(System.lineSeparator())));
                    }
                }
                for (String errorMail : properties.getErrorMails()) {
                    emailService.enviarCorreo(
                            emailService.crearMailDTO(errorMail, "Usuario error", sb.toString(), "email/error"));
                }
            }

            File archivo = new File(ruta);
            archivo.getParentFile().mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
                writer.write(error);
                writer.newLine();
            }
        } catch (IOException e) {
            log.error("[writeError] Error al escribir registro: {}", e.getMessage(), e);
        }
    }
}
