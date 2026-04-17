package com.sngular.captio.dto;

import java.util.ArrayList;
import java.util.List;

import com.sngular.captio.enums.PermisoEnum;
import lombok.Data;

/**
 * Contiene la relación de usuarios y permisos que tiene cada usuario
 */
@Data
public class UserPermissionDTO {
	private UsuarioDTO user;
	List<PermisoEnum> permisos;
	
	public void doPermissionsListToEnumList(List<PermissionDTO> permissions) {
		if(permissions == null || permissions.isEmpty()) {
			return;
		}
		permisos = new ArrayList<>();
		for(PermissionDTO permission : permissions) {
			PermisoEnum permiso = PermisoEnum.fromId(permission.getIdPermiso());
			permisos.add(permiso);
		}
	}
}
