package com.sngular.captio.util;

import com.sngular.captio.dto.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;

public class DIOTPipeExportUtil {

	private DIOTPipeExportUtil() {
	}

	private static final String PIPE = "|";
	private static final String NL = System.lineSeparator();
	private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	public static String toPipe(List<DiotDTO> list) {
		return toPipe(list, true);
	}

	public static String toPipe(List<DiotDTO> list, boolean withHeader) {

		if (list == null || list.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder(256 * list.size());
		if (withHeader) {
			sb.append(
					"Encabezados DIOT")
					.append(NL);
		}

		for (DiotDTO g : list) {
			if (g == null)
				continue;

			sb.append(nz(g.getTipoTerceroCodigo())).append(PIPE).
				    append(nz(g.getTipoOperacionCodigo())).append(PIPE).
				    append(nz(g.getRfc())).append(PIPE).
				    append(nz(g.getIdExtranjero())).append(PIPE).
				    append(nz(g.getNombreExtranjero())).append(PIPE).
				    append(nz(g.getCodigoPais())).append(PIPE).
				    append(nz(g.getCodigoPaisFiscal())).append(PIPE).
					append(nz(g.getValorActosServiciosTasa8_RFN())).append(PIPE).
					append(nz(g.getDescuentoActosServiciosTasa8_RFN())).append(PIPE).
					append(nz(g.getValorActosServiciosTasa8_RFS())).append(PIPE).
					append(nz(g.getDescuentoActosServiciosTasa8_RFS())).append(PIPE).
					append(nz(g.getValorActosServiciosTasa16())).append(PIPE).
					append(nz(g.getDescuentoActosServiciosTasa16())).append(PIPE).
					append(nz(g.getImportancionesTangiblesTasa16())).append(PIPE).
					append(nz(g.getDescuentoImportancionesTangiblesTasa16())).append(PIPE).
					append(nz(g.getImportancionesIntangiblesTasa16())).append(PIPE).
					append(nz(g.getDescuentoImportancionesIntangiblesTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoAcreditableTasa8_RFN())).append(PIPE).
					append(nz(g.getIvaPagadoAcreditableProporcionTasa8_RFN())).append(PIPE).
					append(nz(g.getIvaPagadoAcreditableTasa8_RFS())).append(PIPE).
					append(nz(g.getIvaPagadoAcreditableProporcionTasa8_RFS())).append(PIPE).
					append(nz(g.getIvaPagadoAcreditableTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoAcreditableProporcionTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoAcreditableImportancionTangibleTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoAcreditableProporcionImportacionTangibleTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoAcreditableImportancionIntangibleTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoAcreditableProporcionImportacionIntangibleTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableProporcionalTasa8_RFN())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableTasa8_RFN())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableExentoTasa8_RFN())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableNoObjetoTasa8_RFN())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableProporcionalTasa8_RFS())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableTasa8_RFS())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableExentoTasa8_RFS())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableNoObjetoTasa8_RFS())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableProporcionalTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableExentoTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableNoObjetoTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableProporcionalImportacionTangibleTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableImportacionTangibleTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableImportacionTangibleExentoTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableImportacionTangibleNoObjetoTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableProporcionalImportacionIntangibleTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableImportacionIntangibleTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableImportacionIntangibleExentoTasa16())).append(PIPE).
					append(nz(g.getIvaPagadoNOAcreditableImportacionIntangibleNoObjetoTasa16())).append(PIPE).
					append(nz(g.getIvaRetenidoContribuyente())).append(PIPE).
					append(nz(g.getActosPagadosImportacionExento())).append(PIPE).
					append(nz(g.getExentos())).append(PIPE).
					append(nz(g.getBase0())).append(PIPE).
					append(nz(g.getNoObjeto())).append(PIPE).
					append(nz(g.getNoObjetoSinEstablecimientoNacional())).append(PIPE).
					append(nz(g.getManifiestoEfectosFiscales())).append(PIPE).
				append(NL);

		}
		return sb.toString();
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
}
