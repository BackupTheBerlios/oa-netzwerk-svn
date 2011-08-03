package de.dini.oanetzwerk.admin;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.codec.binary.Base64;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import de.dini.oanetzwerk.utils.PropertyManager;

/**
 * @author Johannes Haubold
 * 
 */

public class OAPSBackend {
	
	
	public boolean sendMail(String receiver, String subject, String content, File attachment) {
		PropertyManager pm = new PropertyManager();
		Properties oapsProperties = pm.getOAPSProperties();
		// Mailserver Einstellungen
		String absender_email = oapsProperties.getProperty("userkontakt_email");
		String mailhost = oapsProperties.getProperty("userkontakt_mailserver");
		int port = Integer.parseInt(oapsProperties.getProperty("userkontakt_mailserver_port"));
		String password = oapsProperties.getProperty("userkontakt_email_password");
		
		Properties props = new Properties();
		props.put("mail.smtp.user", absender_email);
		props.put("mail.smtp.host", mailhost);
		props.put("mail.smtp.port", port);
		//props.put("mail.smtp.starttls.enable","true");
		//props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.auth", "true");
		//props.put("mail.smtp.debug", "true");
		//props.put("mail.smtp.socketFactory.port", port);
		//props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		//props.put("mail.smtp.socketFactory.fallback", "false");

		SecurityManager security = System.getSecurityManager();

		try
		{
			Authenticator auth = new SMTPAuthenticator(absender_email, password );
			Session session = Session.getInstance(props, auth);
			//session.setDebug(true);
	
			MimeMessage msg = new MimeMessage(session);
			msg.setSubject(subject);
			msg.setFrom(new InternetAddress(absender_email));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
			
			
			// erstelle Inhalt und Anhang
			MimeBodyPart mbpContent = new MimeBodyPart();
			MimeBodyPart mbpAttachment = new MimeBodyPart();
			
			// ein bissl Infotext in die Mail
			mbpContent.setText(content,"UTF-8");
			
			if (attachment != null) {
				// Anhang drankleben
				mbpAttachment.attachFile(attachment);
				mbpAttachment.setFileName("Report.html");
			}

			// Multipart Message bauen
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbpContent);
			
			if (attachment != null) {
				mp.addBodyPart(mbpAttachment);
			}
			//Multipart Objekt als Nachricht wegschicken
			msg.setContent(mp);
			Transport.send(msg);
		}
		catch (Exception mex) {
			mex.printStackTrace();
		}
		
		return true;
	}
	
	
	public static String hash(String str) throws Exception {

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.reset();
        md5.update(str.getBytes());
        byte[] result = md5.digest();

        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<result.length; i++) {
            hexString.append(Integer.toHexString(0xFF & result[i]));
        }
        System.out.println("MD5: " + hexString.toString());
        return hexString.toString();
    }

	public static byte[] getBase64(byte[] bytes) {
	
		try {
			//byte[] bytes = file.getBytes();
//			ByteArrayOutputStream baos = new
//			ByteArrayOutputStream();
//			ObjectOutputStream stream = new
//			ObjectOutputStream(baos);
//			stream.write(bytes);
//			stream.flush();
//			stream.close();
			return Base64.encodeBase64(bytes);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null; 
	}
	
	public static byte[] getBytesFromFile(File file) throws
	IOException {
	
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
			throw new IOException("File exceeds max value: "
			+ Integer.MAX_VALUE);
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];
	
		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
		&& (numRead = is.read(
		bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file" + file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
		
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
}

