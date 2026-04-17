package com.sngular.captio.reader;

import java.io.File;
import java.util.Map;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class UsuarioItemReader extends FlatFileItemReader<Map<String, String>> {

	public UsuarioItemReader(@Value("${csv.usuario.path}") Resource resource,
			@Value("${csv.usuario.skip-header:true}") boolean skipHeader, LineMapper<Map<String, String>> lineMapper) {
		File file = new File("/temp/file.csv");
		if (file.exists()) {
			setResource(new FileSystemResource("/temp/file.csv"));
		}

		setLinesToSkip(skipHeader ? 1 : 0);

		setLineMapper(lineMapper);
	}

}
