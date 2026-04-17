package com.sngular.captio.services.impl;

import com.sngular.captio.dto.InformeCaptioDTO;
import com.sngular.captio.model.InformeCaptio;
import com.sngular.captio.repository.InformeCaptioRepository;
import com.sngular.captio.services.InformeCaptioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InformeCaptioServiceImpl  implements InformeCaptioService {

    private final InformeCaptioRepository informeCaptioRepository;

    @Override
    public void saveAll(List<InformeCaptio> entities) {
        informeCaptioRepository.saveAll(entities);
    }

    @Override
    public InformeCaptio save(InformeCaptio entity) {
        return informeCaptioRepository.save(entity);
    }

    @Override
    public InformeCaptio update(InformeCaptio entity) {
        return informeCaptioRepository.save(entity);
    }

    @Override
    public Optional<InformeCaptioDTO> findOneById(Long id) {
        return informeCaptioRepository.findOneByReportId(id);
    }

    @Override
    public List<InformeCaptioDTO> findAllById(Long id) {
        return List.of();
    }

    @Override
    public void deleteAll() {

    }
}
