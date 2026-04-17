package com.sngular.captio.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.InformeLogDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InformeLogProcessor implements ItemProcessor<InformeLogDTO, InformeLogDTO> {

	@Override
	public InformeLogDTO process(InformeLogDTO item) throws Exception {
		return item;
	}
}