package com.sngular.captio.repository;

import com.sngular.captio.dto.UsuarioCaptioDTO;
import com.sngular.captio.model.UsuarioCaptio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioCaptioRepository extends JpaRepository<UsuarioCaptio, Long> {

    @Query("""
        SELECT new com.sngular.captio.dto.UsuarioCaptioDTO(
            u.userId,
            u.employeeCode,
            u.email
        )
        FROM UsuarioCaptio u WHERE u.userId = :userId
    """)
    Optional<UsuarioCaptioDTO> findOneByUserId(Long userId);

}
