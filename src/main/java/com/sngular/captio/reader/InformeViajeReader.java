package com.sngular.captio.reader;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.ViajeDTO;
import com.sngular.captio.services.ViajeService;
import com.sngular.captio.util.DateUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class InformeViajeReader implements ItemReader<ViajeDTO> {

	private final ViajeService viajeService;

	private Iterator<ViajeDTO> iterator;

	@Override
	public ViajeDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (iterator == null) {
			// List<ViajeDTO> viajes = viajeService.obtenerViajesAprobados("{\"Id\":108}");
			List<ViajeDTO> viajes = viajeService.obtenerViajesAprobados("{\"Status\":6,\"StartDate\":\">="
					+ DateUtils.obtenerFechaInicialServicios(LocalDateTime.now().minusDays(10L)) + "\"}");
			iterator = viajes.iterator();
		}
		return iterator.hasNext() ? iterator.next() : null;
	}

}
