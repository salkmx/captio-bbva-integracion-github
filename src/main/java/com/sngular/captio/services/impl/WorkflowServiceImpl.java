package com.sngular.captio.services.impl;

import com.sngular.captio.dto.GenericResponseDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.ValidationDTO;
import com.sngular.captio.dto.WorkFlowDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.ApiRequest;
import com.sngular.captio.services.WorkflowService;
import com.sngular.captio.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowServiceImpl extends ApiRequest<WorkFlowDTO> implements WorkflowService {

    private final RestTemplate restTemplate;
    private final Properties properties;

    @Override
    public List<WorkFlowDTO> findWorkflows(String filters) {
        log.info("[{}] Starting find all Workflows", this.getClass().getSimpleName());
        var entity = setupEntity(properties.getCustomerKey());

        try {
            var response = restTemplate.exchange(
                    properties.getUrlGetWorkflow(),
                    HttpMethod.GET, entity,
                    new ParameterizedTypeReference<List<WorkFlowDTO>>() {
                    });
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.error("[{}] Error 400 - Bad Request: ", this.getClass().getSimpleName(), e);
                this.message(e.getMessage());
            }
        }
        return List.of();
    }


    @Override
    protected void writeError(String formattedJson, List<GenericResponseDTO<WorkFlowDTO>> result) {
        if (result == null) return;
        try {
            String path = properties.getRutaArchivoErrorWorkFlow() + DateUtils.obtenerFechaActual() + ".csv";
            StringBuilder sb = new StringBuilder();
            for (GenericResponseDTO<WorkFlowDTO> error : result) {
                sb.append(error.getValue());
                sb.append(System.lineSeparator());
                if (error.getValidations() != null) {
                    sb.append(error.getValidations().stream().map(ValidationDTO::getMessage)
                            .filter(Objects::nonNull).collect(Collectors.joining(System.lineSeparator())));
                }
            }

            File archivo = new File(path);
            archivo.getParentFile().mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
                writer.write(formattedJson);
                writer.newLine();
            }
        } catch (IOException e) {
            log.error("[{}] Error al escribir registro: {} ", this.getClass().getSimpleName(), e.getMessage(), e);
        }
    }
}
