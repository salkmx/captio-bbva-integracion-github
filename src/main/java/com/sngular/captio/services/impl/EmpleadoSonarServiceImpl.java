package com.sngular.captio.services.impl;

import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.mapper.EmpleadoSonarMapper;
import com.sngular.captio.model.EmpleadoSonar;
import com.sngular.captio.repository.EmpleadoSonarRepository;
import com.sngular.captio.repository.OpcionesEmpleadoRepository;
import com.sngular.captio.services.EmpleadoSonarService;
import com.sngular.captio.services.IGenericService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmpleadoSonarServiceImpl implements EmpleadoSonarService {

    private final EmpleadoSonarRepository empleadoSonarRepository;
    private final OpcionesEmpleadoRepository opcionesEmpleadoRepository;

//    @Override
//    public void saveAll(List<EmpleadoSonar> entities) {
//        empleadoSonarRepository.saveAll(entities);
//    }

    @Override
    public EmpleadoSonar save(EmpleadoSonar entity) {
        empleadoSonarRepository.save(entity);
        entity.getOptions().setEmpleado(entity);
        opcionesEmpleadoRepository.save(entity.getOptions());
        return null;
    }

    @Override
    public EmpleadoSonar update(EmpleadoSonar entity) {
        throw new RuntimeException("Method update not implemented");
    }

    @Override
    public Optional<UsuarioDTO> findOneById(Long id) {
        throw new RuntimeException("Method findOneById not implemented");
    }

    @Override
    public List<UsuarioDTO> findAllById(Long id) {
        throw new RuntimeException("Method findAllById not implemented");
    }

    @Override
    public EmpleadoSonar findOneByEmail(String email) {
        return empleadoSonarRepository.findOneByEmail(email)
                .orElse(null);
    }

    @Override
    public EmpleadoSonar findOneByEmployeeCode(String employeeCode) {
        return empleadoSonarRepository.findOneByEmployeeCode(employeeCode)
                .orElse(null);
    }

    @Override
    public List<UsuarioDTO> findAll() {
        var result = empleadoSonarRepository.findAll();
        var mapper = Mappers.getMapper(EmpleadoSonarMapper.class);
        return mapper.toDtoList(result);
    }

    @Override
    public void deleteAll() {
        throw new RuntimeException("Method deleteAll not implemented");
    }
}
