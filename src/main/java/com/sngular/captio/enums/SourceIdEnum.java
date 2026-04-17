package com.sngular.captio.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SourceIdEnum {

	API_INTEGRATIONS(1, "ApiIntegrations"),
	API_WEB(2, "ApiWeb");

	private final int id;
	private final String description;

	public static String getDescriptionById(Integer id) {
		if (id == null) return "";
		for (SourceIdEnum e : values()) {
			if (e.id == id) return e.description;
		}
		return String.valueOf(id);
	}
}