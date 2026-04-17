package com.sngular.captio.enums;

public enum CiudadExtranjeraLimiteEnum {

	NY("NY", 9120), RESTO_EEUU("Resto EEUU", 6100), SF("SF", 7104), LONDRES("Londres", 8621),
	ESTAMBUL("Estambul", 6660), RESTO_TURQUIA("Resto de Turquía", 6100), PARIS("París", 6100), ROMA("Roma", 6100),
	RESTO_REINO_UNIDO("Resto Reino Unido (menos Londres)", 6100), SUIZA("Suiza", 5070), BOGOTA("Bogotá", 4570),
	RESTO_ITALIA("Resto de Italia (excepto Milán y Roma)", 4570), FRANCIA_MENOS_PARIS("Francia (menos París)", 4570),
	RESTO_EUROPA("Resto de Europa", 4570), BUENOS_AIRES("Buenos Aires", 4570),
	RESTO_ARGENTINA("Resto de Argentina", 4460), LIMA("Lima", 4070), RESTO_COLOMBIA("Resto de Colombia", 3075),
	MADRID("Madrid", 3075), BARCELONA("Barcelona", 3075), PORTUGAL("Portugal", 2768), MALAGA("Málaga", 2460),
	RESTO_ESPANA("Resto de España", 2460);

	private final String clave;
	private final double valor;

	CiudadExtranjeraLimiteEnum(String clave, double valor) {
		this.clave = clave;
		this.valor = valor;
	}

	public String getClave() {
		return clave;
	}

	public double getValor() {
		return valor;
	}

	public static CiudadExtranjeraLimiteEnum buscar(String destino) {
		for (CiudadExtranjeraLimiteEnum h : values()) {
			if (h.getClave().equalsIgnoreCase(destino)) {
				return h;
			}
		}
		return null;
	}

}
