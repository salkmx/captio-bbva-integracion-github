package com.sngular.captio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sngular.captio.model.CuentaContable;

public interface CuentaContableRepository extends JpaRepository<CuentaContable, Long> {

	@Query("""
			SELECT c
			FROM CuentaContable c
			WHERE (:idCategoria IS NULL OR c.idCategoria = :idCategoria)
			  AND (:idTipoGasto IS NULL OR c.idTipoGasto = :idTipoGasto)
			  AND (:idMedioPago IS NULL OR c.idMedioPago = :idMedioPago)
			  AND (:idEmpresa IS NULL OR c.idEmpresa = :idEmpresa)
			""")
	List<CuentaContable> searchAllFieldsExceptId(@Param("idCategoria") Integer idCategoria,
			@Param("idTipoGasto") Integer idTipoGasto, @Param("idMedioPago") Integer idMedioPago,
			@Param("idEmpresa") Integer idEmpresa);

	@Query("""
			SELECT c
			FROM CuentaContable c
			WHERE (:idCategoria IS NULL OR c.idCategoria = :idCategoria)
			  AND (:idTipoGasto IS NULL OR c.idTipoGasto = :idTipoGasto)
			  AND (:idEmpresa IS NULL OR c.idEmpresa = :idEmpresa)
			  AND (:cuentaContable IS NULL OR c.cuentaContable = :cuentaContable)
			""")
	List<CuentaContable> searchWithoutMedioPago(@Param("idCategoria") Integer idCategoria,
			@Param("idTipoGasto") Integer idTipoGasto, @Param("idEmpresa") Integer idEmpresa,
			@Param("cuentaContable") String cuentaContable);

}
