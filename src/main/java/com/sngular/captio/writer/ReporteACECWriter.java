package com.sngular.captio.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.AcecDTO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class ReporteACECWriter implements ItemWriter<AcecDTO> {

	@Override
	public void write(Chunk<? extends AcecDTO> chunk) throws Exception {
		if (chunk == null || chunk.isEmpty())
			return;

	}

}
