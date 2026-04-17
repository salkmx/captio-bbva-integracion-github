package com.sngular.captio.reader;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.sngular.captio.buffer.SftpFileBuffer;
import com.sngular.captio.dto.OpcionesUsuarioDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.layout.LayoutArchivoProperties;
import com.sngular.captio.properties.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UsuarioSonarItemReader implements ItemStreamReader<UsuarioDTO> {

	private final FlatFileItemReader<UsuarioDTO> delegate;
	private final SftpFileBuffer sftpFileBuffer;
	private final Properties properties;

	public UsuarioSonarItemReader(LayoutArchivoProperties props, SftpFileBuffer sftpFileBuffer, Properties properties) {
		var sp = Stopwatch.createStarted();
        this.sftpFileBuffer = sftpFileBuffer;
		this.properties = properties;
		this.delegate = new FlatFileItemReader<>();
		// El resource se configura en open() según el modo (SFTP en memoria o local)

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

			// Campos para identificación en reportes de error (VIBASONAR)
			p.setCodigoRegistro(fieldSet.readString("codigoRegistro").trim());
			p.setNombreEmpleado(fieldSet.readString("nombreEmpleado").trim());
			p.setApellidoPaterno(fieldSet.readString("apellidoPaterno").trim());
			p.setApellidoMaterno(fieldSet.readString("apellidoMaterno").trim());
			p.setCodigoEmpleado(fieldSet.readString("codigoEmpleado").trim());
			p.setCodigoEmpresa(fieldSet.readString("codigoEmpresa").trim());
			p.setNombrePuesto(fieldSet.readString("nombrePuesto").trim());
			p.setCodigoDepartamento(fieldSet.readString("codigoDepartamento").trim());
			p.setNombreDepartamento(fieldSet.readString("nombreDepartamento").trim());
			p.setNivelEstructura(fieldSet.readString("nivelEstructura").trim());
			p.setCodigoDireccionGeneral(fieldSet.readString("codigoDireccionGeneral").trim());
			p.setNombreDireccionGeneral(fieldSet.readString("nombreDireccionGeneral").trim());

			// Campos para Captio API
			p.setEmail(fieldSet.readString("correo").trim());
			p.setLogin(fieldSet.readString("correo").trim());
			p.setName(concatenarConEspacios(p.getNombreEmpleado(), p.getApellidoPaterno(), p.getApellidoMaterno()));
			p.setForceChangePasswordOnFirstLogin(true);
			p.setAuthenticationType(3);
			p.setActive(true);
			opcionesUsuarioDTO.setEmployeeCode(p.getCodigoEmpleado());
			opcionesUsuarioDTO.setCompanyCode(p.getCodigoEmpresa());
			p.setOptions(opcionesUsuarioDTO);
			p.setPassword("1234qwerty");
			p.setEstadoEmpleado(fieldSet.readString("estadoEmpleado").trim());
			p.setLoginResponsable(fieldSet.readString("loginResponsable").trim());
			p.setLoginSupervisor(fieldSet.readString("loginSupervisor").trim());

			// TDC (Tarjeta de Crédito) - posición 939-954 (16 dígitos)
			String tdcValue = fieldSet.readString("tdc").trim();
			p.setTdc(tdcValue);
			// TDC Estatus - posición 955-957 (ACT/BAJ)
			String tdcEstatusValue = fieldSet.readString("tdcEstatus").trim();
			p.setTdcStatus(tdcEstatusValue);

			// Niveles jerárquicos N1-N4 (posiciones 905-938 del VIBASONAR)
			p.setLoginN1(fieldSet.readString("loginN1").trim());
			p.setLoginN2(fieldSet.readString("loginN2").trim());
			p.setLoginN3(fieldSet.readString("loginN3").trim());
			p.setLoginN4(fieldSet.readString("loginN4").trim());

			// Log de diagnóstico para TDC (nivel INFO para ver siempre)
			log.info("READER - Email: {}, TDC: '{}' (len={}), TDC Estatus: '{}' (len={})",
					p.getEmail(), tdcValue, tdcValue.length(), tdcEstatusValue, tdcEstatusValue.length());

			return p;
		});

		delegate.setLineMapper(lineMapper);
        log.info("[{}][UsuarioSonarItemReader()] took {}ms", this.getClass().getSimpleName(), sp.elapsed(TimeUnit.MILLISECONDS));
	}

	@Override
	public UsuarioDTO read() throws Exception {
		return delegate.read();
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		// Si hay datos del SFTP en memoria, usamos ByteArrayResource (sin archivo
		// local)
		if (sftpFileBuffer.hasVibasonarData()) {
			log.info("Leyendo VIBASONAR desde memoria SFTP ({} bytes) - Sin copia local",
					sftpFileBuffer.getVibasonarData().length);
			delegate.setResource(new ByteArrayResource(sftpFileBuffer.getVibasonarData()));
		} else {
			log.info("Leyendo VIBASONAR desde archivo local: {}", properties.getRutaDestinoLocal());
			delegate.setResource(new FileSystemResource(properties.getRutaDestinoLocal()));
		}
		delegate.open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		delegate.update(executionContext);
	}

	@Override
	public void close() throws ItemStreamException {
		delegate.close();
		// Liberar memoria del buffer SFTP si se usó
		if (sftpFileBuffer.hasVibasonarData()) {
			sftpFileBuffer.clear();
		}
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
