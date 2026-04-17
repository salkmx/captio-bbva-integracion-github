package com.sngular.captio.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InformeLogDTO {

	@JsonProperty("Id")
	private Integer id;

	@JsonProperty("ExternalId")
	private String externalId;

	@JsonProperty("Status")
	private Integer status;

	@JsonProperty("StatusDate")
	private String statusDate;

	@JsonProperty("Logs")
	private List<LogDetailDTO> logs;
}