package com.sngular.captio.processor;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.AdjuntoDTO;
import com.sngular.captio.dto.CfdiResumenDTO;
import com.sngular.captio.dto.DiotDTO;
import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.CfdiXmlParserService;
import com.sngular.captio.services.GastoService;
import com.sngular.captio.util.DIOTPipeExportUtil;
import com.sngular.captio.util.DateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RequiredArgsConstructor
@Component
public class GastosDIOTProcessor implements ItemProcessor<InformeDTO, DiotDTO> {

	private final GastoService gastoService;

	private final CfdiXmlParserService cfdiXmlParserService;

	private final Properties properties;

	private static final String DIOT = "DIOT";

	private static BigDecimal TASA_8 = new BigDecimal("0.08");
	private static BigDecimal TASA_16 = new BigDecimal("0.16");

	@Override
	public DiotDTO process(InformeDTO item) throws Exception {

		log.debug(" ***** GastosDIOTProcessor *****");

		int  index = 0;
		List<DiotDTO> reporteDIOT = new ArrayList<>();

		// Obtener gastos asociados al informe

		List<GastoDTO> gastos = gastoService.obtenerGastosPorFiltro("{\"Report_Id\": " + item.getId() + "}");

		log.info("Informe ID: " + item.getId());

		for (GastoDTO gasto : gastos) {

			// Obtener adjuntos asociados al gasto

			log.info("--------> Gasto ID: " + gasto.getId() );

			List<GastoDTO> adjuntos = gastoService.obtenerAdjuntos("{\"Id\": [" + gasto.getId() + "]}");

			if (!adjuntos.isEmpty()) {

				for (GastoDTO adjunto : adjuntos) {

					List<AdjuntoDTO> attachments = adjunto.getAttachments();

					for (AdjuntoDTO attachment : attachments) {

						if (attachment.getFileName() != null && attachment.getFileName().endsWith(".xml")) {

							log.info("-------------> Adjunto XML: [" + attachment.getFileName() + "]");
							log.info("-------------> Adjunto URL: [" + attachment.getUrl() + "]");

							DiotDTO diotDTO = new DiotDTO();

							diotDTO.setReportId(item.getId());
							diotDTO.setExpenseId(gasto.getId());

							AdjuntoDTO adjuntoDTO = new AdjuntoDTO();

							adjuntoDTO.setFileName(attachment.getFileName());
							adjuntoDTO.setUrlKey(attachment.getUrlKey());
							adjuntoDTO.setUrl(attachment.getUrl());

							diotDTO.setAttachmentDTO(adjuntoDTO);

							reporteDIOT.add(diotDTO);
						}

					}
				}
			}
		}


		for (DiotDTO diot : reporteDIOT)  {

			//Se accede al contenido del XML a través de la URL proporcionada por la API de Captio utilizando el servicio gastoService.obtenerXmlAdjunto()

			byte[] xmlByteContent = gastoService.obtenerXmlAdjunto(diot.getAttachmentDTO().getUrl());

			String xml = xmlReaderWithoutBOM(xmlByteContent, diot);

			CfdiResumenDTO cfdi = cfdiXmlParserService.parse(xml);

			// Datos de Tercero Declarado

			diot.setTipoTerceroCodigo("04");
			diot.setTipoOperacionCodigo("85");
			diot.setRfc(cfdi.getReceptorRfc());
			diot.setIdExtranjero(null);
			diot.setNombreExtranjero(null);
			diot.setCodigoPais(null);
			diot.setCodigoPaisFiscal(null);

			// Valor de Actos o Servicios 8% & 16%

			if (cfdi.getTasa().compareTo(TASA_8)==0) {
				// Se debe identificar si el acto a Tasa8 es RFN o RFS -- para este ejemplo se asigna a RFN, pero se debe validar con el cliente la lógica de asignación

				diot.setValorActosServiciosTasa8_RFN(cfdi.getSubTotal());
				diot.setDescuentoActosServiciosTasa8_RFN(cfdi.getDescuento());
				diot.setIvaPagadoAcreditableTasa8_RFN(cfdi.getTotalImpuestosTrasladados());
				diot.setIvaPagadoAcreditableProporcionTasa8_RFN(null);
				diot.setIvaPagadoNOAcreditableProporcionalTasa8_RFN(null);
				diot.setIvaPagadoNOAcreditableTasa8_RFN(null);
				diot.setIvaPagadoNOAcreditableExentoTasa8_RFN(null);
				diot.setIvaPagadoNOAcreditableNoObjetoTasa8_RFN(null);

				/*
				diot.setValorActosServiciosTasa8_RFS(cfdi.getSubTotal());
				diot.setDescuentoActosServiciosTasa8_RFS(cfdi.getDescuento());
				diot.setIvaPagadoAcreditableTasa8_RFS(cfdi.getTotalImpuestosTrasladados());
				diot.setIvaPagadoAcreditableProporcionTasa8_RFS(null);
				diot.setIvaPagadoNOAcreditableProporcionalTasa8_RFS(null);
				diot.setIvaPagadoNOAcreditableTasa8_RFS(null);
				diot.setIvaPagadoNOAcreditableExentoTasa8_RFS(null);
				diot.setIvaPagadoNOAcreditableNoObjetoTasa8_RFS(null);
				*/

			}
			else if (cfdi.getTasa().compareTo(TASA_16)==0) {

				diot.setValorActosServiciosTasa16(cfdi.getSubTotal());
				diot.setDescuentoImportancionesTangiblesTasa16(cfdi.getDescuento());
				diot.setIvaPagadoAcreditableTasa16(cfdi.getTotalImpuestosTrasladados());
				diot.setIvaPagadoAcreditableProporcionTasa16(null);
				diot.setIvaPagadoNOAcreditableProporcionalTasa16(null);
				diot.setIvaPagadoNOAcreditableTasa16(null);
				diot.setIvaPagadoNOAcreditableExentoTasa16(null);
				diot.setIvaPagadoNOAcreditableNoObjetoTasa16(null);

			}

			diot.setImportancionesTangiblesTasa16(null);
			diot.setDescuentoImportancionesTangiblesTasa16(null);
			diot.setImportancionesIntangiblesTasa16(null);
			diot.setDescuentoImportancionesIntangiblesTasa16(null);
			diot.setIvaPagadoAcreditableImportancionTangibleTasa16(null);
			diot.setIvaPagadoAcreditableProporcionImportacionTangibleTasa16(null);
			diot.setIvaPagadoAcreditableImportancionIntangibleTasa16(null);
			diot.setIvaPagadoAcreditableProporcionImportacionIntangibleTasa16(null);

			diot.setIvaPagadoNOAcreditableProporcionalImportacionTangibleTasa16(null);
			diot.setIvaPagadoNOAcreditableImportacionTangibleTasa16(null);
			diot.setIvaPagadoNOAcreditableImportacionTangibleExentoTasa16(null);
			diot.setIvaPagadoNOAcreditableImportacionTangibleNoObjetoTasa16(null);

			diot.setIvaPagadoNOAcreditableProporcionalImportacionIntangibleTasa16(null);
			diot.setIvaPagadoNOAcreditableImportacionIntangibleTasa16(null);
			diot.setIvaPagadoNOAcreditableImportacionIntangibleExentoTasa16(null);
			diot.setIvaPagadoNOAcreditableImportacionIntangibleNoObjetoTasa16(null);

			diot.setIvaRetenidoContribuyente(cfdi.getTotalImpuestosTrasladados());

			diot.setActosPagadosImportacionExento(null);
			diot.setExentos(null);
			diot.setBase0(null);
			diot.setNoObjeto(null);
			diot.setNoObjetoSinEstablecimientoNacional(null);
			diot.setManifiestoEfectosFiscales(null);

			reporteDIOT.set(index, diot);

			index++;
		}

		// SE REALIZA  LA CREACION DEL REPORTE DIOT CON LA INFORMACION PROCESADA

		reporteDIOT(reporteDIOT);

		return null;
	}


	private String xmlReaderWithoutBOM(byte[] xmlByte, DiotDTO diot) {

		String xmlContent = new String(xmlByte, StandardCharsets.UTF_8);
		String xmlContentWithoutBom = null;

		if (xmlContent.indexOf("<?xml") != -1) {
			xmlContentWithoutBom = xmlContent.substring(xmlContent.indexOf("<?xml"), xmlContent.length());
		}
		else {
			xmlContentWithoutBom = xmlContent;
		}

		return xmlContentWithoutBom;

	}


	private void reporteDIOT(List<DiotDTO> listadiot) throws IOException {

		final Path path = Paths.get(properties.getRutaArchivoLocalDiot() + DateUtils.obtenerFechaActual() + "_" + DIOT + ".txt");

		if (path.getParent() != null) {
			Files.createDirectories(path.getParent());
		}

		String block = DIOTPipeExportUtil.toPipe(listadiot, false);

		Files.writeString(path, block, StandardCharsets.UTF_8, StandardOpenOption.CREATE);

	}



}




