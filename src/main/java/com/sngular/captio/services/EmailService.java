package com.sngular.captio.services;

import java.util.List;

import com.sngular.captio.dto.MailDTO;

public interface EmailService {

	void enviarCorreo(String para, String asunto, String cuerpo);

	void enviarCorreo(MailDTO mailDTO);

	MailDTO crearMailDTO(String para, String asunto, String cuerpo, String plantilla);

	MailDTO crearMailDTOErrores(String para, String asunto, List<String> errores, String informe, String plantilla, String id);
}
