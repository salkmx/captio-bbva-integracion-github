package com.sngular.captio.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobsConfig {

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
                .next(stepSftpUploadError)
                .build();
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
                .next(stepSftpUploadError)
                .build();
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

    @Bean
    Job jobInformeDIOT(JobRepository jobRepository, @Qualifier("stepGastosDIOT") Step stepGastosDIOT,
                       @Qualifier("stepSftpUploadOutput") Step stepSftpUploadOutput) {
        return new JobBuilder("jobInformeDIOT", jobRepository).start(stepGastosDIOT).next(stepSftpUploadOutput).build();
    }

    @Bean
    Job jobReporteDescuentoNomina(JobRepository jobRepository,
                                  @Qualifier("stepReporteDescuentoNomina") Step stepReporteDescuentoNomina,
                                  @Qualifier("stepSftpUploadOutput") Step stepSftpUploadOutput) {
        return new JobBuilder("jobReporteDescuentoNomina", jobRepository).start(stepReporteDescuentoNomina)
                .next(stepSftpUploadOutput).build();
    }

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
}
