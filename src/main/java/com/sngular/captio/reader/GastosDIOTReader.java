package com.sngular.captio.reader;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.enums.EstatusInformeEnum;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.InformeService;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.sngular.captio.services.GastoService;
import com.sngular.captio.util.DateUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class GastosDIOTReader implements ItemReader<InformeDTO> {

	private final InformeService informeService;

	private final Properties properties;

	private Iterator<InformeDTO> iterator;

	@Override
	public InformeDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (iterator == null) {

			List<InformeDTO> viajes = informeService.obtenerInformes("{\"Status\":" + EstatusInformeEnum.APROBADO.getId()
							+ ",\"StatusDate\":\">=" + DateUtils.obtenerFechaInicialServicios(LocalDateTime.now().minusDays(Long.parseLong(properties.getReporteDIOTPeriodicidad()))) + "\""
							+ "}");

			iterator = viajes.iterator();

		}
		return iterator.hasNext() ? iterator.next() : null;
	}

}