package com.sngular.captio.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.services.InformeService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class InformeViajeValidacionReader implements ItemReader<InformeDTO> {

	private final InformeService informeService;

	private Iterator<InformeDTO> iterator;

	@Override
	public InformeDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (iterator == null) {
			List<InformeDTO> informes = informeService.obtenerInformes("{\"Status\":[1,2]}");
			//List<InformeDTO> informes = informeService.obtenerInformes("{\"Status\":[1,2], \"Id\":9179}");

			iterator = informes.iterator();
		}
		return iterator.hasNext() ? iterator.next() : null;
	}

}
