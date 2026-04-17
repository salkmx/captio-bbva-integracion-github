package com.sngular.captio.enums;

public enum TintoreriaEnum {

	INTERVALODIASUNO(8), INTERVALODIASDOS(16);

	public Integer getDias() {
		return dias;
	}

	public void setDias(Integer dias) {
		this.dias = dias;
	}

	private Integer dias;

	TintoreriaEnum(Integer dias) {
		this.dias = dias;
	}

}
