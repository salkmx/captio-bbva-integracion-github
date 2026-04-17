package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiotDTO {

	@JsonProperty("ReportId")
	private Integer reportId;

	@JsonProperty("ExpenseId")
	private Integer expenseId;

	@JsonProperty("AttachmentDTO")
	private AdjuntoDTO attachmentDTO;

	@JsonProperty("FileName")
	private String fileName;

	@JsonProperty("UrlFile")
	private String urlFile;

	@JsonProperty("TasaImpuesto")
	private BigDecimal tasaImpuesto;


	// Datos de Tercero Declarado

	@JsonProperty("TipoTerceroCodigo")
	private String tipoTerceroCodigo;

	@JsonProperty("TipoOperacionCodigo")
	private String tipoOperacionCodigo;

	@JsonProperty("RFC")
	private String rfc;

	@JsonProperty("IdExtranjero")
	private String idExtranjero;

	@JsonProperty("NombreExtranjero")
	private String nombreExtranjero;

	@JsonProperty("CodigoPais")
	private String codigoPais;

	@JsonProperty("CodigoPaisFiscal")
	private String codigoPaisFiscal;

	// Valor de Actos o Servicios 8% & 16%

	@JsonProperty("ValorActosServiciosTasa8_RFN")
	private BigDecimal valorActosServiciosTasa8_RFN;

	@JsonProperty("DescuentoActosServiciosTasa8_RFN")
	private BigDecimal descuentoActosServiciosTasa8_RFN;

	@JsonProperty("ValorActosServiciosTasa8_RFS")
	private BigDecimal valorActosServiciosTasa8_RFS;

	@JsonProperty("DescuentoActosServiciosTasa8_RFS")
	private BigDecimal descuentoActosServiciosTasa8_RFS;

	@JsonProperty("ValorActosServiciosTasa16")
	private BigDecimal valorActosServiciosTasa16;

	@JsonProperty("DescuentoActosServiciosTasa16")
	private BigDecimal descuentoActosServiciosTasa16;

	@JsonProperty("ImportancionesTangiblesTasa16")
	private BigDecimal importancionesTangiblesTasa16;

	@JsonProperty("DescuentoImportancionesTangiblesTasa16")
	private BigDecimal descuentoImportancionesTangiblesTasa16;

	@JsonProperty("ImportancionesIntangiblesTasa16")
	private BigDecimal importancionesIntangiblesTasa16;

	@JsonProperty("DescuentoImportancionesIntangiblesTasa16")
	private BigDecimal descuentoImportancionesIntangiblesTasa16;


	// IVA Actos y Servicios 8% & 16%

	@JsonProperty("IVAPagadoAcreditableTasa8_RFN")
	private BigDecimal ivaPagadoAcreditableTasa8_RFN;

	@JsonProperty("IVAPagadoAcreditableProporcionTasa8_RFN")
	private BigDecimal ivaPagadoAcreditableProporcionTasa8_RFN;

	@JsonProperty("IVAPagadoAcreditableTasa8_RFS")
	private BigDecimal ivaPagadoAcreditableTasa8_RFS;

	@JsonProperty("IVAPagadoAcreditableProporcionTasa8_RFS")
	private BigDecimal ivaPagadoAcreditableProporcionTasa8_RFS;

	@JsonProperty("IVAPagadoAcreditableTasa16")
	private BigDecimal ivaPagadoAcreditableTasa16;

	@JsonProperty("IVAPagadoAcreditableProporcionTasa16")
	private BigDecimal ivaPagadoAcreditableProporcionTasa16;

	@JsonProperty("IVAPagadoAcreditableImportancionTangibleTasa16")
	private BigDecimal ivaPagadoAcreditableImportancionTangibleTasa16;

	@JsonProperty("IVAPagadoAcreditableProporcionImportacionTangibleTasa16")
	private BigDecimal ivaPagadoAcreditableProporcionImportacionTangibleTasa16;

	@JsonProperty("IVAPagadoAcreditableImportancionIntangibleTasa16")
	private BigDecimal ivaPagadoAcreditableImportancionIntangibleTasa16;

	@JsonProperty("IVAPagadoAcreditableProporcionImportacionIntangibleTasa16")
	private BigDecimal ivaPagadoAcreditableProporcionImportacionIntangibleTasa16;


	// IVA NO Acreditable Actos y Servicios 8% & 16%

	@JsonProperty("IVAPagadoNOAcreditableProporcionalTasa8_RFN")
	private BigDecimal ivaPagadoNOAcreditableProporcionalTasa8_RFN;

	@JsonProperty("IVAPagadoNOAcreditableTasa8_RFN")
	private BigDecimal ivaPagadoNOAcreditableTasa8_RFN;

	@JsonProperty("IVAPagadoNOAcreditableExentoTasa8_RFN")
	private BigDecimal ivaPagadoNOAcreditableExentoTasa8_RFN;

	@JsonProperty("IVAPagadoNOAcreditableNoObjetoTasa8_RFN")
	private BigDecimal ivaPagadoNOAcreditableNoObjetoTasa8_RFN;

	@JsonProperty("IVAPagadoNOAcreditableProporcionalTasa8_RFS")
	private BigDecimal ivaPagadoNOAcreditableProporcionalTasa8_RFS;

	@JsonProperty("IVAPagadoNOAcreditableTasa8_RFS")
	private BigDecimal ivaPagadoNOAcreditableTasa8_RFS;

	@JsonProperty("IVAPagadoNOAcreditableExentoTasa8_RFS")
	private BigDecimal ivaPagadoNOAcreditableExentoTasa8_RFS;

	@JsonProperty("IVAPagadoNOAcreditableNoObjetoTasa8_RFS")
	private BigDecimal ivaPagadoNOAcreditableNoObjetoTasa8_RFS;

	@JsonProperty("IVAPagadoNOAcreditableProporcionalTasa16")
	private BigDecimal ivaPagadoNOAcreditableProporcionalTasa16;

	@JsonProperty("IVAPagadoNOAcreditableTasa16")
	private BigDecimal ivaPagadoNOAcreditableTasa16;

	@JsonProperty("IVAPagadoNOAcreditableExentoTasa16")
	private BigDecimal ivaPagadoNOAcreditableExentoTasa16;

	@JsonProperty("IVAPagadoNOAcreditableNoObjetoTasa16")
	private BigDecimal ivaPagadoNOAcreditableNoObjetoTasa16;

	@JsonProperty("IVAPagadoNOAcreditableProporcionalImportacionTangibleTasa16")
	private BigDecimal ivaPagadoNOAcreditableProporcionalImportacionTangibleTasa16;

	@JsonProperty("IVAPagadoNOAcreditableImportacionTangibleTasa16")
	private BigDecimal ivaPagadoNOAcreditableImportacionTangibleTasa16;

	@JsonProperty("IVAPagadoNOAcreditableImportacionTangibleExentoTasa16")
	private BigDecimal ivaPagadoNOAcreditableImportacionTangibleExentoTasa16;

	@JsonProperty("IVAPagadoNOAcreditableImportacionTangibleNoObjetoTasa16")
	private BigDecimal ivaPagadoNOAcreditableImportacionTangibleNoObjetoTasa16;

	@JsonProperty("IVAPagadoNOAcreditableProporcionalImportacionIntangibleTasa16")
	private BigDecimal ivaPagadoNOAcreditableProporcionalImportacionIntangibleTasa16;

	@JsonProperty("IVAPagadoNOAcreditableImportacionIntangibleTasa16")
	private BigDecimal ivaPagadoNOAcreditableImportacionIntangibleTasa16;

	@JsonProperty("IVAPagadoNOAcreditableImportacionIntangibleExentoTasa16")
	private BigDecimal ivaPagadoNOAcreditableImportacionIntangibleExentoTasa16;

	@JsonProperty("IVAPagadoNOAcreditableImportacionIntangibleNoObjetoTasa16")
	private BigDecimal ivaPagadoNOAcreditableImportacionIntangibleNoObjetoTasa16;


	// Datos Adicionales

	@JsonProperty("IVARetenidoContribuyente")
	private BigDecimal ivaRetenidoContribuyente;

	@JsonProperty("ActosPagadosImportacionExento")
	private BigDecimal actosPagadosImportacionExento;

	@JsonProperty("Exentos")
	private BigDecimal exentos;

	@JsonProperty("Base0")
	private BigDecimal base0;

	@JsonProperty("NoObjeto")
	private BigDecimal noObjeto;

	@JsonProperty("NoObjetoSinEstablecimientoNacional")
	private BigDecimal noObjetoSinEstablecimientoNacional;

	@JsonProperty("ManifiestoEfectosFiscales")
	private String manifiestoEfectosFiscales;


}
