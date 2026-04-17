package com.sngular.captio.enums;

public enum CategoriasEnum {

	VIAJES(6178, "viajes"), HOSPEDAJE_NACIONAL(6430, "HOSPEDAJE NACIONAL"),
	HOSPEDAJE_EXTRANJERO(6427, "HOSPEDAJE EXTRANJERO"), COMIDA_EXTRANJERO(6425, "COMIDAS EXTRANJERO"),
	COMIDA_NACIONAL(6423, "COMIDA NACIONAL"), TINTORERIA(6193, "TINTORERIA"), TAXI(6200, "TAXIS"),
	TAXI_FACTURA(6432, "TAXIS CON FACTURA"),
	COMIDA_HORARIO_EXTRAORDINARIO(6417, "Gastos locales - Comidas en horario extraordinario"),
	CAJA_CHICA_COMIDA_HORARIO_EXTRAORDINARIO(6282, "Caja chica - Comidas en horario extraordinario"),
	COMIDA_REUNIONES_INTERNAS(6235, "Comidas reuniones internas"), INSUMOS_CAFETERIA(6419, "Insumos de cafetería"),
	GASOLINA(6420, "Gasolina"),
	COMIDA_REPRESENTACION_MAYOR(6244, "Gastos locales - Comidas de representación (mayor a $3,500)"),
	COMIDA_REPRESENTACION_MENOR(6217, "Gastos locales - Comidas de representación (menor a $3,500)"),
	COMIDA_SIN_COMPROBANTE_ESTABLECIMIENTO_PEQUEÑO(6293,
			"Caja chica - Comidas sin comprobante (establecimiento pequeño)"),
	COMIDA_SIN_COMPROBANTE(6288, "Caja chica - Comidas sin comprobante"),
	GASTO_LOCALES_COMIDA_SIN_COMPROBANTE(6249, "Gastos locales - Comidas sin comprobante"),
	GASTOS_LOCALES_HONORARIOS(6225, "Gastos locales - Honorarios"),
	GASTOS_LOCALES_HOSPITAL(6226, "Gastos locales - Hospital"),
	GASTOS_LOCALES_LABORATORIO(6237, "Gastos locales - Laboratorio"),
	GASTOS_LOCALES_LENTES(6229, "Gastos locales - Lentes"),
	GASTOS_LOCALES_MEDICINAS(6228, "Gastos locales - Medicinas"),
	GASTOS_LOCALES_ORTOPEDIA(6231, "Gastos locales - Ortopedia"),
	GASTOS_LOCALES_REEMBOLSO_TRASLADO_MEDICO(6233, "Gastos locales - Reembolso por traslado médico"),
	CASETAS_CON_TAG(6190, "Casetas con TAG"),
	GASTO_KILOMETRAJE(6179, "Vehículo personal"),
	VUELOS_NACIONAL(6185, "Vuelo"), 
	VUELOS_EXTRANJERO(6435, "Vuelo (extranjero)"),
	VUELOS_AGENCIA(6448, "Vuelo registrado por la agencia");

	private final int clave;
	private final String nombre;

	CategoriasEnum(int clave, String nombre) {
		this.clave = clave;
		this.nombre = nombre;
	}

	public int getClave() {
		return clave;
	}

	public String getNombre() {
		return nombre;
	}

	public static CategoriasEnum fromClave(int clave) {
		for (CategoriasEnum c : values()) {
			if (c.getClave() == clave) {
				return c;
			}
		}
		return null;
	}

	public static CategoriasEnum fromNombre(String nombre) {
		for (CategoriasEnum c : values()) {
			if (c.getNombre().equalsIgnoreCase(nombre)) {
				return c;
			}
		}
		return null;
	}

}
