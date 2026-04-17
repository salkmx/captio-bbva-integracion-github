package com.sngular.captio.processor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.CustomFieldDTO;
import com.sngular.captio.dto.ErrorDTO;
import com.sngular.captio.dto.ExpenseDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.dto.MonedaDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.WorkFlowDTO;
import com.sngular.captio.enums.CustomFieldsEnum;
import com.sngular.captio.enums.MonedaEnum;
import com.sngular.captio.enums.WorkFlowEnum;
import com.sngular.captio.mapper.GastoToExpenseMapper;
import com.sngular.captio.services.GastoService;
import com.sngular.captio.services.InformeService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.util.ObjetosUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class GastosMedicosProcessor implements ItemProcessor<GastoDTO, GastoDTO> {

	private final UsuarioService usuarioService;

	private final GastoService gastoService;

	private final GastoToExpenseMapper gastoToExpenseMapper;

	private final InformeService informeService;

	@Override
	public GastoDTO process(GastoDTO item) throws Exception {
		log.info("process");
		List<InformeDTO> informes = null;

		GastoDTO gasto = item.toBuilder().build();

		CustomFieldDTO encontrado = item.getCustomFields().stream()
				.filter(cf -> CustomFieldsEnum.CLAVE_USUARIO.getIdCustomField().equals(cf.getId())).findFirst()
				.orElse(null);

		if (encontrado == null) {
			return null;
		}

		List<UsuarioDTO> usuario = usuarioService
				.obtenerUsuarioByFiltro("{\"UserOptions_EmployeeCode\":\"" + encontrado.getValue() + "\"}");

		if (usuario == null || usuario.isEmpty()) {
			return null;
		}

		String nombreDelInformeMedico = crearInformeLocalMedico(usuario.get(0), "RMED");
		informes = informeService
				.obtenerInformes(String.format("{\"Name\":\"%s\", Status:1}", nombreDelInformeMedico));

		if (informes == null || informes.isEmpty()) {

			WorkFlowDTO workFlowDTO = new WorkFlowDTO();

			workFlowDTO.setId(WorkFlowEnum.GASTOS_MEDICOS.getIdworkFlow());

			InformeDTO informeDTO = crearInforme(usuario.get(0), workFlowDTO, null);

			if (informeDTO == null)
				return null;

			Thread.sleep(2000);

			informes = informeService.obtenerInformes(String.format("{\"Name\":\"%s\", Status:1}", informeDTO.getName()));

		}

		if (informes.isEmpty())
			return null;
		gasto.setNombreInforme(informes.get(0).getName());
		item.setUserId(usuario.get(0).getId());
		MonedaDTO moneda = new MonedaDTO();
		moneda.setCurrencyId(MonedaEnum.MXN.getId());
		item.getExpenseAmount().setCurrency(moneda);
		ExpenseDTO expenseDTO = gastoToExpenseMapper.toExpense(item);
		expenseDTO.setCustomFields(null);
		List<ExpenseDTO> expenses = new ArrayList<>();
		expenses.add(expenseDTO);
		Integer idGasto = gastoService.alta(expenses);
		List<GastoDTO> gastos = new ArrayList<>();
		GastoDTO gastoDTO = new GastoDTO();
		gastoDTO.setId(idGasto);
		gastos.add(gastoDTO);
		ObjetosUtils.limpiarCamposExcepto(gastos, List.of("id"));
		informes.get(0).setGastos(gastos);
		informeService.agregarGastosInforme(informes);
		gasto.setUsuarioCorporativo(usuario.get(0).getUserOptions().getEmployeeCode());
		return gasto;
	}

	private InformeDTO crearInforme(UsuarioDTO usuario, WorkFlowDTO workflow, List<ErrorDTO> errores) {
		StringBuilder sb = new StringBuilder();
		if (errores != null) {
			for (ErrorDTO error : errores) {
				sb.append(error.getDescripcion()).append("; ");
			}
		}
		String comment = sb == null ? "" : sb.toString();
		int maxLen = 400;
		comment = comment.substring(0, Math.min(maxLen, comment.length()));

		return informeService.crearInforme(usuario, workflow, comment, "RMED_");
	}

	private String crearInformeLocalMedico(UsuarioDTO usuario, String prefijo) {
		LocalDateTime fechaHora = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		String formatted = fechaHora.format(formatter);
		return prefijo + "_" + usuario.getName() + "_" + formatted;
	}

}
