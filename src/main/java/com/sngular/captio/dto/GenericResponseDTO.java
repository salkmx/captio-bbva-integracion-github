package com.sngular.captio.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericResponseDTO<T> {

	@JsonProperty("Result")
	private T result;

	@JsonProperty("Key")
	private String key;

	@JsonProperty("Value")
	private String value;

	@JsonProperty("Validations")
	private List<ValidationDTO> validations;

	@JsonProperty("Status")
	private Integer status;

}
