package com.sngular.captio.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InformeDTO {

	@JsonProperty("Id")
	private Integer id;

	@JsonProperty("ExternalId")
	private String externalId;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Code")
	private String code;

	@JsonProperty("Status")
	private Integer status;

	@JsonProperty("StatusDate")
	private LocalDateTime statusDate;

	@JsonProperty("CreationDate")
	private LocalDateTime creationDate;

	@JsonProperty("LiquidationDate")
	private LocalDateTime liquidationDate;

	@JsonProperty("ReimbursableAmount")
	private Double reimbursableAmount;

	@JsonProperty("GenerateAdvanceSettlement")
	private Boolean generateAdvanceSettlement;

	@JsonProperty("UrlKey")
	private String urlKey;

	@JsonProperty("GeneratedAdvance")
	private GeneratedAdvanceDTO generatedAdvance;

	@JsonProperty("AvailableAlerts")
	private Boolean availableAlerts;

	@JsonProperty("User")
	private UsuarioDTO user;

	@JsonProperty("Amount")
	private MontoDTO amount;

	@JsonProperty("CustomFields")
	private List<CustomFieldDTO> customFields;

	@JsonProperty("Workflow")
	private WorkFlowDTO workflow;

	@JsonProperty("StartDate")
	private LocalDateTime startDate;

	@JsonProperty("EndDate")
	private LocalDateTime endDate;

	@JsonProperty("Expenses")
	private List<GastoDTO> gastos;

	@JsonProperty("Advances")
	private List<DotacionDTO> anticipos;

	@JsonProperty("Comment")
	private String comment;

	@JsonProperty("SendEmail")
	private Boolean sendEmail;

	@JsonProperty("SkipAlertsPreview")
	private Boolean skipAlertsPreview;

}
