package com.sngular.captio.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobReporteDescuentoNominaScheduler {

	private final JobLauncher jobLauncher;

	private final Job jobReporteDescuentoNomina;

	public JobReporteDescuentoNominaScheduler(@Qualifier("jobReporteDescuentoNomina") Job jobReporteDescuentoNomina, JobLauncher jobLauncher) {
		this.jobReporteDescuentoNomina = jobReporteDescuentoNomina;
		this.jobLauncher = jobLauncher;
	}

	@Scheduled(cron = "${scheduler.cron.expresion.reporte.descuento.nomina}")
	public void ejecutarJob() throws Exception {
		JobParameters params = new JobParametersBuilder().toJobParameters();
		jobLauncher.run(jobReporteDescuentoNomina, params);
	}

}
