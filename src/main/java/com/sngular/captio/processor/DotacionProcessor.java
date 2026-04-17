package com.sngular.captio.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.CustomFieldDTO;
import com.sngular.captio.dto.DotacionDTO;
import com.sngular.captio.dto.FlightDTO;
import com.sngular.captio.dto.GenericResponseDTO;
import com.sngular.captio.dto.HotelDTO;
import com.sngular.captio.dto.MetodoPagoDTO;
import com.sngular.captio.dto.MonedaDTO;
import com.sngular.captio.dto.TravelServiceDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.ValidationDTO;
import com.sngular.captio.dto.ViajeDTO;
import com.sngular.captio.dto.WorkFlowDTO;
import com.sngular.captio.enums.CustomFieldsEnum;
import com.sngular.captio.enums.MonedaEnum;
import com.sngular.captio.enums.MontoDotacionEnum;
import com.sngular.captio.enums.WorkFlowEnum;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.DotacionService;
import com.sngular.captio.services.EmailService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.services.ViajeService;
import com.sngular.captio.util.CaptioUtils;
import com.sngular.captio.util.DateUtils;
import com.sngular.captio.util.ObjetosUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class DotacionProcessor implements ItemProcessor<ViajeDTO, DotacionDTO> {

	private final DotacionService dotacionService;

	private final Properties properties;

	private final EmailService emailService;

	private final UsuarioService usuarioService;

	private final ViajeService viajeService;
	

	@Override
	public DotacionDTO process(ViajeDTO item) throws Exception {
		log.info("Ejecutando creación de dotación para viaje {}", item.getId());

		Integer usuarioId = item.getUser().getId();
		Integer viajeId = item.getId();

		List<DotacionDTO> dotacionesUsuario = dotacionService.obtenerDotacion(buildFiltroUserId(usuarioId));

		filtrarEnSitio(dotacionesUsuario, viajeId);

		if (dotacionesUsuario != null && !dotacionesUsuario.isEmpty()) {
			log.debug("Ya existe dotación para el usuario {} y viaje {}", usuarioId, viajeId);
			return null;
		}

		boolean esNacional = CaptioUtils.contieneNacionalPalabra(item);
		String tipoViaje = esNacional ? "Nacional" : "Extranjero";

		long dias = DateUtils.diasInclusivos(item.getStartDate(), item.getEndDate());
		String fechaCreacion = DateUtils.obtenerFechaDDMMYYYY(item.getCreationDate());



		List<TravelServiceDTO> serviciosDelViaje = viajeService.obtenerServiciosViajes("{\"Id\":" + viajeId + "}");
		List<FlightDTO> vuelos = serviciosDelViaje.get(0).getFlights();
		//List<HotelDTO> hoteles = serviciosDelViaje.get(0).getHotels();
		String destino = null;
		if(vuelos!=null && !vuelos.isEmpty() && vuelos.size()>0){
			destino = vuelos.get(0).getDestination();
		}

		//double montoDiario = calcularMontoDiario(esNacional);
		double montoDiario = calcularMontoDiarioConDestino(esNacional, destino);
		double montoTotal = calcularMontoTotal(item, esNacional, dias, montoDiario);

		item.setDias(dias);
		item.setMontoDiario(montoDiario);

		DotacionDTO dotacion = construirDotacionBase(item, usuarioId, viajeId, tipoViaje, fechaCreacion, montoTotal);

		if (tieneTarjetaEmpresarial(usuarioId)) {
			log.debug("Usuario {} tiene tarjeta empresarial, no se crea dotación", usuarioId);
			return null;
		}

		try {
			prepararWorkflowDotacion(usuarioId);

			dotacion.setCustomFields(
					List.of(crearCustomField(CustomFieldsEnum.ID_VIAJE.getIdCustomField(), viajeId.toString())));

			List<GenericResponseDTO<DotacionDTO>> respuesta = dotacionService.crearDotacion(dotacion);

			if (respuesta == null) {
				emailService.enviarCorreo(emailService.crearMailDTO(properties.getMailPagaduria(), "Dotación aprobada",
						"La dotación fue aprobada", "email/aviso"));
			} else {
				escribirErrorDotacion(respuesta);
			}
		} catch (Exception e) {
			log.error("Error al crear dotación para viaje {}: {}", viajeId, e.getMessage(), e);
		}

		return dotacion;
	}

	private String buildFiltroUserId(Integer usuarioId) {
		return String.format("{\"User_Id\":\"%d\"}", usuarioId);
	}

	private double calcularMontoDiario(boolean esNacional) {
		if (esNacional) {
			return MontoDotacionEnum.NACIONAL_COMIDA.getMonto() + MontoDotacionEnum.NACIONAL_ADICIONALES.getMonto() + MontoDotacionEnum.NACIONAL_TAXI.getMonto();
		}
		return MontoDotacionEnum.EXTRANJERO_COMIDA.getMonto() + MontoDotacionEnum.EXTRANJERO_ADICIONALES.getMonto() + MontoDotacionEnum.EXTRANJERO_TAXI.getMonto();
	}

	private double calcularMontoDiarioConDestino(boolean esNacional, String destino) {
		double monto = 0;
		if (esNacional) {
			monto =  MontoDotacionEnum.NACIONAL_COMIDA.getMonto() + MontoDotacionEnum.NACIONAL_ADICIONALES.getMonto() + MontoDotacionEnum.NACIONAL_TAXI.getMonto();

			if(destino!=null && !destino.isEmpty()){
				if(destino.contains("Cancún") || destino.contains("Cancun") || destino.contains("Playa Del Carmen") || destino.contains("Tulum"))
					monto += MontoDotacionEnum.NACIONAL_HOTEL_RIVIERA.getMonto();
				else if(destino.contains("Guadalajara"))
					monto += MontoDotacionEnum.NACIONAL_HOTEL_GUADALAJARA.getMonto();
				else
					monto += MontoDotacionEnum.NACIONAL_HOTEL.getMonto();
			}
			return monto;
		}
		
		monto = MontoDotacionEnum.EXTRANJERO_COMIDA.getMonto() + MontoDotacionEnum.EXTRANJERO_ADICIONALES.getMonto() + MontoDotacionEnum.EXTRANJERO_TAXI.getMonto();
		if(destino!=null && !destino.isEmpty() && destino.contains("Estados Unidos"))
			monto += MontoDotacionEnum.EXTRANJERO_HOTEL_EU.getMonto();
		else
			monto += MontoDotacionEnum.EXTRANJERO_HOTEL.getMonto();
		return monto;
	}

	private double calcularMontoTotal(ViajeDTO item, boolean esNacional, long dias, double montoDiarioBase) {

		boolean esCapacitacion = CaptioUtils.contieneFormacion(item.getReason())
				|| CaptioUtils.contieneCapacitacion(item.getReason());

		if (!esCapacitacion) {
			return montoDiarioBase * dias;
		}

		double montoAdicionalCapacitacion = esNacional ? MontoDotacionEnum.NACIONAL_ADICIONALES.getMonto()
				: MontoDotacionEnum.EXTRANJERO_ADICIONALES.getMonto();

		long diasRestantes = Math.max(dias - 2, 0);
		return (montoDiarioBase * 2) + (montoAdicionalCapacitacion * diasRestantes);
	}

	private DotacionDTO construirDotacionBase(ViajeDTO item, Integer usuarioId, Integer viajeId, String tipoViaje,
			String fechaCreacion, double montoTotal) throws Exception {

		MonedaDTO moneda = new MonedaDTO();
		moneda.setCurrencyId(MonedaEnum.MXN.getId());
		moneda.setMonto(montoTotal);

		DotacionDTO dotacion = new DotacionDTO();
		dotacion.setCurrencies(List.of(moneda));
		dotacion.setUserId(usuarioId);
		dotacion.setWorkflowId(WorkFlowEnum.DOTACION_WF.getIdworkFlow());
		dotacion.setReason("Dotación por gastos del viaje: " + item.getReason() + ", " + fechaCreacion);
		dotacion.setComment("Días: " + item.getDias() + ", tipo: " + tipoViaje);

		ObjetosUtils.limpiarCamposExcepto(item, List.of("dias", "montoDiario"));
		dotacion.setTravel(item);

		List<UsuarioDTO> usuarios = usuarioService.obtenerUsuarioByFiltro(String.format("{\"Id\":\"%d\"}", usuarioId));
		String correo = (usuarios != null && !usuarios.isEmpty()) ? usuarios.get(0).getEmail() : null;
		dotacion.setLoginUser(correo);

		return dotacion;
	}

	private boolean tieneTarjetaEmpresarial(Integer usuarioId) throws Exception {
		List<MetodoPagoDTO> metodosPago = usuarioService.obtenerMetodoPago(String.format("{\"Id\":\"%d\"}", usuarioId));

		return metodosPago != null
				&& metodosPago.stream().filter(Objects::nonNull).anyMatch(m -> m.getIdentifierType() == 1);
	}

	private void prepararWorkflowDotacion(Integer userId) throws Exception {
		UsuarioDTO usuarioWf = usuarioService.obtenerFlujoUsuarioByFiltro(String.format("{\"Id\": %d}", userId));

		if (usuarioWf == null) {
			usuarioWf = new UsuarioDTO();
			usuarioWf.setId(userId);
		}

		if (!contieneWorkflowId(usuarioWf, WorkFlowEnum.DOTACION_WF.getIdworkFlow())) {
			List<UsuarioDTO> usuariosWf = new ArrayList<>();
			WorkFlowDTO workFlowDTO = new WorkFlowDTO();
			workFlowDTO.setId(WorkFlowEnum.DOTACION_WF.getIdworkFlow());
			workFlowDTO.setDefaulte(false);
			usuarioWf.setWorkflows(List.of(workFlowDTO));
			usuariosWf.add(usuarioWf);
			usuarioService.joinWorkFlow(usuariosWf);
		}
	}

	private CustomFieldDTO crearCustomField(int id, String valor) {
		CustomFieldDTO customFieldDTO = new CustomFieldDTO();
		customFieldDTO.setId(id);
		customFieldDTO.setValue(valor);
		return customFieldDTO;
	}

	public static void filtrarEnSitio(List<DotacionDTO> dotaciones, Integer idViaje) {
		if (dotaciones == null)
			return;
		final String esperado = String.valueOf(idViaje);

		dotaciones.removeIf(d -> {
			if (d == null)
				return true;
			var cfs = d.getCustomFields();
			if (cfs == null || cfs.isEmpty())
				return true;
			var cf0 = cfs.get(0);
			if (cf0 == null || cf0.getValue() == null)
				return true;
			return !esperado.equals(String.valueOf(cf0.getValue()));
		});
	}

	public void escribirErrorDotacion(List<GenericResponseDTO<DotacionDTO>> errores) {
		String ruta = properties.getRutaArchivoErrorDotaciones() + DateUtils.obtenerFechaActual() + ".csv";

		try {

			StringBuilder sb = new StringBuilder();
			for (GenericResponseDTO<DotacionDTO> error : errores) {
				sb.append(error.getValue());
				sb.append(System.lineSeparator());
				sb.append(error.getValidations().stream().map(ValidationDTO::getMessage).filter(Objects::nonNull)
						.collect(Collectors.joining(System.lineSeparator())));
			}

			for (String errorMail : properties.getErrorMails()) {
				emailService.enviarCorreo(
						emailService.crearMailDTO(errorMail, "Dotación error", sb.toString(), "email/error"));
			}

			File archivo = new File(ruta);

			archivo.getParentFile().mkdirs();

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
				writer.write(sb.toString());
				writer.newLine();
			}

		} catch (IOException e) {
			log.error("Error al escribir registro: " + e.getMessage(), e);
		}
	}

	private boolean contieneWorkflowId(UsuarioDTO root, int idBuscado) {
		if (root == null) {
			return false;
		}

		if (root.getWorkflows() == null) {
			return false;
		}

		for (WorkFlowDTO wf : root.getWorkflows()) {
			if (wf != null && wf.getId() == idBuscado) {
				return true;
			}
		}

		return false;
	}

}
