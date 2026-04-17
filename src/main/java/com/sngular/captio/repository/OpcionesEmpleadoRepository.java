package com.sngular.captio.repository;

import com.sngular.captio.model.OpcionesEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpcionesEmpleadoRepository extends JpaRepository<OpcionesEmpleado, Long> {
}
