package com.sngular.captio.writer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.DescuentoNominaDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.util.DateUtils;
import com.sngular.captio.util.DescuentoNominaPipeExportUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReporteDescuentoNominaWriter implements ItemWriter<DescuentoNominaDTO> {

	private final Properties properties;
	private static final String DESCNOMINA = "DescuentoNomina";

	@Override
	public void write(Chunk<? extends DescuentoNominaDTO> chunk) throws Exception {
		log.debug(" iniciando la solicitud de escritura del reporte de descuento de nomina");
		if (chunk == null || chunk.isEmpty())
			return;

		log.info("Escribiendo reporte de descuento de nomina ...");

		final Path path = Paths.get(properties.getRutaArchivoLocalRepDescNomina() + DateUtils.obtenerFechaActual() + "_" + DESCNOMINA  + ".txt");

		if (path.getParent() != null) {
			Files.createDirectories(path.getParent());
		}

		List<DescuentoNominaDTO> items = new ArrayList<>(chunk.getItems());

		String block = DescuentoNominaPipeExportUtil.toPipe(new ArrayList<>(chunk.getItems()), true);

		Files.writeString(path, block, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

	}

}