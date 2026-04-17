package com.sngular.captio.dto;

import java.util.Map;

public record DynamicRecordRequestDTO(String url, String method, String body, Map<String, String> headers) {

}
