package vn.LeThanhTuan.util;

import java.io.UnsupportedEncodingException;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class MailSenderUtil {

	public static void sendEmail(JavaMailSender mailSender, String hostMail ,String toEmail, String subject, String content, String name) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(toEmail, name);
		helper.setTo(hostMail);
		helper.setSubject(subject);
		helper.setText(content);
		
		mailSender.send(message);
	}
}
