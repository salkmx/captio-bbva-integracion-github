package com.sngular.captio.services.impl;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.sngular.captio.dto.CustomFieldDTO;
import com.sngular.captio.dto.ErrorDTO;
import com.sngular.captio.dto.ExpenseDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.GenericResponseDTO;
import com.sngular.captio.dto.GrupoDTO;
import com.sngular.captio.dto.ViajeDTO;
import com.sngular.captio.enums.CategoriasEnum;
import com.sngular.captio.enums.CategoriasExtranjeroEnum;
import com.sngular.captio.enums.CategoriasNacionalesEnum;
import com.sngular.captio.enums.CiudadExtranjeraLimiteEnum;
import com.sngular.captio.enums.CiudadLimiteEnum;
import com.sngular.captio.enums.GrupoEnum;
import com.sngular.captio.enums.MontosEnum;
import com.sngular.captio.enums.TintoreriaEnum;
import com.sngular.captio.enums.TipoPagoTaxiEnum;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.GastoService;
import com.sngular.captio.util.CaptioJsonUtils;
import com.sngular.captio.util.CaptioUtils;
import com.sngular.captio.util.DateUtils;
import com.sngular.captio.util.ObjetosUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class GastoServiceImpl implements GastoService {

	private final RestTemplate restTemplate;

	private final Properties properties;

	private Integer gastosTintoreria;

	private Map<String, BigDecimal> gastosComida = new HashMap<>();

	@Override
	public List<GastoDTO> obtenerGastosPorUsuario(ViajeDTO viaje) {
		List<GastoDTO> gastos = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetGastos();
		URI uri = UriComponentsBuilder.fromUriString(baseUrl)
				.queryParam("filters",
						"{\"User_Id\":\"" + viaje.getUser().getId() + "\",\"Report_Id\":null, \"Category_Parent_Id\":"
								+ CategoriasEnum.VIAJES.getClave() + ", \"Date\":\">="
								+ DateUtils.obtenerFechaInicialServicios(viaje.getStartDate()) + ",<"
								+ DateUtils.obtenerFechaFinalServicios(viaje.getEndDate()) + "\" }")
				.build().encode().toUri();
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			ResponseEntity<GastoDTO[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity, GastoDTO[].class);
			if (response.getBody() != null) {
				gastos = List.of(response.getBody());
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
		return gastos;
	}

	@Override
	public List<GastoDTO> obtenerGastosPorFiltro(String filtro) {
		List<GastoDTO> gastos = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetGastos();
		URI uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("filters", filtro).build().encode().toUri();
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			ResponseEntity<GastoDTO[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity, GastoDTO[].class);
			if (response.getBody() != null) {
				gastos = List.of(response.getBody());
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
		return gastos;
	}

	public List<ErrorDTO> validacionesGastos(List<GastoDTO> gastos, ViajeDTO viaje) {
		boolean esNacional = CaptioUtils.contieneNacionalPalabra(viaje);

		gastosTintoreria = 0;
		List<ErrorDTO> errores = new ArrayList<>();

		for (GastoDTO gastoDTO : gastos) {
			if (gastoDTO.getCategory() != null) {

				CustomFieldDTO customFieldDTOGasto = null;
				if (gastoDTO.getCustomFields() != null && !gastoDTO.getCustomFields().isEmpty()) {
					customFieldDTOGasto = gastoDTO.getCustomFields().get(0);
				}
				if (esNacional) {
					errores.addAll(validacionesNacionales(gastoDTO, customFieldDTOGasto));
					ErrorDTO error = validaTipoViajeCategoriasNacional(gastoDTO);
					if (error != null)
						errores.add(error);

				} else {
					errores.addAll(validacionesExtranjero(gastoDTO, customFieldDTOGasto));
					ErrorDTO error = validaTipoViajeCategoriasExtranjero(gastoDTO);
					if (error != null)
						errores.add(error);

				}
				if (gastoDTO.getCategory().getId().equals(CategoriasEnum.TINTORERIA.getClave())) {
					gastosTintoreria = +1;
					ErrorDTO error = vaildarGastosTintoreria(gastoDTO, viaje);
					if (error != null) {
						errores.add(error);
					}
				}

				ErrorDTO errorTaxi = vaildarGastosTaxi(gastoDTO);
				if (errorTaxi != null) {
					errores.add(errorTaxi);
				}

				ErrorDTO errorProveedor = validarProveedoresProhibidos(gastoDTO);
				if (errorProveedor != null) {
					errores.add(errorProveedor);
				}
			} else {
				errores.add(crearError(gastoDTO, "Gasto sin categoría ", 6));
			}
		}
		List<ErrorDTO> erroresComida = null;
		if (esNacional) {
			erroresComida = validarLimitePorClave();
		} else {
			erroresComida = validarLimitePorClaveExtranjero();
		}
		if (erroresComida != null && !erroresComida.isEmpty()) {
			errores.addAll(erroresComida);
		}
		if (errores != null && !errores.isEmpty()) {
			try {
				log.info("Errores encontrados durante la ejecucion:",
						errores.stream().map(ErrorDTO::toString).collect(Collectors.joining("\n")));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return errores;
	}

	public List<ErrorDTO> validacionesGastosOtros(List<GastoDTO> gastos, List<GrupoDTO> grupos) {
		List<ErrorDTO> errores = new ArrayList<>();
		for (GastoDTO gasto : gastos) {
			if (gasto.getCategory() != null
					&& gasto.getCategory().getId().equals(CategoriasEnum.COMIDA_HORARIO_EXTRAORDINARIO.getClave())) {

				ErrorDTO error = validarGAstoExtraordinario(gasto);
				if (error != null) {
					errores.add(error);
				}

				error = validarPropinas(gasto, 10.0, 101);
				if (error != null) {
					errores.add(error);
				}

			} else if (gasto.getCategory() != null && gasto.getCategory().getId()
					.equals(CategoriasEnum.CAJA_CHICA_COMIDA_HORARIO_EXTRAORDINARIO.getClave())) {

				ErrorDTO error = validarPropinas(gasto, 10.0, 87);
				if (error != null) {
					errores.add(error);
				}

			} else if (gasto.getCategory() != null
					&& gasto.getCategory().getId().equals(CategoriasEnum.COMIDA_REUNIONES_INTERNAS.getClave())
					&& !grupos.isEmpty()) {

				ErrorDTO error = validarN3(gasto, grupos);
				if (error != null) {
					errores.add(error);
				}

				error = validarPropinas(gasto, 10.0, 97);
				if (error != null) {
					errores.add(error);
				}

			} else if (gasto.getCategory() != null
					&& gasto.getCategory().getId().equals(CategoriasEnum.INSUMOS_CAFETERIA.getClave())
					&& !grupos.isEmpty()) {

				ErrorDTO error = validarGastoInsumosCafeteria(gasto, grupos);
				if (error != null) {
					errores.add(error);
				}

			} else if (gasto.getCategory() != null
					&& gasto.getCategory().getId().equals(CategoriasEnum.GASOLINA.getClave()) && !grupos.isEmpty()) {

				ErrorDTO error = validarGastoGasolina(gasto, grupos);
				if (error != null) {
					errores.add(error);
				}

			} else if (gasto.getCategory() != null
					&& (gasto.getCategory().getId().equals(CategoriasEnum.COMIDA_REPRESENTACION_MENOR.getClave()))
					&& !grupos.isEmpty()) {

				ErrorDTO error = validarPropinas(gasto, 15.0, 93);
				if (error != null) {
					errores.add(error);
				}
			} else if (gasto.getCategory() != null
					&& gasto.getCategory().getId().equals(CategoriasEnum.COMIDA_REPRESENTACION_MAYOR.getClave())
					&& !grupos.isEmpty()) {

				ErrorDTO error = validarPropinas(gasto, 15.0, 95);
				if (error != null) {
					errores.add(error);
				}
			} else if (gasto.getCategory() != null
					&& gasto.getCategory().getId()
							.equals(CategoriasEnum.COMIDA_SIN_COMPROBANTE_ESTABLECIMIENTO_PEQUEÑO.getClave())
					&& !grupos.isEmpty()) {

			} else if (gasto.getCategory() != null
					&& gasto.getCategory().getId().equals(CategoriasEnum.COMIDA_SIN_COMPROBANTE.getClave())
					&& !grupos.isEmpty()) {

				ErrorDTO error = validarPropinas(gasto, 10.0, 89);
				if (error != null) {
					errores.add(error);
				}
			} else if (gasto.getCategory() != null
					&& gasto.getCategory().getId()
							.equals(CategoriasEnum.GASTO_LOCALES_COMIDA_SIN_COMPROBANTE.getClave())
					&& !grupos.isEmpty()) {

				ErrorDTO error = validarPropinas(gasto, 10.0, 99);
				if (error != null) {
					errores.add(error);
				}
			}
		}
		return errores;
	}

	private ErrorDTO validarN3(GastoDTO gastoDTO, List<GrupoDTO> grupos) {
		ErrorDTO errorDTO = null;
		for (GrupoDTO grupo : grupos) {

			if (GrupoEnum.NIVEL_3_O_SUPERIOR.esId(grupo.getId())) {
				errorDTO = crearError(gastoDTO,
						"Este gasto sólo está autorizado para los empleados de nivel N3 o superior", 3);
			}
		}
		return errorDTO;
	}

	private ErrorDTO validarGAstoExtraordinario(GastoDTO gastoDTO) {
		ErrorDTO errorDTO = null;
		if (gastoDTO.getAttachments() != null && !gastoDTO.getAttachments().isEmpty()
				&& gastoDTO.getExpenseAmount().getValue() > MontosEnum.COMIDA_EXT_COMPROBANTE.getMonto()) {
			errorDTO = crearError(gastoDTO, "El gasto extraordinario con comprobante supera el limite", 3);
		} else if (gastoDTO.getAttachments() != null && gastoDTO.getAttachments().isEmpty()
				&& gastoDTO.getExpenseAmount().getValue() > MontosEnum.COMIDA_EXT_SIN_COMPROBANTE.getMonto()) {
			errorDTO = crearError(gastoDTO, "El gasto extraordinario sin comprobante supera el limite", 3);
		}
		return errorDTO;
	}

	private ErrorDTO vaildarGastosTaxi(GastoDTO gastoDTO) {
		ErrorDTO error = null;
		if (gastoDTO.getCategory().getId().equals(CategoriasEnum.TAXI.getClave())) {
			if (gastoDTO.getExpenseAmount().getValue() > TipoPagoTaxiEnum.SIN_COMPROBANTE.getMonto()) {
				error = crearError(gastoDTO, "Monto sobrepasado para taxi sin comprobante", 4);
			}
		} else if (gastoDTO.getCategory().getId().equals(CategoriasEnum.TAXI_FACTURA.getClave())) {
			if (gastoDTO.getExpenseAmount().getValue() > TipoPagoTaxiEnum.CON_COMPROBANTE.getMonto()) {
				error = crearError(gastoDTO, "Monto sobrepasado para taxi con comprobante", 5);
			}
		}
		return error;
	}

	private ErrorDTO vaildarGastosTintoreria(GastoDTO gastoDTO, ViajeDTO viaje) {
		ErrorDTO errorDTO = null;
		LocalDateTime fechaInicioViaje = viaje.getStartDate();
		LocalDateTime fechaTintoreria = gastoDTO.getDate();
		if (gastosTintoreria == 1) {
			fechaInicioViaje = fechaInicioViaje.plusDays(TintoreriaEnum.INTERVALODIASUNO.getDias());
		} else if (gastosTintoreria == 2) {
			fechaInicioViaje = fechaInicioViaje.plusDays(TintoreriaEnum.INTERVALODIASDOS.getDias());
		}
		if (fechaTintoreria.isBefore(fechaInicioViaje) || fechaInicioViaje.isEqual(fechaTintoreria)) {
			errorDTO = crearError(gastoDTO, "Los gastos de tintorería no son en las fechas especifícadas", 3);
		}
		return errorDTO;
	}

	private List<ErrorDTO> validacionesNacionales(GastoDTO gastoDTO, CustomFieldDTO customFieldDTO) {
		List<ErrorDTO> errores = new ArrayList<>();
		if (gastoDTO.getCategory().getId().equals(CategoriasEnum.HOSPEDAJE_NACIONAL.getClave())
				&& !esValidoNacional(customFieldDTO.getValue(), gastoDTO.getExpenseAmount().getValue())) {
			errores.add(crearError(gastoDTO, "Limite de hospedaje por ciudad superado", 1));
		} else if (gastoDTO.getCategory().getId().equals(CategoriasEnum.COMIDA_NACIONAL.getClave())) {

			String fecha = DateUtils.obtenerFechaDDMMYYYY(gastoDTO.getDate());
			acumularGastoComida(fecha, BigDecimal.valueOf(gastoDTO.getExpenseAmount().getValue()));

			// Validar propina (CustomField Id 85) de 10%
			ErrorDTO errorPropina = validarPropinas(gastoDTO, 10.0, 85);
			if (errorPropina != null) {
				errores.add(errorPropina);
			}
		}
		return errores;
	}

	public ErrorDTO validarPropinas(GastoDTO gastoDTO, double porcentajeMaximo, int customFieldId) {
		ErrorDTO errorDTO = null;
		// Validar propina (CustomField Id 85)
		// TODO: Revisar porqué me aparece que la propina es el CustomField 45
		if (gastoDTO.getCustomFields() != null) {
			for (CustomFieldDTO cf : gastoDTO.getCustomFields()) {
				if (cf != null && Integer.valueOf(customFieldId).equals(cf.getId()) && cf.getValue() != null) {
					try {
						// Normalizar formato: reemplazar coma por punto para manejar formatos locales
						// (ej: "150,00" -> "150.00")
						String valorNormalizado = cf.getValue().replace(",", ".");
						double propina = new BigDecimal(valorNormalizado).doubleValue();
						if (propina < 0) {
							return crearError(gastoDTO, "La propina no puede ser negativa", 7);
						}
						double montoGasto = gastoDTO.getExpenseAmount().getValue();
						if (montoGasto <= 0) {
							return crearError(gastoDTO, "Monto de gasto inválido para calcular propina", 7);
						}
						double porcentaje = (propina / montoGasto) * 100.0;
						if (porcentaje > porcentajeMaximo) {
							errorDTO = crearError(gastoDTO,
									String.format("Propina $%.2f (%.2f%%) fuera de rango permitido (%.2f%%) en categoría: %s",
                                            propina, porcentaje, porcentajeMaximo,
											gastoDTO.getCategory() != null ? gastoDTO.getCategory().getName()
													: "Sin categoría"),
									7);
						}
					} catch (NumberFormatException e) {
						errorDTO = crearError(gastoDTO, "Valor de propina inválido", 7);
					}
					break;
				}
			}
		}
		return errorDTO;
	}

	public void acumularGastoComida(String clave, BigDecimal importe) {
		if (importe == null)
			return;
		gastosComida.merge(clave, importe, BigDecimal::add);
	}

	public List<ErrorDTO> validarLimitePorClave() {
		List<ErrorDTO> errores = new ArrayList<>();
		for (Map.Entry<String, BigDecimal> e : gastosComida.entrySet()) {
			BigDecimal total = e.getValue() != null ? e.getValue() : BigDecimal.ZERO;
			if (total.compareTo(BigDecimal.valueOf(MontosEnum.COMIDA_NACIONAL.getMonto())) > 0) {
				ErrorDTO error = crearErrorMonto(e.getKey(), total);
				errores.add(error);
			}
		}
		return errores;
	}

	public List<ErrorDTO> validarLimitePorClaveExtranjero() {
		List<ErrorDTO> errores = new ArrayList<>();
		for (Map.Entry<String, BigDecimal> e : gastosComida.entrySet()) {
			BigDecimal total = e.getValue() != null ? e.getValue() : BigDecimal.ZERO;
			if (total.compareTo(BigDecimal.valueOf(MontosEnum.COMIDA_EXTRANJERO.getMonto())) > 0) {
				ErrorDTO error = crearErrorMonto(e.getKey(), total);
				errores.add(error);
			}
		}
		return errores;
	}

	private ErrorDTO crearError(GastoDTO gastoDTO, String descripcion, Integer id) {
		double amount = gastoDTO.getExpenseAmount().getValue();
		Locale mx = new Locale("es", "MX");
		NumberFormat fmt = NumberFormat.getCurrencyInstance(mx);

		String texto = fmt.format(amount);
		ErrorDTO errorDTO = new ErrorDTO();
		errorDTO.setId(id);
		errorDTO.setDescripcion(descripcion);
		errorDTO.setIdObjeto(gastoDTO.getId());
		StringBuilder sb = new StringBuilder();
		sb.append(gastoDTO.getComment());
		sb.append(", ");
		sb.append(gastoDTO.getCategory() != null ? gastoDTO.getCategory().getName() : "Sin categoría");
		sb.append(", ");
		sb.append(texto);
		errorDTO.setDescripcionObjeto(sb.toString());
		return errorDTO;
	}

	private ErrorDTO crearErrorMonto(String descripcion, BigDecimal monto) {
		double amount = monto.doubleValue();
		Locale mx = new Locale("es", "MX");
		NumberFormat fmt = NumberFormat.getCurrencyInstance(mx);

		String texto = fmt.format(amount);
		ErrorDTO errorDTO = new ErrorDTO();

		errorDTO.setDescripcion("Limite de comidas superado por día: " + descripcion);
		StringBuilder sb = new StringBuilder();
		sb.append(texto);
		errorDTO.setDescripcionObjeto(sb.toString());
		return errorDTO;
	}

	private List<ErrorDTO> validacionesExtranjero(GastoDTO gastoDTO, CustomFieldDTO customFieldDTO) {
		List<ErrorDTO> errores = new ArrayList<>();
		if (gastoDTO.getCategory().getId().equals(CategoriasEnum.HOSPEDAJE_EXTRANJERO.getClave())
				&& !esValidoExtranjero(customFieldDTO.getValue(), gastoDTO.getExpenseAmount().getValue())) {
			log.info("*******************************supero limite hospedaje **********************************");
			errores.add(crearError(gastoDTO, "Limite de hospedaje por ciudad superado", 1));
		} else if (gastoDTO.getCategory().getId().equals(CategoriasEnum.COMIDA_EXTRANJERO.getClave())) {

			String fecha = DateUtils.obtenerFechaDDMMYYYY(gastoDTO.getDate());
			acumularGastoComida(fecha, BigDecimal.valueOf(gastoDTO.getExpenseAmount().getValue()));

			ErrorDTO errorPropina = validarPropinas(gastoDTO, 10.0, 83);
			if (errorPropina != null) {
				errores.add(errorPropina);
			}
		}
		return errores;
	}

	private boolean esValidoNacional(String destino, double gasto) {
		CiudadLimiteEnum limite = CiudadLimiteEnum.buscar(destino);
		if (limite == null) {
			return false;
		}
		return gasto <= limite.getLimite();
	}

	private boolean esValidoExtranjero(String destino, double gasto) {
		CiudadExtranjeraLimiteEnum limite = CiudadExtranjeraLimiteEnum.buscar(destino);
		if (limite == null) {
			return false;
		}
		return gasto <= limite.getValor();
	}

	private ErrorDTO validaTipoViajeCategoriasNacional(GastoDTO gastoDTO) {
		ErrorDTO error = null;

		if (CategoriasExtranjeroEnum.from(gastoDTO.getCategory().getName()) != null) {
			error = crearError(gastoDTO, "Gasto marcado como 'extranjero' en viaje nacional", 2);
		}

		return error;

	}

	private ErrorDTO validaTipoViajeCategoriasExtranjero(GastoDTO gastoDTO) {
		ErrorDTO error = null;
		if (CategoriasNacionalesEnum.from(gastoDTO.getCategory().getName()) != null) {
			error = crearError(gastoDTO, "Gasto marcado como 'nacional' en viaje extranjero", 2);
		}

		return error;

	}

	private ErrorDTO validarGastoInsumosCafeteria(GastoDTO gastoDTO, List<GrupoDTO> grupos) {

		MontosEnum montoDireccion = obtenerMontoDireccionOperativa(grupos);
		if (montoDireccion == null) {
			return null;
		}

		BigDecimal limite = BigDecimal.valueOf(montoDireccion.getMonto());
		BigDecimal importeGasto = BigDecimal.valueOf(gastoDTO.getExpenseAmount().getValue());

		if (importeGasto != null && importeGasto.compareTo(limite) > 0) {

			GrupoEnum grupoDireccion = null;
			for (GrupoDTO grupo : grupos) {
				GrupoEnum gEnum = GrupoEnum.fromId(grupo.getId());
				if (gEnum == GrupoEnum.DIR_DIVISIONAL || gEnum == GrupoEnum.DIR_ZONA || gEnum == GrupoEnum.DIR_REGIONAL
						|| gEnum == GrupoEnum.DIR_BCA_PATRIMONIAL_Y_PRIVADA) {
					grupoDireccion = gEnum;
					break;
				}
			}

			String nombreDireccion = (grupoDireccion != null) ? grupoDireccion.getNombre() : "su dirección operativa";

			String mensaje = String.format(
					"El gasto por Insumos de cafetería excede el límite permitido de $%,.2f para %s.", limite,
					nombreDireccion);

			return crearError(gastoDTO, mensaje, 4);
		}

		return null;
	}

	private MontosEnum obtenerMontoDireccionOperativa(List<GrupoDTO> grupos) {
		if (grupos == null) {
			return null;
		}

		for (GrupoDTO grupo : grupos) {
			GrupoEnum grupoEnum = GrupoEnum.fromId(grupo.getId());
			if (grupoEnum == null) {
				continue;
			}

			switch (grupoEnum) {
			case DIR_DIVISIONAL:
				return MontosEnum.DIR_DIVISIONAL;
			case DIR_ZONA:
				return MontosEnum.DIR_ZONA;
			case DIR_REGIONAL:
				return MontosEnum.DIR_REGIONAL;
			case DIR_BCA_PATRIMONIAL_Y_PRIVADA:
				return MontosEnum.DIR_BCA_PATRIMONIAL_Y_PRIVADA;
			default:
			}
		}

		return null;
	}

	private ErrorDTO validarProveedoresProhibidos(GastoDTO gastoDTO) {
		// Esta lista podría moverse a un archivo de propiedades para ser más
		// configurable.
		List<String> proveedoresProhibidos = Arrays.asList("BAR", "DISCOTECA", "ANTRO", "CANTINA", "CENTRO NOCTURNO",
				"LICORERIA", "ALCOHOL", "CIGARRO", "SPA", "SHOW", "CASINO", "APUESTA", "LOTERIA", "LIVERPOOL",
				"PALACIO DE HIERRO", "SEARS", "WALMART", "COSTCO", "SORIANA", "NETFLIX", "SPOTIFY", "XBOX", "APPLE",
				"APPLE MUSIC", "MAX", "AMAZON MUSIC", "AMAZON PRIME", "ESTETICA", "SALON DE BELLEZA", "BARBERIA",
				"MANICURE", "PEDICURE", "MERCADO LIBRE", "MERCADOLIBRE", "TICKETMASTER", "CINE", "BOLICHE",
				"PISTA DE PATINAJE", "TEATRO", "CONCIERTO", "FUTBOL", "BEISBOL", "AUTOMOTRIZ", "PANADERIA",
				"PASTELERIA", "SUPERMERCADO", "OXXO", "SUPERK", "SUPER K", "ESCUELA", "COLEGIO", "UNIFORME",
				"LIBRERIA");
		String merchant = gastoDTO.getMerchant();

		if (merchant != null && !merchant.isBlank()) {
			String merchantNormalizado = normalizarString(merchant).toUpperCase();
			for (String proveedor : proveedoresProhibidos) {
				if (merchantNormalizado.contains(proveedor)) {
					return crearError(gastoDTO, "Se ha encontrado un proveedor prohibido: " + proveedor, 6);
				}
			}
		}
		return null;
	}

	private ErrorDTO validarGastoGasolina(GastoDTO gastoDTO, List<GrupoDTO> puestos) {

		MontosEnum montoPuesto = obtenerMontoPuestoEmpleado(puestos);
		if (montoPuesto == null) {
			return null;
		}

		BigDecimal limite = BigDecimal.valueOf(montoPuesto.getMonto());
		BigDecimal importeGasto = BigDecimal.valueOf(gastoDTO.getExpenseAmount().getValue());

		if (importeGasto != null && importeGasto.compareTo(limite) > 0) {

			GrupoEnum grupoPuesto = null;
			for (GrupoDTO puesto : puestos) {
				GrupoEnum gEnum = GrupoEnum.fromId(puesto.getId());
				if (gEnum == GrupoEnum.EJECUTIVOS_BEYG || gEnum == GrupoEnum.EJECUTIVOS_CIB
						|| gEnum == GrupoEnum.EJECUTIVOS_PYME || gEnum == GrupoEnum.ASESORES_INVERSION
						|| gEnum == GrupoEnum.EJECUTIVOS_CASH_MANAGEMENT
						|| gEnum == GrupoEnum.EJECUTIVOS_BCA_ELECTRONICA) {
					grupoPuesto = gEnum;
					break;
				}
			}

			String nombrePuesto = (grupoPuesto != null) ? grupoPuesto.getNombre() : "su puesto";

			String mensaje = String.format("El gasto por Gasolina excede el límite permitido de $%,.2f para %s.",
					limite, nombrePuesto);

			return crearError(gastoDTO, mensaje, 5);
		}

		return null;
	}

	private MontosEnum obtenerMontoPuestoEmpleado(List<GrupoDTO> puestos) {
		if (puestos == null) {
			return null;
		}

		for (GrupoDTO puesto : puestos) {
			GrupoEnum grupoEnum = GrupoEnum.fromId(puesto.getId());
			if (grupoEnum == null) {
				continue;
			}

			switch (grupoEnum) {
			case EJECUTIVOS_BEYG:
				return MontosEnum.EJECUTIVOS_BEYG;
			case EJECUTIVOS_CIB:
				return MontosEnum.EJECUTIVOS_CIB;

			case EJECUTIVOS_PYME:
				return MontosEnum.EJECUTIVOS_PYME;
			case ASESORES_INVERSION:
				return MontosEnum.ASESORES_INVERSION;
			case EJECUTIVOS_CASH_MANAGEMENT:
				return MontosEnum.EJECUTIVOS_CASH_MANAGEMENT;
			case EJECUTIVOS_BCA_ELECTRONICA:
				return MontosEnum.EJECUTIVOS_BCA_ELECTRONICA;

			default:
			}
		}

		return null;
	}

	/**
	 * Elimina los acentos y diacríticos de una cadena de texto.
	 */
	private String normalizarString(String texto) {
		String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
		return textoNormalizado.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	@Override
	public Integer alta(List<ExpenseDTO> gastos) {
		log.debug(CaptioJsonUtils.toJson(gastos));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<ExpenseDTO>> entity = new HttpEntity<>(gastos, headers);
		try {
			ResponseEntity<GenericResponseDTO<GastoDTO>[]> response = restTemplate.exchange(
					properties.getUrlPostGastos(), HttpMethod.POST, entity,
					new ParameterizedTypeReference<GenericResponseDTO<GastoDTO>[]>() {
					});
			return response.getBody() != null ? response.getBody()[0].getResult().getId() : null;
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("❌ Error 400 - Bad Request: ", e);
			}
		}
		return null;
	}

	@Override
	public void eliminar(List<GastoDTO> gastos) {
		ObjetosUtils.limpiarCamposExcepto(gastos, List.of("id"));
		log.debug(CaptioJsonUtils.toJson(gastos));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		HttpEntity<List<GastoDTO>> entity = new HttpEntity<>(gastos, headers);
		try {
			restTemplate.exchange(properties.getUrlDeleteGastos(), HttpMethod.DELETE, entity, ExpenseDTO[].class);

		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("❌ Error 400 - Bad Request: ", e);
			}
		}
	}

	@Override
	public List<GastoDTO> obtenerAdjuntos(String filtro) {
		List<GastoDTO> gastos = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", properties.getCustomerKey());
		String baseUrl = properties.getUrlGetAttachments();
		URI uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("filters", filtro).build().encode().toUri();
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		try {
			ResponseEntity<GastoDTO[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity, GastoDTO[].class);
			if (response.getBody() != null) {
				gastos = List.of(response.getBody());
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}
		return gastos;
	}


	@Override
	public byte[] obtenerXmlAdjunto(String urlFile) {

		byte[] utf8Bytes=null;
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "xml", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.add("customerKey", properties.getCustomerKey());

		URI uri = UriComponentsBuilder.fromUriString(urlFile).build().encode().toUri();

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

			if (response.getBody() != null) {

				utf8Bytes = response.getBody().getBytes(StandardCharsets.UTF_8);

			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				log.error("Error 400 - Bad Request: ", e);
			}
		}

        return utf8Bytes;
	}





}
