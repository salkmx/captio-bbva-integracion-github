package com.sngular.captio.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sngular.captio.interceptor.JwtInterceptor;
import com.sngular.captio.services.JwtTokenService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Configuration
public class RestTemplateConfig {

	private final JwtTokenService jwtTokenService;

	@Bean
	public RestTemplate restTemplate() {

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		RestTemplate restTemplate = new RestTemplate(requestFactory);

		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new JwtInterceptor(jwtTokenService));
		interceptors.add(new LoggingInterceptor());
		restTemplate.setInterceptors(interceptors);

		return restTemplate;
	}

	static class LoggingInterceptor implements ClientHttpRequestInterceptor {
		private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

		@Override
		public ClientHttpResponse intercept(HttpRequest req, byte[] body, ClientHttpRequestExecution exec)
				throws IOException {
			// REQUEST
			String rqBody = asBody(body, req.getHeaders().getContentType());
			log.debug("HTTP {} {}\nHeaders: {}\nBody: {}\n", req.getMethod(), req.getURI(), req.getHeaders(), rqBody);

			ClientHttpResponse resp = exec.execute(req, body);

			// RESPONSE
			String rsBody = new String(resp.getBody().readAllBytes(), StandardCharsets.UTF_8);
			String pretty = prettyJson(rsBody);
			log.debug("Status: {} {}\nHeaders: {}\nBody:\n{}\n", resp.getStatusText(), resp.getHeaders(), pretty);

			return new BufferingClientHttpResponseWrapper(resp, rsBody.getBytes(StandardCharsets.UTF_8));
		}

		private String asBody(byte[] body, MediaType ct) {
			if (body == null || body.length == 0)
				return "<empty body>";
			String s = new String(body, StandardCharsets.UTF_8);
			return isJson(ct, s) ? prettyJson(s) : s;
		}

		private boolean isJson(MediaType ct, String s) {
			if (ct != null && (ct.includes(MediaType.APPLICATION_JSON) || ct.getSubtype().contains("+json")))
				return true;
			return s.trim().startsWith("{") || s.trim().startsWith("[");
		}

		private String prettyJson(String raw) {
			try {
				return om.writerWithDefaultPrettyPrinter().writeValueAsString(om.readTree(raw));
			} catch (Exception e) {
				return raw;
			}
		}
	}

}

final class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
	private final ClientHttpResponse delegate;
	private final byte[] body;

	BufferingClientHttpResponseWrapper(ClientHttpResponse d, byte[] b) {
		this.delegate = d;
		this.body = b;
	}

	@Override
	public HttpStatusCode getStatusCode() throws IOException {
		return delegate.getStatusCode();
	}

	@Override
	public String getStatusText() throws IOException {
		return delegate.getStatusText();
	}

	@Override
	public void close() {
		delegate.close();
	}

	@Override
	public InputStream getBody() {
		return new ByteArrayInputStream(body);
	}

	@Override
	public HttpHeaders getHeaders() {
		return delegate.getHeaders();
	}
}