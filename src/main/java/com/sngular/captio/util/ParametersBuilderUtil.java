/**
 * 
 */
package com.sngular.captio.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase para armar los parámetros que se envían a los servicios mediante la URL
 * @author Rafael Miranda - rmiranda
 * @since 31/12/2025
 */
@Slf4j
public class ParametersBuilderUtil{
	//The filter nane
	private String filters;
	//The objet map to stringify
	private ObjectMapper mapper;
	//The param
	Map<String, Object> params;
	
	/**
	 * empty constructor
	 */
	public ParametersBuilderUtil() {
		params = new HashMap<>();
		filters = "filters";
		mapper = new ObjectMapper();
	}
	public String getFormattedParams() {
		String paramAsJson = null;
		try {
			paramAsJson = mapper.writeValueAsString(params);
		} catch (JsonProcessingException e) {
			log.error("Error in getFormattedParams()", e);
		}
		return paramAsJson;
	}
	
	public void addParameter(String name, String value) {
		params.put(name, value);
	}
	
	public void addParameter(String name, Integer value) {
		params.put(name, value);
	}
	
	public String getFilterName() {
		return filters;
	}
}
