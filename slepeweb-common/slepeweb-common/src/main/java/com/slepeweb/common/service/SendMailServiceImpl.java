package com.slepeweb.common.service;

import org.apache.log4j.Logger;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.springframework.stereotype.Service;


@Service
public class SendMailServiceImpl implements SendMailService {

	private static Logger LOG = Logger.getLogger(SendMailServiceImpl.class);
	
	public boolean sendMail(String from, String to, String name, String subject, String message) {
		Email email = EmailBuilder.startingBlank()
			    .from(from)
			    .to(name, to)
			    .withSubject(subject)
			    .withHTMLText(message)
			    .buildEmail();

		Mailer mailer = MailerBuilder
          .withSMTPServer("smtp.gmail.com", 587, "george.buttigieg56", "kaeyfkbofiuszwbc")
          .withTransportStrategy(TransportStrategy.SMTP_TLS)
          .withSessionTimeout(10 * 1000)
          .clearEmailAddressCriteria() // turns off email validation
          .buildMailer();

		try {
			mailer.sendMail(email);
			return true;
		}
		catch (Exception e) {
			LOG.error("Sendmail error", e);
			return false;
		}
	}
	
}
