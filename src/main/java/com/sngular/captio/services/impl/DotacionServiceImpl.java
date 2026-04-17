package com.sngular.captio.services.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sngular.captio.dto.DotacionDTO;
import com.sngular.captio.dto.GenericResponseDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.DotacionService;
import com.sngular.captio.util.CaptioJsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class DotacionServiceImpl implements DotacionService {

	private final RestTemplate restTemplate;

	private final Properties properties;

	@Override
	public List<GenericResponseDTO<DotacionDTO>> crearDotacion(DotacionDTO dotacion)
			throws JsonMappingException, JsonProcessingException {
		List<GenericResponseDTO<DotacionDTO>> response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		List<DotacionDTO> dotaciones = new ArrayList<>();
		dotaciones.add(dotacion);
		log.debug(CaptioJsonUtils.toJson(dotaciones));
		HttpEntity<List<DotacionDTO>> entity = new HttpEntity<>(dotaciones, headers);

		try {
			restTemplate.exchange(properties.getUrlPostAdvances(), HttpMethod.POST, entity, Void.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
				ObjectMapper mapper = new ObjectMapper();
				String error = CaptioJsonUtils.obtenerJsonError(e.getMessage());
				response = mapper.readValue(error, new TypeReference<List<GenericResponseDTO<DotacionDTO>>>() {
				});
			}
		}
		return response;
	}

	@Override
	public List<DotacionDTO> obtenerDotacion(String filtro) throws JsonMappingException, JsonProcessingException {
		ResponseEntity<DotacionDTO[]> response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetAdvances();
		URI uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("filters", filtro).build().encode().toUri();
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, entity, DotacionDTO[].class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
		return Optional.ofNullable(response.getBody()).map(Arrays::asList).map(ArrayList::new)
				.orElseGet(ArrayList::new);
	}

	@Override
	public List<GenericResponseDTO<DotacionDTO>> entregarDotacion(DotacionDTO dotacion)
			throws JsonMappingException, JsonProcessingException {
		List<GenericResponseDTO<DotacionDTO>> response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		List<DotacionDTO> dotaciones = new ArrayList<>();
		dotaciones.add(dotacion);
		log.debug(CaptioJsonUtils.toJson(dotaciones));
		HttpEntity<List<DotacionDTO>> entity = new HttpEntity<>(dotaciones, headers);
		try {
			restTemplate.exchange(properties.getUrlAdvancesDeliver(), HttpMethod.PUT, entity, Void.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
				ObjectMapper mapper = new ObjectMapper();
				String error = CaptioJsonUtils.obtenerJsonError(e.getMessage());
				response = mapper.readValue(error, new TypeReference<List<GenericResponseDTO<DotacionDTO>>>() {
				});
			}
		}
		return response;
	}

}
