package com.sngular.captio.config;

import java.util.Map;
import com.sngular.captio.processor.*;
import com.sngular.captio.reader.*;
import com.sngular.captio.tasklet.*;
import com.sngular.captio.writer.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.sngular.captio.dto.AcecDTO;
import com.sngular.captio.dto.BalanzaDTO;
import com.sngular.captio.dto.DescuentoNominaDTO;
import com.sngular.captio.dto.DiotDTO;
import com.sngular.captio.dto.DotacionDTO;
import com.sngular.captio.dto.ExpenseDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.dto.InformeLogDTO;
import com.sngular.captio.dto.UserPermissionDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.ViajeDTO;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@Slf4j
public class JobsConfig {

	private final UsuarioItemReader csvUsuarioItemReader;

	private final CategoriaItemReader csvCategoriaItemReader;

	private final UsuariosItemWriter csvUsuariosItemWriter;

	private final CategoriasItemWriter csvctegoriasItemWriter;

	private final UsuarioSonarItemReader usuarioSonarItemReader;

	private final UsuariosSyncTasklet personasSyncTasklet;

    private final SftpDownloadTasklet sftpDownloadTasklet;

	private final SftpUploadErrorTasklet sftpUploadErrorTasklet;

	private final SftpUploadOutputTasklet sftpUploadOutputTasklet;

	private final InformeViajeReader restInformeItemReader;

	private final InformeViajesWriter informeViajesItemWriter;

	private final InformeViajeProcessor informeViajeProcessor;

	private final UsuarioSonarItemWriter usuarioSonarItemWriter;

	private final FlujoAprobacionTasklet flujoAprobacionTasklet;

    private final DotacionItemReader dotacionItemReader;

	private final DotacionProcessor dotacionProcessor;

	private final DotacionItemWriter dotacionItemWriter;

	private final InformeOtrosGastosReader informeOtrosGastosReader;

	private final InformeOtrosGastosProcessor informeOtrosGastosProcessor;

	private final InformeOtrosGastosWriter informeOtrosGastosWriter;

	private final ReporteOtrosGastosEnInformeReader reporteOtrosGastosEnInformeReader;

	private final ReporteOtrosGastosEnInformeProcessor reporteOtrosGastosEnInformeProcessor;

	private final ReporteOtrosGastosEnInformeWriter reporteOtrosGastosEnInformeWriter;

	private final GrupoTasklet grupoTasklet;

	private final InformeViajesDiarioWriter informeViajeDiarioWriter;

	private final InformeViajeDiarioProcessor informeViajeDiarioProcessor;

	private final InformeViajeDiarioReader informeViajeDiarioReader;

	private final InformeViajesValidacionWriter informeViajeValidacionWriter;

	private final InformeViajeValidacionProcessor informeViajeValidacionProcessor;

	private final InformeViajeValidacionReader informeViajeValidacionReader;

	private final GastosReader gastosReader;

	private final GastosProcessor gastosProcessor;

	private final GastosWriter gastosWriter;

	private final GastosMedicosReader gastosMedicosReader;

	private final GastosMedicosProcessor gastosMedicosProcessor;

	private final GastosMedicosWriter gastosMedicosWriter;

	private final InformeGastosMedicosReader informeGastosMedicosReader;

	private final InformeGastosMedicosWriter informeGastosMedicosWriter;

	private final GastosVuelosAgenciaReader gastosVuelosAgenciaReader;

	private final GastosVuelosAgenciaProcessor gastosVuelosAgenciaProcessor;

	private final GastosVuelosAgenciaWriter gastosVuelosAgenciaWriter;

	private final BalanzaContableReader balanzaContableReader;

	private final BalanzaContableProcessor balanzaContableProcessor;

	private final BalanzaContableWriter balanzaContableWriter;

	private final ReporteACECReader reporteACECReader;

	private final ReporteACECProcessor reporteACECProcessor;

	private final ReporteACECWriter reporteACECWriter;

	private final SendLogsTasklet sendLogsTasklet;

	// InformeDIOT
	private final GastosDIOTReader gastosDIOTReader;
	private final GastosDIOTProcessor gastosDIOTProcessor;
	private final GastosDIOTWriter gastosDIOTWriter;

	// ReporteDescuentoNomina
	private final ReporteDescuentoNominaReader reporteDescuentoNominaReader;
	private final ReporteDescuentoNominaWriter reporteDescuentoNominaWriter;

	// ReporteUsuariosPermisos
	private final ReporteUsuariosPermisosReader reporteUsuariosPermisosReader;
	private final ReporteUsuariosPermisosWriter reporteUsuariosPermisosWriter;

