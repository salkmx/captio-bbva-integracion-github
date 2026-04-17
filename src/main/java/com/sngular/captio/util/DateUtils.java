package com.sngular.captio.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {

	private static final String FORMATO_FECHA_CAPTIO = "yyyy-MM-dd'T'HH:mm:ss";

	private DateUtils() {

	}

	public static String obtenerFechaActual() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		return LocalDateTime.now().format(formatter);

	}

	public static String obtenerFechaActualLocalDateTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATO_FECHA_CAPTIO);
		return LocalDateTime.now().minusDays(1).format(formatter);

	}

//	public static String obtenerFechaActualFormatoCaptio() {
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATO_FECHA_CAPTIO);
//		return LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).format(formatter);
//
//	}
	
	public static String obtenerFechaActualFormatoCaptio() {
		return "2025-06-01T00:00:00";
	}

	public static String obtenerFechaDDMMYYYY(LocalDateTime fecha) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return fecha.format(formatter);

	}

	public static String obtenerFechaInicialServicios(LocalDateTime fecha) {
		LocalDateTime nuevaFecha = fecha.withHour(0).withMinute(0).withSecond(0);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATO_FECHA_CAPTIO);
		return nuevaFecha.format(formatter);

	}

	public static String obtenerFechaFinalServicios(LocalDateTime fecha) {

		LocalDateTime nuevaFecha = fecha.withHour(23).withMinute(59).withSecond(59);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATO_FECHA_CAPTIO);
		return nuevaFecha.format(formatter);

	}

	public static Long diasInclusivos(LocalDateTime inicio, LocalDateTime fin) {
		long diff = ChronoUnit.DAYS.between(inicio.toLocalDate(), fin.toLocalDate());
		if (diff < 0) {
			throw new IllegalArgumentException("fin no puede ser anterior a inicio");
		}
		return diff + 1;
	}

}
