package com.sngular.captio.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.model.UsuarioAmex;
import com.sngular.captio.model.UsuarioAmexNew;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioAmexNewMapper {

	UsuarioAmexNewMapper INSTANCE = Mappers.getMapper(UsuarioAmexNewMapper.class);

	UsuarioAmexNew toEntity(UsuarioDTO dto);

	UsuarioDTO toDto(UsuarioAmexNew dto);

	List<UsuarioAmex> toEntityList(List<UsuarioDTO> dtos);

	List<UsuarioDTO> toDtoList(List<UsuarioAmexNew> entities);

}
