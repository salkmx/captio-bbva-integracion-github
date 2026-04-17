package com.sngular.captio.config;

import java.util.Map;

import com.sngular.captio.dto.InformeLogDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.reader.CategoriaItemReader;
import com.sngular.captio.reader.InformeLogReader;
import com.sngular.captio.reader.UsuarioItemReader;
import com.sngular.captio.reader.UsuarioSonarItemReader;
import com.sngular.captio.processor.InformeLogProcessor;
import com.sngular.captio.tasklet.*;
import com.sngular.captio.writer.CategoriasItemWriter;
import com.sngular.captio.writer.InformeLogWriter;
import com.sngular.captio.writer.UsuarioSonarItemWriter;
import com.sngular.captio.writer.UsuariosItemWriter;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class SyncStepsConfig {

    private final UsuarioItemReader csvUsuarioItemReader;
    private final CategoriaItemReader csvCategoriaItemReader;
    private final UsuariosItemWriter csvUsuariosItemWriter;
    private final CategoriasItemWriter csvctegoriasItemWriter;
    private final UsuarioSonarItemReader usuarioSonarItemReader;
    private final UsuarioSonarItemWriter usuarioSonarItemWriter;

    private final UsuariosSyncTasklet personasSyncTasklet;
    private final EmpleadosSyncTasklet empleadosSyncTasklet;
    private final SftpDownloadTasklet sftpDownloadTasklet;
    private final SftpUploadErrorTasklet sftpUploadErrorTasklet;
    private final SftpUploadOutputTasklet sftpUploadOutputTasklet;
    private final FlujoAprobacionTasklet flujoAprobacionTasklet;
    private final FlujoAprobacionEmpleadosTasklet flujoAprobacionEmpleadosTasklet;
    private final GrupoTasklet grupoTasklet;
    private final GrupoEmpleadoTasklet grupoEmpleadoTasklet;
    private final SendLogsTasklet sendLogsTasklet;
    private final UsuarioCaptioTasklet usuarioCaptioTasklet;
    private final InformeCaptioTasklet informeCaptioTasklet;
    private final WorkflowStepTasklet workflowStepTasklet;
    private final EmpleadoSonarTasklet empleadoSonarTasklet;

    private final InformeLogReader informeLogReader;
    private final InformeLogProcessor informeLogProcessor;
    private final InformeLogWriter informeLogWriter;

    @Bean
    Step stepUsuarios(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepUsuarios", jobRepository)
                .<Map<String, String>, Map<String, String>>chunk(100, transactionManager)
                .reader(csvUsuarioItemReader)
                .writer(csvUsuariosItemWriter)
                .build();
    }

    @Bean
    Step stepCategorias(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepCategorias", jobRepository)
                .<Map<String, String>, Map<String, String>>chunk(1000, transactionManager)
                .reader(csvCategoriaItemReader)
                .writer(csvctegoriasItemWriter)
                .build();
    }

    @Bean
    Step stepSonar(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepSonar", jobRepository)
                .<UsuarioDTO, UsuarioDTO>chunk(1000, transactionManager)
                .reader(usuarioSonarItemReader)
                .writer(usuarioSonarItemWriter)
                .build();
    }

    @Bean
    Step stepSyncPersonas(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepSyncPersonas", jobRepository)
                .tasklet(personasSyncTasklet, transactionManager)
                .build();
    }

    @Bean
    Step stepSyncEmpleados(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepSyncEmpleados", jobRepository)
                .tasklet(empleadosSyncTasklet, transactionManager)
                .build();
    }

    @Bean
    Step stepWorkFlow(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepWorkFlow", jobRepository)
                .tasklet(flujoAprobacionTasklet, transactionManager)
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
        return new StepBuilder("stepGrupo", jobRepository)
                .tasklet(grupoTasklet, transactionManager)
                .build();
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
                .tasklet(sftpDownloadTasklet, transactionManager)
                .build();
    }

    @Bean
    Step stepSftpUploadError(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepSftpUploadError", jobRepository)
                .tasklet(sftpUploadErrorTasklet, transactionManager)
                .build();
    }

    @Bean
    Step stepSftpUploadOutput(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepSftpUploadOutput", jobRepository)
                .tasklet(sftpUploadOutputTasklet, transactionManager)
                .build();
    }

    @Bean
    Step stepSendLogs(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepSendLogs", jobRepository)
                .tasklet(sendLogsTasklet, transactionManager)
                .build();
    }

    @Bean
    Step stepUsuariosCaptio(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepUsuariosCaptio", jobRepository)
                .tasklet(usuarioCaptioTasklet, transactionManager)
                .build();
    }

    @Bean
    Step stepInformeCaptio(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepInformeCaptio", jobRepository)
                .tasklet(informeCaptioTasklet, transactionManager)
                .build();
    }

    @Bean
    Step stepWorkflowStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepWorkflowStep", jobRepository)
                .tasklet(workflowStepTasklet, transactionManager)
                .build();
    }

    @Bean
    Step stepEmpleadoSonar(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepEmpleadoSonar", jobRepository)
                .tasklet(empleadoSonarTasklet, transactionManager)
                .build();
    }

    @Bean
    Step stepInformeLog(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepInformeLog", jobRepository)
                .<InformeLogDTO, InformeLogDTO>chunk(100, transactionManager)
                .reader(informeLogReader)
                .processor(informeLogProcessor)
                .writer(informeLogWriter)
                .build();
    }
}
