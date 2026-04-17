package com.sngular.captio.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.services.InformeService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class InformeViajesValidacionWriter implements ItemWriter<InformeDTO> {

	private final InformeService informeService;

	@Override
	public void write(Chunk<? extends InformeDTO> chunk) throws Exception {
		log.debug("Iniciando la olicitud de aprobación de los informes");
		for (InformeDTO informeDTO : chunk) {
			informeService.aprobarInforme(informeDTO);
		}
	}
}
