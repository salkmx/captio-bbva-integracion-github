package com.sngular.captio.enums;

import java.math.BigDecimal;

public enum LimiteDireccionEnum {

	DIVISIONAL("Dir. Divisional", new BigDecimal("2480.00")), ZONA("Dir. Zona", new BigDecimal("1030.00")),
	REGIONAL("Dir. Regional", new BigDecimal("1960.00")),
	BCA_PATRIMONIAL_PRIVADA("Dir. Bca Patrimonial y Privada", new BigDecimal("1960.00"));

	private final String nombre;
	private final BigDecimal limite;

	LimiteDireccionEnum(String nombre, BigDecimal limite) {
		this.nombre = nombre;
		this.limite = limite;
	}

	public String getNombre() {
		return nombre;
	}

	public BigDecimal getLimite() {
		return limite;
	}

	public static LimiteDireccionEnum fromNombre(String nombre) {
		for (LimiteDireccionEnum d : values()) {
			if (d.getNombre().equalsIgnoreCase(nombre)) {
				return d;
			}
		}
		return null;
	}

}
