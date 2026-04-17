package com.sngular.captio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sngular.captio.model.UsuarioSonar;

@Repository
public interface UsuarioSonarRepository extends JpaRepository<UsuarioSonar, Long> {

	@Query("select c from UsuarioSonar c where c.estadoEmpleado = 'ACT'")
	List<UsuarioSonar> obtenerUsuariosActivos();
	
	@Query("select c from UsuarioSonar c where c.estadoEmpleado = 'ACT' and email = :correo ")
	UsuarioSonar obtenerUsuariosActivosByCorreo(@Param("correo") String correo);

	@Query("select c from UsuarioSonar c where c.estadoEmpleado = 'BAJ'")
	List<UsuarioSonar> obtenerUsuariosBaja();

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = "UPDATE usuario_sonar SET estado_empleado = 'ERR' WHERE email = :correo ", nativeQuery = true)
	int actualizaEstatusAPI(@Param("correo") String correo);

}
