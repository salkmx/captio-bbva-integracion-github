package com.sngular.captio.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CfdiResumenDTO {

	public String version;
	public String serie;
	public String folio;
	public String fecha;

	public String emisorRfc;
	public String emisorNombre;
	public String receptorRfc;
	public String receptorNombre;
	public String usoCfdi;

	public String moneda;
	public BigDecimal subTotal;
	public BigDecimal total;
	public BigDecimal totalImpuestosTrasladados;

	public String uuid;

	public List<ConceptoDTO> conceptos;

	public BigDecimal tasa;
	public BigDecimal descuento;
	
    @JsonIgnore
	private boolean existePDF;
	
    @JsonIgnore
	private boolean existeXml;

}
