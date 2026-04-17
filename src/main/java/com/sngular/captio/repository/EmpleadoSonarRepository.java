package com.sngular.captio.repository;

import com.sngular.captio.model.EmpleadoSonar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpleadoSonarRepository extends JpaRepository<EmpleadoSonar, Long> {

    @Query("SELECT e FROM EmpleadoSonar e WHERE e.email = :email")
    Optional<EmpleadoSonar> findOneByEmail(String email);

    @Query("SELECT e FROM EmpleadoSonar e WHERE e.options.employeeCode = :employeeCode")
    Optional<EmpleadoSonar> findOneByEmployeeCode(String employeeCode);

}
