package com.sngular.captio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Table(name = "opciones_empleado")
@Accessors(chain = true)
public class OpcionesEmpleado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opciones_empleado_id")
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
	@JoinColumn(name = "empleadoSonarId")
	private EmpleadoSonar empleado;

}
