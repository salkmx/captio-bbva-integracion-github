package com.sngular.captio.repository;

import com.sngular.captio.dto.WorkFlowDTO;
import com.sngular.captio.dto.WorkflowStepDTO;
import com.sngular.captio.model.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Long> {

    @Query("""
        SELECT new com.sngular.captio.dto.WorkflowStepDTO(
            wf.workflowId,
            wf.stepId,
            wf.stepName
        )
        FROM WorkflowStep wf
        WHERE wf.stepId = :stepId
    """)
    Optional<WorkflowStepDTO> findOneByStepId(long stepId);

}
