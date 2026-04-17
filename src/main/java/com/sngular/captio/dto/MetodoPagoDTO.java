package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class MetodoPagoDTO {

	@JsonProperty("Id")
	private Integer id;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("IsReimbursable")
	private Boolean isReimbursable;

	@JsonProperty("IsReconcilable")
	private Boolean isReconcilable;

	@JsonProperty("PaymentId")
	private int paymentId;

	@JsonProperty("Value")
	private String value;

	@JsonProperty("IdentifierType")
	private int identifierType;

	@JsonProperty("PaymentCardToken")
	private String paymentCardToken;

}
