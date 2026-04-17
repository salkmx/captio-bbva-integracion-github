package com.sngular.captio.enums;

public enum TipoPagoTaxiEnum {

	SIN_COMPROBANTE(215), CON_COMPROBANTE(580);

	private double monto;

	TipoPagoTaxiEnum(double monto) {
		this.monto = monto;
	}

	public double getMonto() {
		return monto;
	}

	public void setMonto(double monto) {
		this.monto = monto;
	}

}
