package com.sngular.captio.interceptor;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.sngular.captio.services.JwtTokenService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtInterceptor implements ClientHttpRequestInterceptor {

	private final JwtTokenService tokenService;

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		request.getHeaders().add("Authorization", "Bearer " + tokenService.obtenerJwt());
		return execution.execute(request, body);
	}

}