	private final InformeLogReader informeLogReader;
	private final InformeLogProcessor informeLogProcessor;
	private final InformeLogWriter informeLogWriter;

    private final UsuarioCaptioTasklet usuarioCaptioTasklet;
    private final InformeCaptioTasklet informeCaptioTasklet;
    private final WorkflowStepTasklet workflowStepTasklet;

    private final EmpleadosSyncTasklet empleadosSyncTasklet;
    private final EmpleadoSonarTasklet empleadoSonarTasklet;
    private final FlujoAprobacionEmpleadosTasklet flujoAprobacionEmpleadosTasklet;
    private final GrupoEmpleadoTasklet grupoEmpleadoTasklet;


    @Bean
	Job jobUsuarios(JobRepository jobRepository, @Qualifier("stepUsuarios") Step stepUsuarios) {
		return new JobBuilder("jobUsuarios", jobRepository).start(stepUsuarios).build();
	}

	@Bean
	Job jobCategorias(JobRepository jobRepository, @Qualifier("stepCategorias") Step stepCategorias) {
		return new JobBuilder("jobCategorias", jobRepository).start(stepCategorias).build();
	}

    @Bean
    Job jobPersonas(JobRepository jobRepository,
                    @Qualifier("stepSonar") Step stepSonar,
                    @Qualifier("stepSyncPersonas") Step stepSyncPersonas,
                    @Qualifier("stepSftpDownloadTasklet") Step stepSftpDownloadTasklet,
                    @Qualifier("stepWorkFlow") Step stepWorkFlow,
                    @Qualifier("stepGrupo") Step stepGrupo,
                    @Qualifier("stepSftpUploadError") Step stepSftpUploadError) {
        return new JobBuilder("jobPersonas", jobRepository)
                .start(stepSftpDownloadTasklet)
                .next(stepSonar)
                .next(stepSyncPersonas)
                .next(stepWorkFlow)
                .next(stepGrupo)
                .next(stepSftpUploadError).build();
    }


    @Bean
    Job jobEmpleados(JobRepository jobRepository,
                    @Qualifier("stepSonar") Step stepSonar,
                    @Qualifier("stepEmpleadoSonar") Step stepEmpleadoSonar,
                    @Qualifier("stepSyncEmpleados") Step stepSyncEmpleados,
                    @Qualifier("stepSftpDownloadTasklet") Step stepSftpDownloadTasklet,
                    @Qualifier("stepWorkFlowEmpleados") Step stepWorkFlowEmpleados,
                    @Qualifier("stepGrupoEmpleado") Step stepGrupoEmpleado,
                    @Qualifier("stepSftpUploadError") Step stepSftpUploadError) {
        return new JobBuilder("jobEmpleados", jobRepository)
                .start(stepSftpDownloadTasklet)
                .next(stepSonar)
                .next(stepEmpleadoSonar)
                .next(stepSyncEmpleados)
                .next(stepWorkFlowEmpleados)
                .next(stepGrupoEmpleado)
                .next(stepSftpUploadError).build();
    }

	@Bean
	Job jobInformeViajes(JobRepository jobRepository, @Qualifier("stepInformeViajes") Step stepInformeViajes,
			@Qualifier("stepSftpUploadError") Step stepSftpUploadError) {
		return new JobBuilder("jobInformeViajes", jobRepository).start(stepInformeViajes).next(stepSftpUploadError)
				.build();
	}

	@Bean
	Job jobInformeViajesDiario(JobRepository jobRepository,
			@Qualifier("stepInformeViajesDiario") Step stepInformeViajesDiario,
			@Qualifier("stepSftpUploadError") Step stepSftpUploadError) {
		return new JobBuilder("jobInformeViajesDiario", jobRepository).start(stepInformeViajesDiario)
				.next(stepSftpUploadError).build();
	}

	@Bean
	Job jobInformeViajesValidacion(JobRepository jobRepository,
			@Qualifier("stepInformeViajesValidacion") Step stepInformeViajesValidacion,
			@Qualifier("stepSftpUploadError") Step stepSftpUploadError) {
		return new JobBuilder("jobInformeViajesValidacion", jobRepository).start(stepInformeViajesValidacion)
				.next(stepSftpUploadError).build();
	}

	@Bean
	Job jobDotacion(JobRepository jobRepository, @Qualifier("stepDotacionViajes") Step stepDotacionViajes,
			@Qualifier("stepSftpUploadError") Step stepSftpUploadError,
			@Qualifier("stepSftpUploadOutput") Step stepSftpUploadOutput) {
		return new JobBuilder("jobDotacion", jobRepository).start(stepDotacionViajes).next(stepSftpUploadOutput)
				.next(stepSftpUploadError).build();
	}

