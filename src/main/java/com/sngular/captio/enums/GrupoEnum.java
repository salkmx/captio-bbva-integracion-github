package com.sngular.captio.enums;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum GrupoEnum {

	GENERAL(28, "General"), ASESORES_INVERSION(38, "Asesores de inversión"),
	DIR_BCA_PATRIMONIAL_Y_PRIVADA(34, "Dir. Bca Patrimonial y Privada"), DIR_DIVISIONAL(31, "Dir. Divisional"),
	DIR_REGIONAL(33, "Dir. Regional"), DIR_ZONA(32, "Dir. Zona"),
	EJECUTIVOS_CASH_MANAGEMENT(39, "Ejecutivos cash management"),
	EJECUTIVOS_BCA_ELECTRONICA(40, "Ejecutivos de Bca. electrónica"), EJECUTIVOS_BEYG(35, "Ejecutivos de BEyG"),
	EJECUTIVOS_CIB(36, "Ejecutivos de CIB"), EJECUTIVOS_PYME(37, "Ejecutivos PyME"),
	PERSONAL_RETAIL(45, "Personal de Retail"),
	NIVEL_2_O_INFERIOR(30, "Nivel 2 o inferior"), NIVEL_3_O_SUPERIOR(29, "Nivel 3 o superior");

	private final int id;
	private final String nombre;

	GrupoEnum(int id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}

	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	private static final Map<Integer, GrupoEnum> BY_ID = Arrays.stream(values())
			.collect(Collectors.toMap(GrupoEnum::getId, Function.identity()));

	private static final Map<String, GrupoEnum> BY_NOMBRE = Arrays.stream(values())
			.collect(Collectors.toMap(g -> normalizar(g.getNombre()), Function.identity()));

	private static String normalizar(String s) {
		return s == null ? null : s.trim().toUpperCase(Locale.ROOT);
	}

	public static GrupoEnum fromId(Integer id) {
		if (id == null)
			return null;
		return BY_ID.get(id);
	}

	public static GrupoEnum fromNombre(String nombre) {
		if (nombre == null)
			return null;
		return BY_NOMBRE.get(normalizar(nombre));
	}

	public static boolean esIdValido(Integer id) {
		return fromId(id) != null;
	}

	public static boolean esNombreValido(String nombre) {
		return fromNombre(nombre) != null;
	}

	public boolean esId(Integer id) {
		return id != null && this.id == id;
	}

	public boolean esNombre(String nombre) {
		return nombre != null && normalizar(this.nombre).equals(normalizar(nombre));
	}
}
