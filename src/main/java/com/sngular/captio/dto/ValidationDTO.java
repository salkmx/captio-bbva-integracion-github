package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationDTO {

	@JsonProperty("Message")
	private String message;

	@JsonProperty("Code")
	private String code;

	@JsonProperty("Key")
	private String key;

	@JsonProperty("Value")
	private String value;

}
