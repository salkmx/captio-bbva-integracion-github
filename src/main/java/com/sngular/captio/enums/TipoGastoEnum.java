package com.sngular.captio.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum TipoGastoEnum {

	GASTOS(1, "Gastos"), IVAS(2, "IVAs"), OTROS_GASTOS(3, "Otros gastos"), SIN_DATO(4, "Sin dato"), TBD(5, "TBD");

	private final Integer id;
	private final String descripcion;

	private static final Map<Integer, TipoGastoEnum> BY_ID = Arrays.stream(values())
			.collect(Collectors.toMap(TipoGastoEnum::getId, Function.identity()));

	TipoGastoEnum(Integer id, String descripcion) {
		this.id = id;
		this.descripcion = descripcion;
	}

	public Integer getId() {
		return id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public static TipoGastoEnum fromId(Integer id) {
		TipoGastoEnum result = BY_ID.get(id);
		if (result == null) {
			throw new IllegalArgumentException("TipoGastoEnum inválido para id: " + id);
		}
		return result;
	}

	public static String getDescripcionById(Integer id) {
		return fromId(id).getDescripcion();
	}

	public static boolean isValido(Integer id) {
		return BY_ID.containsKey(id);
	}

	public boolean esGastoPrincipal() {
		return this == GASTOS || this == OTROS_GASTOS;
	}

}
