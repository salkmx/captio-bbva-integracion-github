package com.sngular.captio.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.model.UsuarioSonar;

@Mapper(uses = { OpcionesUsuarioMapper.class })
public interface UsuarioSonarMapper {

	UsuarioSonarMapper INSTANCE = Mappers.getMapper(UsuarioSonarMapper.class);

	UsuarioSonar toEntity(UsuarioDTO dto);

	UsuarioDTO toDto(UsuarioSonar entity);

	List<UsuarioDTO> toDtoList(List<UsuarioSonar> entities);

	List<UsuarioSonar> toEntityList(List<UsuarioDTO> dtos);

}
