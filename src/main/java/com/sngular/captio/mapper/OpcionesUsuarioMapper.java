package com.sngular.captio.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.sngular.captio.dto.OpcionesUsuarioDTO;
import com.sngular.captio.model.OpcionesUsuario;

@Mapper
public interface OpcionesUsuarioMapper {

	OpcionesUsuarioMapper INSTANCE = Mappers.getMapper(OpcionesUsuarioMapper.class);

	OpcionesUsuarioDTO toDto(OpcionesUsuario opcionesUsuario);

	OpcionesUsuario toEntity(OpcionesUsuarioDTO opcionesUsuarioDTO);

}
