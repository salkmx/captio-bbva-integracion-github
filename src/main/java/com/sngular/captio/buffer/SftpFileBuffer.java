package com.sngular.captio.buffer;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Buffer en memoria para almacenar archivos descargados del SFTP.
 * Cuando sftp.enabled=true, el archivo se descarga aquí en vez de a disco,
 * evitando dejar copias locales.
 * Cuando sftp.enabled=false, este buffer permanece vacío y se lee de disco.
 */
@Slf4j
@Getter
@Setter
@Component
public class SftpFileBuffer {

    private byte[] vibasonarData;

    /**
     * Indica si hay datos del VIBASONAR en memoria
     */
    public boolean hasVibasonarData() {
        return vibasonarData != null && vibasonarData.length > 0;
    }

    /**
     * Limpia el buffer de memoria
     */
    public void clear() {
        this.vibasonarData = null;
        log.info("Buffer SFTP limpiado");
    }
}
