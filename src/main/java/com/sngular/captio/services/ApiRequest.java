package com.sngular.captio.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sngular.captio.dto.GenericResponseDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.ValidationDTO;
import com.sngular.captio.dto.WorkFlowDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.util.CaptioJsonUtils;
import com.sngular.captio.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class ApiRequest<DTO> {

    protected HttpEntity<List<DTO>> setupEntity(List<DTO> list, String customerKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("customerKey", customerKey);
        return new HttpEntity<>(list, headers);
    }

    protected HttpEntity<Void> setupEntity(String customerKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("customerKey", customerKey);
        return new HttpEntity<>(headers);
    }

    protected HttpEntity<List<Map<String, Object>>>setupEntityPayload(List<Map<String, Object>> payload, String customerKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("customerKey", customerKey);
        return new HttpEntity<>(payload, headers);
    }

    protected void message(String message) {
        List<GenericResponseDTO<DTO>> result = new ArrayList<>();
        String jsonFormateado = CaptioJsonUtils.obtenerJsonError(message);
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.readValue(jsonFormateado,
                    new TypeReference<List<GenericResponseDTO<DTO>>>() {
                    });
            writeError(jsonFormateado, result);
        } catch (JsonProcessingException e) {
            log.error("Error 400 - Bad Request: ", e);
        }
    }

    protected abstract void writeError(String formattedJson, List<GenericResponseDTO<DTO>> result);
}
