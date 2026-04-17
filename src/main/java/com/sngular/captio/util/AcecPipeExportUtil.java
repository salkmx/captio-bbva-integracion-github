package com.sngular.captio.util;

import java.util.List;

import com.sngular.captio.dto.AcecDTO;

public class AcecPipeExportUtil {

	private AcecPipeExportUtil() {
	}

	private static final String PIPE = "|";
	private static final String NL = System.lineSeparator();

	/** Exporta la lista a texto separado por pipes con encabezado. */
	public static String toPipe(List<AcecDTO> list) {
		return toPipe(list, false);
	}

	/** Exporta la lista a texto separado por pipes, con o sin encabezado. */
	public static String toPipe(List<AcecDTO> list, boolean withHeader) {
		if (list == null || list.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder(256 * list.size());

		if (withHeader) {
			sb.append(header()).append(NL);
		}

		for (AcecDTO d : list) {
			if (d == null) {
				continue;
			}

			sb.append(esc(d.getEmpresa())).append(PIPE).append(esc(d.getClaveInterfaz())).append(PIPE)
					.append(esc(d.getTipoEvento())).append(PIPE).append(nz(d.getNumeroSecuencia())).append(PIPE)

					.append(nz(d.getFechaContable())).append(PIPE).append(nz(d.getFechaOperacion())).append(PIPE)
					.append(nz(d.getFechaValor())).append(PIPE)

					.append(esc(d.getDivisa1())).append(PIPE).append(esc(d.getDivisa2())).append(PIPE)
					.append(esc(d.getDivisa3())).append(PIPE)

					.append(esc(d.getCentroAlta())).append(PIPE).append(esc(d.getCentroOrigen())).append(PIPE)
					.append(esc(d.getCentroDestino())).append(PIPE)

					.append(esc(d.getProducto())).append(PIPE).append(esc(d.getSubproducto())).append(PIPE)
					.append(esc(d.getGarantia())).append(PIPE)

					.append(esc(d.getTipoPlazo())).append(PIPE).append(nz(d.getPlazo())).append(PIPE)

					.append(esc(d.getSubsector())).append(PIPE).append(esc(d.getConceptoSubsector())).append(PIPE)

					.append(esc(d.getDestinoInversion())).append(PIPE).append(esc(d.getAplicacionOrigen())).append(PIPE)

					.append(nz(d.getIva())).append(PIPE)

					.append(esc(d.getMorosidad())).append(PIPE).append(esc(d.getTipoMorosidad())).append(PIPE)

					.append(esc(d.getCodigoOperacion())).append(PIPE).append(esc(d.getConceptoContable())).append(PIPE)
					.append(esc(d.getSubconceptoContable())).append(PIPE)

					.append(esc(d.getTipoDivisa())).append(PIPE).append(esc(d.getVarios())).append(PIPE)

					.append(nz(d.getImporte1())).append(PIPE).append(nz(d.getImporte2())).append(PIPE)
					.append(nz(d.getImporte3())).append(PIPE).append(nz(d.getImporte4())).append(PIPE)
					.append(nz(d.getImporte5())).append(PIPE).append(nz(d.getImporte6())).append(PIPE)
					.append(nz(d.getImporte7())).append(PIPE).append(nz(d.getImporte8())).append(PIPE)
					.append(nz(d.getImporte9())).append(PIPE).append(nz(d.getImporte10())).append(PIPE)

					.append(esc(d.getIndicadorImporte1())).append(PIPE).append(esc(d.getIndicadorImporte2()))
					.append(PIPE).append(esc(d.getIndicadorImporte3())).append(PIPE)
					.append(esc(d.getIndicadorImporte4())).append(PIPE).append(esc(d.getIndicadorImporte5()))
					.append(PIPE).append(esc(d.getIndicadorImporte6())).append(PIPE)
					.append(esc(d.getIndicadorImporte7())).append(PIPE).append(esc(d.getIndicadorImporte8()))
					.append(PIPE).append(esc(d.getIndicadorImporte9())).append(PIPE)
					.append(esc(d.getIndicadorImporte10())).append(PIPE)

					.append(esc(d.getCampoLibre1())).append(PIPE).append(esc(d.getCampoLibre2())).append(PIPE)
					.append(esc(d.getCampoLibre3())).append(PIPE).append(esc(d.getCampoLibre4())).append(PIPE)
					.append(esc(d.getCampoLibre5())).append(PIPE).append(esc(d.getCampoLibre6())).append(PIPE)
					.append(esc(d.getCampoLibre7())).append(PIPE).append(esc(d.getCampoLibre8())).append(PIPE)
					.append(esc(d.getCampoLibre9())).append(PIPE).append(esc(d.getCampoLibre10())).append(PIPE)

					.append(esc(d.getTransaccion())).append(PIPE).append(esc(d.getContrato())).append(PIPE)
					.append(nz(d.getNumeroMovimiento())).append(PIPE).append(esc(d.getTipoContable())).append(PIPE)

					.append(nz(d.getTimestamp())).append(PIPE)

					.append(esc(d.getNumeroTarjeta())).append(PIPE).append(esc(d.getNumeroDocumento())).append(NL);
		}

		return sb.toString();
	}

	private static String header() {
		return String.join(PIPE, "EMPRESA", "CLAVE_INTERFAZ", "TIPO_EVENTO", "NUMERO_SECUENCIA", "FECHA_CONTABLE",
				"FECHA_OPERACION", "FECHA_VALOR", "DIVISA_1", "DIVISA_2", "DIVISA_3", "CENTRO_ALTA", "CENTRO_ORIGEN",
				"CENTRO_DESTINO", "PRODUCTO", "SUBPRODUCTO", "GARANTIA", "TIPO_PLAZO", "PLAZO", "SUBSECTOR",
				"CONCEPTO_SUBSECTOR", "DESTINO_INVERSION", "APLICACION_ORIGEN", "IVA", "MOROSIDAD", "TIPO_MOROSIDAD",
				"CODIGO_OPERACION", "CONCEPTO_CONTABLE", "SUBCONCEPTO_CONTABLE", "TIPO_DIVISA", "VARIOS", "IMPORTE_1",
				"IMPORTE_2", "IMPORTE_3", "IMPORTE_4", "IMPORTE_5", "IMPORTE_6", "IMPORTE_7", "IMPORTE_8", "IMPORTE_9",
				"IMPORTE_10", "INDICADOR_IMPORTE_1", "INDICADOR_IMPORTE_2", "INDICADOR_IMPORTE_3",
				"INDICADOR_IMPORTE_4", "INDICADOR_IMPORTE_5", "INDICADOR_IMPORTE_6", "INDICADOR_IMPORTE_7",
				"INDICADOR_IMPORTE_8", "INDICADOR_IMPORTE_9", "INDICADOR_IMPORTE_10", "CAMPO_LIBRE_1", "CAMPO_LIBRE_2",
				"CAMPO_LIBRE_3", "CAMPO_LIBRE_4", "CAMPO_LIBRE_5", "CAMPO_LIBRE_6", "CAMPO_LIBRE_7", "CAMPO_LIBRE_8",
				"CAMPO_LIBRE_9", "CAMPO_LIBRE_10", "TRANSACCION", "CONTRATO", "NUMERO_MOVIMIENTO", "TIPO_CONTABLE",
				"TIMESTAMP", "NUMERO_TARJETA", "NUMERO_DOCUMENTO");
	}

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