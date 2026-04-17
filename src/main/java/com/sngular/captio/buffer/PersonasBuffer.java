package com.sngular.captio.buffer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sngular.captio.dto.UsuarioDTO;

@Component
public class PersonasBuffer {

	private final List<UsuarioDTO> usuariosAlta = new ArrayList<>();
	private final List<UsuarioDTO> usuariosBaja = new ArrayList<>();

	public List<UsuarioDTO> getUsuariosAlta() {
		return usuariosAlta;
	}

	public List<UsuarioDTO> getUsuariosBaja() {
		return usuariosBaja;
	}

}
