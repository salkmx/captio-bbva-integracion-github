package com.sngular.captio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sngular.captio.model.OpcionesUsuario;

@Repository
public interface OpcionesUsuarioRepository extends JpaRepository<OpcionesUsuario, Long> {

	@Query("select c from OpcionesUsuario c where c.usuario.id = :usuarioId")
	OpcionesUsuario obtenerOpcionesPorUsuario(@Param("usuarioId") Long usuarioId);

}
