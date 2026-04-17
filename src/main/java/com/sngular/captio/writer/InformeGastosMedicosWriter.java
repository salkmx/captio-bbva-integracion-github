package com.sngular.captio.writer;

import java.util.ArrayList;
import java.util.List;

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
public class InformeGastosMedicosWriter implements ItemWriter<InformeDTO> {

	private final InformeService informeService;

	@Override
	public void write(Chunk<? extends InformeDTO> chunk) throws Exception {
		log.debug("Iniciando la olicitud de aprobación de los informes de gastos médicos");

		List<InformeDTO> informes = new ArrayList<>();

		if (chunk == null || chunk.isEmpty())
			return;

		for (InformeDTO informe : chunk) {
			log.debug("informe {}, m {} ", informe.getId(), informe.getAmount().getValue());
			if (informe.getName().startsWith("RMED_")
					&& informe.getAmount().getValue() > 0) {
				informe.setSendEmail(false);
				informes.add(informe);
			}
		}

		if (!informes.isEmpty()) {
			informeService.solicitarAprobacion(informes);

			informeService.aprobarInformes(informes);
		}

	}
}
