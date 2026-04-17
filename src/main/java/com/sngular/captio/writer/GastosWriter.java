package com.sngular.captio.writer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.ExpenseDTO;
import com.sngular.captio.services.GastoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class GastosWriter implements ItemWriter<ExpenseDTO> {

	private final GastoService gastoService;

	@Override
	public void write(Chunk<? extends ExpenseDTO> chunk) throws Exception {
		log.debug(" iniciando la olicitud de aprobación de los informes");
		if (chunk == null || chunk.isEmpty())
			return;

		List<ExpenseDTO> items = new ArrayList<>(chunk.getItems());

		gastoService.alta(items);

	}

}