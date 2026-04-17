package com.sngular.captio.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobInformeViajesScheduler {

	private final JobLauncher jobLauncher;

	private final Job jobInformeViajes;

	public JobInformeViajesScheduler(@Qualifier("jobInformeViajes") Job jobInformeViajes, JobLauncher jobLauncher) {
		this.jobInformeViajes = jobInformeViajes;
		this.jobLauncher = jobLauncher;
	}

	@Scheduled(cron = "${scheduler.cron.expresion.informe.viajes}")
	public void ejecutarJob() throws Exception {
		JobParameters params = new JobParametersBuilder().toJobParameters();
		jobLauncher.run(jobInformeViajes, params);
	}

}
