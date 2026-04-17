package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseTokenDTO {

	@JsonProperty("access_token")
	private String token;

	@JsonProperty("expires_in")
	private int expiracion;

	@JsonProperty("token_type")
	private String tipo;

}
