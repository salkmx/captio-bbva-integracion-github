package com.sngular.captio.writer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.CategoriaDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.MontoDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.EmailService;
import com.sngular.captio.services.GastoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class GastosVuelosAgenciaWriter implements ItemWriter<GastoDTO> {


	private final EmailService emailService;
	private final GastoService gastoService;

	private final Properties properties;

	private static final String SEPARADOR = ",\n";

	@Override
	public void write(Chunk<? extends GastoDTO> chunk) throws Exception {
		log.debug("Escribiendo gastos vuelos agencia");
		if (chunk == null || chunk.isEmpty())
			return;

		List<GastoDTO> items = new ArrayList<>(chunk.getItems());
		sendEmailGastosVuelos(items);


		//Eliminar el gasto original después de asegurar qué el gasto está activo.
		gastoService.eliminar(items);
	}

	private void sendEmailGastosVuelos(List<GastoDTO> items){
		StringBuilder sb = new StringBuilder();
		sb.append("Se han asignado al informe los siguientes gastos de vuelos:\n");
		for (GastoDTO gasto : items) {
			sb.append(buildLine(gasto));
			sb.append(System.lineSeparator());
			sb.append('\n');
		}

		emailService.enviarCorreo(
				emailService.crearMailDTO(properties.getMailGastosVuelos(), "Gasto de vuelo agregado ", sb.toString(), "email/aviso"));
	}

	private static String buildLine(GastoDTO g) {
		return String.join(SEPARADOR, val(g.getUsuarioCorporativo()), val(g.getDate()), val(g.getMerchant()),
				monto(g.getExpenseAmount()), categoria(g.getCategory()), val(g.getNombreInforme()));
	}

	private static String val(Object o) {
		return o == null ? "" : o.toString();
	}

	private static String monto(MontoDTO m) {
		return m == null ? "" : String.valueOf(m.getValue());
	}

	private static String categoria(CategoriaDTO c) {
		return c == null ? "" : String.valueOf(c.getName());
	}
}