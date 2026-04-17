package com.sngular.captio.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.sngular.captio.dto.CuentaContableDTO;
import com.sngular.captio.model.CuentaContable;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CuentaContableMapper {

	CuentaContableDTO toDto(CuentaContable entity);

	List<CuentaContableDTO> toDtoList(List<CuentaContable> entities);

	CuentaContable toEntity(CuentaContableDTO dto);

	List<CuentaContable> toEntityList(List<CuentaContableDTO> dtos);

}
