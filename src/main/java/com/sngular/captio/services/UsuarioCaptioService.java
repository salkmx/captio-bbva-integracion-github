package com.sngular.captio.services;

import com.sngular.captio.dto.UsuarioCaptioDTO;
import com.sngular.captio.model.UsuarioCaptio;

import java.util.List;

public interface UsuarioCaptioService extends IGenericService<UsuarioCaptio, UsuarioCaptioDTO, Long> {

    /**
     * Save all obtained data on DB
     * @param entities
     */
    void saveAll(List<UsuarioCaptio> entities);

}
