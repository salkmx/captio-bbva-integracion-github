package com.sngular.captio.services;

import com.sngular.captio.dto.CfdiResumenDTO;

public interface CfdiXmlParserService {

	CfdiResumenDTO parse(String xml);

}
