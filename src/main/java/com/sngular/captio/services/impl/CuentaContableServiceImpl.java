package com.sngular.captio.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sngular.captio.dto.CuentaContableDTO;
import com.sngular.captio.dto.CuentaContableSearchNoMedioPagoRequestDTO;
import com.sngular.captio.dto.CuentaContableSearchRequestDTO;
import com.sngular.captio.mapper.CuentaContableMapper;
import com.sngular.captio.repository.CuentaContableRepository;
import com.sngular.captio.services.CuentaContableService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CuentaContableServiceImpl implements CuentaContableService {

	private final CuentaContableRepository repository;
	private final CuentaContableMapper mapper;

	@Override
	public List<CuentaContableDTO> buscar(CuentaContableSearchRequestDTO request) {
		return repository.searchAllFieldsExceptId(request.getIdCategoria(), request.getIdTipoGasto(),
				request.getIdMedioPago(), request.getIdEmpresa()).stream().map(mapper::toDto).toList();
	}

	@Override
	public List<CuentaContableDTO> buscarSinMedioPago(CuentaContableSearchNoMedioPagoRequestDTO request) {
		return repository.searchWithoutMedioPago(request.getIdCategoria(), request.getIdTipoGasto(),
				request.getIdEmpresa(), request.getCuentaContable()).stream().map(mapper::toDto).toList();
	}

}
