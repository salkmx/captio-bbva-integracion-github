package com.sngular.captio.services;

import java.util.List;

import com.sngular.captio.dto.WorkFlowDTO;

public interface FlujoAprobacionService {

	Integer crearFlujoAprobacion(List<WorkFlowDTO> wf);

	WorkFlowDTO obtenerFlujoByFiltro(String filtros) throws Exception;

	/**
	 * Obtiene todos los workflows existentes en Captio.
	 * 
	 * @return Lista de workflows (vacía si hay error)
	 */
	List<WorkFlowDTO> obtenerTodosWorkflows();

	void agregarPaso(List<WorkFlowDTO> wf);

}
