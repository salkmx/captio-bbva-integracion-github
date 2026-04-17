package com.sngular.captio.services;

import java.util.List;

import com.sngular.captio.dto.DescuentoNominaDTO;

public interface DescuentoNominaService {

	List<DescuentoNominaDTO> obtenerDescuentosNomina(String StartDate, String EndDate);

}
