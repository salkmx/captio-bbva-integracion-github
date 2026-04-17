package com.sngular.captio.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcecDTO {

	private String empresa;
	private String claveInterfaz;
	private String tipoEvento;
	private Long numeroSecuencia;

	private String fechaContable;
	private String fechaOperacion;
	private String fechaValor;

	private String divisa1;
	private String divisa2;
	private String divisa3;

	private String centroAlta;
	private String centroOrigen;
	private String centroDestino;

	private String producto;
	private String subproducto;
	private String garantia;

	private String tipoPlazo;
	private Integer plazo;

	private String subsector;
	private String conceptoSubsector;

	private String destinoInversion;
	private String aplicacionOrigen;

	private BigDecimal iva;

	private String morosidad;
	private String tipoMorosidad;

	private String codigoOperacion;
	private String conceptoContable;
	private String subconceptoContable;

	private String tipoDivisa;
	private String varios;

	private BigDecimal importe1;
	private BigDecimal importe2;
	private BigDecimal importe3;
	private BigDecimal importe4;
	private BigDecimal importe5;
	private BigDecimal importe6;
	private BigDecimal importe7;
	private BigDecimal importe8;
	private BigDecimal importe9;
	private BigDecimal importe10;

	private String indicadorImporte1;
	private String indicadorImporte2;
	private String indicadorImporte3;
	private String indicadorImporte4;
	private String indicadorImporte5;
	private String indicadorImporte6;
	private String indicadorImporte7;
	private String indicadorImporte8;
	private String indicadorImporte9;
	private String indicadorImporte10;

	private String campoLibre1;
	private String campoLibre2;
	private String campoLibre3;
	private String campoLibre4;
	private String campoLibre5;
	private String campoLibre6;
	private String campoLibre7;
	private String campoLibre8;
	private String campoLibre9;
	private String campoLibre10;

	private String transaccion;
	private String contrato;
	private Long numeroMovimiento;
	private String tipoContable;

	private String timestamp;

	private String numeroTarjeta;
	private String numeroDocumento;
}