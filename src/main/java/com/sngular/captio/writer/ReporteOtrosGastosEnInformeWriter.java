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

import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.util.DateUtils;
import com.sngular.captio.util.GastoPipeExportUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class ReporteOtrosGastosEnInformeWriter implements ItemWriter<InformeDTO> {

	private final Properties properties;

	@Override
	public void write(Chunk<? extends InformeDTO> chunk) throws Exception {
		log.debug("Iniciando la solicitud de aprobación de los informes");
		if (!chunk.getItems().isEmpty()) {
			final Path path = Paths.get(properties.getRutaArchivoLocalReporteOtrosGastos()+ DateUtils.obtenerFechaActual() + "_"
					+ properties.getNombreArchivoLocalReporteOtrosGastos());
			if (path.getParent() != null) {
				Files.createDirectories(path.getParent());
			}

			boolean withHeader = true;
			for (InformeDTO informe : chunk.getItems()) {
				List<GastoDTO> items = informe.getGastos();
				if(items!=null){
					String block = GastoPipeExportUtil.toPipe(new ArrayList<>(items), withHeader);
					withHeader = false; //La primera vez pone el header, después ya no es necesario.
					Files.writeString(path, block, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
							StandardOpenOption.APPEND);
					log.info("{} gastos escritos en {}", items.size(), path);
				}else{
					log.error("No hay gastos en informe ", informe.getName());
				}
			}
		}

	}
}