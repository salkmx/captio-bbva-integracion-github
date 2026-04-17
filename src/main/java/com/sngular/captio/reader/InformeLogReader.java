package com.sngular.captio.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sngular.captio.dto.InformeLogDTO;
import com.sngular.captio.properties.Properties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InformeLogReader implements ItemReader<InformeLogDTO> {

	private final RestTemplate restTemplate;
	private final Properties properties;
	private Iterator<InformeLogDTO> iterator;

	@Override
	public InformeLogDTO read() throws Exception {
		if (iterator == null) {
			log.info("Leyendo logs de informes desde API: {}", properties.getUrlReportsLogs());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("customerKey", properties.getCustomerKey());
			HttpEntity<Void> entity = new HttpEntity<>(headers);

			try {
				ResponseEntity<InformeLogDTO[]> response = restTemplate.exchange(
						properties.getUrlReportsLogs(),
						HttpMethod.GET,
						entity,
						InformeLogDTO[].class);

				if (response.getBody() != null) {
					List<InformeLogDTO> logs = Arrays.asList(response.getBody());
					log.info("Se encontraron {} logs de informes.", logs.size());
					iterator = logs.iterator();
				} else {
					iterator = new ArrayList<InformeLogDTO>().iterator();
				}
			} catch (Exception e) {
				log.error("Error al leer logs de informes: {}", e.getMessage());
				throw e;
			}
		}

		return (iterator != null && iterator.hasNext()) ? iterator.next() : null;
	}
}