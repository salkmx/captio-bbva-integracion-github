package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DescuentoNominaDTO {

	@JsonProperty("EmployeeCode")
	private String employeeCode;

	@JsonProperty("EmployeeEmail")
	private String employeeEmail;

	@JsonProperty("TravelStartDate")
	private LocalDateTime travelStartDate;

	@JsonProperty("TravelEndDate")
	private LocalDateTime travelEndDate;

	@JsonProperty("TotalAmountExpenses")
	private Double totalAmountExpenses;

	@JsonProperty("TotalAmountAdvances")
	private Double totalAmountAdvances;

	@JsonProperty("AmountRefund")
	private Double amountRefund;

	@JsonProperty("AmountDebt")
	private Double amountDebt;

	@JsonProperty("IdEmployee")
	private Integer idEmployee;

	@JsonProperty("TravelName")
	private String travelName;

	@JsonProperty("ExternalId")
	private String externalId;

	@JsonProperty("GeneratedAdvance")
	private boolean generatedAdvance;

}
