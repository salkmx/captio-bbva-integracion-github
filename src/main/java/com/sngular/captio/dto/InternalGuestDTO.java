package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternalGuestDTO {

	@JsonProperty("Id")
	private Integer id;

}
