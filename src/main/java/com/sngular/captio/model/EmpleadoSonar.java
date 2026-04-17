package com.sngular.captio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Entity
@Table(name = "empleado_sonar")
@Data
@Accessors(chain = true)
public class EmpleadoSonar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empleadoSonarId")
	private Long empleadoSonarId;

    @Column(name = "id")
    private Long id;

	@Column(name = "codigo_registro")
	private String codigoRegistro;

	@Column(name = "codigo_empresa")
	private String codigoEmpresa;

	@Column(name = "apellido_paterno")
	private String apellidoPaterno;

	@Column(name = "apellido_materno")
	private String apellidoMaterno;

	@Column(name = "nombre_empleado")
	private String nombreEmpleado;

	@Column(name = "fecha_nacimiento")
	private LocalDate fechaNacimiento;

	@Column(name = "sexo")
	private String sexo;

	@Column(name = "estado_empleado")
	private String estadoEmpleado;

	@Column(name = "fecha_ingreso")
	private LocalDate fechaIngreso;

	@Column(name = "codigo_puesto")
	private String codigoPuesto;

	@Column(name = "nombre_puesto")
	private String nombrePuesto;

	@Column(name = "tipo_funcionario")
	private String tipoFuncionario;

	@Column(name = "codigo_departamento")
	private String codigoDepartamento;

	@Column(name = "nombre_departamento")
	private String nombreDepartamento;

	@Column(name = "ubicacion")
	private String ubicacion;

	@Column(name = "plaza")
	private String plaza;

	@Column(name = "centro_costo")
	private String centroCosto;

	@Column(name = "plaza1")
	private String plaza1;

	@Column(name = "telefono_departamento")
	private String telefonoDepartamento;

	@Column(name = "codigo_cr")
	private String codigoCr;

	@Column(name = "nombre_cr")
	private String nombreCr;

	@Column(name = "codigo_direccion_general")
	private String codigoDireccionGeneral;

	@Column(name = "nombre_direccion_general")
	private String nombreDireccionGeneral;

	@Column(name = "codigo_direccion")
	private String codigoDireccion;

	@Column(name = "nombre_direccion")
	private String nombreDireccion;

	@Column(name = "codigo_sub_direccion")
	private String codigoSubDireccion;

	@Column(name = "nombre_sub_direccion")
	private String nombreSubDireccion;

	@Column(name = "loginResponsable")
	private String loginResponsable;

	@Column(name = "loginSupervisor")
	private String loginSupervisor;

	@Column(name = "rfc")
	private String rfc;

	@Column(name = "fecha_ingreso_puesto")
	private LocalDate fechaIngresoPuesto;

	@Column(name = "fecha_ingreso_depto")
	private LocalDate fechaIngresoDepto;

	@Column(name = "centro_costo_rh")
	private String centroCostoRh;

	@Column(name = "nivel_estructura")
	private String nivelEstructura;

	@Column(name = "codigo_cheques")
	private String codigoCheques;

	@Column(name = "codigo_motivo_baja")
	private String codigoMotivoBaja;

	@Column(name = "registro_viba")
	private String registroViba;

	@Column(name = "nivel_estructural")
	private String nivelEstructural;

	@Column(name = "categoria")
	private String categoria;

	@Column(name = "nombre_informacion_fiduciario")
	private String nombreInformacionFiduciario;

	@Column(name = "campo3")
	private String campo3;

	@Column(name = "campo4")
	private String campo4;

	@Column(name = "fecha_ingreso1")
	private LocalDate fechaIngreso1;

	@Column(name = "codigo_empleado")
	private String codigoEmpleado;

	@Column(name = "codigo_colectivo")
	private String codigoColectivo;

	@Column(name = "correo")
	private String correo;

	@OneToOne(mappedBy = "empleado")
	private OpcionesEmpleado options;

	@Column(name = "forceChangePasswordOnFirstLogin")
	private boolean forceChangePasswordOnFirstLogin;

	@Column(name = "password")
	private String password;

	@Column(name = "authenticationType")
	private Integer authenticationType;

	@Column(name = "active")
	private Boolean active;

	@Column(name = "email")
	private String email;

	@Column(name = "login")
	private String login;

	@Column(name = "name")
	private String name;

	@Column(name = "loginN1")
	private String loginN1;

	@Column(name = "loginN2")
	private String loginN2;

	@Column(name = "loginN3")
	private String loginN3;

	@Column(name = "loginN4")
	private String loginN4;

	@Column(name = "tdc")
	private String tdc;

	@Column(name = "tdc_status")
	private String tdcStatus;

}
