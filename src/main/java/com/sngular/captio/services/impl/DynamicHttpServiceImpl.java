package com.sngular.captio.services.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sngular.captio.dto.DynamicRecordRequestDTO;
import com.sngular.captio.dto.DynamicRecordResponseDTO;
import com.sngular.captio.services.DynamicHttpService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicHttpServiceImpl implements DynamicHttpService {

	private final RestTemplate restClient;

	public DynamicRecordResponseDTO execute(DynamicRecordRequestDTO req) {
		validate(req);

		HttpMethod httpMethod = HttpMethod.valueOf(req.method().toUpperCase());
		URI uri = URI.create(req.url());

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(MediaType.parseMediaTypes("*/*"));

		if (req.headers() != null) {
			req.headers().forEach(headers::set);
		}

		boolean hasBody = StringUtils.hasText(req.body()) && allowsRequestBody(httpMethod);
		JsonNode jsonBody = null;
		if (hasBody) {
			ObjectMapper mapper = new ObjectMapper();

			try {
				jsonBody = mapper.readTree(req.body());
			} catch (JsonProcessingException e) {
				log.error(e.getMessage());
			}
		}

		RequestEntity<?> requestEntity = hasBody ? RequestEntity.method(httpMethod, uri).headers(headers)
				.contentType(resolveContentType(headers)).body(jsonBody)
				: RequestEntity.method(httpMethod, uri).headers(headers).build();

		ResponseEntity<String> response = restClient.exchange(requestEntity, String.class);

		return new DynamicRecordResponseDTO(response.getStatusCode().value(), flattenHeaders(response.getHeaders()),
				response.getBody());
	}

	private void validate(DynamicRecordRequestDTO req) {
		if (!StringUtils.hasText(req.url()))
			throw new IllegalArgumentException("url es requerida");
		if (!StringUtils.hasText(req.method()))
			throw new IllegalArgumentException("method es requerido");

		HttpMethod.valueOf(req.method().toUpperCase());

		URI.create(req.url());
	}

	private boolean allowsRequestBody(HttpMethod method) {
		return method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH;
	}

	private MediaType resolveContentType(HttpHeaders headers) {
		return headers.getContentType() != null ? headers.getContentType() : MediaType.APPLICATION_JSON;
	}

	private Map<String, String> flattenHeaders(HttpHeaders headers) {
		Map<String, String> map = new HashMap<>();
		headers.forEach((k, v) -> map.put(k, String.join(",", v)));
		return map;
	}

}
