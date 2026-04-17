package com.sngular.captio.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.enums.EstatusInformeEnum;
import com.sngular.captio.services.InformeService;
import com.sngular.captio.util.DateUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ReporteACECReader implements ItemReader<InformeDTO> {

	private final InformeService informeService;

	private Iterator<InformeDTO> iterator;

	@Override
	public InformeDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (iterator == null) {
			List<InformeDTO> viajes = informeService
					.obtenerInformes("{\"Status\":" + EstatusInformeEnum.APROBADO.getId() 
					+ ",\"StatusDate\":\">=" + DateUtils.obtenerFechaActualFormatoCaptio() + "\""
					+ "}");
			iterator = viajes.iterator();
		}
		return iterator.hasNext() ? iterator.next() : null;
	}

}
