package com.sngular.captio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class UsuarioCaptioDTO {
    Long userId;
    String employeeCode;
    String email;
}
