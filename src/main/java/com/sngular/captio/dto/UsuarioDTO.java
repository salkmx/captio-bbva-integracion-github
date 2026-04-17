package com.sngular.captio.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Accessors(chain = true)
public class UsuarioDTO {

	@JsonProperty("UserId")
	private Integer userId;

	@JsonProperty("Id")
	private Integer id;

	@JsonProperty("Email")
	private String email;

	@JsonProperty("Login")
	private String login;

	@JsonProperty("Password")
	private String password;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Active")
	private Boolean active;

	@JsonProperty("ExternalLogin")
	private String externalLogin;

	@JsonProperty("AuthenticationType")
	private Integer authenticationType;

	@JsonProperty("Options")
	private OpcionesUsuarioDTO options;
	
	@JsonProperty("UserOptions")
	private OpcionesUsuarioDTO userOptions;

	@JsonProperty("LanguageCode")
	private String languageCode;

	@JsonProperty("ForceChangePasswordOnFirstLogin")
	private Boolean forceChangePasswordOnFirstLogin;

	@JsonProperty("DefaultPerdiemCategory")
	private Integer defaultPerdiemCategory;

	@JsonProperty("codigoRegistro")
	private String codigoRegistro;

	@JsonProperty("codigoEmpresa")
	private String codigoEmpresa;

	@JsonProperty("apellidoPaterno")
	private String apellidoPaterno;

	@JsonProperty("apellidoMaterno")
	private String apellidoMaterno;

	@JsonProperty("nombreEmpleado")
	private String nombreEmpleado;

	@JsonProperty("fechaNacimiento")
	private LocalDate fechaNacimiento;

	@JsonProperty("sexo")
	private String sexo;

	@JsonProperty("estadoEmpleado")
	private String estadoEmpleado;

	@JsonProperty("fechaIngreso")
	private LocalDate fechaIngreso;

	@JsonProperty("codigoPuesto")
	private String codigoPuesto;

	@JsonProperty("nombrePuesto")
	private String nombrePuesto;

	@JsonProperty("tipoFuncionario")
	private String tipoFuncionario;

	@JsonProperty("codigoDepartamento")
	private String codigoDepartamento;

	@JsonProperty("nombreDepartamento")
	private String nombreDepartamento;

	@JsonProperty("ubicacion")
	private String ubicacion;

	@JsonProperty("plaza")
	private String plaza;

	@JsonProperty("centroCosto")
	private String centroCosto;

	@JsonProperty("plaza1")
	private String plaza1;

	@JsonProperty("telefonoDepartamento")
	private String telefonoDepartamento;

	@JsonProperty("codigoCr")
	private String codigoCr;

	@JsonProperty("nombreCr")
	private String nombreCr;

	@JsonProperty("codigoDireccionGeneral")
	private String codigoDireccionGeneral;

	@JsonProperty("nombreDireccionGeneral")
	private String nombreDireccionGeneral;

	@JsonProperty("codigoDireccion")
	private String codigoDireccion;

	@JsonProperty("nombreDireccion")
	private String nombreDireccion;

	@JsonProperty("codigoSubDireccion")
	private String codigoSubDireccion;

	@JsonProperty("nombreSubDireccion")
	private String nombreSubDireccion;

	@JsonProperty("loginResponsable")
	private String loginResponsable;

	@JsonProperty("loginSupervisor")
	private String loginSupervisor;

	@JsonProperty("rfc")
	private String rfc;

	@JsonProperty("fechaIngresoPuesto")
	private LocalDate fechaIngresoPuesto;

	@JsonProperty("fechaIngresoDepto")
	private LocalDate fechaIngresoDepto;

	@JsonProperty("centroCostoRh")
	private String centroCostoRh;

	@JsonProperty("nivelEstructura")
	private String nivelEstructura;

	@JsonProperty("codigoCheques")
	private String codigoCheques;

	@JsonProperty("codigoMotivoBaja")
	private String codigoMotivoBaja;

	@JsonProperty("registroViba")
	private String registroViba;

	@JsonProperty("nivelEstructural")
	private String nivelEstructural;

	@JsonProperty("categoria")
	private String categoria;

	@JsonProperty("nombreInformacionFiduciario")
	private String nombreInformacionFiduciario;

	@JsonProperty("campo3")
	private String campo3;

	@JsonProperty("campo4")
	private String campo4;

	@JsonProperty("fechaIngreso1")
	private LocalDate fechaIngreso1;

	@JsonProperty("codigoEmpleado")
	private String codigoEmpleado;

	@JsonProperty("codigoColectivo")
	private String codigoColectivo;

	@JsonProperty("correo")
	private String correo;

	@JsonProperty("loginN1")
	private String loginN1;

	@JsonProperty("loginN2")
	private String loginN2;

	@JsonProperty("loginN3")
	private String loginN3;

	@JsonProperty("loginN4")
	private String loginN4;

	@JsonIgnoreProperties("ActiveTravelGroup")
	private Boolean activeTravelGroup;

	@JsonProperty("Workflows")
	private List<WorkFlowDTO> workflows;

	@JsonProperty("Payments")
	private List<MetodoPagoDTO> payments;

	@JsonProperty("Groups")
	private List<GrupoDTO> grupos;

	@JsonIgnore
	private String tdc;

	@JsonIgnore
	private String tdcStatus;
	
	@JsonProperty("Permissions")
	List<PermissionDTO> permissions;
}
