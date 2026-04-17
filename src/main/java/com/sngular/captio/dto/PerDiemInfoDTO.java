package com.sngular.captio.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerDiemInfoDTO {

	@JsonProperty("StartDate")
	private LocalDateTime startDate;

	@JsonProperty("EndDate")
	private LocalDateTime endDate;

	@JsonProperty("PolicyId")
	private Integer policyId;

	@JsonProperty("DestinationId")
	private Integer destinationId;

}
