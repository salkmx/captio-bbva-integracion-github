package com.sngular.captio.services.impl;

import java.net.URI;
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

import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.GrupoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class GrupoServiceImpl implements GrupoService {

	private final RestTemplate restTemplate;

	private final Properties properties;

	@Override
	public List<UsuarioDTO> obtenerGrupos(String filters) {
		List<UsuarioDTO> grupos = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetUserGroups();
		URI uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("filters", filters).build().encode().toUri();
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			ResponseEntity<UsuarioDTO[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity,
					UsuarioDTO[].class);
			if (response.getBody() != null) {
				grupos = List.of(response.getBody());
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
		return grupos;
	}

}
