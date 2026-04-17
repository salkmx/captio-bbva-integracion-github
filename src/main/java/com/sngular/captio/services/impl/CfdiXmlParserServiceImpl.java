package com.sngular.captio.services.impl;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.sngular.captio.dto.CfdiResumenDTO;
import com.sngular.captio.dto.ConceptoDTO;
import com.sngular.captio.services.CfdiXmlParserService;
import com.sngular.captio.util.CfdiNamespaceContext;

@Component
public class CfdiXmlParserServiceImpl implements CfdiXmlParserService {

	@Override
	public CfdiResumenDTO parse(String xml) {
		try {
			Document doc = buildDocument(xml);

			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(new CfdiNamespaceContext());

			CfdiResumenDTO out = new CfdiResumenDTO();

			out.version = eval(xpath, doc, "/cfdi:Comprobante/@Version");
			out.serie = eval(xpath, doc, "/cfdi:Comprobante/@Serie");
			out.folio = eval(xpath, doc, "/cfdi:Comprobante/@Folio");
			out.fecha = eval(xpath, doc, "/cfdi:Comprobante/@Fecha");
			out.moneda = eval(xpath, doc, "/cfdi:Comprobante/@Moneda");

			out.subTotal = toBig(eval(xpath, doc, "/cfdi:Comprobante/@SubTotal"));
			out.descuento = toBig(eval(xpath, doc, "/cfdi:Comprobante/@Descuento"));

			out.total = toBig(eval(xpath, doc, "/cfdi:Comprobante/@Total"));

			out.emisorRfc = eval(xpath, doc, "/cfdi:Comprobante/cfdi:Emisor/@Rfc");
			out.emisorNombre = eval(xpath, doc, "/cfdi:Comprobante/cfdi:Emisor/@Nombre");

			out.receptorRfc = eval(xpath, doc, "/cfdi:Comprobante/cfdi:Receptor/@Rfc");
			out.receptorNombre = eval(xpath, doc, "/cfdi:Comprobante/cfdi:Receptor/@Nombre");
			out.usoCfdi = eval(xpath, doc, "/cfdi:Comprobante/cfdi:Receptor/@UsoCFDI");

			out.totalImpuestosTrasladados = toBig(
					eval(xpath, doc, "/cfdi:Comprobante/cfdi:Impuestos/@TotalImpuestosTrasladados"));

			out.tasa = toBig(
					eval(xpath, doc, "/cfdi:Comprobante/cfdi:Impuestos/cfdi:Traslados/cfdi:Traslado/@TasaOCuota"));

			out.uuid = eval(xpath, doc, "/cfdi:Comprobante/cfdi:Complemento/tfd:TimbreFiscalDigital/@UUID");

			out.totalImpuestosTrasladados = toBig(
					eval(xpath, doc, "/cfdi:Comprobante/cfdi:Impuestos/@TotalImpuestosTrasladados"));


			NodeList conceptoNodes = (NodeList) xpath.evaluate("/cfdi:Comprobante/cfdi:Conceptos/cfdi:Concepto", doc,
					XPathConstants.NODESET);

			List<ConceptoDTO> conceptos = new ArrayList<>();
			for (int i = 0; i < conceptoNodes.getLength(); i++) {
				String base = "/cfdi:Comprobante/cfdi:Conceptos/cfdi:Concepto[" + (i + 1) + "]";
				ConceptoDTO c = new ConceptoDTO();
				c.claveProdServ = eval(xpath, doc, base + "/@ClaveProdServ");
				c.descripcion = eval(xpath, doc, base + "/@Descripcion");
				c.cantidad = toBig(eval(xpath, doc, base + "/@Cantidad"));
				c.valorUnitario = toBig(eval(xpath, doc, base + "/@ValorUnitario"));
				c.importe = toBig(eval(xpath, doc, base + "/@Importe"));
				conceptos.add(c);
			}
			out.conceptos = conceptos;

			return out;
		} catch (Exception e) {
			throw new IllegalArgumentException("No se pudo parsear el CFDI: " + e.getMessage(), e);
		}
	}

	private Document buildDocument(String xml) throws Exception {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

		dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

		return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
	}

	private String eval(XPath xpath, Document doc, String expr) throws XPathExpressionException {
		return xpath.evaluate(expr, doc);
	}

	private BigDecimal toBig(String s) {
		if (s == null || s.isBlank())
			return null;
		return new BigDecimal(s.trim());
	}

}
