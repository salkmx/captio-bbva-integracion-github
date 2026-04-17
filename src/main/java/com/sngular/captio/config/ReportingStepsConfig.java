package com.sngular.captio.config;

import com.sngular.captio.dto.*;
import com.sngular.captio.processor.*;
import com.sngular.captio.reader.*;
import com.sngular.captio.writer.*;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class ReportingStepsConfig {

    private final InformeViajeReader restInformeItemReader;
    private final InformeViajesWriter informeViajesItemWriter;
    private final InformeViajeProcessor informeViajeProcessor;

    private final DotacionItemReader dotacionItemReader;
    private final DotacionProcessor dotacionProcessor;
    private final DotacionItemWriter dotacionItemWriter;

    private final InformeOtrosGastosReader informeOtrosGastosReader;
    private final InformeOtrosGastosProcessor informeOtrosGastosProcessor;
    private final InformeOtrosGastosWriter informeOtrosGastosWriter;

    private final ReporteOtrosGastosEnInformeReader reporteOtrosGastosEnInformeReader;
    private final ReporteOtrosGastosEnInformeProcessor reporteOtrosGastosEnInformeProcessor;
    private final ReporteOtrosGastosEnInformeWriter reporteOtrosGastosEnInformeWriter;

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

    private final GastosDIOTReader gastosDIOTReader;
    private final GastosDIOTProcessor gastosDIOTProcessor;
    private final GastosDIOTWriter gastosDIOTWriter;

    private final ReporteDescuentoNominaReader reporteDescuentoNominaReader;
    private final ReporteDescuentoNominaWriter reporteDescuentoNominaWriter;

    private final ReporteUsuariosPermisosReader reporteUsuariosPermisosReader;
    private final ReporteUsuariosPermisosWriter reporteUsuariosPermisosWriter;

    @Bean
    Step stepInformeViajes(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepInformeViajes", jobRepository)
                .<ViajeDTO, InformeDTO>chunk(100, transactionManager)
                .reader(restInformeItemReader)
                .processor(informeViajeProcessor)
                .writer(informeViajesItemWriter)
                .build();
    }

    @Bean
    Step stepInformeViajesDiario(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepInformeViajesDiario", jobRepository)
                .<ViajeDTO, InformeDTO>chunk(100, transactionManager)
                .reader(informeViajeDiarioReader)
                .processor(informeViajeDiarioProcessor)
                .writer(informeViajeDiarioWriter)
                .build();
    }

    @Bean
    Step stepInformeViajesValidacion(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepInformeViajesValidacion", jobRepository)
                .<InformeDTO, InformeDTO>chunk(100, transactionManager)
                .reader(informeViajeValidacionReader)
                .processor(informeViajeValidacionProcessor)
                .writer(informeViajeValidacionWriter)
                .build();
    }

    @Bean
    Step stepInformeOtrosGastos(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepInformeOtrosGastos", jobRepository)
                .<InformeDTO, InformeDTO>chunk(100, transactionManager)
                .reader(informeOtrosGastosReader)
                .processor(informeOtrosGastosProcessor)
                .writer(informeOtrosGastosWriter)
                .build();
    }

    @Bean
    Step stepReporteOtrosGastosEnInforme(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepReporteOtrosGastosEnInforme", jobRepository)
                .<InformeDTO, InformeDTO>chunk(100, transactionManager)
                .reader(reporteOtrosGastosEnInformeReader)
                .processor(reporteOtrosGastosEnInformeProcessor)
                .writer(reporteOtrosGastosEnInformeWriter)
                .build();
    }

    @Bean
    Step stepGastos(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepGastos", jobRepository)
                .<GastoDTO, ExpenseDTO>chunk(100, transactionManager)
                .reader(gastosReader)
                .processor(gastosProcessor)
                .writer(gastosWriter)
                .build();
    }

    @Bean
    Step stepGastosMedicos(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepGastosMedicos", jobRepository)
                .<GastoDTO, GastoDTO>chunk(100, transactionManager)
                .reader(gastosMedicosReader)
                .processor(gastosMedicosProcessor)
                .writer(gastosMedicosWriter)
                .build();
    }

    @Bean
    Step stepGastosVuelosAgencia(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepGastosVuelosAgencia", jobRepository)
                .<GastoDTO, GastoDTO>chunk(100, transactionManager)
                .reader(gastosVuelosAgenciaReader)
                .processor(gastosVuelosAgenciaProcessor)
                .writer(gastosVuelosAgenciaWriter)
                .build();
    }

    @Bean
    Step stepInformeGastosMedicos(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepInformeGastosMedicos", jobRepository)
                .<InformeDTO, InformeDTO>chunk(100, transactionManager)
                .reader(informeGastosMedicosReader)
                .writer(informeGastosMedicosWriter)
                .build();
    }

    @Bean
    Step stepDotacionViajes(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepDotacionViajes", jobRepository)
                .<ViajeDTO, DotacionDTO>chunk(100, transactionManager)
                .reader(dotacionItemReader)
                .processor(dotacionProcessor)
                .writer(dotacionItemWriter)
                .build();
    }

    @Bean
    Step stepGastosDIOT(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepGastosDIOT", jobRepository)
                .<InformeDTO, DiotDTO>chunk(100, transactionManager)
                .reader(gastosDIOTReader)
                .processor(gastosDIOTProcessor)
                .writer(gastosDIOTWriter)
                .build();
    }

    @Bean
    Step stepReporteDescuentoNomina(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepReporteDescuentoNomina", jobRepository)
                .<DescuentoNominaDTO, DescuentoNominaDTO>chunk(100, transactionManager)
                .reader(reporteDescuentoNominaReader)
                .writer(reporteDescuentoNominaWriter)
                .build();
    }

    @Bean
    Step stepReporteUsuariosPermisos(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepReporteUsuariosPermisos", jobRepository)
                .<UserPermissionDTO, UserPermissionDTO>chunk(100, transactionManager)
                .reader(reporteUsuariosPermisosReader)
                .writer(reporteUsuariosPermisosWriter)
                .build();
    }

    @Bean
    Step stepBalanzaContable(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepBalanzaContable", jobRepository)
                .<InformeDTO, BalanzaDTO>chunk(100, transactionManager)
                .reader(balanzaContableReader)
                .processor(balanzaContableProcessor)
                .writer(balanzaContableWriter)
                .build();
    }

    @Bean
    Step stepReporteACEC(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepReporteACEC", jobRepository)
                .<InformeDTO, AcecDTO>chunk(100, transactionManager)
                .reader(reporteACECReader)
                .processor(reporteACECProcessor)
                .writer(reporteACECWriter)
                .build();
    }
}
