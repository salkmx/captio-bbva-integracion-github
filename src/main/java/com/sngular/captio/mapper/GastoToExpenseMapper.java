package com.sngular.captio.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.sngular.captio.dto.ExpenseDTO;
import com.sngular.captio.dto.GastoDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GastoToExpenseMapper {

	@Mapping(source = "userId", target = "userId")
	@Mapping(source = "repositoryId", target = "repositoryId")

	@Mapping(source = "paymentMethod.id", target = "paymentMethodId")
	@Mapping(source = "category.id", target = "categoryId")

	@Mapping(source = "expenseAmount.value", target = "amount")

	@Mapping(source = "merchant", target = "merchant")
	@Mapping(source = "date", target = "date")
	@Mapping(source = "comment", target = "comment")

	@Mapping(source = "vatExempt", target = "vatExempt")
	@Mapping(source = "vatExemptAmount", target = "vatExemptAmount")

	@Mapping(source = "tin", target = "tin")
	@Mapping(source = "invoiceNumber", target = "invoiceNumber")

	@Mapping(source = "customFields", target = "customFields")
	@Mapping(source = "expenseAmount.currency.currencyId", target = "currencyId")
	ExpenseDTO toExpense(GastoDTO gasto);
}
