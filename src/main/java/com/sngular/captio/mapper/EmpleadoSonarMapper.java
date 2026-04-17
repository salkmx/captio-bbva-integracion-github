package com.sngular.captio.mapper;

import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.model.EmpleadoSonar;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = { OpcionesEmpleadoMapper.class })
public interface EmpleadoSonarMapper {

	EmpleadoSonarMapper INSTANCE = Mappers.getMapper(EmpleadoSonarMapper.class);

	EmpleadoSonar toEntity(UsuarioDTO dto);

	UsuarioDTO toDto(EmpleadoSonar entity);

	List<UsuarioDTO> toDtoList(List<EmpleadoSonar> entities);

	List<EmpleadoSonar> toEntityList(List<UsuarioDTO> dtoList);

}