	@Bean
	Job jobInformeOtrosGastos(JobRepository jobRepository,
			@Qualifier("stepInformeOtrosGastos") Step stepInformeOtrosGastos,
			@Qualifier("stepSftpUploadError") Step stepSftpUploadError,
			@Qualifier("stepSftpUploadOutput") Step stepSftpUploadOutput) {
		return new JobBuilder("jobInformeOtrosGastos", jobRepository).start(stepInformeOtrosGastos)
				.next(stepSftpUploadOutput).next(stepSftpUploadError).build();
	}

	@Bean
	Job jobReporteOtrosGastosEnInforme(JobRepository jobRepository,
			@Qualifier("stepReporteOtrosGastosEnInforme") Step stepReporteOtrosGastosEnInforme,
			@Qualifier("stepSftpUploadError") Step stepSftpUploadError,
			@Qualifier("stepSftpUploadOutput") Step stepSftpUploadOutput) {
		return new JobBuilder("jobReporteOtrosGastosEnInforme", jobRepository).start(stepReporteOtrosGastosEnInforme)
				.next(stepSftpUploadOutput).next(stepSftpUploadError).build();
	}

	@Bean
	Job jobGastos(JobRepository jobRepository, @Qualifier("stepGastos") Step stepGastos) {
		return new JobBuilder("jobGastos", jobRepository).start(stepGastos).build();
	}

	@Bean
	Job jobGastosMedicos(JobRepository jobRepository, @Qualifier("stepGastosMedicos") Step stepGastosMedicos,
			@Qualifier("stepInformeGastosMedicos") Step stepInformeGastosMedicos,
			@Qualifier("stepSftpUploadOutput") Step stepSftpUploadOutput) {
		return new JobBuilder("jobGastosMedicos", jobRepository).start(stepGastosMedicos).next(stepInformeGastosMedicos)
				.next(stepSftpUploadOutput).build();
	}

	@Bean
	Job jobGastosVuelosAgencia(JobRepository jobRepository, @Qualifier("stepGastosVuelosAgencia") Step stepGastosVuelosAgencia) {
		return new JobBuilder("jobGastosVuelosAgencia", jobRepository).start(stepGastosVuelosAgencia).build();
	}

	@Bean
	Job jobBalanzaContable(JobRepository jobRepository, @Qualifier("stepBalanzaContable") Step stepBalanzaContable,
			@Qualifier("stepSonar") Step stepSonar, @Qualifier("stepSftpUploadOutput") Step stepSftpUploadOutput) {
		return new JobBuilder("jobBalanzaContable", jobRepository).start(stepSonar).next(stepBalanzaContable)
				.next(stepSftpUploadOutput).build();
	}

	@Bean
	Job jobReporteACEC(JobRepository jobRepository, @Qualifier("stepReporteACEC") Step stepReporteACEC,
			@Qualifier("stepSonar") Step stepSonar, @Qualifier("stepSftpUploadOutput") Step stepSftpUploadOutput) {
		return new JobBuilder("jobReporteACEC", jobRepository).start(stepSonar).next(stepReporteACEC)
				.next(stepSftpUploadOutput).build();
	}

	// InformeDIOT
	@Bean
	Job jobInformeDIOT(JobRepository jobRepository, @Qualifier("stepGastosDIOT") Step stepGastosDIOT,
			@Qualifier("stepSftpUploadOutput") Step stepSftpUploadOutput) {
		return new JobBuilder("jobInformeDIOT", jobRepository).start(stepGastosDIOT).next(stepSftpUploadOutput).build();
	}

	// ReporteDescuentoNomina
	@Bean
	Job jobReporteDescuentoNomina(JobRepository jobRepository,
			@Qualifier("stepReporteDescuentoNomina") Step stepReporteDescuentoNomina,
			@Qualifier("stepSftpUploadOutput") Step stepSftpUploadOutput) {
		return new JobBuilder("jobReporteDescuentoNomina", jobRepository).start(stepReporteDescuentoNomina)
				.next(stepSftpUploadOutput).build();
	}

	// ReporteUsuariosPermisos - rmiranda
	@Bean
	Job jobReporteUsuariosPermisos(JobRepository jobRepository,
			@Qualifier("stepReporteUsuariosPermisos") Step stepReporteUsuariosPermisos,
			@Qualifier("stepSftpUploadOutput") Step stepSftpUploadOutput) {
		return new JobBuilder("jobReporteUsuariosPermisos", jobRepository).start(stepReporteUsuariosPermisos)
				.next(stepSftpUploadOutput).build();
	}

