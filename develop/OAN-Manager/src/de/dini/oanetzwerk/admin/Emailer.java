package de.dini.oanetzwerk.admin;

import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;


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
	
	
	public boolean sendValidatorInfoMail(List<String> recipients, String subject, String message) {
		
		return sendPlainTextEMail(validatorSender, recipients, user, password, subject, message, mailhost, port);
		
	}
	
	
	private boolean sendPlainTextEMail(String sender, List<String> recipients, String user, String password, String subject, String message, String mailhost, String port) {
				
		// Mailserver Einstellungen
		
		Properties props = new Properties();
//		props.put("mail.smtp.user", user);
		props.put("mail.smtp.host", mailhost);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");
		//props.put("mail.smtp.starttls.enable","true");
		//props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.debug", "true");
		//props.put("mail.smtp.socketFactory.port", port);
		//props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		//props.put("mail.smtp.socketFactory.fallback", "false");

		

		try
		{
			Authenticator auth = new SMTPAuthenticator(user, password);
			Session session = Session.getInstance(props, auth);
	
		    Message msg = new MimeMessage(session);
		    
		    // set sender and recipient address
		    InternetAddress addressFrom = new InternetAddress(sender);
		    msg.setFrom(addressFrom);
		    
		    
		    InternetAddress[] addressTo = new InternetAddress[recipients.size()]; 			
		    for (int i = 0; i < recipients.size(); i++) {
				addressTo[i] = new InternetAddress(recipients.get(i));
			}
		    msg.setRecipients(Message.RecipientType.TO, addressTo);
		   
		    // Setting the Subject and Text
		    System.out.println("Setting mail subject: " + subject);
		    msg.setSubject(subject);
		    System.out.println("setting msg: "+ message);
		    msg.setText(message);
		    
		    Transport.send(msg);
		    
		    return true;
		}
		catch (Exception e) {
			logger.warn("Could not send email to " + getCommaSeperatedList(recipients) + "! ", e);
			e.printStackTrace();
		}
		
		return false;
	}
	
	public String getCommaSeperatedList(List<String> strings) {
		
		if (strings == null || strings.isEmpty()) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		
		for (String string : strings) {
	        buffer.append(string).append(",");
        }
		
		return buffer.toString().substring(0, buffer.toString().length() - 1);
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
