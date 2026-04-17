package com.sngular.captio.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.sngular.captio.services.LogsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendLogsTasklet implements Tasklet {

	private final LogsService logsService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info("========== INICIANDO ENVIO DE LOGS ==========");
		sendLogs();
		log.info("========== ENVIO DE LOGS COMPLETADO ==========");
		return RepeatStatus.FINISHED;
	}

	private void sendLogs() {
		try {
			logsService.sendLogs();
		} catch (Exception e) {
			log.error("Error al enviar logs", e);
		}
	}
}