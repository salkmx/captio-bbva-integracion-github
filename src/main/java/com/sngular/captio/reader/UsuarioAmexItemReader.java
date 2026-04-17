package com.sngular.captio.reader;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.OpcionesUsuarioDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.layout.LayoutArchivoProperties;

@Component
public class UsuarioAmexItemReader implements ItemStreamReader<UsuarioDTO> {

	private final FlatFileItemReader<UsuarioDTO> delegate;

	@Autowired
	public UsuarioAmexItemReader(LayoutArchivoProperties props) {
		this.delegate = new FlatFileItemReader<>();
		delegate.setResource(new FileSystemResource("/temp/VIBASONAR.TXT"));

		FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
		List<String> nombres = props.getColumnas();
		tokenizer.setNames(nombres.toArray(new String[0]));
		tokenizer.setColumns(props.getRangos());
		tokenizer.setStrict(false);

		DefaultLineMapper<UsuarioDTO> lineMapper = new DefaultLineMapper<>();
		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(fieldSet -> {
			OpcionesUsuarioDTO opcionesUsuarioDTO = new OpcionesUsuarioDTO();
			UsuarioDTO p = new UsuarioDTO();

			p.setEmail(fieldSet.readString("correo").trim());
			p.setLogin(fieldSet.readString("correo").trim());
			p.setName(concatenarConEspacios(fieldSet.readString("nombreEmpleado").trim(),
					fieldSet.readString("apellidoPaterno").trim(), fieldSet.readString("apellidoMaterno").trim()));
			p.setForceChangePasswordOnFirstLogin(true);
			p.setAuthenticationType(3);
			p.setActive(true);
			opcionesUsuarioDTO.setEmployeeCode(fieldSet.readString("codigoEmpleado").trim());
			opcionesUsuarioDTO.setCompanyCode(fieldSet.readString("codigoEmpresa").trim());
			p.setOptions(opcionesUsuarioDTO);
			p.setPassword("1234qwerty");
			return p;
		});

		delegate.setLineMapper(lineMapper);
	}

	@Override
	public UsuarioDTO read() throws Exception {
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

	public String eliminarDominioCorreo(String correo) {
		if (correo == null || !correo.contains("@")) {
			return correo;
		}
		return correo.substring(0, correo.indexOf("@"));
	}

	public String concatenarConEspacios(String... cadenas) {
		return String.join(" ", cadenas);
	}

}
