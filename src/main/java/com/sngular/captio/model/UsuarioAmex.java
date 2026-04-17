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
@Table(name = "usuario_amex")
public class UsuarioAmex {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String pais;

	@Column(name = "codigo_empresa")
	private String codigoEmpresa;

	@Column(name = "nombre_empresa")
	private String nombreEmpresa;

	@Column(name = "centro_costo")
	private String centroCosto;

	@Column(name = "estado_directivo")
	private String estadoDirectivo;

	@Column(name = "codigo_registro")
	private String codigoRegistro;

	@Column(name = "xm_usuario")
	private String xmUsuario;

	@Column(name = "nombre_plaza")
	private String nombrePlaza;

	@Column(name = "nombre_empleado")
	private String nombreEmpleado;

	@Column(name = "apellido_paterno")
	private String apellidoPaterno;

	@Column(name = "apellido_materno")
	private String apellidoMaterno;

	@Column(name = "correo_usuario")
	private String correoUsuario;

	@Column(name = "xm_jefe")
	private String xmJefe;

	@Column(name = "correo_jefe")
	private String correoJefe;

	@Column(name = "direccion_usuario")
	private String direccionUsuario;

	@Column(name = "nombre_autorizador")
	private String nombreAutorizador;

	@Column(name = "registro_autorizador")
	private String registroAutorizador;

	@Column(name = "xm_autorizador")
	private String xmAutorizador;

	@Column(name = "correo_autorizador")
	private String correoAutorizador;

	@Column(name = "estado_empleado")
	private String estadoEmpleado;

	@Column(name = "telefono_departamento")
	private String telefonoDepartamento;

	@Column(name = "nombre_puesto")
	private String nombrePuesto;

}
