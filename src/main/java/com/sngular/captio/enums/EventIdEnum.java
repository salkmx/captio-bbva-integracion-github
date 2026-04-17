package com.sngular.captio.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventIdEnum {

	EXPENSE_MODIFIED(1, "Expense Modified"),
	EXPENSE_REMOVED(2, "Expense Removed"),
	CURRENCY_MODIFIED(3, "Currency Modified"),
	ADVANCE_WITH_REMAINING_CREATED(4, "Advance With Remaining Created"),
	DIGITAL_SIGNATURE_APPLIED(5, "Digital Signature Applied"),
	EXPENSE_ADDED(6, "Expense Added");

	private final int id;
	private final String description;

	public static String getDescriptionById(Integer id) {
		if (id == null) return "";
		for (EventIdEnum e : values()) {
			if (e.id == id) return e.description;
		}
		return String.valueOf(id);
	}
}