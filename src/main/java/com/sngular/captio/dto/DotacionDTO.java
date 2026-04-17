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
public class DotacionDTO {

	@JsonProperty("UserId")
	private Integer userId;

	@JsonProperty("Reason")
	private String reason;

	@JsonProperty("Comment")
	private String comment;

	@JsonProperty("WorkflowId")
	private Integer workflowId;

	@JsonProperty("Currencies")
	private List<MonedaDTO> currencies;

	@JsonProperty("Travel")
	private ViajeDTO travel;

	@JsonProperty("CustomFields")
	private List<CustomFieldDTO> customFields;

	private String loginUser;

	@JsonProperty("Id")
	private Integer id;

	@JsonProperty("User")
	private UsuarioDTO usuario;

	@JsonProperty("RequestedDate")
	private LocalDateTime requestedDate;

	@JsonProperty("DeliveryDate")
	private String deliveryDate;

}
