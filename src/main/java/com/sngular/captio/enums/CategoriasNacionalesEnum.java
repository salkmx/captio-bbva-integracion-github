package com.sngular.captio.enums;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

public enum CategoriasNacionalesEnum {

	VUELO("Vuelo"), COMIDAS("Comidas (nacional)"), HOTEL("Hotel (nacional)"), RENTA_AUTO("Renta de auto (nacional)");

	private String categoria;

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	CategoriasNacionalesEnum(String categoria) {
		this.categoria = categoria;
	}

	public static CategoriasNacionalesEnum from(String name) {
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
