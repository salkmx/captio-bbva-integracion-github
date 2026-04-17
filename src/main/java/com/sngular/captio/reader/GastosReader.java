package com.sngular.captio.reader;

import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.mapper.GastoRowMapper;

@Component
public class GastosReader implements ItemStreamReader<GastoDTO> {

	private final PoiItemReader<GastoDTO> delegate;

	public GastosReader() {
		this.delegate = new PoiItemReader<>();
		this.delegate.setResource(new FileSystemResource("c:\\temp\\gastos.xlsx"));
		this.delegate.setLinesToSkip(1);
		this.delegate.setRowMapper(new GastoRowMapper());
		try {
			this.delegate.afterPropertiesSet();
		} catch (Exception e) {
			throw new IllegalStateException("Error inicializando PoiItemReader", e);
		}

	}

	@Override
	public GastoDTO read() throws Exception {
		return delegate.read();
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		delegate.open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		delegate.update(executionContext);
	}

	@Override
	public void close() throws ItemStreamException {
		delegate.close();
	}

}
