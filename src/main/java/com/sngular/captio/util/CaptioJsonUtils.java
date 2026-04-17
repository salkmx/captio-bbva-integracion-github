package com.sngular.captio.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sngular.captio.dto.GrupoDTO;
import com.sngular.captio.dto.UsuarioDTO;

public class CaptioJsonUtils {

	private static final ObjectMapper mapper = new ObjectMapper();

	private CaptioJsonUtils() {

	}

	public static String convertirUsuariosAJson(List<UsuarioDTO> usuarios) throws JsonProcessingException {
		try {
			mapper.findAndRegisterModules();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);

			return mapper.writeValueAsString(usuarios);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error convirtiendo lista de usuarios a JSON", e);
		}
	}

	public static String convertirGruposAJson(List<GrupoDTO> grupos) throws JsonProcessingException {
		try {
			mapper.findAndRegisterModules();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);

			return mapper.writeValueAsString(grupos);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error convirtiendo lista de grupos a JSON", e);
		}
	}

	public static String toJson(Object obj) {
		try {
			mapper.registerModule(new JavaTimeModule());
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error al convertir objeto a JSON", e);
		}
	}

	public static String obtenerJsonError(String respuesta) {
		String json = null;
		Pattern pattern = Pattern.compile("\\[.*\\]");
		Matcher matcher = pattern.matcher(respuesta);
		if (matcher.find()) {
			json = matcher.group();
		}
		return json;
	}
}
