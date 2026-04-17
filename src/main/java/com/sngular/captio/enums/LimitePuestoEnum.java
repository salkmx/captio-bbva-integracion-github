package com.sngular.captio.enums;

import java.math.BigDecimal;

public enum LimitePuestoEnum {

	EJECUTIVOS_BEYG("Ejecutivos de BEyG", new BigDecimal("1960.00")),
	EJECUTIVOS_CIB("Ejecutivos de CIB", new BigDecimal("1960.00")),
	EJECUTIVOS_PYME("Ejecutivos PyME", new BigDecimal("1960.00")),
	ASESORES_INVERSION("Asesores de inversión", new BigDecimal("1960.00")),

	EJECUTIVOS_CASH_MANAGEMENT("Ejecutivos cash management", new BigDecimal("1800.00")),
	EJECUTIVOS_BCA_ELECTRONICA("Ejecutivos de Bca electrónica", new BigDecimal("1800.00"));

	private final String nombre;
	private final BigDecimal limite;

	LimitePuestoEnum(String nombre, BigDecimal limite) {
		this.nombre = nombre;
		this.limite = limite;
	}

	public String getNombre() {
		return nombre;
	}

	public BigDecimal getLimite() {
		return limite;
	}

	public static LimitePuestoEnum fromNombre(String nombre) {
		for (LimitePuestoEnum p : values()) {
			if (p.getNombre().equalsIgnoreCase(nombre)) {
				return p;
			}
		}
		return null;
	}

}
