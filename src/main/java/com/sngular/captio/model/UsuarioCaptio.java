package com.sngular.captio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "usuario_captio")
@Data
@Accessors(chain = true)
public class UsuarioCaptio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_captio")
    private Long id;

    private Long userId;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "email")
    private String email;
}
