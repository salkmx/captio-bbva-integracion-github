package com.sngular.captio.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.CustomFieldDTO;
import com.sngular.captio.dto.ExpenseDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.MonedaDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.enums.CategoriasEnum;
import com.sngular.captio.enums.CustomFieldsEnum;
import com.sngular.captio.enums.DestinoViajeNacionalEnum;
import com.sngular.captio.enums.MetodoPagoEnum;
import com.sngular.captio.enums.MonedaEnum;
import com.sngular.captio.mapper.GastoToExpenseMapper;
import com.sngular.captio.services.UsuarioService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class GastosProcessor implements ItemProcessor<GastoDTO, ExpenseDTO> {

	private final UsuarioService usuarioService;

	private final GastoToExpenseMapper gastoToExpenseMapper;

	@Override
	public ExpenseDTO process(GastoDTO item) throws Exception {
		log.debug("GastosProcessor");

		List<UsuarioDTO> usuario = usuarioService
				.obtenerUsuarioByFiltro("{\"UserOptions_EmployeeCode\":\"" + item.getUser().getCodigo() + "\"}");
		item.setUserId(usuario.get(0).getId());
		MonedaDTO moneda = new MonedaDTO();
		moneda.setCurrencyId(MonedaEnum.MXN.getId());
		item.getExpenseAmount().setCurrency(moneda);
		item.setUser(null);
		List<CustomFieldDTO> customFields = new ArrayList<>();
		CustomFieldDTO customFieldDTO = new CustomFieldDTO();
		customFieldDTO.setId(CustomFieldsEnum.DESTINO_VIAJE_NACIONAL.getIdCustomField());
		customFieldDTO.setValue(String.valueOf(DestinoViajeNacionalEnum.ACAPULCO_EMPORIO_ACAPULCO.getId()));
		customFields.add(customFieldDTO);
		ExpenseDTO expenseDTO = gastoToExpenseMapper.toExpense(item);
		expenseDTO.setCategoryId(CategoriasEnum.HOSPEDAJE_NACIONAL.getClave());
		expenseDTO.setPaymentMethodId(MetodoPagoEnum.EFECTIVO.getIdMetodo());
		expenseDTO.setComment(item.getMerchant());
		expenseDTO.setCustomFields(customFields);
		return expenseDTO;
	}

}
