package com.sngular.captio.repository;

import com.sngular.captio.dto.InformeCaptioDTO;
import com.sngular.captio.model.InformeCaptio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InformeCaptioRepository extends JpaRepository<InformeCaptio, Long> {

    @Query("""
        SELECT new com.sngular.captio.dto.InformeCaptioDTO(
            i.name,
            i.code
        )
        FROM InformeCaptio i
        WHERE i.reportId = :reportId
    """)
    Optional<InformeCaptioDTO> findOneByReportId(Long reportId);

}
