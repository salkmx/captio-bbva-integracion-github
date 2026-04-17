package com.sngular.captio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroExportDTO {

	private String DIF;
	private LocalDate FECHA_CONTABLE;
	private String OPERACION_LIG;
	private String REGISTRO;
	private String NUM_NOMINA;
	private String VIAJERO;
	private String OP;
	private String FOLIO;
	private LocalDate FCH_REG;
	private String CR;
	private String CR_NOM;
	private String CO;
	private String CO_NOM;
	private String ZONA;
	private String DOT_DIT;
	private String TAG;
	private String TPO_VIAJE;
	private String ORIGEN_PLAZA;
	private String DESTINO;
	private LocalDate FCH_INICIO;
	private LocalDate FCH_FIN;
	private LocalDate FCH_DEP;
	private String MOTIVO;
	private String PROYECTO;
	private String DESCRIPCION;
	private String NOTA;
	private String CONCEPTO_GTO;
	private String FORMA_PAGO;
	private String XML;
	private String PDF;
	private String NOM_COMERCIAL;
	private String RAZON_SOCIAL;
	private String RFC_PROVEEDOR;
	private String CD_PAIS;
	private String PAIS;
	private LocalDate FCH_FACTURA;
	private BigDecimal IMP_TOT;
	private BigDecimal TASA_IVA;
	private BigDecimal IMP_NETO;
	private BigDecimal IMP_IVA;
	private BigDecimal IMP_PROPINA;
	private BigDecimal OTROS_IMPUESTOS;
	private BigDecimal IMP_CONCEPTO;
	private String CC_IMP_NETO;
	private String CC_IMP_IVA;
	private String CC_PROPINA;
	private String CC_OTROS_IMPUESTOS;

}
