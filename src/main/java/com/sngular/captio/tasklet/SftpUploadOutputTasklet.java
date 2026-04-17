package com.sngular.captio.tasklet;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

/**
 * Tasklet que sube los archivos de salida generados por los writers al servidor
 * SFTP
 * y elimina las copias locales. Solo opera cuando sftp.enabled=true.
 * Cuando sftp.enabled=false, no hace nada (los archivos permanecen en local).
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SftpUploadOutputTasklet implements Tasklet {

    private final SftpService sftpService;
    private final Properties properties;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        if (!properties.isSftpEnabled()) {
            log.info("SFTP deshabilitado (sftp.enabled=false) - Los archivos de salida permanecen en local");
            return RepeatStatus.FINISHED;
        }

        String fechaActual = DateUtils.obtenerFechaActual();
        String remoteDir = properties.getRutaRemotaError().replace("/Error/", "/");
        // Usar el directorio remoto base (sin /Error/)
        // Si el remoteDir termina con / lo dejamos así

        List<File> archivosSalida = obtenerArchivosSalida(fechaActual);

        if (archivosSalida.isEmpty()) {
            log.info("No se encontraron archivos de salida para subir al SFTP");
            return RepeatStatus.FINISHED;
        }

        log.info("Subiendo {} archivos de salida al SFTP en {}", archivosSalida.size(), remoteDir);

        for (File archivo : archivosSalida) {
            try {
                sftpService.subirArchivoYLimpiarLocal(archivo, remoteDir);
                log.info("Archivo de salida subido y limpiado: {}", archivo.getName());
            } catch (Exception e) {
                log.error("Error al subir archivo de salida {}: {}", archivo.getName(), e.getMessage());
            }
        }

        return RepeatStatus.FINISHED;
    }

    /**
     * Recopila todos los archivos de salida generados en los directorios locales
     * configurados.
     * Busca archivos que contengan la fecha actual en su nombre.
     */
    private List<File> obtenerArchivosSalida(String fecha) {
        List<File> archivos = new ArrayList<>();
        Set<String> directorios = obtenerDirectoriosLocales();

        for (String dir : directorios) {
            Path dirPath = Paths.get(dir);
            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                continue;
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*" + fecha + "*")) {
                for (Path entry : stream) {
                    if (Files.isRegularFile(entry)) {
                        archivos.add(entry.toFile());
                    }
                }
            } catch (Exception e) {
                log.warn("Error al buscar archivos de salida en {}: {}", dir, e.getMessage());
            }
        }

        return archivos;
    }

    /**
     * Obtiene el conjunto de directorios locales configurados donde los writers
     * generan archivos de salida.
     */
    private Set<String> obtenerDirectoriosLocales() {
        Set<String> dirs = new HashSet<>();
        agregarSiNoVacio(dirs, properties.getRutaArchivoLocalDotacion());
        agregarSiNoVacio(dirs, properties.getRutaArchivoLocalGastos());
        agregarSiNoVacio(dirs, properties.getRutaArchivoLocalDiot());
        agregarSiNoVacio(dirs, properties.getRutaArchivoLocalRepDescNomina());
        agregarSiNoVacio(dirs, properties.getRutaArchivoUsuariosPermisos());
        agregarSiNoVacio(dirs, properties.getGastosmedicosPath());
        agregarSiNoVacio(dirs, properties.getBalanzaLocalDir());
        return dirs;
    }

    private void agregarSiNoVacio(Set<String> dirs, String dir) {
        if (dir != null && !dir.trim().isEmpty()) {
            dirs.add(dir.trim());
        }
    }
}
