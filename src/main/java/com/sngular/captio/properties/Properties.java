package com.sngular.captio.properties;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class Properties {

	@Value("${sftp.enabled:false}")
	private boolean sftpEnabled;

	@Value("${captio.api.customerkey}")
	private String customerKey;

	@Value("${captio.api.categoria.alta.url}")
	private String urlCategorias;

	@Value("${captio.api.categoria.consulta.url}")
	private String urlGetCategorias;

	@Value("${captio.api.categoria.delete.url}")
	private String deleteCategoriaUrl;

	@Value("${captio.api.usuario.alta.url}")
	private String urlUsuarios;

	@Value("${captio.api.usuario.viaje.alta.url}")
	private String urlUsuariosViajes;

	@Value("${captio.api.usuario.kmgroups.alta.url}")
	private String urlUsuarioskmgroups;

	@Value("${captio.api.usuario.consulta.url}")
	private String urlGetUsuarios;

	@Value("${captio.api.usuario.payments.consulta.url}")
	private String urlGetPaymentsUsuarios;

	@Value("${captio.api.usuario.payments.put.url}")
	private String urlPutPaymentsUsuarios;

	@Value("${captio.api.usuario.payments.post.url}")
	private String urlPostPaymentsUsuarios;

	@Value("${captio.api.usuario.payments.delete.url}")
	private String urlDeletePaymentsUsuarios;

	@Value("${captio.api.usuario.groups.consulta.url}")
	private String urlGetGroupsUsuarios;

	@Value("${captio.api.usuario.delete.url}")
	private String urlDeleteUsuarios;

	@Value("${sftp.remote.file}")
	private String nombreArchivoRemoto;

	@Value("${sftp.local.dir}")
	private String rutaDestinoLocal;

	@Value("${sftp.remote-dir.error}")
	private String rutaRemotaError;

	@Value("${captio.api.viajes.consulta.url}")
	private String urlGetViajes;

	@Value("${captio.api.viajes.customfields.url}")
	private String urlPatchViajesCustomFields;

	@Value("${captio.api.viajes.services.url}")
	private String urlPatchViajesServices;

	@Value("${captio.api.gastos.consulta.url}")
	private String urlGetGastos;

	@Value("${captio.api.gastos.delete.url}")
	private String urlDeleteGastos;

	@Value("${captio.api.gastos.alta.url}")
	private String urlPostGastos;

	@Value("${captio.api.gastos.attachments.url}")
	private String urlGetAttachments;

	@Value("${captio.api.informes.consulta.url}")
	private String urlGetInformes;

	@Value("${captio.api.informes.alta.url}")
	private String urlPostInformes;

	@Value("${captio.api.informes.update.url}")
	private String urlPatchInformes;

	@Value("${captio.api.informes.aprobacion.url}")
	private String urlSolicitarAprobacionInformes;

	@Value("${captio.api.informes.gastos.alta.url}")
	private String urlPostInformesGastos;

	@Value("${captio.api.informes.anticipos.alta.url}")
	private String urlPostInformesAnticipos;

	@Value("${captio.api.informes.aprobacion.paso1.url}")
	private String urlPostInformesAprobar;

	@Value("${captio.api.usuarios.permisos.url}")
	private String urlUsuersPermissions;

	@Value("${captio.archivo.salida.error.viaje}")
	private String rutaArchivoErrorViajes;

	@Value("${captio.archivo.salida.error.gasto}")
	private String rutaArchivoErrorGastos;

	@Value("${captio.archivo.salida.error.usuario}")
	private String rutaArchivoErrorUsuarios;

	@Value("${captio.api.wf.alta.url}")
	private String urlPostWorkflows;

	@Value("${captio.archivo.salida.error.workflow}")
	private String rutaArchivoErrorWorkFlow;

	@Value("${captio.api.wf.alta.paso.url}")
	private String urlPostWorkflowsSteps;

	@Value("${captio.api.usuario.joinwf.alta.url}")
	private String urlPostUsersJoinWorkflow;

	@Value("${captio.api.usuario.joinwf.patch.url}")
	private String urlPatchUsersJoinWorkflow;

	@Value("${captio.api.usuario.unjoinwf.delete.url}")
	private String urlDeleteUsersUnjoinWorkflow;

	@Value("${captio.api.usuario.joingrupo.alta.url}")
	private String urlPostUsersJoinGrupo;

	@Value("${captio.api.usuario.wf.consulta.url}")
	private String urlGetUsersWorkflow;

	@Value("${captio.api.wf.consulta.url}")
	private String urlGetWorkflow;

	@Value("${captio.api.advances.alta.url}")
	private String urlPostAdvances;

	@Value("${captio.api.usuario.groups.consulta.url}")
	private String urlGetUserGroups;

	@Value("${captio.api.advances.consulta.url}")
	private String urlGetAdvances;

	@Value("${captio.api.advances.deliver.url}")
	private String urlAdvancesDeliver;

	@Value("${captio.api.logs.send.url}")
	private String urlSendLogs;

	@Value("${captio.api.reports.logs.url}")
	private String urlReportsLogs;

	@Value("${captio.archivo.salida.logs}")
	private String rutaArchivoLogs;

	@Value("${mail.from}")
	private String mailFrom;

	@Value("${mail.pagaduria}")
	private String mailPagaduria;

	@Value("${mail.gastos_medicos.to}")
	private String mailGastosMedicos;

	@Value("${mail.gastos_vuelos.to}")
	private String mailGastosVuelos;

	@Value("${captio.archivo.salida.error.anticipos}")
	private String rutaArchivoErrorDotaciones;

	@Value("#{'${mail.error.to}'.split(',')}")
	private List<String> errorMails;

	@Value("${sftp.local.file.dotaciones}")
	private String nombreArchivoLocalDotacion;

	@Value("${sftp.local.file.gastos}")
	private String nombreArchivoLocalGastos;

	@Value("${sftp.local.dir.dotaciones}")
	private String rutaArchivoLocalDotacion;

	@Value("${sftp.local.dir.gastos}")
	private String rutaArchivoLocalGastos;

	@Value("${excel.gastos}")
	private String rutaExcelGastos;

	// Informes DIOT
	@Value("${sftp.local.dir.diot}")
	private String rutaArchivoLocalDiot;

	@Value("${bbva.captio.reporte.DIOT.periodicidad}")
	private String reporteDIOTPeriodicidad;


	// ReporteDescuentoNomina
	@Value("${sftp.local.dir.repdescnomina}")
	private String rutaArchivoLocalRepDescNomina;

	@Value("${bbva.captio.reporte.descuento.nomina.periodicidad}")
	private String reporteDescuentoNominaPeriodicidad;

	// Reporte usuarios permisos
	@Value("${sftp.local.dir.reporteusuariospermisos}")
	private String rutaArchivoUsuariosPermisos;

	@Value("${batch.balanza.fields}")
	private String balanzaContableFields;

	@Value("${sftp.local.file.gatosmedicos}")
	private String gastosmedicosPath;

	@Value("${sftp.local.file.gastosvuelosagencia}")
	private String gastosVuelosAgenciaPath;

	@Value("${sftp.local.dir.balanza}")
	private String balanzaLocalDir;

	@Value("${sftp.local.file.balanza}")
	private String balanzaLocalName;

	@Value("${captio.workflow.autorizador.fijo.email}")
	private String emailAutorizadorFijo;

	@Value("${sftp.local.dir.acec}")
	private String acecLocalDir;

	@Value("${sftp.local.file.acec}")
	private String acecLocalName;

	// Reporte Otros Gastos en Informe - Ruta
	@Value("${sftp.local.dir.reporteotrosgastos}")
	private String rutaArchivoLocalReporteOtrosGastos;

	// Reporte Otros Gastos en Informe - Nombre
	@Value("${sftp.local.file.reporteotrosgastos}")
	private String nombreArchivoLocalReporteOtrosGastos;


}
