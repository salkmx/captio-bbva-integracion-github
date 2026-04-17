package com.sngular.captio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Table(name = "opciones_usuario")
@Accessors(chain = true)
public class OpcionesUsuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "costCentre")
	private String costCentre;

	@Column(name = "employeeCode")
	private String employeeCode;

	@Column(name = "companyCode")
	private String companyCode;

	@Column(name = "taxPayerId")
	private String taxPayerId;

	@OneToOne
	@JoinColumn(name = "id_usuario")
	private UsuarioSonar usuario;

}
