/**
 * 
 */
package com.sngular.captio.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enum con los permisos - rmiranda
 */
public enum PermisoEnum {
	
	REPORTADOR(1, "Reportador"),
	REPORTADOR_VER_TODOS_LOS_INFORMES(2, "Ver todos los informes"),
	REPORTADOR_VER_TODAS_LAS_ESTADISTICAS(21, "Ver todas las estadísticas"),
	ADMINISTRADOR(4, "Administrador"),
	USUARIOS(5, "Usuarios"),
	CREACION(6, "Creación"),
	EDICION(7, "Edición"),
	DESACTIVACION(8, "Desactivación"),
	CONFIGURACION(9, "Configuración"),
	CATEGORIAS(10, "Categorías"),
	FORMAS_DE_PAGO(11, "Formas de pago"),
	CAMPOS_PERSONALIZADOS(12, "Campos personalizados"),
	WORKFLOWS(13, "Workflows"),
	DISENIO_INFORME(14, "Diseño informe"),
	INFORMES(15, "Informes"),
	INFORMES_VER_TODOS(18, "Ver todos los informes"),
	AVISOS(19, "Avisos"),
	CONCILIACION(23, "Conciliación"),
	VER_TODOS_MOVIMIENTOS_TARJETAS(24, "Ver todos los movimientos de tarjetas"),
	VIAJES(25,"Viajes"),
	VER_TODOS_VIAJES (26, "Ver todos los viajes"),
	
	DESCONOCIDO(-1, "Desconocido");
	
	private int idPermiso;
	private String description;
	
	PermisoEnum(int idPermiso, String description) {
		this.idPermiso = idPermiso;
		this.description = description;
	}

	public int getIdPermiso() {
		return idPermiso;
	}

	public String getDescription() {
		return description;
	}
	
	 private static final Map<Integer,PermisoEnum> BY_ID =
	            Arrays.stream(values())
	                  .collect(Collectors.toMap(
	                          PermisoEnum::getIdPermiso,
	                          Function.identity()
	                  ));


    public static PermisoEnum fromId(int id) {
        return BY_ID.getOrDefault(id, DESCONOCIDO);
    }
	
	
}
