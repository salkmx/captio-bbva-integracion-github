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
public class CategoriaDTO {

	@JsonProperty("Id")
	private Integer id;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Code")
	private String code;

	@JsonProperty("Account")
	private String account;

	@JsonProperty("Active")
	private Boolean active;

	@JsonProperty("SelfLimited")
	private Boolean selfLimited;

	@JsonProperty("MaxAmount")
	private Integer maxAmount;

	@JsonProperty("OnlyKM")
	private Boolean onlyKM;

	@JsonProperty("Deleted")
	private Boolean deleted;

	@JsonProperty("OnlyIntegrations")
	private Boolean onlyIntegrations;

	@JsonProperty("SubCategories")
	private List<CategoriaDTO> subCategories;

	@JsonProperty("Languages")
	private List<LenguajeDTO> languages;

	@JsonProperty("VatPercent")
	private Double vatPercent;

	@JsonProperty("Deductible")
	private Boolean deductible;

	@JsonProperty("DeductiblePercentage")
	private Double deductiblePercentage;

	@JsonProperty("Parent")
	private CategoriaDTO parent;

}
