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

import com.sngular.captio.dto.DotacionDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.util.DateUtils;
import com.sngular.captio.util.DotacionPipeExportUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class DotacionItemWriter implements ItemWriter<DotacionDTO> {

	private final Properties properties;

	@Override
	public void write(Chunk<? extends DotacionDTO> chunk) throws Exception {
		log.debug(" iniciando la olicitud de aprobación de los informes");
		if (chunk == null || chunk.isEmpty())
			return;

		final Path path = Paths.get(properties.getRutaArchivoLocalDotacion() + DateUtils.obtenerFechaActual() + "_" 
				+ properties.getNombreArchivoLocalDotacion());
		if (path.getParent() != null) {
			Files.createDirectories(path.getParent());
		}

		List<DotacionDTO> items = new ArrayList<>(chunk.getItems());

		String block = DotacionPipeExportUtil.toPipe(new ArrayList<>(chunk.getItems()), false);

		Files.writeString(path, block, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

		log.info("Escritas {} dotaciones en {}", items.size(), path);
	}

}