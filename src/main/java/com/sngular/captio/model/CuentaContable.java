package com.sngular.captio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cuenta_contable")
public class CuentaContable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "id_categoria")
	private Integer idCategoria;

	@Column(name = "id_tipo_gasto")
	private Integer idTipoGasto;

	@Column(name = "id_medio_pago")
	private Integer idMedioPago;

	@Column(name = "id_empresa")
	private Integer idEmpresa;

	@Column(name = "cuenta_contable")
	private String cuentaContable;

}
