package com.sngular.captio.services.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.List;

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

import com.sngular.captio.dto.GenericResponseDTO;
import com.sngular.captio.dto.WorkFlowDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.FlujoAprobacionService;
import com.sngular.captio.util.CaptioJsonUtils;
import com.sngular.captio.util.DateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class FlujoAprobacionServiceImpl implements FlujoAprobacionService {

	private final RestTemplate restTemplate;

	private final Properties properties;

	@Override
	public Integer crearFlujoAprobacion(List<WorkFlowDTO> wf) {
		log.debug(CaptioJsonUtils.toJson(wf));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<WorkFlowDTO>> entity = new HttpEntity<>(wf, headers);

		try {
			ResponseEntity<GenericResponseDTO<WorkFlowDTO>[]> response = restTemplate.exchange(
					properties.getUrlPostWorkflows(), HttpMethod.POST, entity,
					new ParameterizedTypeReference<GenericResponseDTO<WorkFlowDTO>[]>() {
					});
			return response.getBody()[0].getResult().getId();
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e.getMessage());
				String json = CaptioJsonUtils.obtenerJsonError(e.getMessage());
				escribirErrorWorkFLow(json);
			}
		} catch (Exception e) {
			log.error("Error 400 - Bad Request: ", e.getMessage());
			escribirErrorWorkFLow("Error 400 - Bad Request: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void agregarPaso(List<WorkFlowDTO> wf) {
		log.debug(CaptioJsonUtils.toJson(wf));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<WorkFlowDTO>> entity = new HttpEntity<>(wf, headers);

		try {
			restTemplate.exchange(properties.getUrlPostWorkflowsSteps(), HttpMethod.POST, entity,
					new ParameterizedTypeReference<GenericResponseDTO<WorkFlowDTO>[]>() {
					});
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e.getMessage());
				String json = CaptioJsonUtils.obtenerJsonError(e.getMessage());
				escribirErrorWorkFLow(json);
			}
		} catch (Exception e) {
			log.error("Error 400 - Bad Request: ", e.getMessage());
			escribirErrorWorkFLow("Error 400 - Bad Request: " + e.getMessage());
		}
	}

	public void escribirErrorWorkFLow(String error) {
		String ruta = properties.getRutaArchivoErrorWorkFlow() + DateUtils.obtenerFechaActual() + ".csv";

		try {
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

	public WorkFlowDTO obtenerFlujoByFiltro(String filtro) throws Exception {
		ResponseEntity<List<WorkFlowDTO>> response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetWorkflow();
		URI uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("filters", filtro).build().encode().toUri();
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<WorkFlowDTO>>() {
					});
			List<WorkFlowDTO> workflows = response.getBody();
			return (workflows != null && !workflows.isEmpty()) ? workflows.get(0) : null;
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
		return null;

	}

	@Override
	public List<WorkFlowDTO> obtenerTodosWorkflows() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetWorkflow();
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		try {
			// Obtener workflows paginados
			int pagina = 1;
			int porPagina = 100;
			List<WorkFlowDTO> todosWorkflows = new java.util.ArrayList<>();

			boolean hayMas = true;
			while (hayMas) {
				URI uri = UriComponentsBuilder.fromUriString(baseUrl)
						.queryParam("page", pagina)
						.queryParam("pageSize", porPagina)
						.build().encode().toUri();

				ResponseEntity<List<WorkFlowDTO>> response = restTemplate.exchange(
						uri, HttpMethod.GET, entity,
						new ParameterizedTypeReference<List<WorkFlowDTO>>() {
						});

				List<WorkFlowDTO> workflows = response.getBody();
				if (workflows != null && !workflows.isEmpty()) {
					todosWorkflows.addAll(workflows);
					if (workflows.size() < porPagina) {
						hayMas = false;
					} else {
						pagina++;
					}
				} else {
					hayMas = false;
				}
			}

			log.info("Obtenidos {} workflows de Captio", todosWorkflows.size());
			return todosWorkflows;

		} catch (HttpClientErrorException e) {
			log.error("Error al obtener todos los workflows: {}", e.getMessage());
		} catch (Exception e) {
			log.error("Error inesperado al obtener workflows: {}", e.getMessage());
		}

		return java.util.Collections.emptyList();
	}

}
