package com.sngular.captio.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.sngular.captio.services.SftpService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SftpServiceImpl implements SftpService {

	@Value("${sftp.enabled:false}")
	private boolean sftpEnabled;

	@Value("${sftp.host}")
	private String sftpHost;

	@Value("${sftp.port}")
	private int sftpPort;

	@Value("${sftp.user}")
	private String sftpUser;

	@Value("${sftp.password}")
	private String sftpPassword;

	@Value("${sftp.remote-dir}")
	private String remoteDirWrite;

	@Value("${sftp.remote-dir.read}")
	private String remoteDirRead;

	@Value("${sftp.keyfile:}")
	private String sftpKeyfile;

	@Value("${sftp.keyfile.passphrase:}")
	private String sftpKeyfilePassphrase;

	/**
	 * Indica si el modo SFTP está habilitado
	 */
	public boolean isSftpEnabled() {
		return sftpEnabled;
	}

	/**
	 * Sube un archivo al servidor SFTP
	 * 
	 * @throws IOException
	 */
	public void subirArchivo(File archivoLocal) throws JSchException, SftpException, IOException {
		if (!sftpEnabled) {
			log.info("SFTP deshabilitado - archivo permanece en local: {}", archivoLocal.getName());
			return;
		}
		ChannelSftp sftpChannel = conectar();
		try (FileInputStream fis = new FileInputStream(archivoLocal)) {
			sftpChannel.cd(remoteDirWrite);
			sftpChannel.put(fis, archivoLocal.getName());
		} finally {
			desconectar(sftpChannel);
		}
	}

	/**
	 * Sube un archivo de error a una carpeta específica del servidor SFTP
	 */
	public void subirArchivoError(File archivoLocal, String remoteDirError)
			throws JSchException, SftpException, IOException {
		if (!sftpEnabled) {
			log.info("SFTP deshabilitado - archivo de error permanece en local: {}", archivoLocal.getName());
			return;
		}
		ChannelSftp sftpChannel = conectar();
		try (FileInputStream fis = new FileInputStream(archivoLocal)) {
			crearDirectorioSiNoExiste(sftpChannel, remoteDirError);
			sftpChannel.cd(remoteDirError);
			sftpChannel.put(fis, archivoLocal.getName());
		} finally {
			desconectar(sftpChannel);
		}
	}

	private void crearDirectorioSiNoExiste(ChannelSftp sftpChannel, String directorio) {
		try {
			sftpChannel.stat(directorio);
		} catch (SftpException e) {
			try {
				sftpChannel.mkdir(directorio);
			} catch (SftpException ex) {
				// El directorio puede existir o no tener permisos
			}
		}
	}

	/**
	 * Descarga un archivo del servidor SFTP
	 */
	public void descargarArchivo(String nombreRemoto, String rutaDestinoLocal)
			throws JSchException, SftpException, IOException {
		if (!sftpEnabled) {
			log.info("SFTP deshabilitado - se espera que el archivo exista en local: {}", rutaDestinoLocal);
			return;
		}
		ChannelSftp sftpChannel = conectar();
		try (FileOutputStream fos = new FileOutputStream(rutaDestinoLocal)) {
			sftpChannel.cd(remoteDirRead);
			sftpChannel.get(nombreRemoto, fos);
		} finally {
			desconectar(sftpChannel);
		}
	}

	/**
	 * Descarga un archivo del SFTP directamente a memoria sin crear copia local.
	 */
	@Override
	public byte[] descargarArchivoEnMemoria(String nombreRemoto) throws JSchException, SftpException, IOException {
		ChannelSftp sftpChannel = conectar();
		try {
			sftpChannel.cd(remoteDirRead);
			try (InputStream is = sftpChannel.get(nombreRemoto);
					ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				byte[] buffer = new byte[8192];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					baos.write(buffer, 0, bytesRead);
				}
				log.info("Archivo descargado en memoria desde SFTP: {} ({} bytes)", nombreRemoto, baos.size());
				return baos.toByteArray();
			}
		} finally {
			desconectar(sftpChannel);
		}
	}

	/**
	 * Sube un archivo al directorio remoto especificado y elimina la copia local.
	 * Solo opera cuando sftp.enabled=true.
	 */
	@Override
	public void subirArchivoYLimpiarLocal(File archivoLocal, String remoteDirDestino)
			throws JSchException, SftpException, IOException {
		if (!sftpEnabled) {
			log.info("SFTP deshabilitado - archivo permanece en local: {}", archivoLocal.getName());
			return;
		}
		ChannelSftp sftpChannel = conectar();
		try (FileInputStream fis = new FileInputStream(archivoLocal)) {
			crearDirectorioSiNoExiste(sftpChannel, remoteDirDestino);
			sftpChannel.cd(remoteDirDestino);
			sftpChannel.put(fis, archivoLocal.getName());
			log.info("Archivo subido a SFTP: {} -> {}", archivoLocal.getName(), remoteDirDestino);
		} finally {
			desconectar(sftpChannel);
		}
		// Eliminar copia local después de subir exitosamente
		if (archivoLocal.delete()) {
			log.info("Copia local eliminada: {}", archivoLocal.getAbsolutePath());
		} else {
			log.warn("No se pudo eliminar la copia local: {}", archivoLocal.getAbsolutePath());
		}
	}

	/**
	 * Crea una conexión SFTP y la devuelve
	 */
	private ChannelSftp conectar() throws JSchException {
		JSch jsch = new JSch();

		boolean useKey = sftpKeyfile != null && !sftpKeyfile.trim().isEmpty();
		if (useKey) {
			if (sftpKeyfilePassphrase != null && !sftpKeyfilePassphrase.trim().isEmpty()) {
				jsch.addIdentity(sftpKeyfile, sftpKeyfilePassphrase);
			} else {
				jsch.addIdentity(sftpKeyfile);
			}
		}

		Session session = jsch.getSession(sftpUser, sftpHost, sftpPort);
		// Use password only when keyfile is not configured
		if (!useKey) {
			session.setPassword(sftpPassword);
		}

		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		if (useKey) {
			config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
		}
		session.setConfig(config);

		session.connect(10_000);

		Channel channel = session.openChannel("sftp");
		channel.connect();

		return (ChannelSftp) channel;
	}

	/**
	 * Cierra canal y sesión
	 * 
	 * @throws JSchException
	 */
	private void desconectar(ChannelSftp channelSftp) throws JSchException {
		if (channelSftp != null && channelSftp.isConnected()) {
			channelSftp.disconnect();
			Session session = channelSftp.getSession();
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
	}

}
