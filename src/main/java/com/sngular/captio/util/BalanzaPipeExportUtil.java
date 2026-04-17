package com.sngular.captio.util;

import java.util.List;

import com.sngular.captio.dto.BalanzaDTO;

public class BalanzaPipeExportUtil {

	private BalanzaPipeExportUtil() {
	}

	private static final String PIPE = "|";
	private static final String NL = System.lineSeparator();

	/** Exporta la lista a texto separado por pipes con encabezado. */
	public static String toPipe(List<BalanzaDTO> list) {
		return toPipe(list, false);
	}

	/** Exporta la lista a texto separado por pipes, con o sin encabezado. */
	public static String toPipe(List<BalanzaDTO> list, boolean withHeader) {
		if (list == null || list.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder(256 * list.size());
		for (BalanzaDTO d : list) {
			if (d == null)
				continue;
			if (withHeader) {
				sb.append(
						"DIF|FECHA_CONTABLE|OPERACION_LIG|REGISTRO|NUM_NOMINA|VIAJERO|OP|FOLIO|FCH_REG|CR|CR_NOM|CO|CO_NOM|ZONA|DOT_DIT|TAG|TPO_VIAJE|ORIGEN_PLAZA|DESTINO|FCH_INICIO|FCH_FIN|FCH_DEP|MOTIVO|PROYECTO|DESCRIPCION|NOTA|CONCEPTO_GTO|FORMA_PAGO|XML|PDF|NOM_COMERCIAL|RAZON_SOCIAL|RFC_PROVEEDOR|CD_PAIS|PAIS|FCH_FACTURA|IMP_TOT|TASA_IVA|IMP_NETO|IMP_IVA|IMP_PROPINA|OTROS_IMPUESTOS|IMP_X_CONCEPTO|CC_IMP_NETO|CC_IMP_IVA|CC_PROPINA|CC_OTROS_IMPUESTOS|SECC_IMP_NETO|SECC_IMP_IVA|SECC_PROPINA|SECC_OTROS_IMPUESTOS|RIESGO|MOTIVO_RIESGO|FCH_AUTORIZACION|AUTORIZADOR|FCH_PRELIBERACION|OPER_EXTERNO|FECHA_TRAMITE|OPER_PAGADURIA|CVE_EMP|EMPRESA|EMP_CORTO|TP_GASTO|DGA|DIRECCION|SUBDIRECCION|DEP|PUESTO|CVE_PUESTO|KILOMETRAJE|CR_2|NOMBRE_CR_2|CR_3|NOMBRE_CR_3|CR_4|NOMBRE_CR_4|FCH_REG_DOT|NB_AUTORIZADOR|FUERA_DE_NORMATIVA|MOTIVO_FN|MES|DD|MM|AA|GASTO|GTO_CONCEP|REGISTRO_CONTABLE|TIPO_GTO|TIPO_RFC|GTO_PORC|IMP_NETO_RES|IMP_NETO_NO_DED|IMP_IVA_1510|IMP_IVA_5117|IMP_PROPINA2|OTROS_IMPUESTOS2|IMP_X_CONCEPTO2|CC_IMP_NETO2|IVA_REAL|TK|FINIQUITADO|DESCRIPCION5|TAGS|CTA_GRAL|TIPO_EMPR|NUM_GTO|DIF2|TASA_TXT|IDENTIF_TAGS|ASTERISCO|PUESTO_BIS|ESPACIO|CONCATENADA|TPO_DOT|SENALADO|SEGMENTO|BANCA|SIPRES|ARTICULO_69B_CFF|ACTIVIDAD|ETIQUETA|NVO|TRES_ASTERISCOS|BANCA_2|ORDEN|IVA_REAL2|ACTUALIZACION|SUBTOTAL|IVA|BUSCAR|DUPLICADO|REGISTRO_FOLIO_NOTA|EXTRACCION|REGISTRO_VIAJERO|NOMBRE_VIAJERO|FOLIO2|NUM_NOTA|CR2|CODIGO_EMPRESA|NOMBRE_EMPRESA|IMP_TOTAL|IMP_PAGADO|RFC_PROVEEDOR2|RAZON_SOCIAL2|FECHA_REGISTRO|OPER_SERV_CONTABLE|FECHA_PROCESO|CODIGO_ESTATUS|NOMBRE_ESTATUS|MOTIVO_RECHAZO|DEDUCIBLE|XML2|ASTERISCO2|VERSION_4_0|VERSION_EQ_4_0|VERSION_3_3|OTRA_VERS|VERSION|FACT_DD|FACT_MM|FACT_AA|FCH_FACTURA2|BCA2_MarcOsorio|DIFER_BCAS|PAIS2|UUID")
						.append(NL);
			}

			sb.append(nz(d.getDif1())).append(PIPE).append(nz(d.getFechaContable())).append(PIPE)
					.append(nz(d.getOperacionLig())).append(PIPE).append(nz(d.getRegistro())).append(PIPE)
					.append(nz(d.getNumNomina())).append(PIPE).append(esc(d.getViajero())).append(PIPE)
					.append(nz(d.getOp())).append(PIPE).append(nz(d.getFolio())).append(PIPE).append(nz(d.getFchReg()))
					.append(PIPE)

					.append(nz(d.getCr())).append(PIPE).append(esc(d.getCrNom())).append(PIPE).append(nz(d.getCo()))
					.append(PIPE).append(esc(d.getCoNom())).append(PIPE).append(esc(d.getZona())).append(PIPE)

					.append(esc(d.getDotDit())).append(PIPE).append(esc(d.getTag())).append(PIPE)

					.append(nz(d.getTpoViaje())).append(PIPE).append(esc(d.getOrigenPlaza())).append(PIPE)
					.append(esc(d.getDestino())).append(PIPE)

					.append(nz(d.getFchInicio())).append(PIPE).append(nz(d.getFchFin())).append(PIPE)
					.append(nz(d.getFchDep())).append(PIPE).append(esc(d.getMotivo())).append(PIPE)

					.append(esc(d.getProyecto())).append(PIPE).append(esc(d.getDescripcion())).append(PIPE)
					.append(esc(d.getNota())).append(PIPE)

					.append(esc(d.getConceptoGto())).append(PIPE).append(esc(d.getFormaPago())).append(PIPE)
					.append(nz(d.getXml())).append(PIPE).append(nz(d.getPdf())).append(PIPE)

					.append(esc(d.getNomComercial())).append(PIPE).append(esc(d.getRazonSocial())).append(PIPE)
					.append(nz(d.getRfcProveedor())).append(PIPE).append(nz(d.getCdPais())).append(PIPE)
					.append(esc(d.getPais())).append(PIPE).append(nz(d.getFchFactura())).append(PIPE)

					.append(nz(d.getImpTot())).append(PIPE).append(nz(d.getTasaIva())).append(PIPE)
					.append(nz(d.getImpNeto())).append(PIPE).append(nz(d.getImpIva())).append(PIPE)
					.append(nz(d.getImpPropina())).append(PIPE).append(nz(d.getOtrosImpuestos())).append(PIPE)
					.append(nz(d.getImpPorConcepto())).append(PIPE)

					.append(nz(d.getCcImpNeto())).append(PIPE).append(nz(d.getCcImpIva())).append(PIPE)
					.append(nz(d.getCcPropina())).append(PIPE).append(nz(d.getCcOtrosImpuestos())).append(PIPE)

					.append(nz(d.getSeccImpNeto())).append(PIPE).append(nz(d.getSeccImpIva())).append(PIPE)
					.append(nz(d.getSeccPropina())).append(PIPE).append(nz(d.getSeccOtrosImpuestos())).append(PIPE)

					.append(nz(d.getRiesgo())).append(PIPE).append(esc(d.getMotivoRiesgo())).append(PIPE)
					.append(nz(d.getFchAutorizacion())).append(PIPE).append(esc(d.getAutorizador())).append(PIPE)
					.append(nz(d.getFchPreliberacion())).append(PIPE).append(esc(d.getOperExterno())).append(PIPE)

					.append(nz(d.getFechaTramite())).append(PIPE).append(esc(d.getOperPagaduria())).append(PIPE)
					.append(nz(d.getCveEmp())).append(PIPE).append(esc(d.getEmpresa())).append(PIPE)
					.append(esc(d.getEmpCorto())).append(PIPE).append(esc(d.getTpGasto())).append(PIPE)

					.append(esc(d.getDga())).append(PIPE).append(esc(d.getDireccion())).append(PIPE)
					.append(esc(d.getSubdireccion())).append(PIPE).append(esc(d.getDep())).append(PIPE)
					.append(esc(d.getPuesto())).append(PIPE).append(nz(d.getCvePuesto())).append(PIPE)
					.append(nz(d.getKilometraje())).append(PIPE)

					.append(nz(d.getCr2())).append(PIPE).append(esc(d.getNombreCr2())).append(PIPE)
					.append(nz(d.getCr3())).append(PIPE).append(esc(d.getNombreCr3())).append(PIPE)
					.append(nz(d.getCr4())).append(PIPE).append(esc(d.getNombreCr4())).append(PIPE)

					.append(nz(d.getFchRegDot())).append(PIPE).append(esc(d.getNbAutorizador())).append(PIPE)
					.append(esc(d.getFueraDeNormativa())).append(PIPE).append(esc(d.getMotivoFn())).append(PIPE)

					.append(nz(d.getMes())).append(PIPE).append(nz(d.getDd())).append(PIPE).append(nz(d.getMm()))
					.append(PIPE).append(nz(d.getAa())).append(PIPE)

					.append(esc(d.getGasto())).append(PIPE).append(esc(d.getGtoConcep())).append(PIPE)
					.append(esc(d.getRegistroContable())).append(PIPE).append(esc(d.getTipoGto())).append(PIPE)
					.append(esc(d.getTipoRfc())).append(PIPE).append(esc(d.getGtoConPorc())).append(PIPE)

					.append(nz(d.getImpNetoRes())).append(PIPE).append(nz(d.getImpNetoNoDed())).append(PIPE)
					.append(nz(d.getImpIva1510())).append(PIPE).append(nz(d.getImpIva5117())).append(PIPE)
					.append(nz(d.getImpPropina2())).append(PIPE).append(nz(d.getOtrosImpuestos2())).append(PIPE)
					.append(nz(d.getImpPorConcepto2())).append(PIPE).append(nz(d.getCcImpNeto2())).append(PIPE)
					.append(nz(d.getIvaReal())).append(PIPE).append(esc(d.getTk())).append(PIPE)

					.append(esc(d.getFiniquitado())).append(PIPE).append(esc(d.getDescripcion5())).append(PIPE)
					.append(esc(d.getTagsTexto())).append(PIPE).append(esc(d.getCtaGral())).append(PIPE)
					.append(esc(d.getTipoEmpr())).append(PIPE).append(nz(d.getNumGto())).append(PIPE)

					.append(nz(d.getDif107())).append(PIPE).append(esc(d.getTasaTxt())).append(PIPE)
					.append(esc(d.getIdentifTags())).append(PIPE).append(esc(d.getAsterisco110())).append(PIPE)

					.append(esc(d.getPuestoBis())).append(PIPE).append(esc(d.getEspacio112())).append(PIPE)
					.append(esc(d.getConcatenada())).append(PIPE).append(esc(d.getTpoDot())).append(PIPE)
					.append(esc(d.getSenalado())).append(PIPE).append(esc(d.getSegmento())).append(PIPE)

					.append(esc(d.getBanca())).append(PIPE).append(esc(d.getSipres())).append(PIPE)
					.append(esc(d.getArticulo69B())).append(PIPE).append(esc(d.getActividad())).append(PIPE)
					.append(esc(d.getEtiqueta())).append(PIPE)

					.append(esc(d.getNvo())).append(PIPE).append(esc(d.getAsteriscos123())).append(PIPE)
					.append(esc(d.getBanca2())).append(PIPE).append(esc(d.getOrden())).append(PIPE)
					.append(nz(d.getIvaReal2())).append(PIPE)

					.append(esc(d.getActualizacion())).append(PIPE).append(nz(d.getSubtotal())).append(PIPE)
					.append(nz(d.getIva())).append(PIPE).append(esc(d.getBuscar())).append(PIPE)
					.append(esc(d.getDuplicado())).append(PIPE).append(esc(d.getRegistroFolioNota())).append(PIPE)

					.append(esc(d.getExtraccion())).append(PIPE).append(esc(d.getRegistroViajero())).append(PIPE)
					.append(esc(d.getNombreViajero())).append(PIPE).append(esc(d.getFolio2())).append(PIPE)
					.append(esc(d.getNumNota())).append(PIPE).append(esc(d.getCrExtraccion())).append(PIPE)
					.append(esc(d.getCodigoEmpresa())).append(PIPE).append(esc(d.getNombreEmpresa())).append(PIPE)
					.append(nz(d.getImpTotal())).append(PIPE).append(nz(d.getImpPagado())).append(PIPE)
					.append(esc(d.getRfcProveedor2())).append(PIPE)

					.append(esc(d.getRazonSocial2())).append(PIPE).append(nz(d.getFechaRegistro())).append(PIPE)
					.append(esc(d.getOperServContable())).append(PIPE).append(nz(d.getFechaProceso())).append(PIPE)
					.append(esc(d.getCodigoEstatus())).append(PIPE).append(esc(d.getNombreEstatus())).append(PIPE)
					.append(esc(d.getMotivoRechazo())).append(PIPE).append(esc(d.getDeducible())).append(PIPE)

					.append(nz(d.getXml2())).append(PIPE).append(esc(d.getAsterisco153())).append(PIPE)
					.append(esc(d.getVersion40a())).append(PIPE).append(esc(d.getVersion40b())).append(PIPE)
					.append(esc(d.getVersion33())).append(PIPE).append(esc(d.getOtraVers())).append(PIPE)
					.append(esc(d.getVersion())).append(PIPE)

					.append(nz(d.getFactDd())).append(PIPE).append(nz(d.getFactMm())).append(PIPE)
					.append(nz(d.getFactAa())).append(PIPE).append(nz(d.getFchFactura2())).append(PIPE)

					.append(esc(d.getBca2MarcOsorio())).append(PIPE).append(esc(d.getDiferBcas())).append(PIPE)
					.append(esc(d.getPaisCamelCase())).append(PIPE).append(esc(d.getUuid())).append(NL);
		}
		return sb.toString();
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
