package com.sngular.captio.tasklet;

import com.sngular.captio.model.WorkflowStep;
import com.sngular.captio.services.WorkflowService;
import com.sngular.captio.services.WorkflowStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowStepTasklet implements Tasklet {

    private final WorkflowService workflowService;
    private final WorkflowStepService workflowStepService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("[{}] Starting WorkflowSteps Tasklet", this.getClass().getSimpleName());
        var workflows = workflowService.findWorkflows("");

        var workflowList = new ArrayList<WorkflowStep>();
        workflows.forEach(wf -> {
            if (wf.getSteps() != null) {
                var workflowSteps = wf.getSteps().stream().map(step -> new WorkflowStep()
                                .setWorkflowId(wf.getId().longValue())
                                .setWorkflowName(wf.getName())
                                .setStepId(step.getId().longValue())
                                .setStepName(step.getName()))
                        .toList();
                workflowList.addAll(workflowSteps);
            }
        });

        workflowStepService.saveAll(workflowList);
        return RepeatStatus.FINISHED;
    }

}
