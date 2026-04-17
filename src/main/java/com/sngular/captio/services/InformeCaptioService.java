package com.sngular.captio.services;

import com.sngular.captio.dto.InformeCaptioDTO;
import com.sngular.captio.model.InformeCaptio;

import java.util.List;

public interface InformeCaptioService extends IGenericService<InformeCaptio, InformeCaptioDTO, Long> {

    void saveAll(List<InformeCaptio> entities);

}
