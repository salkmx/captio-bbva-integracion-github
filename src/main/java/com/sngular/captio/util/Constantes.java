package com.sngular.captio.util;

import java.util.regex.Pattern;

public class Constantes {

	private Constantes() {

	}

	public static final Pattern FORMACION = Pattern.compile("\\bformacion\\b");

	public static final Pattern CAPACITACION = Pattern.compile("\\bcapacitacion\\b");

	public static final Pattern P_NACIONAL = Pattern.compile("\\bNacional\\b",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);

}
