package com.sngular.captio.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sngular.captio.dto.LogsDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.LogsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogsServiceImpl implements LogsService {

	private final RestTemplate restTemplate;

	private final Properties properties;

	@Override
	public void sendLogs() throws Exception {
		log.info("Enviando logs a {}", properties.getUrlSendLogs());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());

		LogsDTO logsDTO = new LogsDTO();
		logsDTO.setLogFileName("logs.txt");
		logsDTO.setSendTo("octavio.rodriguez@sngular.com");
		logsDTO.setTaskName("Logs del día");
		logsDTO.setLogStatus(0);
		logsDTO.setLanguageCode("es");
		logsDTO.setLogContent(getLogContentBase64(logsDTO.getLogFileName()));

		HttpEntity<LogsDTO> entity = new HttpEntity<>(logsDTO, headers);

		try {
			restTemplate.exchange(properties.getUrlSendLogs(), HttpMethod.POST, entity, Void.class);
			log.info("Logs enviados correctamente");
		} catch (Exception e) {
			log.error("Error al enviar logs: {}", e.getMessage());
			throw e;
		}
	}

	private String getLogContentBase64(String fileName) {
		try {
			Path path = Paths.get(fileName);
			if (Files.exists(path)) {
				byte[] content = Files.readAllBytes(path);
				return Base64.getEncoder().encodeToString(content);
			} else {
				log.warn("El archivo de log {} no existe, se enviará contenido vacío.", fileName);
			}
		} catch (IOException e) {
			log.error("Error leyendo archivo de log: {}", e.getMessage());
		}
		return "";
	}

}