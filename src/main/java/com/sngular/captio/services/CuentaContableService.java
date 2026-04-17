package com.sngular.captio.services;

import java.util.List;

import com.sngular.captio.dto.CuentaContableDTO;
import com.sngular.captio.dto.CuentaContableSearchNoMedioPagoRequestDTO;
import com.sngular.captio.dto.CuentaContableSearchRequestDTO;

public interface CuentaContableService {
	
    List<CuentaContableDTO> buscar(CuentaContableSearchRequestDTO request);
    List<CuentaContableDTO> buscarSinMedioPago(CuentaContableSearchNoMedioPagoRequestDTO request);


}
