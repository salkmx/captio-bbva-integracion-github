package com.sngular.captio.services.impl;

import com.sngular.captio.dto.UsuarioCaptioDTO;
import com.sngular.captio.model.UsuarioCaptio;
import com.sngular.captio.repository.UsuarioCaptioRepository;
import com.sngular.captio.services.UsuarioCaptioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioCaptioServiceImpl implements UsuarioCaptioService {

    private final UsuarioCaptioRepository usuarioCaptioRepository;

    @Override
    public void saveAll(List<UsuarioCaptio> entities) {
        usuarioCaptioRepository.saveAll(entities);
    }

    @Override
    public UsuarioCaptio save(UsuarioCaptio entity) {
        return usuarioCaptioRepository.save(entity);
    }

    @Override
    public UsuarioCaptio update(UsuarioCaptio entity) {
        return null;
    }

    @Override
    public Optional<UsuarioCaptioDTO> findOneById(Long id) {
        return usuarioCaptioRepository.findOneByUserId(id);
    }

    @Override
    public List<UsuarioCaptioDTO> findAllById(Long id) {
        return List.of();
    }

    @Override
    public void deleteAll() {

    }
}
