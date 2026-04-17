package com.sngular.captio.services;

import java.io.File;
import java.io.IOException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public interface SftpService {

	/**
	 * Indica si el modo SFTP está habilitado
	 */
	boolean isSftpEnabled();

	void subirArchivo(File archivoLocal) throws JSchException, SftpException, IOException;

	void subirArchivoError(File archivoLocal, String remoteDirError) throws JSchException, SftpException, IOException;

	void descargarArchivo(String nombreRemoto, String rutaDestinoLocal)
			throws JSchException, SftpException, IOException;

	/**
	 * Descarga un archivo del SFTP directamente a memoria (byte[]).
	 * No crea ningún archivo local.
	 */
	byte[] descargarArchivoEnMemoria(String nombreRemoto) throws JSchException, SftpException, IOException;

	/**
	 * Sube un archivo al directorio remoto del SFTP y elimina la copia local.
	 * Solo opera cuando sftp.enabled=true.
	 */
	void subirArchivoYLimpiarLocal(File archivoLocal, String remoteDir)
			throws JSchException, SftpException, IOException;

}
