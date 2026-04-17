package com.sngular.captio.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sngular.captio.dto.OpcionesUsuarioDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.services.UsuarioService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class UsuariosItemWriter implements ItemWriter<Map<String, String>> {

	private final UsuarioService usuarioService;

	@Override
	public void write(Chunk<? extends Map<String, String>> chunk) throws Exception {
		for (Map<String, String> item : chunk.getItems()) {
			List<UsuarioDTO> usuarios = new ArrayList<>();
			OpcionesUsuarioDTO opcionesUsuarioDTO = new OpcionesUsuarioDTO();
			UsuarioDTO usuario = new UsuarioDTO();
			usuario.setEmail(item.get("columna0"));
			usuario.setLogin(item.get("columna1"));
			usuario.setName(item.get("columna2"));
			usuario.setForceChangePasswordOnFirstLogin(true);
			usuario.setAuthenticationType(0);
			usuario.setActive(true);
			opcionesUsuarioDTO.setEmployeeCode(item.get("columna6"));
			opcionesUsuarioDTO.setCompanyCode(item.get("columna7"));
			usuario.setOptions(opcionesUsuarioDTO);
			usuario.setPassword(item.get("columna9"));
			usuarios.add(usuario);
			altaUsuario(usuarios);
		}
	}

	public String convertirUsuariosAJson(List<UsuarioDTO> usuarios) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		return mapper.writeValueAsString(usuarios);
	}

	private void altaUsuario(List<UsuarioDTO> usuarios) throws Exception {
		log.info("altaUsuario");
		usuarioService.altaUsuario(usuarios);
	}

}
