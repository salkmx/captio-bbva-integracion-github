package com.sngular.captio.tasklet;

import com.google.common.base.Stopwatch;
import com.sngular.captio.mapper.EmpleadoSonarMapper;
import com.sngular.captio.services.EmpleadoSonarService;
import com.sngular.captio.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmpleadoSonarTasklet implements Tasklet {

    private final UsuarioService usuarioService;
    private final EmpleadoSonarService empleadoSonarService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        var sp = Stopwatch.createStarted();
        var users = usuarioService.obtenerUsuarioByFiltro("");
        users.forEach(usr -> usr.setOptions(usr.getUserOptions()));

        EmpleadoSonarMapper empleadoSonarMapper = Mappers.getMapper(EmpleadoSonarMapper.class);

        var empleados = empleadoSonarMapper.toEntityList(users);

        empleados.forEach(empleadoSonarService::save);

        log.info("[execute] took {}sg = {}ms", sp.elapsed(TimeUnit.SECONDS), sp.elapsed(TimeUnit.MILLISECONDS));
        return RepeatStatus.FINISHED;
    }
}
