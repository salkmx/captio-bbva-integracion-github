package com.sngular.captio.services.impl;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.WorkFlowDTO;
import com.sngular.captio.enums.NombreInformeEnum;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.InformeService;
import com.sngular.captio.util.CaptioJsonUtils;
import com.sngular.captio.util.ObjetosUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class InformeServiceImpl implements InformeService {

	private final RestTemplate restTemplate;

	private final Properties properties;

	@Override
	public List<InformeDTO> obtenerInformes(String filters) {
		List<InformeDTO> informes = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetInformes();
		URI uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("filters", filters).build().encode().toUri();
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			ResponseEntity<InformeDTO[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity,
					InformeDTO[].class);
			if (response.getBody() != null) {
				informes = List.of(response.getBody());
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
		return informes;
	}

	@Override
	public InformeDTO crearInforme(UsuarioDTO usuario, WorkFlowDTO workFlowDTO, String comments, String nombre) {
		log.info("crearInforme()");
		try {
			List<InformeDTO> informes = null;

			if (Arrays.stream(NombreInformeEnum.values()).anyMatch(t -> nombre.equals(t.getNombreInforme()))) {
				informes = crearInformeLocal(usuario, workFlowDTO, nombre);
			} else {
				informes = crearInformeLocalMedico(usuario, workFlowDTO, comments, nombre);
			}
			altaInforme(informes);
			return informes.get(0);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean agregarGastosInforme(List<InformeDTO> informes) {
		ObjetosUtils.limpiarCamposExcepto(informes, Arrays.asList("id", "gastos", "sendEmail", "skipAlertsPreview"));
		log.debug(CaptioJsonUtils.toJson(informes));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<InformeDTO>> entity = new HttpEntity<>(informes, headers);

		try {
			restTemplate.exchange(properties.getUrlPostInformesGastos(), HttpMethod.POST, entity, Void.class);
			return true;
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
			return false;
		}

	}

	@Override
	public boolean agregarAnticipoInforme(List<InformeDTO> informes) {
		log.debug(CaptioJsonUtils.toJson(informes));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<InformeDTO>> entity = new HttpEntity<>(informes, headers);

		try {
			restTemplate.exchange(properties.getUrlPostInformesAnticipos(), HttpMethod.POST, entity, Void.class);
			return true;
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
			return false;
		}

	}

	@Override
	public void solicitarAprobacion(List<InformeDTO> informes) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		ObjetosUtils.limpiarCamposExcepto(informes, Arrays.asList("id", "comment", "sendEmail", "skipAlertsPreview"));
		log.debug("***********************Solicitando la aprobación de los informes*******************************");
		log.debug(CaptioJsonUtils.toJson(informes));
		HttpEntity<List<InformeDTO>> entity = new HttpEntity<>(informes, headers);

		try {
			restTemplate.exchange(properties.getUrlSolicitarAprobacionInformes(), HttpMethod.PUT, entity, Void.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
				ObjectMapper mapper = new ObjectMapper();
				String json = CaptioJsonUtils.obtenerJsonError(e.getMessage());
				List<GenericResponseDTO<InformeDTO>> lista = mapper.readValue(json,
						new TypeReference<List<GenericResponseDTO<InformeDTO>>>() {
						});
				log.debug(CaptioJsonUtils.toJson(lista));
			}
		}

	}

	private void altaInforme(List<InformeDTO> informeDTO) throws Exception {
		log.debug(CaptioJsonUtils.toJson(informeDTO));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<InformeDTO>> entity = new HttpEntity<>(informeDTO, headers);

		try {
			restTemplate.exchange(properties.getUrlPostInformes(), HttpMethod.POST, entity, Void.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
				ObjectMapper mapper = new ObjectMapper();
				String json = CaptioJsonUtils.obtenerJsonError(e.getMessage());
				mapper.readValue(json, new TypeReference<List<GenericResponseDTO<Object>>>() {
				});
			}
		}
	}

	public void actualizarInforme(InformeDTO informeDTO) {
		List<InformeDTO> informes = new ArrayList<>();
		informes.add(informeDTO);
		log.debug(CaptioJsonUtils.toJson(informeDTO));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<InformeDTO>> entity = new HttpEntity<>(informes, headers);

		try {
			restTemplate.exchange(properties.getUrlPatchInformes(), HttpMethod.PATCH, entity, Void.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
				ObjectMapper mapper = new ObjectMapper();
				String json = CaptioJsonUtils.obtenerJsonError(e.getMessage());
				try {
					mapper.readValue(json, new TypeReference<List<GenericResponseDTO<Object>>>() {
					});
				} catch (JsonProcessingException e1) {

				}
			}
		}
	}

	public void aprobarInforme(InformeDTO informeDTO) {
		// Solo aprobar informes en estado 2 (Pendiente de Aprobación)
		// Otros estados (ej: 6 = Aprobado) no permiten re-aprobación
		if (informeDTO.getStatus() != null && informeDTO.getStatus() != 2) {
			log.info("Informe {} con status {} no requiere aprobación, se omite", informeDTO.getId(),
					informeDTO.getStatus());
			return;
		}

		List<InformeDTO> informes = new ArrayList<>();
		informes.add(informeDTO);
		log.debug(CaptioJsonUtils.toJson(informeDTO));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<InformeDTO>> entity = new HttpEntity<>(informes, headers);

		try {
			restTemplate.exchange(properties.getUrlPostInformesAprobar(), HttpMethod.PUT, entity, Void.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
				ObjectMapper mapper = new ObjectMapper();
				String json = CaptioJsonUtils.obtenerJsonError(e.getMessage());
				try {
					mapper.readValue(json, new TypeReference<List<GenericResponseDTO<Object>>>() {
					});
				} catch (JsonProcessingException e1) {

				}
			}
		}
	}

	public void aprobarInformes(List<InformeDTO> informes) {
		log.debug(CaptioJsonUtils.toJson(informes));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<InformeDTO>> entity = new HttpEntity<>(informes, headers);

		try {
			restTemplate.exchange(properties.getUrlPostInformesAprobar(), HttpMethod.PUT, entity, Void.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("❌ Error 400 - Bad Request: ", e);
				ObjectMapper mapper = new ObjectMapper();
				String json = CaptioJsonUtils.obtenerJsonError(e.getMessage());
				try {
					mapper.readValue(json, new TypeReference<List<GenericResponseDTO<Object>>>() {
					});
				} catch (JsonProcessingException e1) {

				}
			}
		}
	}

	private List<InformeDTO> crearInformeLocal(UsuarioDTO usuario, WorkFlowDTO workFlowDTO, String prefijo) {
		List<InformeDTO> informes = new ArrayList<>();
		LocalDateTime fechaHora = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		String formatted = fechaHora.format(formatter);
		InformeDTO informeDTO = new InformeDTO();

		WorkFlowDTO wfDTO = new WorkFlowDTO();
		informeDTO.setName(prefijo + usuario.getName() + "_" + formatted);
		informeDTO.setUser(usuario);
		wfDTO.setId(workFlowDTO != null ? workFlowDTO.getId() : 253);
		informeDTO.setWorkflow(wfDTO);
		informes.add(informeDTO);
		return informes;
	}

	private List<InformeDTO> crearInformeLocalMedico(UsuarioDTO usuario, WorkFlowDTO workFlowDTO, String comments,
			String prefijo) {
		List<InformeDTO> informes = new ArrayList<>();
		LocalDateTime fechaHora = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		String formatted = fechaHora.format(formatter);
		InformeDTO informeDTO = new InformeDTO();

		WorkFlowDTO wfDTO = new WorkFlowDTO();
		informeDTO.setName(prefijo + "_" + usuario.getName() + "_" + formatted);
		informeDTO.setUser(usuario);
		informeDTO.setSendEmail(false);
		wfDTO.setId(workFlowDTO != null ? workFlowDTO.getId() : 253);
		informeDTO.setWorkflow(wfDTO);
		informes.add(informeDTO);
		return informes;
	}

}