	@Bean
	Job jobSendLogs(JobRepository jobRepository, @Qualifier("stepSendLogs") Step stepSendLogs) {
		return new JobBuilder("jobSendLogs", jobRepository).start(stepSendLogs).next(stepSendLogs).build();
	}

    @Bean
    Job jobGeneraInformeLog(JobRepository jobRepository,
                            @Qualifier("stepInformeLog") Step stepInformeLog,
                            @Qualifier("stepUsuariosCaptio") Step stepUsuariosCaptio,
                            @Qualifier("stepInformeCaptio") Step stepInformeCaptio,
                            @Qualifier("stepWorkflowStep") Step stepWorkflowStep,
                            @Qualifier("stepSftpUploadOutput") Step stepSftpUploadOutput) {
        return new JobBuilder("jobGeneraInformeLog", jobRepository)
                .start(stepUsuariosCaptio)
                .next(stepInformeCaptio)
                .next(stepWorkflowStep)
                .next(stepInformeLog)
                .next(stepSftpUploadOutput)
                .build();
    }


	@Bean
	Step stepUsuarios(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepUsuarios", jobRepository)
				.<Map<String, String>, Map<String, String>>chunk(100, transactionManager).reader(csvUsuarioItemReader)
				.writer(csvUsuariosItemWriter).build();
	}

	@Bean
	Step stepCategorias(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepCategorias", jobRepository)
				.<Map<String, String>, Map<String, String>>chunk(1000, transactionManager)
				.reader(csvCategoriaItemReader).writer(csvctegoriasItemWriter).build();
	}

	@Bean
	Step stepSonar(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepSonar", jobRepository).<UsuarioDTO, UsuarioDTO>chunk(1000, transactionManager)
				.reader(usuarioSonarItemReader).writer(usuarioSonarItemWriter).build();
	}

	@Bean
	Step stepSyncPersonas(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepSyncPersonas", jobRepository).tasklet(personasSyncTasklet, transactionManager)
				.build();
	}

    @Bean
    Step stepSyncEmpleados(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepSyncEmpleados", jobRepository).tasklet(empleadosSyncTasklet, transactionManager)
                .build();
    }

	@Bean
	Step stepWorkFlow(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepWorkFlow", jobRepository).tasklet(flujoAprobacionTasklet, transactionManager)
				.build();
	}

    @Bean
    Step stepWorkFlowEmpleados(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepWorkFlowEmpleados", jobRepository)
                .tasklet(flujoAprobacionEmpleadosTasklet, transactionManager)
                .build();
    }

	@Bean
	Step stepGrupo(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepGrupo", jobRepository).tasklet(grupoTasklet, transactionManager).build();
	}

    @Bean
    Step stepGrupoEmpleado(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepGrupoEmpleado", jobRepository)
                .tasklet(grupoEmpleadoTasklet, transactionManager)
                .build();
    }

	@Bean
	Step stepSftpDownloadTasklet(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepSftpDownloadTasklet", jobRepository)
				.tasklet(sftpDownloadTasklet, transactionManager).build();
	}

	@Bean
	Step stepSftpUploadError(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepSftpUploadError", jobRepository).tasklet(sftpUploadErrorTasklet, transactionManager)
				.build();
	}

	@Bean
	Step stepSftpUploadOutput(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepSftpUploadOutput", jobRepository)
				.tasklet(sftpUploadOutputTasklet, transactionManager).build();
	}

	@Bean
	Step stepInformeViajes(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepInformeViajes", jobRepository).<ViajeDTO, InformeDTO>chunk(100, transactionManager)
				.reader(restInformeItemReader).processor(informeViajeProcessor).writer(informeViajesItemWriter).build();
	}

	@Bean
	Step stepInformeViajesDiario(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepInformeViajesDiario", jobRepository)
				.<ViajeDTO, InformeDTO>chunk(100, transactionManager).reader(informeViajeDiarioReader)
				.processor(informeViajeDiarioProcessor).writer(informeViajeDiarioWriter).build();
	}

	@Bean
	Step stepInformeViajesValidacion(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepInformeViajesValidacion", jobRepository)
				.<InformeDTO, InformeDTO>chunk(100, transactionManager).reader(informeViajeValidacionReader)
				.processor(informeViajeValidacionProcessor).writer(informeViajeValidacionWriter).build();
	}

	@Bean
	Step stepInformeOtrosGastos(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepInformeOtrosGastos", jobRepository)
				.<InformeDTO, InformeDTO>chunk(100, transactionManager).reader(informeOtrosGastosReader)
				.processor(informeOtrosGastosProcessor).writer(informeOtrosGastosWriter).build();
	}

