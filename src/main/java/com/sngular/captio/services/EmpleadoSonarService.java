package com.sngular.captio.services;

import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.model.EmpleadoSonar;

import java.util.List;

public interface EmpleadoSonarService extends IGenericService<EmpleadoSonar, UsuarioDTO, Long> {

//    void saveAll(List<EmpleadoSonar> entities);

    List<UsuarioDTO> findAll();

    EmpleadoSonar findOneByEmail(String email);

    EmpleadoSonar findOneByEmployeeCode(String employeeCode);

}
