package com.sngular.captio.enums;

public enum WorkFlowEnum {

	DOTACION_WF(232), GASTOS_MEDICOS(294);

	private Integer idworkFlow;

	public Integer getIdworkFlow() {
		return idworkFlow;
	}

	WorkFlowEnum(Integer idworkFlow) {
		this.idworkFlow = idworkFlow;
	}

}
