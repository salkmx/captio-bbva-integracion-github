package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LogsDTO {

	@JsonProperty("LogContent")
	private String logContent;

	@JsonProperty("LogFileName")
	private String logFileName;

	@JsonProperty("SendTo")
	private String sendTo;

	@JsonProperty("TaskName")
	private String taskName;

	@JsonProperty("LogStatus")
	private Integer logStatus;

	@JsonProperty("LanguageCode")
	private String languageCode;

}