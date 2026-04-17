package com.sngular.captio.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacturaDTO {

	@JsonProperty("Number")
	private String number;

	@JsonProperty("Provider")
	private String provider;

	@JsonProperty("Lines")
	private List<FacturaLineaDTO> lines;

	@JsonProperty("Deductible")
	private Boolean deductible;

}
