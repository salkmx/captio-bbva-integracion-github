package com.sngular.captio.services.impl;

import org.springframework.stereotype.Component;

import com.sngular.captio.services.BalanzaContableService;
import com.sngular.captio.services.CfdiXmlParserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class BalanzaContableServiceImpl implements BalanzaContableService {

	private final CfdiXmlParserService cfdiXmlParserService;

}
