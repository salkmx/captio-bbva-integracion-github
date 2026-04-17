package com.sngular.captio.services.impl;

import java.net.URI;
import java.util.ArrayList;
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
import com.sngular.captio.dto.TravelServiceDTO;
import com.sngular.captio.dto.ViajeDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.ViajeService;
import com.sngular.captio.util.CaptioJsonUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class ViajeServiceImpl implements ViajeService {

	private final RestTemplate restTemplate;

	private final Properties properties;

	@Override
	public List<ViajeDTO> obtenerViajesAprobados(String filtros) {
		List<ViajeDTO> viajes = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		URI uri = UriComponentsBuilder.fromUriString(properties.getUrlGetViajes()).queryParam("filters", filtros)
				.build().encode().toUri();
		try {
			ResponseEntity<ViajeDTO[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity, ViajeDTO[].class);
			if (response.getBody() != null) {
				viajes = List.of(response.getBody());
				log.info("Se obtuvieron " + viajes.size());
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
		return viajes;
	}

	@Override
	public void actualizarViajeCustomFields(ViajeDTO viajeDTO) {
		List<ViajeDTO> viajes = new ArrayList<>();
		viajes.add(viajeDTO);
		log.debug(CaptioJsonUtils.toJson(viajes));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<ViajeDTO>> entity = new HttpEntity<>(viajes, headers);

		try {
			restTemplate.exchange(properties.getUrlPatchViajesCustomFields(), HttpMethod.PATCH, entity, Void.class);
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

	@Override
	public List<TravelServiceDTO> obtenerServiciosViajes(String filtros) {
		List<TravelServiceDTO> servicios = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		URI uri = UriComponentsBuilder.fromUriString(properties.getUrlPatchViajesServices())
				.queryParam("filters", filtros).build().encode().toUri();
		try {
			ResponseEntity<TravelServiceDTO[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity,
					TravelServiceDTO[].class);
			if (response.getBody() != null) {
				servicios = List.of(response.getBody());
				log.info("Se obtuvieron servicios" + servicios.size());
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
		return servicios;
	}

}
