package com.sngular.captio.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class CfdiNamespaceContext implements NamespaceContext {

	private static final Map<String, String> MAP = Map.of("cfdi", "http://www.sat.gob.mx/cfd/4", "tfd",
			"http://www.sat.gob.mx/TimbreFiscalDigital");

	@Override
	public String getNamespaceURI(String prefix) {
		return MAP.getOrDefault(prefix, XMLConstants.NULL_NS_URI);
	}

	@Override
	public String getPrefix(String namespaceURI) {
		return MAP.entrySet().stream().filter(e -> e.getValue().equals(namespaceURI)).map(Map.Entry::getKey).findFirst()
				.orElse(null);
	}

	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		String p = getPrefix(namespaceURI);
		return p == null ? Collections.emptyIterator() : List.of(p).iterator();
	}

}
