package com.sngular.captio.enums;

public enum MetodoPagoEnum {

	TARJETA_EMPRESARIAL(16), EFECTIVO(17), AGENCIA_VIAJES(18), PROPINA_COMIDA(86);

	private Integer idMetodo;

	public Integer getIdMetodo() {
		return idMetodo;
	}

	public void setIdMetodo(Integer idMetodo) {
		this.idMetodo = idMetodo;
	}

	MetodoPagoEnum(Integer idMetodo) {
		this.idMetodo = idMetodo;
	}

}
