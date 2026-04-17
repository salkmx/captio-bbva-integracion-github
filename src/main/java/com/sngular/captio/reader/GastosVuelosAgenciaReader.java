package com.sngular.captio.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.enums.CategoriasEnum;
import com.sngular.captio.services.GastoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GastosVuelosAgenciaReader implements ItemReader<GastoDTO> {

	private final GastoService gastoService;
	private Iterator<GastoDTO> iterator;

	@Override
	public GastoDTO read() throws Exception {
		if (iterator == null) {
			String filtro = "{\"Category_Id\": " + CategoriasEnum.VUELOS_AGENCIA.getClave() 
							+ ", \"User_Id\": 5792}"; // Se filtran por el usuario 5792, Agencia Viajes Amexbgt - agenciaviajes@amexgbt.com

			List<GastoDTO> gastos = gastoService
					.obtenerGastosPorFiltro(filtro);
			iterator = gastos.iterator();
		}
		return iterator.hasNext() ? iterator.next() : null;
	}
}