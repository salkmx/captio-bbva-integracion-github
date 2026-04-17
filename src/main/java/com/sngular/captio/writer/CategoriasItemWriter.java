package com.sngular.captio.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sngular.captio.dto.CategoriaDTO;
import com.sngular.captio.dto.GenericResponseDTO;
import com.sngular.captio.properties.Properties;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class CategoriasItemWriter implements ItemWriter<Map<String, String>> {

	protected RestTemplate restTemplate;

	protected Properties properties;

	@Override
	public void write(Chunk<? extends Map<String, String>> chunk) throws Exception {
		Map<String, CategoriaDTO> categoriasMap = new HashMap<>();
		List<CategoriaDTO> categorias = new ArrayList<>();
		CategoriaDTO categoria = null;
		for (Map<String, String> item : chunk.getItems()) {
			if (!categoriasMap.containsKey(item.get("columna1"))) {
				List<CategoriaDTO> subCategorias = new ArrayList<>();
				categoria = new CategoriaDTO();
				categoria.setName(item.get("columna0"));
				categoria.setCode(item.get("columna1"));
				categoria.setActive(true);
				categoria.setSubCategories(subCategorias);
				categoriasMap.put(categoria.getCode(), categoria);
			} else {
				categoria = categoriasMap.get(item.get("columna1"));
			}
			CategoriaDTO subCategoria = new CategoriaDTO();
			subCategoria.setName(item.get("columna2"));
			subCategoria.setCode(item.get("columna3"));
			subCategoria.setAccount(item.get("columna4"));
			subCategoria.setActive(true);
			subCategoria.setSelfLimited(!item.get("columna5").equals("No"));
			subCategoria.setOnlyKM(!item.get("columna7").equals("No"));
			categoria.getSubCategories().add(subCategoria);
		}
		for (Map.Entry<String, CategoriaDTO> entry : categoriasMap.entrySet()) {
			CategoriaDTO valor = entry.getValue();
			categorias.add(valor);
		}
		altaCategoria(categorias);
	}

	public String convertirCategoriasAJson(List<CategoriaDTO> categorias) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(categorias);
	}

	private void altaCategoria(List<CategoriaDTO> categorias) throws Exception {
		log.info(convertirCategoriasAJson(categorias));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<CategoriaDTO>> entity = new HttpEntity<>(categorias, headers);
		try {
			ResponseEntity<GenericResponseDTO[]> response = restTemplate.exchange(properties.getUrlCategorias(),
					HttpMethod.POST, entity, GenericResponseDTO[].class);

			for (GenericResponseDTO nombre : response.getBody()) {
				log.debug(nombre.getValue());
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - BadRequest: ", e);
			}
		}

	}

}
