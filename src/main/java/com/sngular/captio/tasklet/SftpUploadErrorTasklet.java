package com.sngular.captio.tasklet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.SftpService;
import com.sngular.captio.util.DateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SftpUploadErrorTasklet implements Tasklet {

	private final SftpService sftpService;
	private final Properties properties;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		if (!properties.isSftpEnabled()) {
			log.info("SFTP deshabilitado (sftp.enabled=false) - Los archivos de error permanecen en local");
			return RepeatStatus.FINISHED;
		}

		String fechaActual = DateUtils.obtenerFechaActual();
		List<File> archivosError = obtenerArchivosError(fechaActual);

		if (archivosError.isEmpty()) {
			log.info("No se encontraron archivos de error para subir al SFTP");
			return RepeatStatus.FINISHED;
		}

		String remoteDirError = properties.getRutaRemotaError();
		log.info("Subiendo {} archivos de error a {}", archivosError.size(), remoteDirError);

		for (File archivo : archivosError) {
			try {
				sftpService.subirArchivoError(archivo, remoteDirError);
				log.info("Archivo subido exitosamente: {}", archivo.getName());
				// Eliminar copia local después de subir al SFTP
				if (archivo.delete()) {
					log.info("Copia local de error eliminada: {}", archivo.getAbsolutePath());
				} else {
					log.warn("No se pudo eliminar la copia local de error: {}", archivo.getAbsolutePath());
				}
			} catch (Exception e) {
				log.error("Error al subir archivo {}: {}", archivo.getName(), e.getMessage());
			}
		}

		return RepeatStatus.FINISHED;
	}

	private List<File> obtenerArchivosError(String fecha) {
		List<File> archivos = new ArrayList<>();

		agregarArchivoSiExiste(archivos, properties.getRutaArchivoErrorViajes() + fecha + ".csv");
		agregarArchivoSiExiste(archivos, properties.getRutaArchivoErrorGastos() + fecha + ".csv");
		agregarArchivoSiExiste(archivos, properties.getRutaArchivoErrorUsuarios() + fecha + ".csv");
		agregarArchivoSiExiste(archivos, properties.getRutaArchivoErrorWorkFlow() + fecha + ".csv");
		agregarArchivoSiExiste(archivos, properties.getRutaArchivoErrorDotaciones() + fecha + ".csv");

		return archivos;
	}

	private void agregarArchivoSiExiste(List<File> archivos, String ruta) {
		File archivo = new File(ruta);
		if (archivo.exists() && archivo.isFile()) {
			archivos.add(archivo);
		}
	}
}
