package com.sngular.captio.enums;

public enum ResultadoAPIEnum {

	CORRECTO(0), ERROR(1);

	private Integer estatus;

	public Integer getEstatus() {
		return estatus;
	}

	public void setEstatus(Integer estatus) {
		this.estatus = estatus;
	}

	ResultadoAPIEnum(Integer estatus) {
		this.estatus = estatus;
	}

}
