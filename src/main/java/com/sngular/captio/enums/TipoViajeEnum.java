package com.sngular.captio.enums;

public enum TipoViajeEnum {

	NACIONAL(3, "Nacional"), EXTRANJERO(4, "Extranjero");

	public Integer getClave() {
		return clave;
	}

	public String getDescripcion() {
		return descripcion;
	}

	private final Integer clave;
	private final String descripcion;

	TipoViajeEnum(Integer clave, String descripcion) {
		this.clave = clave;
		this.descripcion = descripcion;
	}

}
