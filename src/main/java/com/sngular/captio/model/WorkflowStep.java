package com.sngular.captio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "workflow_step")
@Accessors(chain = true)
@Data
public class WorkflowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_workflow_step")
    private Long id;

    private Long workflowId;

    private String workflowName;

    private Long stepId;

    private String stepName;

}
