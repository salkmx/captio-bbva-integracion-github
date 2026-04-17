package com.sngular.captio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaContableDTO {

	private Long id;
	private Integer idCategoria;
	private Integer idTipoGasto;
	private Integer idMedioPago;
	private Integer idEmpresa;
	private String cuentaContable;

}
