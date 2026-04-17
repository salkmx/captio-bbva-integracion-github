package com.sngular.captio.writer;

import java.nio.file.Path;
import java.nio.file.Paths;
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
import com.sngular.captio.util.GastoMedicoPipeExporter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class GastosMedicosWriter implements ItemWriter<GastoDTO> {


	private final EmailService emailService;
	private final GastoService gastoService;

	private final Properties properties;

	private static final String SEPARADOR = ",";


	@Override
	public void write(Chunk<? extends GastoDTO> chunk) throws Exception {
		log.debug(" iniciando el envío de los gastos médicos");
		if (chunk == null || chunk.isEmpty())
			return;

		List<GastoDTO> items = new ArrayList<>(chunk.getItems());

		Path salida = Paths.get(properties.getGastosmedicosPath());

		GastoMedicoPipeExporter.exportPendientes(items, salida);

		sendEmailGastosMedicos(items);
		
		gastoService.eliminar(items);

	}

	private void sendEmailGastosMedicos(List<GastoDTO> items){
		StringBuilder sb = new StringBuilder();
		for (GastoDTO gasto : items) {
			sb.append(buildLine(gasto));
			sb.append(System.lineSeparator());
		}

		emailService.enviarCorreo(
				emailService.crearMailDTO(properties.getMailGastosMedicos(), "Gastos médicos agregados ", sb.toString(), "email/aviso"));
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