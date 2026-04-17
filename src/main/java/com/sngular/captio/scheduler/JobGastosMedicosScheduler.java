package com.sngular.captio.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobGastosMedicosScheduler {

	private final JobLauncher jobLauncher;

	private final Job jobGastosMedicos;

	public JobGastosMedicosScheduler(@Qualifier("jobGastosMedicos") Job jobGastosMedicos, JobLauncher jobLauncher) {
		this.jobGastosMedicos = jobGastosMedicos;
		this.jobLauncher = jobLauncher;
	}

	@Scheduled(cron = "${scheduler.cron.expresion.carga.gastos.medicos}")
	public void ejecutarJob() throws Exception {
		JobParameters params = new JobParametersBuilder().toJobParameters();
		jobLauncher.run(jobGastosMedicos, params);
	}

}
