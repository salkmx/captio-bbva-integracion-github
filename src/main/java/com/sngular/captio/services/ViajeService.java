package com.sngular.captio.services;

import java.util.List;

import com.sngular.captio.dto.TravelServiceDTO;
import com.sngular.captio.dto.ViajeDTO;

public interface ViajeService {

	List<ViajeDTO> obtenerViajesAprobados(String filtros);

	void actualizarViajeCustomFields(ViajeDTO viajeDTO);

	List<TravelServiceDTO> obtenerServiciosViajes(String filtros);

}
