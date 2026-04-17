package com.sngular.captio.enums;

public enum MonedaEnum {

	MXN("MXN", 66);

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	private String codigo;

	private Integer id;

	MonedaEnum(String codigo, Integer id) {
		this.codigo = codigo;
		this.id = id;
	}

}
