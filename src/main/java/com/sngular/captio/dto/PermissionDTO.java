package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * clase para los permisos de usuario.
 */
@Data
public class PermissionDTO {
	@JsonProperty("Id")
	private int idPermiso;
}
