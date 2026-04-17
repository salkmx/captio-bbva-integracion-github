package com.sngular.captio.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.model.UsuarioAmex;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioAmexMapper {

	UsuarioAmexMapper INSTANCE = Mappers.getMapper(UsuarioAmexMapper.class);

	UsuarioAmex toEntity(UsuarioDTO dto);

	UsuarioDTO toDto(UsuarioAmex dto);

	List<UsuarioAmex> toEntityList(List<UsuarioDTO> dtos);

	List<UsuarioDTO> toDtoList(List<UsuarioAmex> entities);

}
