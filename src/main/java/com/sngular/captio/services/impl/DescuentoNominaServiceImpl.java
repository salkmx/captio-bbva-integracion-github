package com.sngular.captio.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.sngular.captio.dto.CustomFieldDTO;
import com.sngular.captio.dto.DescuentoNominaDTO;
import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.dto.OpcionesUsuarioDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.ViajeDTO;
import com.sngular.captio.enums.CustomFieldsEnum;
import com.sngular.captio.services.DescuentoNominaService;
import com.sngular.captio.services.InformeService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.services.ViajeService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class DescuentoNominaServiceImpl implements DescuentoNominaService {

	private final ViajeService viajeService;
	private final InformeService informeService;
	private final UsuarioService usuarioService;

	@Override
	public List<DescuentoNominaDTO> obtenerDescuentosNomina(String StartDate, String EndDate) {

		log.debug("DescuentoNominaServiceImpl");
		List<DescuentoNominaDTO> descuentos = new ArrayList<>();
		Map<String, DescuentoNominaDTO> mapaViajes = new HashMap<>();
		Map<String, DescuentoNominaDTO> mapaReportes = new HashMap<>();

		String filter = "";
		String mayorIgual = ">=";
		String menor = "<";

		// Recuperamos los viajes aprobados en el periodo indicado

		filter = "{\"Status\":\"6\",\"StartDate\":\"" + mayorIgual + StartDate +  "\",\"EndDate\":\"" + menor + EndDate + "\"}";
		List<ViajeDTO> viajes = viajeService.obtenerViajesAprobados(filter);

		viajes.forEach(viaje -> {

			if (viaje.getCustomFields() != null && !viaje.getCustomFields().isEmpty()){
				List<CustomFieldDTO> customFields = viaje.getCustomFields();
				customFields.forEach(cf -> {
					if (Objects.equals(cf.getId(), CustomFieldsEnum.NOMBRE_INFORME.getIdCustomField())) {
						// Este viaje tiene el campo personalizado 111 que indica que debemos buscar la información de descuentos en nómina
						DescuentoNominaDTO descuento = new DescuentoNominaDTO();
						descuento.setIdEmployee(viaje.getUser().getId());
						descuento.setTravelStartDate(viaje.getStartDate());
						descuento.setTravelEndDate(viaje.getEndDate());
						descuento.setTravelName(cf.getValue());

						mapaViajes.put(cf.getValue(), descuento);
					}
				});
			}
		});

		// Se recupera informacion de todos los viajes aprobados Status=4 para completar los datos de descuentos en nómina

		List<InformeDTO> informes = informeService.obtenerInformes(obtenerFiltroReportes(mapaViajes));

		informes.forEach(informe -> {

			if (informe.getName() != null) {

				DescuentoNominaDTO desc = mapaViajes.get(informe.getName());

				if(informe.getReimbursableAmount()>0) {
					desc.setTotalAmountAdvances(informe.getAmount().getValue()-informe.getReimbursableAmount());
					desc.setTotalAmountExpenses(informe.getAmount().getValue());
					desc.setAmountRefund(informe.getReimbursableAmount());
					desc.setAmountDebt(0.0);
				}
				else {
					if (informe.getReimbursableAmount() < 0) {
						desc.setTotalAmountAdvances(informe.getAmount().getValue()-Math.abs(informe.getReimbursableAmount()));
						desc.setTotalAmountExpenses(informe.getAmount().getValue());
						desc.setAmountRefund(0.0);
						desc.setAmountDebt(informe.getReimbursableAmount());
					} else {
						desc.setTotalAmountAdvances(0.0);
						desc.setTotalAmountExpenses(informe.getAmount().getValue());
						desc.setAmountRefund(informe.getAmount().getValue());
						desc.setAmountDebt(0.0);
					}
				}

				mapaReportes.put(informe.getName(), desc);
			}

		});

		// Se recupera informacion de todos los usuarios con descuentos en nómina

        try {

			List<UsuarioDTO> users = usuarioService.obtenerUsuarioByFiltro(obtenerFiltroUsers(mapaReportes));

			for (Map.Entry<String, DescuentoNominaDTO> registro : mapaReportes.entrySet()) {

				DescuentoNominaDTO desc = mapaReportes.get(registro.getKey());

				users.forEach(user -> {

					if (user.getId().equals(desc.getIdEmployee())) {

						desc.setEmployeeEmail(user.getEmail());

						OpcionesUsuarioDTO opcionesUsuario;
						opcionesUsuario = user.getOptions();

						if (opcionesUsuario != null) {
							desc.setEmployeeCode(opcionesUsuario.getEmployeeCode());
						}

						mapaReportes.replace(registro.getKey(), desc);

					}
				});

			}

		} catch (Exception e) {
            throw new RuntimeException(e);
        }


		for(Map.Entry<String, DescuentoNominaDTO> entry : mapaReportes.entrySet())
		{
			DescuentoNominaDTO d = mapaReportes.get(entry.getKey());
			descuentos.add(d);
		}

		return descuentos;
	}

	private String obtenerFiltroReportes(Map<String, DescuentoNominaDTO> mapa) {

		//String filterReport = "{\"Name\":\"['";
		String filterReport = "{\"Status\":\"4\",\"Name\":\"['";

		for (Map.Entry<String, DescuentoNominaDTO> registro : mapa.entrySet()) {
			filterReport += "" + registro.getKey() +"','";
		}
		filterReport = filterReport.substring(0, filterReport.length() - 2) + "]\"}";

		return filterReport;
	}


	private String obtenerFiltroUsers(Map<String, DescuentoNominaDTO> mapa) {

		String filterUser = "{\"Id\":\"[";

		for (Map.Entry<String, DescuentoNominaDTO> registro : mapa.entrySet()) {
			DescuentoNominaDTO d = mapa.get(registro.getKey());
			filterUser += "" + d.getIdEmployee() +",";
		}
		filterUser = filterUser.substring(0, filterUser.length() - 1) + "]\"}";

		return filterUser;
	}

}
