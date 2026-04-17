package com.sngular.captio.reader;

import com.sngular.captio.dto.DescuentoNominaDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.DescuentoNominaService;
import com.sngular.captio.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ReporteDescuentoNominaReader implements ItemReader<DescuentoNominaDTO> {

	private final DescuentoNominaService descuentoNominaService;
	private Iterator<DescuentoNominaDTO> iterator;
	private final Properties properties;

	@Override
	public DescuentoNominaDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		if (iterator == null) {

			List<DescuentoNominaDTO>descuento = descuentoNominaService.obtenerDescuentosNomina(DateUtils.obtenerFechaInicialServicios(LocalDateTime.now().minusDays(Long.parseLong(properties.getReporteDescuentoNominaPeriodicidad()))), DateUtils.obtenerFechaFinalServicios(LocalDateTime.now()));

			iterator = descuento.iterator();
		}
		return iterator.hasNext() ? iterator.next() : null;

	}

}
