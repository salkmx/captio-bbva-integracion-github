package com.sngular.captio.services;

import java.util.List;

import com.sngular.captio.dto.UsuarioDTO;

public interface GrupoService {

	List<UsuarioDTO> obtenerGrupos(String filters);

}
