package com.sngular.captio.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.sngular.captio.buffer.SftpFileBuffer;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.SftpService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class SftpDownloadTasklet implements Tasklet {

	private final SftpService sftpService;
	private final Properties properties;
	private final SftpFileBuffer sftpFileBuffer;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		if (!properties.isSftpEnabled()) {
			log.info("SFTP deshabilitado (sftp.enabled=false) - Se omite la descarga desde SFTP. "
					+ "Se espera que el archivo exista en local: {}", properties.getRutaDestinoLocal());
			return RepeatStatus.FINISHED;
		}

		try {
			// Descargar directamente a memoria, sin crear copia local
			byte[] data = sftpService.descargarArchivoEnMemoria(properties.getNombreArchivoRemoto());
			sftpFileBuffer.setVibasonarData(data);
			log.info("Archivo descargado desde SFTP a memoria: {} ({} bytes) - Sin copia local",
					properties.getNombreArchivoRemoto(), data.length);
			return RepeatStatus.FINISHED;
		} catch (Exception e) {
			throw new RuntimeException("Error al descargar archivo desde SFTP", e);
		}
	}

}
