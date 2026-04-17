package com.sngular.captio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sngular.captio.model.UsuarioAmexNew;

@Repository
public interface UsuarioAmexNewRepository extends JpaRepository<UsuarioAmexNew, Long> {

	@Query("select c.tdc from UsuarioAmexNew c where c.correoUsuario = :correoUsuario")
	String obtenerTarjeta(String correoUsuario);

}
