package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdjuntoDTO {

	@JsonProperty("UrlKey")
	private String urlKey;

	@JsonProperty("FileName")
	private String fileName;

	@JsonProperty("Url")
	private String url;

}
