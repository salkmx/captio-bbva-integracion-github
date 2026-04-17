package com.sngular.captio.services;

import java.util.List;

import com.sngular.captio.dto.ErrorDTO;
import com.sngular.captio.dto.ExpenseDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.GrupoDTO;
import com.sngular.captio.dto.ViajeDTO;

public interface GastoService {

	List<GastoDTO> obtenerGastosPorUsuario(ViajeDTO viaje);

	List<ErrorDTO> validacionesGastos(List<GastoDTO> gastos, ViajeDTO viaje);

	List<ErrorDTO> validacionesGastosOtros(List<GastoDTO> gastos, List<GrupoDTO> grupo);

	List<GastoDTO> obtenerGastosPorFiltro(String filtro);

	Integer alta(List<ExpenseDTO> gastos);

	void eliminar(List<GastoDTO> gastos);

	List<GastoDTO> obtenerAdjuntos(String filtro);

	byte[] obtenerXmlAdjunto(String urlFile);


}
