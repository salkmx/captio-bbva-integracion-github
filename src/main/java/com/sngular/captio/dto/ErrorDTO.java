package com.sngular.captio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDTO {

	private Integer id;

	private String descripcion;

	private Integer idObjeto;
	
	private String descripcionObjeto;

	@Override
	public String toString() {
		return "descripcion=" + descripcion + ", Datos del gasto=" + descripcionObjeto;
	}

}
