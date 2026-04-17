package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonedaDTO {

	@JsonProperty("Id")
	private Integer id;

	@JsonProperty("CurrencyId")
	private Integer currencyId;

	@JsonProperty("Code")
	private String code;

	@JsonProperty("Symbol")
	private String symbol;

	@JsonProperty("ISOCode")
	private String isoCode;

	@JsonProperty("Amount")
	private double monto;

}
