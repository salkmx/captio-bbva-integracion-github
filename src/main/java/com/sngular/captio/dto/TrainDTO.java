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
public class TrainDTO {

	@JsonProperty("Id")
	private Long id;

	@JsonProperty("StartDate")
	private LocalDateTime startDate;

	@JsonProperty("EndDate")
	private LocalDateTime endDate;

	@JsonProperty("StartMinHour")
	private String startMinHour;

	@JsonProperty("StartMaxHour")
	private String startMaxHour;

	@JsonProperty("Departure")
	private String departure;

	@JsonProperty("Destination")
	private String destination;

	@JsonProperty("Type")
	private Integer type;

}
