package com.sngular.captio.services.impl;

import com.sngular.captio.dto.WorkflowStepDTO;
import com.sngular.captio.model.WorkflowStep;
import com.sngular.captio.repository.WorkflowStepRepository;
import com.sngular.captio.services.WorkflowStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowStepServiceImpl implements WorkflowStepService {

    private final WorkflowStepRepository workflowStepRepository;

    @Override
    public void saveAll(List<WorkflowStep> steps) {
        workflowStepRepository.saveAll(steps);
    }

    @Override
    public WorkflowStep save(WorkflowStep entity) {
        return workflowStepRepository.save(entity);
    }

    @Override
    public WorkflowStep update(WorkflowStep entity) {
        return null;
    }

    @Override
    public Optional<WorkflowStepDTO> findOneById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<WorkflowStepDTO> findAllById(Long id) {
        return List.of();
    }

    @Override
    public Optional<WorkflowStepDTO> findOneByStepId(Long stepId) {
        return workflowStepRepository.findOneByStepId(stepId);
    }

    @Override
    public void deleteAll() {

    }
}
