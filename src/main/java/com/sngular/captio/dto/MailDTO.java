package com.sngular.captio.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailDTO {

	private String from;
	private String plantilla;
	private List<String> to;
	private List<String> cc;
	private List<String> bcc;
	private String subject;
	private String text;
	private String html;
	private List<String> attachments;
	private List<InLineInnerImageDTO> inlineImages;
	private Map<String, Object> model;

}
