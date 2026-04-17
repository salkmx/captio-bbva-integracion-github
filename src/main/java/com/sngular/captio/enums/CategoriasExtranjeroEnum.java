package com.sngular.captio.enums;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

public enum CategoriasExtranjeroEnum {

	VUELO("Vuelo (extranjero)"), COMIDAS("Comidas (extranjero)"), HOTEL("Hotel (extranjero)"),
	RENTA_AUTO("Renta de auto (extranjero)");

	private String categoria;

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	CategoriasExtranjeroEnum(String categoria) {
		this.categoria = categoria;
	}

	public static CategoriasExtranjeroEnum from(String name) {
		String n = norm(name);
		return Arrays.stream(values()).filter(e -> norm(e.categoria).equals(n)).findFirst().orElse(null);
	}

	private static String norm(String s) {
		if (s == null)
			return "";
		String x = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
		return x.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
	}

}
