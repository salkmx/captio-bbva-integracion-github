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
public class CategoriaItemReader extends FlatFileItemReader<Map<String, String>> {

	public CategoriaItemReader(@Value("${csv.categoria.path}") Resource resource,
			@Value("${csv.categoria.skip-header:true}") boolean skipHeader,
			LineMapper<Map<String, String>> lineMapper) {
		File file = new File("/temp/categoria.csv");
		if (file.exists()) {
			setResource(new FileSystemResource("/temp/categoria.csv"));
		}

		setLinesToSkip(skipHeader ? 1 : 0);

		setLineMapper(lineMapper);
	}

}
