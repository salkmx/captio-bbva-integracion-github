package com.sngular.captio.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjetosUtils {

	private ObjetosUtils() {

	}

	public static <T> void limpiarCamposExcepto(T dto, List<String> camposQueSeConservan) {
		if (dto == null) {
			return;
		}

		Set<String> camposPermitidos = new HashSet<>(camposQueSeConservan);
		Class<?> clazz = dto.getClass();

		for (Field field : clazz.getDeclaredFields()) {
			String nombreCampo = field.getName();

			if (camposPermitidos.contains(nombreCampo)) {
				continue;
			}

			try {
				String setterName = "set" + Character.toUpperCase(nombreCampo.charAt(0)) + nombreCampo.substring(1);

				Method setter = clazz.getMethod(setterName, field.getType());
				setter.invoke(dto, (Object) null);

			} catch (NoSuchMethodException e) {
				log.error("No existe setter para " + nombreCampo + " en " + clazz.getSimpleName());
			} catch (Exception e) {
				log.error("Error limpiando campo " + nombreCampo + ": " + e.getMessage());
			}
		}
	}

	public static <T> void limpiarCamposExcepto(List<T> listaDtos, List<String> camposQueSeConservan) {
		if (listaDtos == null || listaDtos.isEmpty()) {
			return;
		}

		Set<String> camposPermitidos = new HashSet<>(camposQueSeConservan);

		for (T dto : listaDtos) {
			if (dto == null)
				continue;

			Class<?> clazz = dto.getClass();

			for (Field field : clazz.getDeclaredFields()) {
				String nombreCampo = field.getName();

				if (camposPermitidos.contains(nombreCampo)) {
					continue;
				}

				try {
					String setterName = "set" + Character.toUpperCase(nombreCampo.charAt(0)) + nombreCampo.substring(1);
					Method setter = clazz.getMethod(setterName, field.getType());

					setter.invoke(dto, (Object) null);

				} catch (NoSuchMethodException e) {
					log.error("No existe setter para " + nombreCampo + " en " + clazz.getSimpleName());
				} catch (Exception e) {
					log.error("Error limpiando campo " + nombreCampo + ": " + e.getMessage());
				}
			}
		}
	}

}
