package com.sngular.captio.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.sngular.captio.dto.CustomFieldDTO;
import com.sngular.captio.dto.DotacionDTO;
import com.sngular.captio.dto.MonedaDTO;

public class DotacionPipeExportUtil {

	private DotacionPipeExportUtil() {
	}

	private static final String PIPE = "|";
	private static final String DPIPE = "||"; // separador entre elementos internos
	private static final String NL = System.lineSeparator();

	/** Exporta la lista a texto separado por pipes con encabezado. */
	public static String toPipe(List<DotacionDTO> list) {
		return toPipe(list, false);
	}

	/** Exporta la lista a texto separado por pipes, con o sin encabezado. */
	public static String toPipe(List<DotacionDTO> list, boolean withHeader) {
		if (list == null || list.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder(256 * list.size());
		if (withHeader) {
			sb.append("UserId|Reason|Comment|WorkflowId|Currencies|CustomFields").append(NL);
		}

		for (DotacionDTO d : list) {
			if (d == null)
				continue;

			sb.append(nz(d.getUserId())).append(PIPE).append(esc(d.getLoginUser())).append(PIPE)
					.append(esc(d.getReason())).append(PIPE).append(esc(d.getComment())).append(PIPE)
					.append(nz(d.getWorkflowId())).append(PIPE).append(renderCurrencies(d.getCurrencies())).append(PIPE)
					.append(renderCustomFields(d.getCustomFields())).append(NL);
		}
		return sb.toString();
	}

	// ---- Render anidados ----

	/**
	 * Currencies como elementos internos separados por '||' y pares clave=valor.
	 */
	private static String renderCurrencies(List<MonedaDTO> cs) {
		if (cs == null || cs.isEmpty())
			return "";
		return cs.stream().filter(Objects::nonNull)
				.map(c -> String.format("Id=%s,CurrencyId=%s,Code=%s,Symbol=%s,ISO=%s,Amount=%s", nz(c.getId()),
						nz(c.getCurrencyId()), esc(c.getCode()), esc(c.getSymbol()), esc(c.getIsoCode()), c.getMonto()))
				.collect(Collectors.joining(DPIPE));
	}

	/**
	 * CustomFields como elementos internos separados por '||'. Asume getters
	 * típicos getName()/getValue(); ajusta si tus campos difieren.
	 */
	private static String renderCustomFields(List<CustomFieldDTO> cfs) {
		if (cfs == null || cfs.isEmpty())
			return "";
		return cfs.stream().filter(Objects::nonNull)
				.map(cf -> "Name=" + esc(cf.getName()) + ",Value=" + esc(cf.getValue()))
				.collect(Collectors.joining(DPIPE));
	}

	// ---- Helpers ----

	/** Escapa pipes y saltos de línea para no romper el formato. */
	private static String esc(Object o) {
		if (o == null)
			return "";
		String s = String.valueOf(o);
		return s.replace("|", "\\|").replace("\n", " ").replace("\r", " ");
	}

	/** null -> "" para Strings; para números/objetos -> String.valueOf(o). */
	private static String nz(Object o) {
		return o == null ? "" : String.valueOf(o);
	}

}
