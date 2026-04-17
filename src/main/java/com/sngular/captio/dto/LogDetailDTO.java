package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LogDetailDTO {

	@JsonProperty("Id")
	private Integer id;

	@JsonProperty("UserId")
	private Integer userId;

	@JsonProperty("StatusId")
	private Integer statusId;

	@JsonProperty("StatusDate")
	private String statusDate;

	@JsonProperty("Comments")
	private String comments;

	@JsonProperty("StepId")
	private Integer stepId;

	@JsonProperty("ExpenseId")
	private Integer expenseId;

	@JsonProperty("DelegantUserId")
	private Integer delegantUserId;

	@JsonProperty("EventId")
	private Integer eventId;

	@JsonProperty("SourceId")
	private Integer sourceId;
}