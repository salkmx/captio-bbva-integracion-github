package com.sngular.captio.enums;

import lombok.Getter;

@Getter
public enum EstatusEmpleadoEnum {

	ACTIVO("ACT"), JUBILADO("JUB"), BAJA("BAJ");

	private String estatus;

	private EstatusEmpleadoEnum(String estatus) {
		this.estatus = estatus;
	}

}
