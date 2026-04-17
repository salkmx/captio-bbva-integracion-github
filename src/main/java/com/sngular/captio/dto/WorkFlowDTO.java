package com.sngular.captio.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkFlowDTO {

	@JsonProperty("Id")
	private Integer id;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Type")
	private Integer type;

	@JsonProperty("TypeActivationStep")
	private Integer typeActivationStep;

	@JsonProperty("RequesterDigitalSignatureRequired")
	private Boolean requesterDigitalSignatureRequired;

	@JsonProperty("CustomFields")
	private List<CustomFieldDTO> customFields;

	@JsonProperty("Steps")
	private List<StepDTO> steps;

	@JsonProperty("Default")
	private Boolean defaulte;

}
