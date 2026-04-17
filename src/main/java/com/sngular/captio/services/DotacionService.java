package com.sngular.captio.services;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sngular.captio.dto.DotacionDTO;
import com.sngular.captio.dto.GenericResponseDTO;

public interface DotacionService {

	List<GenericResponseDTO<DotacionDTO>> crearDotacion(DotacionDTO dotacion)
			throws JsonMappingException, JsonProcessingException;

	List<DotacionDTO> obtenerDotacion(String filtro) throws JsonMappingException, JsonProcessingException;

	List<GenericResponseDTO<DotacionDTO>> entregarDotacion(DotacionDTO dotacion)
			throws JsonMappingException, JsonProcessingException;

}
