package com.sngular.captio.mapper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class GenericFieldSetMapper implements FieldSetMapper<Map<String, String>> {

	@Override
	public Map<String, String> mapFieldSet(FieldSet fieldSet) throws BindException {
		Map<String, String> resultado = new LinkedHashMap<>();
		int cantidadCampos = fieldSet.getFieldCount();

		for (int i = 0; i < cantidadCampos; i++) {
			resultado.put("columna" + i, fieldSet.readString(i));
		}
		return resultado;
	}

}
