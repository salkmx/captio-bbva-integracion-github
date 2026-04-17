package com.sngular.captio.writer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.UserPermissionDTO;
import com.sngular.captio.enums.PermisoEnum;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.util.DateUtils;
import com.sngular.captio.util.FileWritterUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReporteUsuariosPermisosWriter implements ItemWriter<UserPermissionDTO> {

	private final Properties properties;

	@Override
	public void write(Chunk<? extends UserPermissionDTO> chunk) throws Exception {
		log.debug("Inicia escritura de reporte de permisos por usuario. ");
		if (chunk == null || chunk.isEmpty())
			return;

		final Path path = Paths.get(properties.getRutaArchivoUsuariosPermisos() + DateUtils.obtenerFechaActual() + "_UsuariosPermisos.txt");

		if (path.getParent() != null) {
			Files.createDirectories(path.getParent());
		}

		List<UserPermissionDTO> items = new ArrayList<>(chunk.getItems());
		StringBuilder infoToFile = new StringBuilder();
		infoToFile.append(FileWritterUtil.getHeaderUsuarioPermisos());
		String idUserStr = "";
		String employeeCode = "";
		for (UserPermissionDTO user : items) {
			idUserStr = String.valueOf(user.getUser().getId());
			employeeCode = user.getUser().getUserOptions().getEmployeeCode();
			for(PermisoEnum pemission: user.getPermisos()) {
				infoToFile.append(FileWritterUtil.getLineUsuarioPermisos(idUserStr, employeeCode,
						pemission.getIdPermiso(), pemission.getDescription()));
				idUserStr = "";
				employeeCode = "";
			}
			infoToFile.append(FileWritterUtil.addNewEndLine());
		}
		Files.writeString(path, infoToFile.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		log.info("Reporte usuarios permisos total: {} ", items.size());
	}
	
		

}