package com.sngular.captio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class InformeCaptioDTO {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Code")
    private String code;

}
