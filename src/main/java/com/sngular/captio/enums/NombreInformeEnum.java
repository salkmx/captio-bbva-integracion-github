package com.sngular.captio.enums;

public enum NombreInformeEnum {

	NACIONAL("VIA-NAC_"), INTERNACIONAL("VIA-EXTR_"), INTERCONTINENTAL("VIA-INTER_"),
	MULTIDESTINO_NACIONAL("VIA-MD-NAC_"), MULTIDESTINO_INTERNACIONAL("VIA-MD-EXTR_"), MULTIDESTINO_INTERCONTINENTAL("VIA-MD-INTER_"),
	MEDICO("RMED_");

	private String nombreInforme;

	public String getNombreInforme() {
		return nombreInforme;
	}

	NombreInformeEnum(String nombreInforme) {
		this.nombreInforme = nombreInforme;
	}

}
