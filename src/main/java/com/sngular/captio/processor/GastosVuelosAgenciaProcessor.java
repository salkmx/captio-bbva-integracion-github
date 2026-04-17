package com.sngular.captio.processor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.sngular.captio.dto.CategoriaDTO;
import com.sngular.captio.dto.CustomFieldDTO;
import com.sngular.captio.dto.ErrorDTO;
import com.sngular.captio.dto.ExpenseDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.dto.MetodoPagoDTO;
import com.sngular.captio.dto.MonedaDTO;
import com.sngular.captio.dto.ViajeDTO;
import com.sngular.captio.enums.CategoriasEnum;
import com.sngular.captio.enums.CustomFieldsEnum;
import com.sngular.captio.enums.MetodoPagoEnum;
import com.sngular.captio.enums.MonedaEnum;
import com.sngular.captio.mapper.GastoToExpenseMapper;
import com.sngular.captio.services.GastoService;
import com.sngular.captio.services.InformeService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.services.ViajeService;
import com.sngular.captio.util.CaptioUtils;
import com.sngular.captio.util.ObjetosUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class GastosVuelosAgenciaProcessor implements ItemProcessor<GastoDTO, GastoDTO> {

	private final UsuarioService usuarioService;

    private final ViajeService viajeService;

	private final GastoService gastoService;

	private final GastoToExpenseMapper gastoToExpenseMapper;

	private final InformeService informeService;

	@Override
	public GastoDTO process(GastoDTO item) throws Exception {
		log.info("process GastosVuelosAgenciaProcessor");
		List<InformeDTO> informes = null;

		GastoDTO gasto = item.toBuilder().build();

		CustomFieldDTO referenciaDeViaje = item.getCustomFields().stream()
				.filter(cf -> CustomFieldsEnum.REFERENCIA_VIAJE.getIdCustomField().equals(cf.getId())).findFirst()
				.orElse(null);

		if (referenciaDeViaje == null) {
			return null;
		}

        List<ViajeDTO> viajes = viajeService.obtenerViajesAprobados(String.format("{\"Status\":6, \"Reference\": \"%s\"}", referenciaDeViaje.getValue()));
		
		if (viajes == null || viajes.isEmpty()) {
			return null;
		}

        ViajeDTO viaje = viajes.get(0);

		informes = informeService
				.obtenerInformes(String.format("{Status:1, \"Name\":\"%s\"}",
				buscarPorId(viaje.getCustomFields(), CustomFieldsEnum.NOMBRE_INFORME.getIdCustomField()).getValue()));

		if (informes == null || informes.isEmpty()) {
            return null;
		}

		gasto.setNombreInforme(informes.get(0).getName());

		//Cambiar el usuario del gasto actual por el usuario al que corresponde el viaje.
		item.setUserId(viaje.getUser().getId());

		MonedaDTO moneda = new MonedaDTO();
		moneda.setCurrencyId(MonedaEnum.MXN.getId());
		item.getExpenseAmount().setCurrency(moneda);

        //Cambiar la categoría del gasto por el gasto de vuelo (Nacional o Extranjero según corresponda).
		CategoriaDTO categoriaDTO = new CategoriaDTO();
		boolean esNacional = CaptioUtils.contieneNacionalPalabra(viaje);
		if(esNacional)
			categoriaDTO.setId(CategoriasEnum.VUELOS_NACIONAL.getClave());
		else
			categoriaDTO.setId(CategoriasEnum.VUELOS_EXTRANJERO.getClave());
		item.setCategory(categoriaDTO);

		//Cambiar la forma de pago del gasto por Agencia de Viaje, que es Agencia de Viaje.
		//MetodoPagoDTO metodoPagoDTO = new MetodoPagoDTO();
		//metodoPagoDTO.setPaymentId(MetodoPagoEnum.AGENCIA_VIAJES.getIdMetodo());
		//item.setPaymentMethod(metodoPagoDTO);

		ExpenseDTO expenseDTO = gastoToExpenseMapper.toExpense(item);

		//Cambiar la forma de pago del gasto por Agencia de Viaje, que es Agencia de Viaje.
		expenseDTO.setPaymentMethodId(MetodoPagoEnum.AGENCIA_VIAJES.getIdMetodo());
		expenseDTO.setCustomFields(null);

		
		List<ExpenseDTO> expenses = new ArrayList<>();
		expenses.add(expenseDTO);

		try{
			Integer idGasto = gastoService.alta(expenses);
		
			List<GastoDTO> gastos = new ArrayList<>();
			GastoDTO gastoDTO = new GastoDTO();
			gastoDTO.setId(idGasto);
			gastos.add(gastoDTO);
			ObjetosUtils.limpiarCamposExcepto(gastos, List.of("id"));
			informes.get(0).setGastos(gastos);
			informeService.agregarGastosInforme(informes);
		}catch(HttpClientErrorException ex){
			log.error(ex.getLocalizedMessage());
		}

		return gasto;
	}

	public CustomFieldDTO buscarPorId(List<CustomFieldDTO> customFields, Integer idBuscado) {
		if (customFields == null || idBuscado == null) {
			return null;
		}

		return customFields.stream().filter(cf -> idBuscado.equals(cf.getId())).findFirst().orElse(null);
	}

}