	@Bean
	Step stepReporteOtrosGastosEnInforme(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepReporteOtrosGastosEnInforme", jobRepository)
				.<InformeDTO, InformeDTO>chunk(100, transactionManager).reader(reporteOtrosGastosEnInformeReader)
				.processor(reporteOtrosGastosEnInformeProcessor).writer(reporteOtrosGastosEnInformeWriter).build();
	}

	@Bean
	Step stepGastos(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepGastos", jobRepository).<GastoDTO, ExpenseDTO>chunk(100, transactionManager)
				.reader(gastosReader).processor(gastosProcessor).writer(gastosWriter).build();
	}

	@Bean
	Step stepGastosMedicos(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepGastosMedicos", jobRepository).<GastoDTO, GastoDTO>chunk(100, transactionManager)
				.reader(gastosMedicosReader).processor(gastosMedicosProcessor).writer(gastosMedicosWriter).build();
	}

	@Bean
	Step stepGastosVuelosAgencia(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepGastosVuelosAgencia", jobRepository).<GastoDTO, GastoDTO>chunk(100, transactionManager)
				.reader(gastosVuelosAgenciaReader).processor(gastosVuelosAgenciaProcessor).writer(gastosVuelosAgenciaWriter).build();
	}

	@Bean
	Step stepInformeGastosMedicos(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepInformeGastosMedicos", jobRepository)
				.<InformeDTO, InformeDTO>chunk(100, transactionManager).reader(informeGastosMedicosReader)
				.writer(informeGastosMedicosWriter).build();
	}

	@Bean
	Step stepDotacionViajes(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepDotacionViajes", jobRepository)
				.<ViajeDTO, DotacionDTO>chunk(100, transactionManager).reader(dotacionItemReader)
				.processor(dotacionProcessor).writer(dotacionItemWriter).build();
	}

	// InformeDIOT
	@Bean
	Step stepGastosDIOT(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepGastosDIOT", jobRepository)
				.<InformeDTO, DiotDTO>chunk(100, transactionManager)
				.reader(gastosDIOTReader)
				.processor(gastosDIOTProcessor)
				.writer(gastosDIOTWriter).build();
	}

	// ReporteDescuentoNomina
	@Bean
	Step stepReporteDescuentoNomina(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepReporteDescuentoNomina", jobRepository)
				.<DescuentoNominaDTO, DescuentoNominaDTO>chunk(100, transactionManager)
				.reader(reporteDescuentoNominaReader).writer(reporteDescuentoNominaWriter).build();
	}

	// ReporteUsuariosPermisos - rmiranda
	@Bean
	Step stepReporteUsuariosPermisos(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepReporteUsuariosPermisos", jobRepository)
				.<UserPermissionDTO, UserPermissionDTO>chunk(100, transactionManager)
				.reader(reporteUsuariosPermisosReader).writer(reporteUsuariosPermisosWriter).build();
	}

	@Bean
	Step stepBalanzaContable(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepBalanzaContable", jobRepository)
				.<InformeDTO, BalanzaDTO>chunk(100, transactionManager).reader(balanzaContableReader)
				.processor(balanzaContableProcessor).writer(balanzaContableWriter).build();
	}

	@Bean
	Step stepReporteACEC(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepReporteACEC", jobRepository).<InformeDTO, AcecDTO>chunk(100, transactionManager)
				.reader(reporteACECReader).processor(reporteACECProcessor).writer(reporteACECWriter).build();
	}

	@Bean
	Step stepSendLogs(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepSendLogs", jobRepository)
				.tasklet(sendLogsTasklet, transactionManager).build();

	}

	@Bean
	Step stepInformeLog(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("stepInformeLog", jobRepository)
				.<InformeLogDTO, InformeLogDTO>chunk(100, transactionManager)
				.reader(informeLogReader)
				.processor(informeLogProcessor)
				.writer(informeLogWriter).build();
	}

    @Bean
    Step stepUsuariosCaptio(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepUsuariosCaptio", jobRepository)
                .tasklet(usuarioCaptioTasklet, transactionManager).build();
    }

    @Bean
    Step stepInformeCaptio(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepInformeCaptio", jobRepository)
                .tasklet(informeCaptioTasklet, transactionManager).build();
    }

    @Bean
    Step stepWorkflowStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepWorkflowStep", jobRepository)
                .tasklet(workflowStepTasklet, transactionManager).build();
    }

    @Bean
    Step stepEmpleadoSonar(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepEmpleadoSonar", jobRepository)
                .tasklet(empleadoSonarTasklet, transactionManager).build();
    }
}
