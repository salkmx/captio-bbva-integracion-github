package com.sngular.captio.services;

import com.sngular.captio.dto.WorkFlowDTO;

import java.util.List;

public interface WorkflowService {

    List<WorkFlowDTO> findWorkflows(String filters);

}
