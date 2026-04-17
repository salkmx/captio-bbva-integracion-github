/**
 * 
 */
package com.sngular.captio.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sngular.captio.dto.UserPermissionDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.enums.PermisoEnum;
import com.sngular.captio.services.ReporteUsuarioPermisosService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.util.ParametersBuilderUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de reportes service - rmiranda
 */
@Slf4j
@AllArgsConstructor
@Component
public class ReporteUsuarioPermisosServiceImpl implements ReporteUsuarioPermisosService {
	
	/**
	 * El servicio de los usuarios
	 */
	private final UsuarioService usuariosService;

	@Override
	public List<UserPermissionDTO> getUsersPermissions() {
		List<UserPermissionDTO> usesrsPermissions = new ArrayList<>();
		try {
			//Todos los usuarios
			List<UsuarioDTO> users = usuariosService.obtenerUsuarios();
			if(users == null || users.isEmpty()) {
				return usesrsPermissions;
			}
			//solo la lista de ids
			List<String> userIds =
					users.stream()
			                .filter(Objects::nonNull)
			                .map(UsuarioDTO::getId)
			                .filter(Objects::nonNull)
			                .map(String::valueOf)
			                .distinct()
			                .toList();
			//La lista de ids separados por comas
			String userIdsCom = userIds.stream()
	                .map(String::valueOf)
	                .collect(Collectors.joining(","));
			
			//Se arman los filtros
			ParametersBuilderUtil params = new ParametersBuilderUtil();
			params.addParameter("Id", "[" + userIdsCom + "]");
			
			//Los usuarios con sus permisos, -> el servicio solo retorna los id de ususario y la lista de permisos
			List<UsuarioDTO> usersPermissions =  usuariosService.getUsersPermissions(params);
			
			//Iteramos los usuarios y ids de usuarios con sus permisos
			for (UsuarioDTO user : users) {
			    for (UsuarioDTO userPermnission : usersPermissions ) {
			        if(user.getId().intValue() == userPermnission.getId().intValue() && (userPermnission.getPermissions() != null && !userPermnission.getPermissions().isEmpty())) {
			        	UserPermissionDTO usr = new UserPermissionDTO();
			        	usr.setUser(user);
			        	usr.doPermissionsListToEnumList(userPermnission.getPermissions());
			        	usesrsPermissions.add(usr);
			        }
			    }
			}
			//Regla: si un usuario tiene 1 sólo permiso y es reportador no se agrega en el reporte
			List<UserPermissionDTO> usesrsPermissionsTmp = new ArrayList<>();
			for(UserPermissionDTO usrpermission : usesrsPermissions) {
				if(!(usrpermission.getPermisos().size() == 1 && usrpermission.getPermisos().get(0).equals(PermisoEnum.REPORTADOR))) {
					usesrsPermissionsTmp.add(usrpermission);
				}
			}
			usesrsPermissions = new ArrayList<>(usesrsPermissionsTmp);
		}catch(Exception e) {
			log.error("** error en getUsersPermissions()", e);
		}
		return usesrsPermissions;
	}

}
