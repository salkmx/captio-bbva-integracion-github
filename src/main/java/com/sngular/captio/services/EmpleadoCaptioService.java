package com.sngular.captio.services;

import com.sngular.captio.dto.MetodoPagoDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.util.ParametersBuilderUtil;

import java.util.List;

public interface EmpleadoCaptioService {

    void uploadUser(List<UsuarioDTO> usuarios) throws Exception;

    void deleteUser(List<UsuarioDTO> usuarios) throws Exception;

    void updateUsers(List<UsuarioDTO> usuarios);

    void sincronizarGrupoViajes(List<UsuarioDTO> usuarios) throws Exception;

    void sincronizarGrupoKm(List<UsuarioDTO> usuarios) throws Exception;

    void sincronizarPayments(List<UsuarioDTO> usuarios) throws Exception;


}
