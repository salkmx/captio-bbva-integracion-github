package com.sngular.captio.config;

import java.util.Map;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.sngular.captio.mapper.GenericFieldSetMapper;

@Component
public class LineMapperConfig {
	
    @Bean
    public LineMapper<Map<String, String>> lineMapper() {
        DefaultLineMapper<Map<String, String>> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(";");
        tokenizer.setStrict(false);
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new GenericFieldSetMapper());
        return lineMapper;
    }

}
