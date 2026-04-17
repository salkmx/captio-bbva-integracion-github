package com.sngular.captio.enums;

import java.util.Arrays;

public enum EstatusInformeEnum {

	BORRADOR(1, "Borrador"), APROBACION_SOLICITADA(2, "Aprobación solicitada"), RECHAZADO(3, "Rechazado"),
	APROBADO(4, "Aprobado");

	private final Integer id;
	private final String descripcion;

	EstatusInformeEnum(Integer id, String descripcion) {
		this.id = id;
		this.descripcion = descripcion;
	}

	public Integer getId() {
		return id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public static EstatusInformeEnum fromId(Integer id) {
		return Arrays.stream(values()).filter(e -> e.id.equals(id)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("EstatusInformeEnum inválido para id: " + id));
	}

	public static String getDescripcionById(Integer id) {
		return fromId(id).getDescripcion();
	}

	public static boolean isValido(Integer id) {
		return Arrays.stream(values()).anyMatch(e -> e.id.equals(id));
	}

	public boolean esFinal() {
		return this == APROBADO || this == RECHAZADO;
	}

	public boolean esEditable() {
		return this == BORRADOR || this == RECHAZADO;
	}

}
