package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServicesDTO {

	@JsonProperty("Flight")
	private Boolean flight;

	@JsonProperty("Train")
	private Boolean train;

	@JsonProperty("Hotel")
	private Boolean hotel;

	@JsonProperty("Vehicle")
	private Boolean vehicle;

	@JsonProperty("Ship")
	private Boolean ship;

	@JsonProperty("Other")
	private Boolean other;
}
