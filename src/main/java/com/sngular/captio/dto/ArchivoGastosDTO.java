package com.sngular.captio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchivoGastosDTO {
	
    /** Columna "Statement Information" */
    private String statementInformation;

    /** Columna "Fecha_Reporting" (fecha del gasto) */
    private LocalDate fechaGasto;

    /** Columna "Carrier / Hotel Name" (proveedor) */
    private String proveedor;

    /** Columna "Full Amount" (monto) */
    private BigDecimal monto;

}
