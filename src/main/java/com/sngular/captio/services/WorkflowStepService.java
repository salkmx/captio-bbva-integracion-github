package com.sngular.captio.services;

import com.sngular.captio.dto.WorkflowStepDTO;
import com.sngular.captio.model.WorkflowStep;

import java.util.List;
import java.util.Optional;

public interface WorkflowStepService extends IGenericService<WorkflowStep, WorkflowStepDTO, Long> {

    void saveAll(List<WorkflowStep> steps);

    Optional<WorkflowStepDTO> findOneByStepId(Long stepId);

}
