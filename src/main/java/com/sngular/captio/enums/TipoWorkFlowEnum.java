package com.sngular.captio.enums;

public enum TipoWorkFlowEnum {

	INFORMES(1),
	INFORMES_VIAJE_NACIONAL(1),
	INFORMES_VIAJE_EXTRANJERO(1),
	GASTOS_LOCALES(1),
	VIAJES_NACIONAL(3),
	VIAJES_EXTRANJERO(3);

	private Integer tipoFlujo;

	public Integer getTipoFlujo() {
		return tipoFlujo;
	}

	public void setTipoFlujo(Integer tipoFlujo) {
		this.tipoFlujo = tipoFlujo;
	}

	TipoWorkFlowEnum(Integer tipoFlujo) {
		this.tipoFlujo = tipoFlujo;
	}

}
