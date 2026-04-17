package com.sngular.captio.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.sngular.captio.dto.CustomFieldDTO;
import com.sngular.captio.enums.CustomFieldsEnum;

public class CustomFieldsUtils {

	private CustomFieldsUtils() {

	}

	public static CustomFieldDTO buscarPorId(List<CustomFieldDTO> customFields, Integer idBuscado) {
		if (customFields == null || idBuscado == null) {
			return null;
		}

		return customFields.stream().filter(cf -> idBuscado.equals(cf.getId())).findFirst().orElse(null);
	}

	public static CustomFieldDTO buscarPorValor(List<CustomFieldDTO> customFields, String valor) {
		if (customFields == null || valor == null) {
			return null;
		}

		return customFields.stream().filter(cf -> valor.equals(cf.getValue())).findFirst().orElse(null);
	}

	public static CustomFieldDTO buscarPropina(List<CustomFieldDTO> customFields) {

		if (customFields == null || customFields.isEmpty()) {
			return null;
		}

		Set<Integer> idsPropina = Arrays.stream(CustomFieldsEnum.values()).filter(e -> e.name().startsWith("PROPINA"))
				.map(CustomFieldsEnum::getIdCustomField).collect(Collectors.toSet());

		return customFields.stream().filter(Objects::nonNull)
				.filter(cf -> cf.getId() != null && idsPropina.contains(cf.getId())).findFirst().orElse(null);
	}

}
