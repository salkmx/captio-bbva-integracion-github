package com.sngular.captio.mapper;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.sngular.captio.dto.BalanzaDTO;
import com.sngular.captio.dto.GastoDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GastoBalanzaMapper {

	@Mapping(target = "folio", source = "externalId")
	@Mapping(target = "fchReg", source = "creationDate", qualifiedByName = "localDateTimeToDateTimeString")
	@Mapping(target = "fechaRegistro", source = "creationDate", qualifiedByName = "localDateTimeToDateTimeString")
	@Mapping(target = "fechaContable", source = "date", qualifiedByName = "localDateTimeToDateString")
	@Mapping(target = "descripcion", source = "comment")
	@Mapping(target = "nomComercial", source = "merchant")
	@Mapping(target = "viajero", source = "usuarioCorporativo")
	@Mapping(target = "registroViajero", source = "userId", qualifiedByName = "integerToString")
	@Mapping(target = "gasto", source = "expenseType", qualifiedByName = "integerToString")
	@Mapping(target = "impTot", source = "finalAmount", qualifiedByName = "montoToBigDecimal")
	@Mapping(target = "impTotal", source = "finalAmount", qualifiedByName = "montoToBigDecimal")
	@Mapping(target = "impNeto", source = "expenseAmount", qualifiedByName = "montoToBigDecimal")
	@Mapping(target = "subtotal", source = "expenseAmount", qualifiedByName = "montoToBigDecimal")
	@Mapping(target = "deducible", source = "vatExempt", qualifiedByName = "vatExemptToDeducibleString")
	@Mapping(target = "xml", source = "uuid")
	@Mapping(target = "numNota", source = "invoiceNumber")
	@Mapping(target = "fchFactura", source = "invoice", qualifiedByName = "facturaToFechaString")
	@Mapping(target = "rfcProveedor", source = "invoice", qualifiedByName = "facturaToRfcString")
	@Mapping(target = "razonSocial", source = "invoice", qualifiedByName = "facturaToRazonSocialString")
	@Mapping(target = "formaPago", source = "paymentMethod", qualifiedByName = "metodoPagoToFormaPagoString")
	BalanzaDTO toBalanza(GastoDTO gasto);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "externalId", source = "folio")
	@Mapping(target = "creationDate", source = "fchReg", qualifiedByName = "dateTimeStringToLocalDateTime")
	@Mapping(target = "date", source = "fechaContable", qualifiedByName = "dateStringToLocalDateTime")
	@Mapping(target = "comment", source = "descripcion")
	@Mapping(target = "merchant", source = "nomComercial")
	@Mapping(target = "usuarioCorporativo", source = "viajero")
	@Mapping(target = "userId", source = "registroViajero", qualifiedByName = "stringToInteger")
	@Mapping(target = "expenseType", source = "gasto", qualifiedByName = "stringToInteger")
	@Mapping(target = "vatExempt", source = "deducible", qualifiedByName = "deducibleStringToVatExempt")
	@Mapping(target = "expenseAmount", ignore = true)
	@Mapping(target = "finalAmount", ignore = true)
	@Mapping(target = "invoice", ignore = true)
	@Mapping(target = "paymentMethod", ignore = true)
	@Mapping(target = "vatRates", ignore = true)
	@Mapping(target = "attachments", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "customFields", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "report", ignore = true)
	@Mapping(target = "payment", ignore = true)
	@Mapping(target = "mileageInfo", ignore = true)
	@Mapping(target = "perDiemInfo", ignore = true)
	GastoDTO toGasto(BalanzaDTO balanza);

	List<BalanzaDTO> toBalanzaList(List<GastoDTO> gastos);

	List<GastoDTO> toGastoList(List<BalanzaDTO> balanzas);

	DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Named("localDateTimeToDateString")
	static String localDateTimeToDateString(LocalDateTime value) {
		return value == null ? null : value.format(DATE);
	}

	@Named("localDateTimeToDateTimeString")
	static String localDateTimeToDateTimeString(LocalDateTime value) {
		return value == null ? null : value.format(DATETIME);
	}

	@Named("dateStringToLocalDateTime")
	static LocalDateTime dateStringToLocalDateTime(String value) {
		if (value == null || value.isBlank())
			return null;
		return LocalDateTime.parse(value.trim() + " 00:00:00", DATETIME);
	}

	@Named("dateTimeStringToLocalDateTime")
	static LocalDateTime dateTimeStringToLocalDateTime(String value) {
		if (value == null || value.isBlank())
			return null;
		return LocalDateTime.parse(value.trim(), DATETIME);
	}

	@Named("integerToString")
	static String integerToString(Integer value) {
		return value == null ? null : String.valueOf(value);
	}

	@Named("stringToInteger")
	static Integer stringToInteger(String value) {
		if (value == null || value.isBlank())
			return null;
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Named("deducibleStringToVatExempt")
	static Boolean deducibleStringToVatExempt(String deducible) {
		if (deducible == null)
			return null;
		String v = deducible.trim().toUpperCase();
		if (v.equals("SI") || v.equals("S") || v.equals("1") || v.equals("TRUE"))
			return false;
		if (v.equals("NO") || v.equals("N") || v.equals("0") || v.equals("FALSE"))
			return true;
		return null;
	}

	@Named("vatExemptToDeducibleString")
	static String vatExemptToDeducibleString(Boolean vatExempt) {
		if (vatExempt == null)
			return null;
		return Boolean.TRUE.equals(vatExempt) ? "NO" : "SI";
	}

	@Named("montoToBigDecimal")
	static BigDecimal montoToBigDecimal(Object montoDto) {
		if (montoDto == null)
			return null;

		BigDecimal bd = tryReadBigDecimal(montoDto, "getAmount", "getValue", "getTotal", "getImporte");
		if (bd != null)
			return bd;

		Double d = tryReadDouble(montoDto, "getAmount", "getValue", "getTotal", "getImporte");
		return d == null ? null : BigDecimal.valueOf(d);
	}

	@Named("facturaToFechaString")
	static String facturaToFechaString(Object facturaDto) {
		if (facturaDto == null)
			return null;
		LocalDateTime ldt = tryReadLocalDateTime(facturaDto, "getDate", "getFecha", "getIssueDate");
		return ldt == null ? null : ldt.format(DATE);
	}

	@Named("facturaToRfcString")
	static String facturaToRfcString(Object facturaDto) {
		if (facturaDto == null)
			return null;
		return tryReadString(facturaDto, "getRfc", "getTin", "getProviderRfc", "getSupplierTin");
	}

	@Named("facturaToRazonSocialString")
	static String facturaToRazonSocialString(Object facturaDto) {
		if (facturaDto == null)
			return null;
		return tryReadString(facturaDto, "getRazonSocial", "getProviderName", "getSupplierName", "getNombreProveedor");
	}

	@Named("metodoPagoToFormaPagoString")
	static String metodoPagoToFormaPagoString(Object metodoPagoDto) {
		if (metodoPagoDto == null)
			return null;

		String name = tryReadString(metodoPagoDto, "getName", "getDescripcion", "getDescription");
		if (name != null && !name.isBlank())
			return name;

		Integer id = tryReadInteger(metodoPagoDto, "getId", "getCode");
		return id == null ? null : String.valueOf(id);
	}

	static String tryReadString(Object target, String... getters) {
		for (String g : getters) {
			try {
				Method m = target.getClass().getMethod(g);
				Object v = m.invoke(target);
				if (v instanceof String s)
					return s;
			} catch (Exception ignored) {
			}
		}
		return null;
	}

	static Integer tryReadInteger(Object target, String... getters) {
		for (String g : getters) {
			try {
				Method m = target.getClass().getMethod(g);
				Object v = m.invoke(target);
				if (v instanceof Integer i)
					return i;
				if (v instanceof Number n)
					return n.intValue();
				if (v instanceof String s && !s.isBlank())
					return Integer.valueOf(s.trim());
			} catch (Exception ignored) {
			}
		}
		return null;
	}

	static Double tryReadDouble(Object target, String... getters) {
		for (String g : getters) {
			try {
				Method m = target.getClass().getMethod(g);
				Object v = m.invoke(target);
				if (v instanceof Double d)
					return d;
				if (v instanceof Number n)
					return n.doubleValue();
				if (v instanceof String s && !s.isBlank())
					return Double.valueOf(s.trim());
			} catch (Exception ignored) {
			}
		}
		return null;
	}

	static BigDecimal tryReadBigDecimal(Object target, String... getters) {
		for (String g : getters) {
			try {
				Method m = target.getClass().getMethod(g);
				Object v = m.invoke(target);
				if (v instanceof BigDecimal bd)
					return bd;
				if (v instanceof Number n)
					return BigDecimal.valueOf(n.doubleValue());
				if (v instanceof String s && !s.isBlank())
					return new BigDecimal(s.trim());
			} catch (Exception ignored) {
			}
		}
		return null;
	}

	static LocalDateTime tryReadLocalDateTime(Object target, String... getters) {
		for (String g : getters) {
			try {
				Method m = target.getClass().getMethod(g);
				Object v = m.invoke(target);
				if (v instanceof LocalDateTime ldt)
					return ldt;
			} catch (Exception ignored) {
			}
		}
		return null;
	}
}
