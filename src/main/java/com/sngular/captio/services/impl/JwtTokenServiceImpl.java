package com.sngular.captio.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.sngular.captio.dto.ResponseTokenDTO;
import com.sngular.captio.services.JwtTokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenServiceImpl implements JwtTokenService {

	private final RestTemplate plainRestTemplate = new RestTemplate();

	private String jwtToken;

	@Value("${captio.api.token.client_secret}")
	private String secret;

	@Value("${captio.api.token.client_id}")
	private String client;

	@Value("${captio.api.token.scope}")
	private String scope;

	@Value("${captio.api.token.grant_type}")
	private String grant;

	@Value("${captio.api.token.url}")
	private String urlToken;

	public String obtenerJwt() {
		if (jwtToken == null) {
			MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
			request.add("client_id", client);
			request.add("client_secret", secret);
			request.add("grant_type", grant);
			request.add("scope", scope);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(request, headers);

			ResponseEntity<ResponseTokenDTO> response = plainRestTemplate.exchange(urlToken, HttpMethod.POST, entity,
					ResponseTokenDTO.class);
			if (response.getBody() != null) {
				jwtToken = response.getBody().getToken();
			}
		}
		log.info(jwtToken);
		return jwtToken;
	}

}
