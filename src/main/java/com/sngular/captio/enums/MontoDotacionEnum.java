package com.sngular.captio.enums;

public enum MontoDotacionEnum {

	NACIONAL_COMIDA("Nacional", "Comida", 983), EXTRANJERO_COMIDA("Extranjero", "Comida", 1970),
	NACIONAL_ADICIONALES("Nacional", "Gastos adicionales a Comida, Hospedaje y Vuelos.", 223),
	EXTRANJERO_ADICIONALES("Extranjero", "Gastos adicionales a Comida, Hospedaje y Vuelos.", 342),
	NACIONAL_TAXI("Nacional", "Gastos de taxi.", 1500),
	EXTRANJERO_TAXI("Extranjero", "Gastos de taxi.", 2500),
	NACIONAL_HOTEL("Nacional", "Gastos de hotel.", 7500),
	NACIONAL_HOTEL_RIVIERA("Nacional", "Gastos de hotel en la Riviera.", 3629),
	NACIONAL_HOTEL_GUADALAJARA("Nacional", "Gastos de hotel en Guadalajara.", 2385),
	EXTRANJERO_HOTEL("Extranjero", "Gastos de hotel en el Extranjero.", 6100),
	EXTRANJERO_HOTEL_EU("Extranjero", "Gastos de taxi en Estados Unidos.", 9120);

	private final String tipo;
	private final String concepto;
	private final double monto;

	MontoDotacionEnum(String tipo, String concepto, double monto) {
		this.tipo = tipo;
		this.concepto = concepto;
		this.monto = monto;
	}

	public String getTipo() {
		return tipo;
	}

	public String getConcepto() {
		return concepto;
	}

	public double getMonto() {
		return monto;
	}

	// Método auxiliar para buscar por tipo y concepto
	public static MontoDotacionEnum from(String tipo, String concepto) {
		for (MontoDotacionEnum v : values()) {
			if (v.tipo.equalsIgnoreCase(tipo) && v.concepto.equalsIgnoreCase(concepto)) {
				return v;
			}
		}
		throw new IllegalArgumentException("No existe viático para tipo=" + tipo + ", concepto=" + concepto);
	}

}
