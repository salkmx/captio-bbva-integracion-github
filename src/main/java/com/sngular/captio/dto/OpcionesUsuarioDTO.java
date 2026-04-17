package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Accessors(chain = true)
public class OpcionesUsuarioDTO {

	@JsonProperty("CostCentre")
	private String costCentre;

	@JsonProperty("EmployeeCode")
	private String employeeCode;

	@JsonProperty("CompanyCode")
	private String companyCode;

	@JsonProperty("TaxPayerId")
	private String taxPayerId;

}
