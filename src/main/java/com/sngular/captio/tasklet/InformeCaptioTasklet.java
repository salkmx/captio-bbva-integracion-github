package com.sngular.captio.tasklet;

import com.sngular.captio.model.InformeCaptio;
import com.sngular.captio.services.InformeCaptioService;
import com.sngular.captio.services.InformeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class InformeCaptioTasklet implements Tasklet {

    private final InformeService informeService;
    private final InformeCaptioService informeCaptioService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("[{}] Executando proceso para guardar informes captio", this.getClass().getSimpleName());
        var informes = informeService.obtenerInformes("{\"Status\":[1,2,3,4], \"}");

        log.info("[{}] Informes encontrados: {} ",this.getClass().getSimpleName(), informes.size());

        var informesCaptio = informes.stream().map(inf -> new InformeCaptio()
                        .setReportId(inf.getId().longValue())
                        .setName(inf.getName())
                        .setCode(inf.getCode()))
                .toList();

        informeCaptioService.saveAll(informesCaptio);
        return RepeatStatus.FINISHED;
    }


}
