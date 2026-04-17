package com.sngular.captio.services.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.sngular.captio.dto.InLineInnerImageDTO;
import com.sngular.captio.dto.MailDTO;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailServiceImpl implements EmailService {

	private final Properties properties;

	private final JavaMailSender mailSender;

	private final TemplateEngine templateEngine;

	public void enviarCorreo(String para, String asunto, String cuerpo) {
		SimpleMailMessage mensaje = new SimpleMailMessage();
		mensaje.setTo(para);
		mensaje.setSubject(asunto);
		mensaje.setText(cuerpo);
		mensaje.setFrom(properties.getMailFrom());
		mailSender.send(mensaje);
	}

	public void enviarCorreo(MailDTO mailDTO) {
		try {

			Context ctx = new Context(new Locale("es", "MX"));
			ctx.setVariables(mailDTO.getModel());

			mailDTO.setHtml(templateEngine.process(mailDTO.getPlantilla(), ctx));

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setSubject(mailDTO.getSubject());
			setRemitente(helper, mailDTO);
			setDestinatarios(helper, mailDTO);
			setContenido(helper, mailDTO);
			setAdjuntos(helper, mailDTO);
			setImagenesInline(helper, mailDTO);

			mailSender.send(message);
			log.info("Correo enviado correctamente a " + mailDTO.getTo());

		} catch (MessagingException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException("Error enviando correo: " + e.getMessage(), e);
		}
	}

	private void setRemitente(MimeMessageHelper helper, MailDTO mailDTO) throws MessagingException {
		if (mailDTO.getFrom() != null) {
			helper.setFrom(mailDTO.getFrom());
		}
	}

	private void setDestinatarios(MimeMessageHelper helper, MailDTO mailDTO) throws MessagingException {
		if (mailDTO.getTo() != null && !mailDTO.getTo().isEmpty()) {
			helper.setTo(mailDTO.getTo().toArray(new String[0]));
		}
		if (mailDTO.getCc() != null && !mailDTO.getCc().isEmpty()) {
			helper.setCc(mailDTO.getCc().toArray(new String[0]));
		}
		if (mailDTO.getBcc() != null && !mailDTO.getBcc().isEmpty()) {
			helper.setBcc(mailDTO.getBcc().toArray(new String[0]));
		}
	}

	private void setContenido(MimeMessageHelper helper, MailDTO mailDTO) throws MessagingException {
		helper.setSubject(mailDTO.getSubject());
		if (mailDTO.getHtml() != null && !mailDTO.getHtml().isEmpty()) {
			helper.setText(mailDTO.getText() != null ? mailDTO.getText() : "", mailDTO.getHtml());
		} else {
			helper.setText(mailDTO.getText() != null ? mailDTO.getText() : "");
		}
	}

	private void setAdjuntos(MimeMessageHelper helper, MailDTO mailDTO) throws MessagingException {
		if (mailDTO.getAttachments() != null) {
			for (String path : mailDTO.getAttachments()) {
				FileSystemResource file = new FileSystemResource(new File(path));
				helper.addAttachment(file.getFilename(), file);
			}
		}
	}

	private void setImagenesInline(MimeMessageHelper helper, MailDTO mailDTO) throws MessagingException {
		if (mailDTO.getInlineImages() != null) {
			for (InLineInnerImageDTO image : mailDTO.getInlineImages()) {
				FileSystemResource file = new FileSystemResource(new File(image.getPath()));
				helper.addInline(image.getContentId(), file);
			}
		}
	}
	
	public MailDTO crearMailDTO(String para, String asunto, String cuerpo, String plantilla) {
		Map<String, Object> model = new HashMap<>();
		model.put("mensaje", cuerpo);
		model.put("subject", "Aviso-Captio");
		MailDTO mailDTO = new MailDTO();
		mailDTO.setFrom(properties.getMailFrom());
		mailDTO.setTo(java.util.List.of(para));
		mailDTO.setModel(model);
		mailDTO.setSubject(asunto);
		mailDTO.setPlantilla(plantilla);
		return mailDTO;
	}
	
	public MailDTO crearMailDTOErrores(String para, String asunto, List<String> errores, String informe, String plantilla, String id) {
		Map<String, Object> model = new HashMap<>();
		model.put("informe", informe);
		model.put("mensaje", errores);
		model.put("subject", "Aviso-Captio");
		model.put("idReport", id);
		MailDTO mailDTO = new MailDTO();
		mailDTO.setFrom(properties.getMailFrom());
		mailDTO.setTo(java.util.List.of(para));
		mailDTO.setModel(model);
		mailDTO.setSubject(asunto);
		mailDTO.setPlantilla(plantilla);
		return mailDTO;
	}

}
