package com.sngular.captio.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.enums.CategoriasEnum;
import com.sngular.captio.services.GastoService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class GastosMedicosReader implements ItemReader<GastoDTO> {

	private final GastoService gastoService;

	private Iterator<GastoDTO> iterator;

	@Override
	public GastoDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (iterator == null) {
			List<GastoDTO> viajes = gastoService
					.obtenerGastosPorFiltro("{\"Category_Id\":[" + CategoriasEnum.GASTOS_LOCALES_HONORARIOS.getClave()
							+ "," + CategoriasEnum.GASTOS_LOCALES_HOSPITAL.getClave() + ","
							+ CategoriasEnum.GASTOS_LOCALES_LABORATORIO.getClave() + ","
							+ CategoriasEnum.GASTOS_LOCALES_LENTES.getClave() + ","
							+ CategoriasEnum.GASTOS_LOCALES_MEDICINAS.getClave() + ","
							+ CategoriasEnum.GASTOS_LOCALES_ORTOPEDIA.getClave() + ","
							+ CategoriasEnum.GASTOS_LOCALES_REEMBOLSO_TRASLADO_MEDICO.getClave() 
							+ "],\"Report_Id\":null, \"User_Id\": 5799}");	// Usuario: Gastos Médicos -- Id: 5799 -- Email: gastosmedicos@gmed.com
			iterator = viajes.iterator();
		}
		return iterator.hasNext() ? iterator.next() : null;
	}

}