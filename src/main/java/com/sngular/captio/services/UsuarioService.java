package com.sngular.captio.services;

import java.util.List;

import com.sngular.captio.dto.MetodoPagoDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.util.ParametersBuilderUtil;

public interface UsuarioService {

	void eliminarUsuarios();

	void altaUsuario(List<UsuarioDTO> usuarios) throws Exception;

	void updateUsuarios(List<UsuarioDTO> usuarios) throws Exception;

	void bajaUsuario(List<UsuarioDTO> usuarios) throws Exception;

	boolean existeUsuario(UsuarioDTO usuario) throws Exception;

	List<UsuarioDTO> obtenerUsuarioByFiltro(String filtro) throws Exception;

	void joinWorkFlow(List<UsuarioDTO> usuarios) throws Exception;

	void unjoinWorkFlow(List<UsuarioDTO> usuarios) throws Exception;

	void joinGroup(List<UsuarioDTO> usuarios) throws Exception;

	List<MetodoPagoDTO> obtenerMetodoPago(String filtro) throws Exception;

	UsuarioDTO obtenerFlujoUsuarioByFiltro(String filtros) throws Exception;
	
	List<UsuarioDTO> obtenerUsuarios()throws Exception ;

	/**
	 * Obtiene todos los usuarios de Captio con sus workflows asignados.
	 * Útil para construir un mapa completo de workflows actuales.
	 */
	List<UsuarioDTO> obtenerTodosUsuariosConWorkflows() throws Exception;

	void sincronizarPayments(List<UsuarioDTO> usuarios) throws Exception;

	UsuarioDTO obtenerUsuarioConGrupos(Integer userId) throws Exception;

	void sincronizarGrupoViajes(List<UsuarioDTO> usuarios) throws Exception;

	void sincronizarGrupoKm(List<UsuarioDTO> usuarios) throws Exception;

	List<UsuarioDTO> getUsersPermissions(ParametersBuilderUtil params);

}
