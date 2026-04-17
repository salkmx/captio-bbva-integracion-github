package com.sngular.captio.writer;

import com.sngular.captio.dto.DiotDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.util.DateUtils;
import com.sngular.captio.util.DIOTPipeExportUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class GastosDIOTWriter implements ItemWriter<DiotDTO> {

	private final Properties properties;
	private static final String DIOT = "DIOT";

	@Override
	public void write(Chunk<? extends DiotDTO> chunk) throws Exception {

		if (chunk == null || chunk.isEmpty()) {
			return;
		}

	}

}