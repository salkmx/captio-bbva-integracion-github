package com.sngular.captio.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StepDTO {

	@JsonProperty("Id")
	private Integer id;

    @JsonProperty("Name")
    private String name;

	@JsonProperty("Languages")
	private List<LenguajeDTO> languages;

	@JsonProperty("MaxValue")
	private Integer maxValue;

	@JsonProperty("SupervisorId")
	private Integer supervisorId;

	@JsonProperty("ExternalValidation")
	private Boolean externalValidation;

	@JsonProperty("RejectAction")
	private Integer rejectAction;

	@JsonProperty("RejectStepPosition")
	private Integer rejectStepPosition;

	@JsonProperty("RejectReasonRequired")
	private Boolean rejectReasonRequired;

	@JsonProperty("Permissions")
	private PermisoDTO permissions;

	@JsonProperty("ActiveAllAlerts")
	private Boolean activeAllAlerts;

	@JsonProperty("DigitalSignatureRequired")
	private Boolean digitalSignatureRequired;

}
