package com.sngular.captio.mapper;

import com.sngular.captio.dto.OpcionesUsuarioDTO;
import com.sngular.captio.model.OpcionesEmpleado;
import com.sngular.captio.model.OpcionesUsuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OpcionesEmpleadoMapper {

	OpcionesEmpleadoMapper INSTANCE = Mappers.getMapper(OpcionesEmpleadoMapper.class);

	OpcionesUsuarioDTO toDto(OpcionesEmpleado opcionesEmpleado);

    OpcionesEmpleado toEntity(OpcionesUsuarioDTO opcionesUsuarioDTO);

}
