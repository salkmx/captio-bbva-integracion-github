/**
 * 
 */
package com.sngular.captio.services;

import java.util.List;

import com.sngular.captio.dto.UserPermissionDTO;

/**
 * Servicio para reportes - rmiranda
 */
public interface ReporteUsuarioPermisosService {
	
	/**
	 * Obtine los usuarios y sus permisos
	 * @return
	 */
	public List<UserPermissionDTO> getUsersPermissions();
}
