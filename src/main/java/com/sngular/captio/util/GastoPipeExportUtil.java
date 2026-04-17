package com.sngular.captio.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;

import com.sngular.captio.dto.CategoriaDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.MetodoPagoDTO;
import com.sngular.captio.dto.MontoDTO;
import com.sngular.captio.dto.SimpleIdDTO;

public class GastoPipeExportUtil {

	private GastoPipeExportUtil() {
	}

	private static final String PIPE = "|";
	private static final String NL = System.lineSeparator();
	private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	/** Exporta la lista a texto separado por pipes con encabezado. */
	public static String toPipe(List<GastoDTO> list) {
		return toPipe(list, true);
	}

	/** Exporta la lista a texto separado por pipes, con o sin encabezado. */
	public static String toPipe(List<GastoDTO> list, boolean withHeader) {
		if (list == null || list.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder(256 * list.size());
		if (withHeader) {
			sb.append(
					"Id|ExternalId|Date|CreationDate|Merchant|ExpenseAmount|FinalAmount|Comment|Category|PaymentMethod|User|Report|Payment")
					.append(NL);
		}

		for (GastoDTO g : list) {
			if (g == null)
				continue;

			sb.append(nz(g.getId())).append(PIPE).append(esc(g.getExternalId())).append(PIPE).append(fmt(g.getDate()))
					.append(PIPE).append(fmt(g.getCreationDate())).append(PIPE).append(esc(g.getMerchant()))
					.append(PIPE).append(renderMonto(g.getExpenseAmount())).append(PIPE)
					.append(renderMonto(g.getFinalAmount())).append(PIPE).append(esc(g.getComment())).append(PIPE)
					.append(renderCategoria(g.getCategory())).append(PIPE)
					.append(renderMetodoPago(g.getPaymentMethod())).append(PIPE).append(renderUsuario(g.getUser(), g.getUsuarioCorporativo()))
					.append(PIPE).append(renderReporte(g.getReport(), g.getNombreInforme())).append(PIPE).append(renderSimple(g.getPayment()))
					.append(NL);
		}
		return sb.toString();
	}

	// ---- Render anidados ----

	/**
	 * Monto como pares clave=valor separados por coma. Ajusta getters según tu DTO.
	 */
	private static String renderMonto(MontoDTO m) {
		if (m == null)
			return "";
		return new StringBuilder(64).append("Amount=").append(nz(m.getValue())).append(',').append("Currency=")
				.append(nz(m.getCurrency().getCode())).toString();
	}

	/** Categoria: Id y Name si existen. */
	private static String renderCategoria(CategoriaDTO c) {
		if (c == null)
			return "";
		StringBuilder sb = new StringBuilder(48);
		sb.append("Id=").append(nzSafe(() -> c.getId()));
		String name = escSafe(() -> c.getName());
		if (!name.isEmpty())
			sb.append(",Name=").append(name);
		// Si tienes code: sb.append(",Code=").append(escSafe(() -> c.getCode()));
		return sb.toString();
	}

	/** Método de pago: Id y Name si existen. */
	private static String renderMetodoPago(MetodoPagoDTO pm) {
		if (pm == null)
			return "";
		StringBuilder sb = new StringBuilder(48);
		sb.append("Id=").append(nzSafe(() -> pm.getId()));
		String name = escSafe(() -> pm.getName());
		if (!name.isEmpty())
			sb.append(",Name=").append(name);
		// Ejemplos opcionales:
		// sb.append(",Type=").append(escSafe(() -> pm.getType()));
		// sb.append(",Last4=").append(escSafe(() -> pm.getLast4()));
		return sb.toString();
	}

	/** Método de pago: Id y Name si existen. */
	private static String renderUsuario(SimpleIdDTO u, String usuarioCorporativo) {
		if (u == null)
			return "";
		StringBuilder sb = new StringBuilder(48);
		sb.append("Id=").append(nzSafe(() -> u.getId()));
		String name = escSafe(() -> usuarioCorporativo);
		if (!name.isEmpty())
			sb.append(",UsuarioCorporativo=").append(name);
		return sb.toString();
	}

	/** Método de pago: Id y Name si existen. */
	private static String renderReporte(SimpleIdDTO u, String nombreInforme) {
		if (u == null)
			return "";
		StringBuilder sb = new StringBuilder(48);
		sb.append("Id=").append(nzSafe(() -> u.getId()));
		String name = escSafe(() -> nombreInforme);
		if (!name.isEmpty())
			sb.append(",nombreReporte=").append(name);
		return sb.toString();
	}

	/** SimpleIdDTO: Id y Name si existen. */
	private static String renderSimple(SimpleIdDTO s) {
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder(32);
		sb.append("Id=").append(nzSafe(() -> s.getId()));
		String name = escSafe(() -> s.getId());
		if (!name.isEmpty())
			sb.append(",Name=").append(name);
		return sb.toString();
	}

	// ---- Helpers ----

	/** Escapa pipes y saltos de línea para no romper el formato. */
	private static String esc(Object o) {
		if (o == null)
			return "";
		String s = String.valueOf(o);
		return s.replace("|", "\\|").replace("\n", " ").replace("\r", " ");
	}

	/** null -> "" para cualquier objeto. */
	private static String nz(Object o) {
		return (o == null) ? "" : String.valueOf(o);
	}

	/** Formatea LocalDateTime a ISO_LOCAL_DATE_TIME. */
	private static String fmt(LocalDateTime dt) {
		return (dt == null) ? "" : dt.format(ISO);
	}

	// Helpers "seguros" para casos donde algún getter no exista o devuelva null.
	@FunctionalInterface
	private interface getter<T> {
		T get();
	}

	private static String escSafe(Supplier<?> s) {
		try {
			return esc(s.get());
		} catch (RuntimeException e) {
			return "";
		}
	}

	private static String nzSafe(getter<?> g) {
		try {
			return nz(g.get());
		} catch (Throwable t) {
			return "";
		}
	}
}
