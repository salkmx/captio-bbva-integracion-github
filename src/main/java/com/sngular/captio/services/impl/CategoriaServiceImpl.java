package com.sngular.captio.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.sngular.captio.dto.CategoriaDTO;
import com.sngular.captio.dto.GenericResponseDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.CategoriaService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class CategoriaServiceImpl implements CategoriaService {

	private final RestTemplate restTemplate;

	private final Properties properties;

	@Override
	public void eliminarCategorias() {
		try {
			// ObjectMapper mapper = new ObjectMapper();
			// String contenido =
			// Files.readString(Path.of("/home/davidsanchez/Descargas/categories_captio.txt"));
			//
			// List<CategoriaDTO> categorias = mapper.readValue(contenido, new
			// TypeReference<List<CategoriaDTO>>() {
			// });

			List<CategoriaDTO> categorias = obtenerCategorias();
			log.info("Categorías encontradas: " + categorias.size());
			categorias.forEach(categoria -> {
				if (categoria.getId() != null) {
					log.info(categoria.getId().toString());
					limpiarCamposExceptoId(categoria);
				}

			});
			eliminarCategoria(categorias);
		} catch (Exception e) {
			log.error("Error al elimiminar categoría", e);
		}
	}

	private List<CategoriaDTO> obtenerCategorias() throws Exception {
		ResponseEntity<List<CategoriaDTO>> response = null;
		List<CategoriaDTO> respuesta = new ArrayList<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			response = restTemplate.exchange(properties.getUrlCategorias(), HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<CategoriaDTO>>() {
					});
			respuesta = response.getBody();
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
		return respuesta;
	}

	private void eliminarCategoria(List<CategoriaDTO> categorias) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<CategoriaDTO>> entity = new HttpEntity<>(categorias, headers);
		try {
			restTemplate.exchange(properties.getUrlCategorias(), HttpMethod.DELETE, entity, GenericResponseDTO.class);
			log.info("Se eliminaron las categorías");
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
	}

	private void limpiarCamposExceptoId(CategoriaDTO categoria) {
		Integer id = categoria.getId(); // Guardamos el id original

		// Limpiamos todos los campos excepto id
		categoria.setName(null);
		categoria.setCode(null);
		categoria.setAccount(null);
		categoria.setActive(null);
		categoria.setSelfLimited(null);
		categoria.setMaxAmount(null);
		categoria.setOnlyKM(null);
		categoria.setOnlyIntegrations(null);
		categoria.setSubCategories(null);
		categoria.setLanguages(null);

		// Volvemos a asignar el id por seguridad
		categoria.setId(id);

	}

}
