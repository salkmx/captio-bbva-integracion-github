package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalGuestDTO {

	@JsonProperty("Email")
	private String email;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Surname")
	private String surname;
}
