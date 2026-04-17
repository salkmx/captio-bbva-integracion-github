package com.sngular.captio.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.sngular.captio.dto.CategoriaDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.MontoDTO;

public class GastoMedicoPipeExporter {

	private GastoMedicoPipeExporter() {
	}

	private static final String PIPE = "|";
	private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("ddMMyyyy");

	public static void exportPendientes(List<GastoDTO> gastos, Path outputDir) throws IOException {

		String fileName = "GASTOMEDICO_" + LocalDate.now().format(FILE_DATE) + ".txt";

		Path filePath = outputDir.resolve(fileName);

		Files.createDirectories(outputDir);

		try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE,
				StandardOpenOption.APPEND)) {

			for (GastoDTO g : gastos) {
/*
				if (!esPendiente(g)) {
					continue;
				}
*/
				writer.write(buildLine(g));
				writer.newLine();
			}
		}
	}

	private static String buildLine(GastoDTO g) {
		return String.join(PIPE, val(g.getUsuarioCorporativo()), val(g.getDate()), val(g.getMerchant()),
				monto(g.getExpenseAmount()), categoria(g.getCategory()), val(g.getNombreInforme()));
	}

	private static String val(Object o) {
		return o == null ? "" : o.toString();
	}

	private static String monto(MontoDTO m) {
		return m == null ? "" : String.valueOf(m.getValue());
	}

	private static String categoria(CategoriaDTO c) {
		return c == null ? "" : String.valueOf(c.getName());
	}
}
