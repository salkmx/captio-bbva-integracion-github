package com.sngular.captio.services.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sngular.captio.dto.GenericResponseDTO;
import com.sngular.captio.dto.GrupoDTO;
import com.sngular.captio.dto.MetodoPagoDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.ValidationDTO;
import com.sngular.captio.dto.WorkFlowDTO;
import com.sngular.captio.enums.MetodoPagoEnum;
import com.sngular.captio.enums.ResultadoAPIEnum;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.repository.UsuarioSonarRepository;
import com.sngular.captio.services.EmailService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.util.CaptioJsonUtils;
import com.sngular.captio.util.DateUtils;
import com.sngular.captio.util.ObjetosUtils;
import com.sngular.captio.util.ParametersBuilderUtil;
import com.sngular.captio.util.WebUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class UsuarioServiceImpl implements UsuarioService {

	private final RestTemplate restTemplate;

	private final Properties properties;

	private final EmailService emailService;

	private final UsuarioSonarRepository usuarioSonarRepository;

	@Override
	public void eliminarUsuarios() {
		try {
			List<UsuarioDTO> usuarios = obtenerUsuarios();
			log.info("Categorías encontradas: " + usuarios.size());
			usuarios.forEach(usuario -> {
				if (usuario.getId() != null) {
					log.info(usuario.getId().toString());
					ObjetosUtils.limpiarCamposExcepto(usuario, Arrays.asList(new String[] { "id" }));
				}

			});
			bajaUsuario(usuarios);
		} catch (Exception e) {
			log.error("Error al elimiminar categoría", e);
		}
	}

	public List<UsuarioDTO> obtenerUsuarios() throws Exception {
		ResponseEntity<List<UsuarioDTO>> response = null;
		List<UsuarioDTO> respuesta = new ArrayList<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			response = restTemplate.exchange(properties.getUrlUsuarios(), HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<UsuarioDTO>>() {
					});
			respuesta = response.getBody();
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
				alertarError(e.getMessage());
			}
		}
		return respuesta;
	}

	public void altaUsuario(List<UsuarioDTO> usuarios) throws Exception {
		log.debug(CaptioJsonUtils.convertirUsuariosAJson(usuarios));

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

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<UsuarioDTO>> entity = new HttpEntity<>(usuarios, headers);
		try {
			ResponseEntity<GenericResponseDTO<UsuarioDTO>[]> response = restTemplate.exchange(
					properties.getUrlUsuarios(), HttpMethod.POST, entity,
					new ParameterizedTypeReference<GenericResponseDTO<UsuarioDTO>[]>() {
					});
			GenericResponseDTO<UsuarioDTO>[] body = response.getBody();
			if (body != null && body.length > 0) {
				List<UsuarioDTO> usuariosViajes = new ArrayList<>();
				List<GrupoDTO> gruposKm = new ArrayList<>();
				for (GenericResponseDTO<UsuarioDTO> dto : response.getBody()) {
					UsuarioDTO usuario = dto.getResult();
					GrupoDTO grupoKm = new GrupoDTO();
					log.info(usuario.getUserId().toString());
					usuario.setActiveTravelGroup(true);
					usuario.setId(usuario.getUserId());
					ObjetosUtils.limpiarCamposExcepto(usuario, Arrays.asList("id", "activeTravelGroup"));
					grupoKm.setId(usuario.getId());
					grupoKm.setGroupId(13);
					usuariosViajes.add(usuario);
					gruposKm.add(grupoKm);

					// Crear método de pago (TDC) para usuario nuevo (solo si NO es BAJ)
					String emailKey = usuario.getEmail() != null ? usuario.getEmail().toLowerCase() : null;
					String tdc = emailKey != null ? emailToTdc.get(emailKey) : null;
					String tdcStatus = emailKey != null ? emailToTdcStatus.get(emailKey) : null;
					if (tdc != null && usuario.getUserId() != null) {
						// Si el estatus es BAJ, no crear método de pago
						if (tdcStatus != null && "BAJ".equals(tdcStatus.trim().toUpperCase())) {
							log.info("Usuario nuevo {} tiene TDC con estatus BAJ, no se crea método de pago",
									usuario.getEmail());
						} else {
							try {
								crearMetodoPago(usuario.getUserId(), tdc, tdcStatus);
								log.info("Método de pago creado para usuario nuevo {}", usuario.getEmail());
							} catch (Exception e) {
								log.warn("No se pudo crear método de pago para usuario nuevo {}: {}",
										usuario.getEmail(), e.getMessage());
							}
						}
					}
				}
				altaUsuarioViajes(usuariosViajes);
				altaUsuarioGrupoKm(gruposKm);
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
				alertarError(e.getMessage());
				actualizaEstadoUsuario(e.getMessage());
			}
		} catch (Exception e) {
			log.error("Error 400 - Bad Request: ", e.getMessage());
		}
	}

	/**
	 * Actualiza usuarios existentes en Captio usando PUT.
	 * Actualiza datos básicos del usuario.
	 */
	@Override
	public void updateUsuarios(List<UsuarioDTO> usuarios) throws Exception {
		if (usuarios == null || usuarios.isEmpty()) {
			return;
		}
		log.info("Actualizando {} usuarios existentes", usuarios.size());
		log.debug(CaptioJsonUtils.convertirUsuariosAJson(usuarios));

		// Primero sincronizar métodos de pago (crear, actualizar o eliminar) para cada
		// usuario
		for (UsuarioDTO usuario : usuarios) {
			try {
				if (usuario.getId() == null) {
					continue;
				}

				String tdc = usuario.getTdc();
				String tdcStatus = usuario.getTdcStatus();

				// Obtener métodos de pago actuales del usuario
				List<MetodoPagoDTO> pagosActuales = obtenerMetodoPago(
						String.format("{\"Id\":\"%d\"}", usuario.getId()));

				// Caso 1: TDC con estatus BAJ - eliminar método de pago si existe
				if (tdcStatus != null && "BAJ".equals(tdcStatus.trim().toUpperCase())) {
					if (pagosActuales != null && !pagosActuales.isEmpty()) {
						MetodoPagoDTO pagoExistente = pagosActuales.get(0);
						try {
							eliminarMetodoPago(usuario.getId(), pagoExistente.getId());
							log.info("Método de pago ELIMINADO para usuario {} (TDC con estatus BAJ)",
									usuario.getEmail());
						} catch (Exception e) {
							log.warn("No se pudo eliminar método de pago para usuario {} (BAJ): {}",
									usuario.getEmail(), e.getMessage());
						}
					} else {
						log.debug("Usuario {} tiene TDC BAJ pero no tiene métodos de pago registrados",
								usuario.getEmail());
					}
					continue; // No procesar más este usuario
				}

				// Caso 2: Validar TDC antes de procesar (solo para ACT o casos válidos)
				if (!validarTdc(tdc, tdcStatus, usuario.getId())) {
					// Si es null/vacío se omite silenciosamente, si es inválido ya se registró
					// error
					continue;
				}

				// Caso 3: TDC válida y activa - crear o actualizar método de pago
				boolean tieneTarjeta = pagosActuales != null && pagosActuales.stream()
						.anyMatch(p -> p != null && tdcCoincide(tdc, p.getValue()));
				if (tieneTarjeta) {
					continue; // ya tiene la TDC correcta
				}
				if (pagosActuales != null && !pagosActuales.isEmpty()) {
					MetodoPagoDTO pagoExistente = pagosActuales.get(0);
					pagoExistente.setValue(tdc);
					try {
						actualizarMetodoPago(usuario.getId(), pagoExistente);
						log.info("Método de pago actualizado para usuario {}", usuario.getEmail());
					} catch (Exception e) {
						log.warn("No se pudo actualizar método de pago para usuario {}: {}", usuario.getEmail(),
								e.getMessage());
					}
				} else {
					try {
						crearMetodoPago(usuario.getId(), tdc, tdcStatus);
						log.info("Método de pago creado para usuario {}", usuario.getEmail());
					} catch (Exception e) {
						log.warn("No se pudo crear método de pago para usuario {}: {}", usuario.getEmail(),
								e.getMessage());
					}
				}
			} catch (Exception e) {
				log.error("Error sincronizando payments antes de update para usuario {}: {}",
						usuario != null ? usuario.getEmail() : "null", e.getMessage());
			}
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<UsuarioDTO>> entity = new HttpEntity<>(usuarios, headers);

		try {
			restTemplate.exchange(properties.getUrlUsuarios(), HttpMethod.PUT, entity,
					new ParameterizedTypeReference<GenericResponseDTO<UsuarioDTO>[]>() {
					});
			log.info("Usuarios actualizados correctamente");
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request al actualizar usuarios: ", e);
				alertarError(e.getMessage());
			}
		}
	}

	/**
	 * Actualiza un método de pago: elimina el existente y crea uno nuevo con el
	 * valor actualizado.
	 * La API de Captio no soporta PUT para Payments, por lo que se hace
	 * DELETE + POST.
	 */
	private void actualizarMetodoPago(Integer userId, MetodoPagoDTO pagoExistente) throws Exception {
		if (userId == null || pagoExistente == null || pagoExistente.getId() == null) {
			throw new IllegalArgumentException("userId y pago.id son requeridos para actualizar método de pago");
		}

		String nuevoValor = pagoExistente.getValue();

		// 1. Eliminar el método de pago existente
		eliminarMetodoPago(userId, pagoExistente.getId());
		log.info("Método de pago anterior eliminado para userId {} (paymentId {})", userId, pagoExistente.getId());

		// 2. Crear uno nuevo con el valor actualizado
		crearMetodoPago(userId, nuevoValor, "ACT");
		log.info("Nuevo método de pago creado para userId {} con valor {}", userId, nuevoValor);
	}

	/**
	 * Crea un nuevo método de pago para un usuario (POST).
	 * Envía payload con Id del usuario y array Payments.
	 */
	private void crearMetodoPago(Integer userId, String valorTdc, String tdcStatus) throws Exception {
		if (userId == null) {
			throw new IllegalArgumentException("userId es requerido para crear método de pago");
		}

		// Validar TDC
		if (!validarTdc(valorTdc, tdcStatus, userId)) {
			return; // Si es null/vacío se ignora, si es inválido ya se registró el error
		}

		MetodoPagoDTO pago = new MetodoPagoDTO();
		pago.setValue(valorTdc);
		pago.setPaymentId(MetodoPagoEnum.TARJETA_EMPRESARIAL.getIdMetodo());
		pago.setIdentifierType(1); // 1 = Número de tarjeta
		pago.setName("Tarjeta de Crédito Empresarial");
		pago.setIsReimbursable(false); //La forma de pago Tarjeta de Crédito no es reembolsable.
		pago.setIsReconcilable(true);

		Map<String, Object> userMap = new HashMap<>();
		userMap.put("Id", userId);
		userMap.put("Payments", Arrays.asList(pago));

		List<Map<String, Object>> payload = Arrays.asList(userMap);

		log.info("Creando método de pago para userId {}: TDC={}", userId, valorTdc);
		log.debug("Payload POST Payments: {}", payload);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(payload, headers);

		try {
			if (properties.getUrlPostPaymentsUsuarios() == null || properties.getUrlPostPaymentsUsuarios().isBlank()) {
				throw new IllegalStateException(
						"URL POST Payments no configurada (captio.api.usuario.payments.post.url)");
			}
			restTemplate.exchange(properties.getUrlPostPaymentsUsuarios(), HttpMethod.POST, entity, Void.class);
			log.info("POST método de pago exitoso para usuarioId {}", userId);
		} catch (HttpClientErrorException e) {
			log.error("Error creando método de pago para userId {}: {}", userId, e.getMessage());
			alertarError(e.getMessage());
			throw e;
		}
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
	private void eliminarMetodoPago(Integer usuarioId, Integer paymentId) throws Exception {
		if (usuarioId == null || paymentId == null) {
			log.warn("No se puede eliminar método de pago: usuarioId={}, paymentId={}", usuarioId, paymentId);
			return;
		}

		// Construir payload según formato Captio: [{"Id": 5685, "Payments": [{"Id":
		// 5959}]}]
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

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(payload, headers);

		try {
			if (properties.getUrlDeletePaymentsUsuarios() == null
					|| properties.getUrlDeletePaymentsUsuarios().isBlank()) {
				throw new IllegalStateException(
						"URL DELETE Payments no configurada (captio.api.usuario.payments.delete.url)");
			}
			log.info("URL DELETE: {}", properties.getUrlDeletePaymentsUsuarios());
			log.info("Ejecutando DELETE...");
			restTemplate.exchange(properties.getUrlDeletePaymentsUsuarios(), HttpMethod.DELETE, entity, Void.class);
			log.info("✅ DELETE método de pago EXITOSO - Usuario ID: {}, Payment ID: {}", usuarioId, paymentId);
		} catch (HttpClientErrorException e) {
			log.error("❌ Error eliminando método de pago - Usuario ID: {}, Payment ID: {}", usuarioId, paymentId);
			log.error("Status Code: {}", e.getStatusCode());
			log.error("Error Message: {}", e.getMessage());
			log.error("Response Body: {}", e.getResponseBodyAsString());
			alertarError(e.getMessage());
			throw e;
		}
	}

	/**
	 * Valida el formato de la TDC (Tarjeta de Crédito) y su estatus.
	 * 
	 * @param tdc       Valor de la tarjeta a validar (16 dígitos numéricos)
	 * @param tdcStatus Estatus de la tarjeta ("ACT" = activa, "BAJ" = inactiva)
	 * @param userId    ID del usuario (para logging)
	 * @return true si la TDC es válida (16 dígitos numéricos y estatus ACT), false
	 *         si es null/vacía, inválida, o no está activa
	 */
	private boolean validarTdc(String tdc, String tdcStatus, Integer userId) {
		// Si es null o vacío, simplemente ignoramos (usuario sin tarjeta)
		if (tdc == null || tdc.isBlank()) {
			log.debug("Usuario {} no tiene TDC asignada, se omite sincronización de método de pago", userId);
			return false;
		}

		String tdcLimpia = tdc.trim();

		// Si TDC tiene sufijo ACT/BAJ pegado, limpiarlo (tomar solo primeros 16
		// caracteres)
		if (tdcLimpia.length() > 16 && (tdcLimpia.endsWith("ACT") || tdcLimpia.endsWith("BAJ"))) {
			log.warn("Usuario {}: TDC viene con estatus pegado '{}', se limpia a 16 dígitos", userId, tdcLimpia);
			tdcLimpia = tdcLimpia.substring(0, 16);
		}

		// Validar que sean exactamente 16 caracteres numéricos
		if (!tdcLimpia.matches("^\\d{16}$")) {
			String errorMsg = String.format(
					"TDC inválida para usuario %d: '%s' - Debe contener exactamente 16 dígitos numéricos (tiene %d caracteres)",
					userId, tdcLimpia, tdcLimpia.length());
			log.error(errorMsg);
			// Registrar error para notificación
			try {
				String jsonError = String.format(
						"[{\"result\":null,\"key\":\"UserId\",\"value\":\"%d\",\"validations\":[{\"message\":\"%s\",\"code\":\"TDC_INVALID_FORMAT\",\"key\":\"TDC\",\"value\":\"%s\"}],\"status\":1}]",
						userId, errorMsg.replace("\"", "'"), tdcLimpia);
				alertarError(jsonError);
			} catch (Exception e) {
				log.warn("No se pudo enviar alerta de error para TDC inválida: {}", e.getMessage());
			}
			return false;
		}

		// Validar estatus: solo procesar tarjetas ACTIVAS
		if (tdcStatus == null || tdcStatus.isBlank()) {
			log.warn("Usuario {}: TDC '{}' no tiene estatus definido, se omite sincronización", userId, tdcLimpia);
			return false;
		}

		String estatusLimpio = tdcStatus.trim().toUpperCase();
		if (!"ACT".equals(estatusLimpio)) {
			if ("BAJ".equals(estatusLimpio)) {
				log.info("Usuario {}: TDC '{}' está INACTIVA (BAJ), se omite sincronización de método de pago", userId,
						tdcLimpia);
			} else {
				log.warn("Usuario {}: TDC '{}' tiene estatus desconocido '{}', se omite sincronización", userId,
						tdcLimpia, estatusLimpio);
			}
			return false;
		}

		log.debug("Usuario {}: TDC '{}' validada correctamente con estatus ACTIVO", userId, tdcLimpia);
		return true;
	}

	/**
	 * Compara una TDC completa (16 dígitos) contra el valor almacenado en Captio,
	 * que puede estar enmascarado con formato "XXXXXX|YYYY" (primeros 6 + últimos
	 * 4).
	 * Retorna true si representan la misma tarjeta.
	 */
	private boolean tdcCoincide(String tdcCompleta, String valorCaptio) {
		if (tdcCompleta == null || valorCaptio == null) {
			return false;
		}
		// Comparación directa primero
		if (tdcCompleta.equals(valorCaptio)) {
			return true;
		}
		// Captio enmascara como "123123|4302" (primeros 6 + pipe + últimos 4)
		if (valorCaptio.contains("|") && tdcCompleta.length() >= 10) {
			String[] partes = valorCaptio.split("\\|", 2);
			if (partes.length == 2) {
				String primeros6 = partes[0];
				String ultimos4 = partes[1];
				boolean coincide = tdcCompleta.startsWith(primeros6)
						&& tdcCompleta.endsWith(ultimos4);
				if (coincide) {
					log.debug("TDC coincide por formato enmascarado: {}...{} = {}", primeros6, ultimos4, valorCaptio);
				}
				return coincide;
			}
		}
		return false;
	}

	private void altaUsuarioGrupoKm(List<GrupoDTO> grupos) throws Exception {
		log.debug(CaptioJsonUtils.convertirGruposAJson(grupos));
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

	@Transactional
	private void actualizaEstadoUsuario(String error) {
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
				altaUsuarioViajes(usuarios);
			} catch (Exception e) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
	}

	public void bajaUsuario(List<UsuarioDTO> usuarios) throws Exception {
		log.debug(CaptioJsonUtils.convertirUsuariosAJson(usuarios));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<UsuarioDTO>> entity = new HttpEntity<>(usuarios, headers);
		try {
			restTemplate.exchange(properties.getUrlDeleteUsuarios(), HttpMethod.DELETE, entity,
					GenericResponseDTO[].class);
			log.debug("Exito bajaUsuario");

		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
				alertarError(e.getMessage());
			}
		}
	}

	/**
	 * Sincroniza grupos de viaje (TravelGroups) para usuarios.
	 * Activa el grupo de viajes para los usuarios indicados.
	 */
	@Override
	public void sincronizarGrupoViajes(List<UsuarioDTO> usuarios) throws Exception {
		if (usuarios == null || usuarios.isEmpty()) {
			return;
		}
		log.info("Sincronizando grupo de viajes para {} usuarios", usuarios.size());
		altaUsuarioViajes(usuarios);
	}

	/**
	 * Sincroniza grupos KM para usuarios.
	 * Asigna el grupo KM 13 a los usuarios indicados.
	 */
	@Override
	public void sincronizarGrupoKm(List<UsuarioDTO> usuarios) throws Exception {
		if (usuarios == null || usuarios.isEmpty()) {
			return;
		}
		log.info("Sincronizando grupo KM para {} usuarios", usuarios.size());
		List<GrupoDTO> gruposKm = new ArrayList<>();
		for (UsuarioDTO usuario : usuarios) {
			if (usuario.getId() != null) {
				GrupoDTO grupoKm = new GrupoDTO();
				grupoKm.setId(usuario.getId());
				grupoKm.setGroupId(13);
				gruposKm.add(grupoKm);
			}
		}
		if (!gruposKm.isEmpty()) {
			altaUsuarioGrupoKm(gruposKm);
		}
	}

	private void altaUsuarioViajes(List<UsuarioDTO> usuarios) throws Exception {
		log.debug(CaptioJsonUtils.convertirUsuariosAJson(usuarios));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<UsuarioDTO>> entity = new HttpEntity<>(usuarios, headers);

		try {
			restTemplate.exchange(properties.getUrlUsuariosViajes(), HttpMethod.PUT, entity, Void.class);
			log.info("Grupos de viaje sincronizados correctamente");
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request altaUsuarioViajes: ", e);
				alertarError(e.getMessage());

			}
		}
	}

	public boolean existeUsuario(UsuarioDTO usuario) throws Exception {
		boolean existeUsuario = false;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetUsuarios();
		URI uri = UriComponentsBuilder.fromUriString(baseUrl)
				.queryParam("filters", "{\"Email\":\"" + usuario.getEmail() + "\"}").build().encode().toUri();
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			ResponseEntity<UsuarioDTO[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity,
					UsuarioDTO[].class);
			for (UsuarioDTO usuarioDTO : response.getBody()) {
				log.info(usuarioDTO.getLogin());
				usuario.setId(usuarioDTO.getId());
				existeUsuario = true;
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
				alertarError(e.getMessage());
			}
		}
		return existeUsuario;
	}

	public List<UsuarioDTO> obtenerUsuarioByFiltro(String filtro) throws Exception {
		ResponseEntity<UsuarioDTO[]> response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetUsuarios();
		URI uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("filters", filtro).build().encode().toUri();
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, entity, UsuarioDTO[].class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
				alertarError(e.getMessage());
			}
		}
		return Optional.ofNullable(response.getBody()).map(Arrays::asList).map(ArrayList::new)
				.orElseGet(ArrayList::new);
	}

	public void joinWorkFlow(List<UsuarioDTO> usuarios) throws Exception {
		for (UsuarioDTO usuario : usuarios) {
			if (usuario.getWorkflows() != null) {
				ObjetosUtils.limpiarCamposExcepto(usuario.getWorkflows(),
						Arrays.asList(new String[] { "id", "defaulte" }));
			}
		}
		ObjetosUtils.limpiarCamposExcepto(usuarios, Arrays.asList(new String[] { "id", "workflows" }));
		log.debug(CaptioJsonUtils.convertirUsuariosAJson(usuarios));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<UsuarioDTO>> entity = new HttpEntity<>(usuarios, headers);
		try {
			restTemplate.exchange(properties.getUrlPostUsersJoinWorkflow(), HttpMethod.POST, entity, Void.class);
			log.info("Workflows asignados correctamente a {} usuarios", usuarios.size());
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				String errorBody = e.getResponseBodyAsString();
				log.info("🔍 Error body: {}", errorBody);

				// WORKFLOW_ASSIGNED_ON_USER significa que algunos ya estaban asignados
				// Estrategia: PATCH para los ya asignados + POST para los NO asignados
				if (errorBody.contains("WORKFLOW_ASSIGNED_ON_USER")) {
					log.info("ℹ️ Algunos workflows ya asignados, procesando por separado...");

					// Log de usuarios originales para debug
					for (UsuarioDTO u : usuarios) {
						log.info("🔍 Usuario original ID={}, Workflows={}", u.getId(),
								u.getWorkflows() != null ? u.getWorkflows().stream()
										.map(wf -> wf.getId() != null ? wf.getId().toString() : "null")
										.collect(Collectors.joining(",")) : "null");
					}

					// 1. Construir lista con workflows ya asignados para PATCH (actualizar Default)
					List<UsuarioDTO> usuariosParaPatch = extraerWorkflowsYaAsignados(usuarios, errorBody);
					log.info("🔍 Usuarios para PATCH: {}", usuariosParaPatch.size());

					if (!usuariosParaPatch.isEmpty()) {
						try {
							HttpEntity<List<UsuarioDTO>> patchEntity = new HttpEntity<>(usuariosParaPatch, headers);
							restTemplate.exchange(properties.getUrlPatchUsersJoinWorkflow(), HttpMethod.PATCH,
									patchEntity, Void.class);
							log.info("✅ Flag Default actualizado vía PATCH para {} usuarios", usuariosParaPatch.size());
						} catch (Exception patchEx) {
							log.warn("⚠️ No se pudo aplicar PATCH: {}", patchEx.getMessage());
						}
					}

					// 2. Construir lista con workflows NO asignados para reintentar POST
					List<UsuarioDTO> usuariosParaPostRetry = extraerWorkflowsNoAsignados(usuarios, errorBody);
					log.info("🔍 Usuarios para POST retry: {}", usuariosParaPostRetry.size());

					for (UsuarioDTO u : usuariosParaPostRetry) {
						log.info("🔍 POST retry - Usuario ID={}, Workflows={}", u.getId(),
								u.getWorkflows() != null ? u.getWorkflows().stream()
										.map(wf -> wf.getId() != null ? wf.getId().toString() : "null")
										.collect(Collectors.joining(",")) : "null");
					}

					if (!usuariosParaPostRetry.isEmpty()) {
						try {
							HttpEntity<List<UsuarioDTO>> postRetryEntity = new HttpEntity<>(usuariosParaPostRetry,
									headers);
							restTemplate.exchange(properties.getUrlPostUsersJoinWorkflow(), HttpMethod.POST,
									postRetryEntity, Void.class);
							log.info("Workflows pendientes asignados vía POST retry para {} usuarios",
									usuariosParaPostRetry.size());
						} catch (Exception postRetryEx) {
							log.warn("No se pudo completar POST retry: {}", postRetryEx.getMessage());
						}
					}
				} else {
					log.error("Error 400 - Bad Request joinWorkFlow: {}", e.getMessage());
					alertarError(e.getMessage());
				}
			} else {
				log.error("Error {} en joinWorkFlow: {}", e.getStatusCode(), e.getMessage());
			}
		}
	}

	/**
	 * Extrae de la lista original solo los usuarios y workflows que ya estaban
	 * asignados según el mensaje de error de la API.
	 * Incluye TODOS los workflows (fijos y dinámicos) para hacer PATCH y actualizar
	 * Default.
	 */
	private List<UsuarioDTO> extraerWorkflowsYaAsignados(List<UsuarioDTO> usuariosOriginal, String errorBody) {
		List<UsuarioDTO> resultado = new ArrayList<>();

		for (UsuarioDTO usuario : usuariosOriginal) {
			if (usuario.getId() == null || usuario.getWorkflows() == null) {
				continue;
			}

			// Filtrar TODOS los workflows que aparecen en el error como ya asignados
			List<WorkFlowDTO> workflowsYaAsignados = new ArrayList<>();
			for (WorkFlowDTO wf : usuario.getWorkflows()) {
				if (wf.getId() != null) {
					// Verificar si este workflow aparece como ya asignado en el error
					String patron = "\"Value\":\"" + wf.getId() + "\"";
					if (errorBody.contains(patron) && errorBody.contains("WORKFLOW_ASSIGNED_ON_USER")) {
						workflowsYaAsignados.add(wf);
						log.debug("Workflow {} ya asignado para usuario {}, se hará PATCH", wf.getId(),
								usuario.getId());
					}
				}
			}

			if (!workflowsYaAsignados.isEmpty()) {
				UsuarioDTO usuarioPatch = new UsuarioDTO();
				usuarioPatch.setId(usuario.getId());
				usuarioPatch.setWorkflows(workflowsYaAsignados);
				resultado.add(usuarioPatch);
			}
		}

		log.debug("Usuarios para PATCH: {}", resultado.size());
		return resultado;
	}

	/**
	 * Extrae de la lista original solo los usuarios y workflows que NO estaban
	 * asignados según el mensaje de error de la API (para reintentar POST).
	 * Incluye TODOS los workflows (fijos y dinámicos) que no estaban asignados.
	 */
	private List<UsuarioDTO> extraerWorkflowsNoAsignados(List<UsuarioDTO> usuariosOriginal, String errorBody) {
		List<UsuarioDTO> resultado = new ArrayList<>();

		for (UsuarioDTO usuario : usuariosOriginal) {
			if (usuario.getId() == null || usuario.getWorkflows() == null) {
				continue;
			}

			// Filtrar TODOS los workflows que NO aparecen en el error (no están asignados
			// aún)
			List<WorkFlowDTO> workflowsNoAsignados = new ArrayList<>();
			for (WorkFlowDTO wf : usuario.getWorkflows()) {
				if (wf.getId() != null) {
					// Verificar si este workflow NO aparece en el error
					String patron = "\"Value\":\"" + wf.getId() + "\"";
					if (!errorBody.contains(patron)) {
						// No está en el error, significa que no está asignado aún
						workflowsNoAsignados.add(wf);
						log.debug("Workflow {} no asignado para usuario {}, se hará POST", wf.getId(), usuario.getId());
					}
				}
			}

			if (!workflowsNoAsignados.isEmpty()) {
				UsuarioDTO usuarioPost = new UsuarioDTO();
				usuarioPost.setId(usuario.getId());
				usuarioPost.setWorkflows(workflowsNoAsignados);
				resultado.add(usuarioPost);
			}
		}

		log.debug("Usuarios para POST retry: {}", resultado.size());
		return resultado;
	}

	/**
	 * Cuenta las ocurrencias de un substring en un string.
	 */
	private int contarOcurrencias(String texto, String buscar) {
		int count = 0;
		int idx = 0;
		while ((idx = texto.indexOf(buscar, idx)) != -1) {
			count++;
			idx += buscar.length();
		}
		return count;
	}

	/**
	 * Elimina la asignación de workflows a usuarios.
	 * Usa DELETE /api/v3.1/Users/Workflows/Join
	 */
	public void unjoinWorkFlow(List<UsuarioDTO> usuarios) throws Exception {
		for (UsuarioDTO usuario : usuarios) {
			ObjetosUtils.limpiarCamposExcepto(usuario.getWorkflows(), Arrays.asList(new String[] { "id" }));
		}
		ObjetosUtils.limpiarCamposExcepto(usuarios, Arrays.asList(new String[] { "id", "workflows" }));
		log.debug("Unjoin workflows: {}", CaptioJsonUtils.convertirUsuariosAJson(usuarios));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<UsuarioDTO>> entity = new HttpEntity<>(usuarios, headers);
		try {
			restTemplate.exchange(properties.getUrlDeleteUsersUnjoinWorkflow(), HttpMethod.DELETE, entity, Void.class);
			log.info("Workflows desasignados correctamente");
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request unjoinWorkFlow: ", e);
				// No alertar por errores de unjoin, pueden ser por workflows ya no asignados
			}
		}
	}

	public void joinGroup(List<UsuarioDTO> usuarios) throws Exception {
		// La API espera Groups como array de integers, no de objetos
		// Formato esperado: { "Id": 5612, "Groups": [29] }
		List<Map<String, Object>> payload = new ArrayList<>();

		for (UsuarioDTO usuario : usuarios) {
			if (usuario.getId() != null && usuario.getGrupos() != null && !usuario.getGrupos().isEmpty()) {
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("Id", usuario.getId());

				// Extraer solo los IDs de los grupos como integers
				List<Integer> groupIds = new ArrayList<>();
				for (GrupoDTO grupo : usuario.getGrupos()) {
					Integer groupId = grupo.getId() != null ? grupo.getId() : grupo.getGroupId();
					if (groupId != null) {
						groupIds.add(groupId);
					}
				}
				userMap.put("Groups", groupIds);
				payload.add(userMap);
			}
		}

		if (payload.isEmpty()) {
			log.warn("No hay usuarios con grupos válidos para asignar");
			return;
		}

		log.debug("JSON a enviar para grupos: {}",
				new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(payload));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(payload, headers);
		try {
			// Usar endpoint /Users/Groups con PUT
			restTemplate.exchange(properties.getUrlPostUsersJoinGrupo(), HttpMethod.PUT, entity, String.class);
			log.info("Grupos asignados correctamente a {} usuarios", payload.size());
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				String errorBody = e.getResponseBodyAsString();
				// GROUP_ASSIGNED_ON_USER indica que el grupo ya está asignado (no es error
				// crítico)
				if (errorBody.contains("GROUP_ASSIGNED_ON_USER")) {
					log.warn("Algunos grupos ya estaban asignados a los usuarios (esto es normal)");
				} else if (errorBody.contains("EMPTY_OR_INVALID_JSON")) {
					log.warn("Error al asignar grupos - formato no esperado por la API");
					log.debug("Detalle del error: {}", errorBody);
				} else {
					log.error("Error 400 - Bad Request joinGroup: ", e);
					alertarError(e.getMessage());
				}
			}
		}
	}

	public List<MetodoPagoDTO> obtenerMetodoPago(String filtro) throws Exception {
		ResponseEntity<UsuarioDTO[]> response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetPaymentsUsuarios();
		URI uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("filters", filtro).build().encode().toUri();
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, entity, UsuarioDTO[].class);
			for (UsuarioDTO usuarioDTO : response.getBody()) {
				return usuarioDTO.getPayments();
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
				alertarError(e.getMessage());
			}
		}
		return null;
	}

	private void alertarError(String json) {
		List<GenericResponseDTO<UsuarioDTO>> resultado = new ArrayList<>();
		String jsonFormateado = CaptioJsonUtils.obtenerJsonError(json);
		ObjectMapper mapper = new ObjectMapper();
		try {
			resultado = mapper.readValue(jsonFormateado, new TypeReference<List<GenericResponseDTO<UsuarioDTO>>>() {
			});
		} catch (JsonProcessingException e) {
			log.error("Error 400 - Bad Request: ", e);
		}
		escribirErrorUsuarios(jsonFormateado, resultado);
	}

	private void escribirErrorUsuarios(String error, List<GenericResponseDTO<UsuarioDTO>> resultado) {
		String ruta = properties.getRutaArchivoErrorUsuarios() + DateUtils.obtenerFechaActual() + ".csv";

		try {

			if (resultado != null) {
				StringBuilder sb = new StringBuilder();
				for (GenericResponseDTO<UsuarioDTO> errorUsuario : resultado) {
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
			log.error("Error al escribir registro: " + e.getMessage(), e);
		}
	}

	public UsuarioDTO obtenerFlujoUsuarioByFiltro(String filtro) throws Exception {
		ResponseEntity<List<UsuarioDTO>> response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetUsersWorkflow();
		URI uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("filters", filtro).build().encode().toUri();
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<UsuarioDTO>>() {
					});
			return (response.getBody() != null && !response.getBody().isEmpty()) ? response.getBody().get(0) : null;
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
		return null;

	}

	@Override
	public List<UsuarioDTO> getUsersPermissions(ParametersBuilderUtil paramsBuilder) {

		// Lista de usuarios con sus permisos
		List<UsuarioDTO> listOfUsers = new ArrayList<>();
		String baseUrl = properties.getUrlUsuersPermissions();

		URI uri = UriComponentsBuilder.fromUriString(baseUrl)
				.queryParam(paramsBuilder.getFilterName(), paramsBuilder.getFormattedParams())
				.build()
				.encode()
				.toUri();
		WebUtils webUtils = new WebUtils(properties.getCustomerKey());
		HttpEntity<Void> entity = new HttpEntity<>(webUtils.getHeaders());

		try {
			ResponseEntity<UsuarioDTO[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity,
					UsuarioDTO[].class);
			if (response.getBody() != null) {
				listOfUsers = List.of(response.getBody());
			}

		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
		return listOfUsers;
	}

	/**
	 * Obtiene TODOS los usuarios de Captio con sus workflows asignados.
	 * Usa paginación para obtener todos los registros.
	 */
	@Override
	public List<UsuarioDTO> obtenerTodosUsuariosConWorkflows() throws Exception {
		List<UsuarioDTO> todosUsuarios = new ArrayList<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());

		String baseUrl = properties.getUrlGetUsersWorkflow();
		int pageNum = 1;
		int pageSize = 500;
		boolean hayMasPaginas = true;

		log.info("Obteniendo todos los usuarios con workflows de Captio...");

		while (hayMasPaginas) {
			URI uri = UriComponentsBuilder.fromUriString(baseUrl)
					.queryParam("pagNum", pageNum)
					.queryParam("pageSize", pageSize)
					.build()
					.encode()
					.toUri();

			HttpEntity<Void> entity = new HttpEntity<>(headers);

			try {
				ResponseEntity<List<UsuarioDTO>> response = restTemplate.exchange(uri, HttpMethod.GET, entity,
						new ParameterizedTypeReference<List<UsuarioDTO>>() {
						});

				List<UsuarioDTO> pagina = response.getBody();
				if (pagina != null && !pagina.isEmpty()) {
					todosUsuarios.addAll(pagina);
					log.debug("Página {}: {} usuarios obtenidos", pageNum, pagina.size());

					// Si la página tiene menos registros que el tamaño, es la última
					if (pagina.size() < pageSize) {
						hayMasPaginas = false;
					} else {
						pageNum++;
					}
				} else {
					hayMasPaginas = false;
				}
			} catch (HttpClientErrorException e) {
				log.error("Error obteniendo usuarios con workflows (página {}): {}", pageNum, e.getMessage());
				hayMasPaginas = false;
			}
		}

		log.info("Total de usuarios con workflows obtenidos de Captio: {}", todosUsuarios.size());
		return todosUsuarios;
	}

	/**
	 * Sincroniza los métodos de pago (tarjetas de crédito) de usuarios.
	 * Si el usuario tiene TDC en el archivo, se asegura de que esté configurada en
	 * Captio.
	 * Si la TDC tiene estatus BAJ, elimina el método de pago.
	 */
	@Override
	public void sincronizarPayments(List<UsuarioDTO> usuarios) throws Exception {
		if (usuarios == null || usuarios.isEmpty()) {
			return;
		}

		for (UsuarioDTO usuario : usuarios) {
			if (usuario.getId() != null) {
				String tdc = usuario.getTdc();
				String tdcStatus = usuario.getTdcStatus();

				try {
					// Obtener los métodos de pago actuales del usuario
					List<MetodoPagoDTO> pagosActuales = obtenerMetodoPago(
							String.format("{\"Id\":\"%d\"}", usuario.getId()));

					// Caso 1: TDC con estatus BAJ - eliminar método de pago si existe
					if (tdcStatus != null && "BAJ".equals(tdcStatus.trim().toUpperCase())) {
						if (pagosActuales != null && !pagosActuales.isEmpty()) {
							MetodoPagoDTO pagoExistente = pagosActuales.get(0);
							try {
								eliminarMetodoPago(usuario.getId(), pagoExistente.getId());
								log.info("Método de pago ELIMINADO para usuario {} (TDC con estatus BAJ)",
										usuario.getEmail());
							} catch (Exception e) {
								log.warn("No se pudo eliminar método de pago para usuario {} (BAJ): {}",
										usuario.getEmail(), e.getMessage());
							}
						} else {
							log.debug("Usuario {} tiene TDC BAJ pero no tiene métodos de pago registrados",
									usuario.getEmail());
						}
						continue; // No procesar más este usuario
					}

					// Caso 2: Validar TDC antes de procesar (solo para ACT o casos válidos)
					if (!validarTdc(tdc, tdcStatus, usuario.getId())) {
						// Si es null/vacío se omite silenciosamente, si es inválido ya se registró
						// error
						continue;
					}

					// Caso 3: TDC válida y activa - crear o actualizar método de pago
					boolean tieneTarjeta = pagosActuales != null && pagosActuales.stream()
							.anyMatch(p -> p != null && tdcCoincide(tdc, p.getValue()));

					if (!tieneTarjeta) {
						log.info("Usuario {} necesita sincronizar tarjeta de crédito: {}", usuario.getEmail(), tdc);

						if (pagosActuales != null && !pagosActuales.isEmpty()) {
							// Ya tiene un método de pago, actualizar el valor
							MetodoPagoDTO pagoExistente = pagosActuales.get(0);
							pagoExistente.setValue(tdc);
							actualizarMetodoPago(usuario.getId(), pagoExistente);
							log.info("Método de pago actualizado para usuario {}", usuario.getEmail());
						} else {
							// No tiene método de pago, crear uno nuevo
							crearMetodoPago(usuario.getId(), tdc, tdcStatus);
							log.info("Método de pago creado para usuario {}", usuario.getEmail());
						}
					}
				} catch (Exception e) {
					log.error("Error al sincronizar payments para usuario {}: {}", usuario.getEmail(), e.getMessage());
				}
			}
		}
	}

	/**
	 * Obtiene un usuario de Captio con sus grupos asignados.
	 */
	@Override
	public UsuarioDTO obtenerUsuarioConGrupos(Integer userId) throws Exception {
		ResponseEntity<List<UsuarioDTO>> response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetGroupsUsuarios();
		URI uri = UriComponentsBuilder.fromUriString(baseUrl)
				.queryParam("filters", String.format("{\"Id\":\"%d\"}", userId))
				.build().encode().toUri();
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<UsuarioDTO>>() {
					});
			return (response.getBody() != null && !response.getBody().isEmpty()) ? response.getBody().get(0) : null;
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request obtenerUsuarioConGrupos: ", e);
			}
		}
		return null;
	}

}
