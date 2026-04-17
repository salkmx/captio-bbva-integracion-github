package com.sngular.captio.enums;

public enum MontosEnum {

	COMIDA_NACIONAL(948), COMIDA_EXTRANJERO(1900), COMIDA_EXT_COMPROBANTE(170), COMIDA_EXT_SIN_COMPROBANTE(100),
	DIR_DIVISIONAL(2480), DIR_ZONA(1030), DIR_REGIONAL(1960), DIR_BCA_PATRIMONIAL_Y_PRIVADA(1960),
	EJECUTIVOS_BEYG(1960), EJECUTIVOS_CIB(1960), EJECUTIVOS_PYME(1800), ASESORES_INVERSION(1800),
	EJECUTIVOS_CASH_MANAGEMENT(1800), EJECUTIVOS_BCA_ELECTRONICA(1800);

	private double monto;

	public double getMonto() {
		return monto;
	}

	public void setMonto(double monto) {
		this.monto = monto;
	}

	MontosEnum(double monto) {
		this.monto = monto;
	}

}
