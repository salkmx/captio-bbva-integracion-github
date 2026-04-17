package com.sngular.captio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "informes_captio")
@Data
@Accessors(chain = true)
public class InformeCaptio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_informe_captio")
    private Long id;

    private Long reportId;

    private String name;

    private String code;

}
