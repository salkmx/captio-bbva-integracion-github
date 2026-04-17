package com.sngular.captio.dto;

import java.util.Map;

public record DynamicRecordResponseDTO(int status, Map<String, String> headers, String body) {

}
