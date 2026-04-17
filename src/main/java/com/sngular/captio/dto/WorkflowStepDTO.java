package com.sngular.captio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@AllArgsConstructor
public class WorkflowStepDTO {
    private Long workflowId;
    private Long stepId;
    private String stepName;
}
