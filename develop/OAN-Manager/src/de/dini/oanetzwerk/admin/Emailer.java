package de.dini.oanetzwerk.admin;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.PropertyManager;

@ManagedBean(name = "emailer")
@ApplicationScoped
public class Emailer {

	private final static Logger logger = Logger.getLogger(Emailer.class);
	
	@ManagedProperty(value = "#{propertyManager}")
	private PropertyManager propertyManager;

	private String mailhost;
	private String port;
	private String user;
	private String password;
	
	
	private String validatorSender;
	
	
	@PostConstruct
	public void init() {

		// initialize
		
		Properties mailProperties = propertyManager.getMailProperties();
		
		validatorSender = mailProperties.getProperty("mail.sender.validator");
		mailhost 		= mailProperties.getProperty("mail.host");
		port 			= mailProperties.getProperty("mail.port");
		user 			= mailProperties.getProperty("mail.user");
		password 		= mailProperties.getProperty("mail.password");
		
    }
	
	
	public boolean sendValidatorInfoMail(String recipient, String subject, String message) {
		
		return sendPlainTextEMail(validatorSender, recipient, user, password, subject, message, mailhost, port);
		
	}
	
	
	private boolean sendPlainTextEMail(String sender, String recipient, String user, String password, String subject, String message, String mailhost, String port) {
		
		Properties mailProperties = propertyManager.getOAPSProperties();
		
		// Mailserver Einstellungen
		
		Properties props = new Properties();
		props.put("mail.smtp.user", user);
		props.put("mail.smtp.host", mailhost);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");
		//props.put("mail.smtp.starttls.enable","true");
		//props.put("mail.smtp.ssl.enable", "true");
		//props.put("mail.smtp.debug", "true");
		//props.put("mail.smtp.socketFactory.port", port);
		//props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		//props.put("mail.smtp.socketFactory.fallback", "false");


		try
		{
			Authenticator auth = new SMTPAuthenticator(user, password);
			Session session = Session.getInstance(props, auth);
			//session.setDebug(true);
	
		    Message msg = new MimeMessage(session);

		    // set sender and recipient address
		    InternetAddress addressFrom = new InternetAddress(sender);
		    msg.setFrom(addressFrom);

		    InternetAddress[] addressTo = new InternetAddress[1]; 
	        addressTo[0] = new InternetAddress(recipient);
		    msg.setRecipients(Message.RecipientType.TO, addressTo);
		   
		    // Setting the Subject and Content Type
		    msg.setSubject(subject);
		    msg.setContent(message, "text/plain");
		    Transport.send(msg);
		    
		    return true;
		}
		catch (Exception e) {
			logger.warn("Could not send email to " + recipient + "! ", e);
			e.printStackTrace();
		}
		
		return false;
	}
	
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		String mail;
		String pass;
		private SMTPAuthenticator(String mail, String pass) {
			this.mail = mail;
			this.pass = pass;
		}
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(mail, pass);
		}
	}

	public void setPropertyManager(PropertyManager propertyManager) {
    	this.propertyManager = propertyManager;
    }
	
}
