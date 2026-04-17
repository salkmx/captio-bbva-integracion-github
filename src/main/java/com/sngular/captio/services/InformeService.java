package com.sngular.captio.services;

import java.util.List;

import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.WorkFlowDTO;

public interface InformeService {

	List<InformeDTO> obtenerInformes(String filters);

	InformeDTO crearInforme(UsuarioDTO usuario, WorkFlowDTO workFlowDTO, String comments, String nombre);

	boolean agregarGastosInforme(List<InformeDTO> informes);

	void solicitarAprobacion(List<InformeDTO> informes) throws Exception;

	boolean agregarAnticipoInforme(List<InformeDTO> informes);

	void actualizarInforme(InformeDTO informe);

	void aprobarInforme(InformeDTO informe);
	
	void aprobarInformes(List<InformeDTO> informes);

}
