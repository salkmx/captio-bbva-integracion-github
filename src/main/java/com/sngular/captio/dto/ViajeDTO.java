package com.sngular.captio.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViajeDTO {

	@JsonProperty("Id")
	private Integer id;

	@JsonProperty("Reference")
	private String reference;

	@JsonProperty("Reason")
	private String reason;

	@JsonProperty("Comments")
	private String comments;

	@JsonProperty("Status")
	private Integer status;

	@JsonProperty("StatusDate")
	private LocalDateTime statusDate;

	@JsonProperty("CreationDate")
	private LocalDateTime creationDate;

	@JsonProperty("StartDate")
	private LocalDateTime startDate;

	@JsonProperty("EndDate")
	private LocalDateTime endDate;

	@JsonProperty("RejectReasonId")
	private Integer rejectReasonId;

	@JsonProperty("ExcludedOwner")
	private Boolean excludedOwner;

	@JsonProperty("User")
	private UsuarioDTO user;

	@JsonProperty("Workflow")
	private WorkFlowDTO workflow;

	@JsonProperty("CustomFields")
	private List<CustomFieldDTO> customFields;

	@JsonProperty("InternalGuests")
	private List<InternalGuestDTO> internalGuests;

	@JsonProperty("ExternalGuests")
	private List<ExternalGuestDTO> externalGuests;

	@JsonProperty("Services")
	private ServicesDTO services;

	@JsonProperty("Days")
	private long dias;

	@JsonProperty("DailyAmount")
	private double montoDiario;

	@JsonIgnore
	private boolean descartarGastosComidas;

	@JsonIgnore
	private boolean descartarTintoreria;

}
