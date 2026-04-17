package com.sngular.captio.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.MontoDTO;
import com.sngular.captio.dto.SimpleIdDTO;

public class GastoRowMapper implements RowMapper<GastoDTO> {

	private static final int COL_STATEMENT_INFORMATION = 8;
	private static final int COL_FECHA_REPORTING = 12;
	private static final int COL_CARRIER_HOTEL_NAME = 24;
	private static final int COL_FULL_AMOUNT = 29;

	private static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy");

	@Override
	public GastoDTO mapRow(RowSet rowSet) throws Exception {
		String[] row = rowSet.getCurrentRow();

		String statementInfo = get(row, COL_STATEMENT_INFORMATION);
		String fechaReporting = get(row, COL_FECHA_REPORTING);
		String proveedor = get(row, COL_CARRIER_HOTEL_NAME);
		String fullAmount = get(row, COL_FULL_AMOUNT);
		fullAmount = fullAmount.replace("$", "").replace(",", "");

		GastoDTO dto = new GastoDTO();
		MontoDTO montoDTO = new MontoDTO();
		SimpleIdDTO simpleIdDTO = new SimpleIdDTO();

		simpleIdDTO.setCodigo(nullIfBlank(statementInfo));
		dto.setUser(simpleIdDTO);

		dto.setMerchant(nullIfBlank(proveedor));

		BigDecimal monto = parseBigDecimal(fullAmount);
		if (monto != null) {
			montoDTO.setValue(monto.doubleValue());
		}
		dto.setExpenseAmount(montoDTO);

		dto.setDate(parseLocalDateTime(fechaReporting));

		return dto;
	}

	private String get(String[] row, int index) {
		if (row == null || index < 0 || index >= row.length) {
			return null;
		}
		return row[index];
	}

	private String nullIfBlank(String s) {
		if (s == null)
			return null;
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}

	private BigDecimal parseBigDecimal(String s) {
		if (s == null)
			return null;
		String t = s.trim();
		if (t.isEmpty())
			return null;
		t = t.replace(",", "");

		try {
			return new BigDecimal(t);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private LocalDateTime parseLocalDateTime(String s) {
		if (s == null)
			return null;
		String t = s.trim();
		if (t.isEmpty())
			return null;
		t = completarConCero(t);
		try {
			LocalDate date = LocalDate.parse(t, FECHA_FORMATTER);
			return date.atStartOfDay();
		} catch (Exception ex) {
			return null;
		}
	}

	public static String completarConCero(String fecha) {
		if (fecha == null || fecha.trim().isEmpty())
			return fecha;

		String[] partes = fecha.split("/");

		if (partes.length != 3)
			return fecha;
		String mes = partes[0].length() == 1 ? "0" + partes[0] : partes[0];
		String dia = partes[1].length() == 1 ? "0" + partes[1] : partes[1];
		String anio = partes[2];

		return mes + "/" + dia + "/" + anio;
	}
}
