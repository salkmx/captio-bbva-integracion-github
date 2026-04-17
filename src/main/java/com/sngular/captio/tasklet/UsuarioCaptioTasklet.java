package com.sngular.captio.tasklet;

import com.sngular.captio.model.UsuarioCaptio;
import com.sngular.captio.services.UsuarioCaptioService;
import com.sngular.captio.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UsuarioCaptioTasklet implements Tasklet {

    private final UsuarioService usuarioService;
    private final UsuarioCaptioService usuarioCaptioService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        var users = usuarioService.obtenerUsuarioByFiltro("");

        var userCaptioEntities = users.stream().map(user -> new UsuarioCaptio()
                        .setUserId(user.getId().longValue())
                        .setEmployeeCode(user.getUserOptions().getEmployeeCode())
                        .setEmail(user.getEmail()))
                .toList();

        usuarioCaptioService.deleteAll();
        usuarioCaptioService.saveAll(userCaptioEntities);
        return RepeatStatus.FINISHED;
    }

}
