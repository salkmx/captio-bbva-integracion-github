package com.sngular.captio.util;

import com.sngular.captio.dto.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;

public class DescuentoNominaPipeExportUtil {

	private DescuentoNominaPipeExportUtil() {
	}

	private static final String PIPE = "|";
	private static final String NL = System.lineSeparator();
	private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	public static String toPipe(List<DescuentoNominaDTO> list, boolean withHeader) {
		if (list == null || list.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder(256 * list.size());
		if (withHeader) {
			sb.append(
					"CodigoUsuario|EmailUsuario|FechaInicioViaje|FechaFinViaje|TotalGastos|TotalDotacion|TotalReembolso|TotalDeuda|")
					.append(NL);
		}

		for (DescuentoNominaDTO g : list) {
			if (g == null)
				continue;

			sb.append(nz(g.getEmployeeCode()) + "|");
			sb.append(nz(g.getEmployeeEmail()) + "|");
			sb.append(nz(g.getTravelStartDate()) + "|");
			sb.append(nz(g.getTravelEndDate()) + "|");
			sb.append(nz(g.getTotalAmountExpenses()) + "|");
			sb.append(nz(g.getTotalAmountAdvances()) + "|");
			sb.append(nz(g.getAmountRefund()) + "|");
			sb.append(nz(g.getAmountDebt()) + "|");
			sb.append(NL);

		}
		return sb.toString();
	}

	private static String nz(Object o) {
		return (o == null) ? " " : String.valueOf(o);
	}

}
