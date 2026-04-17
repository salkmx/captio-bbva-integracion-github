package com.sngular.captio.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConceptoDTO {

	public String claveProdServ;
	public String descripcion;
	public BigDecimal cantidad;
	public BigDecimal valorUnitario;
	public BigDecimal importe;

}
