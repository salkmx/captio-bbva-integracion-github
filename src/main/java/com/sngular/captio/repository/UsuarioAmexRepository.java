package com.sngular.captio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sngular.captio.model.UsuarioAmex;

@Repository
public interface UsuarioAmexRepository extends JpaRepository<UsuarioAmex, Long> {

}
