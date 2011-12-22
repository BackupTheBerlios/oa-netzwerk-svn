package de.dini.oanetzwerk.userfrontend;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
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
import javax.servlet.http.HttpServletRequest;

import org.apache.axis.AxisFault;
import org.apache.commons.codec.binary.Base64;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import OapsAPI_pkg.Login;
import OapsAPI_pkg.OapsAPI;
import OapsAPI_pkg.OapsAPIBindingStub;
import OapsAPI_pkg.OapsAPILocator;
import OapsAPI_pkg.ReportResult;

import de.dini.oanetzwerk.userfrontend.PropertyManager;

/**
 * @author Johannes Haubold
 * 
 */
@ManagedBean(name="OAPSConnector")
@RequestScoped 
public class OAPSBackend {
	
	private String jobMailHash = null;
	private String action = null;
	private OAPSBean bean = new OAPSBean();
	private String errorMessage = null;
	private Properties oapsProp = null;
	private String status = null;
	private UploadedFile file;
	
	@ManagedProperty(value = "#{propertyManager}")
	private PropertyManager propertyManager;
	
	@PostConstruct
	public void init() {
		
		oapsProp = propertyManager.getOAPSProperties();

		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		this.jobMailHash = request.getParameter("code");
		this.action = request.getParameter("action");
		if (this.jobMailHash != null) {
			String code2 = new String(Base64.decodeBase64(this.jobMailHash.getBytes()));
			String[] codearray = code2.split(",");
			bean.setJobID(Integer.parseInt(codearray[0]));
			bean.setUserEmail(codearray[1]);
		}
		if (this.action != null) {
			if (this.action.equals("checkResults")) {
				checkResults();
			}
			else if (this.action.equals("receiveReport")) {
				receiveReport();
			}
		}
		
	}
	
	public void receiveReport() {
		System.out.println("receiveReport()");
		if (bean.getJobID() == 0 || bean.getUserEmail().equals(null)) {
			this.errorMessage = "Dieser Link ist nicht gültig.";
			
		}
		
		System.out.println("JobID = "+bean.getJobID());
		System.out.println("receiveReport angestoßen");
		OapsAPI srv = new OapsAPILocator();
		URL url;
		OapsAPIBindingStub mb;
		try {
			url = new URL(srv.getOapsAPIPortAddress());
			mb = new OapsAPIBindingStub(url.openConnection().getURL(), srv);
			
			
			ReportResult rr = mb.getReport(
				new Login(
					oapsProp.getProperty("absender_email"), 
					OAPSBackend.hash(oapsProp.getProperty("absender_email_password"))
				), 
				Integer.toString(bean.getJobID()));
			this.status = rr.getStatus();
			
			if (this.status.equals("OK")) {
				// prepare temp file for the results
				File f = new File(bean.getJobID()+".html");
				if (!f.exists()) {
					f.createNewFile();
				}
				FileWriter fw = new FileWriter(bean.getJobID()+".html");
				BufferedWriter out = new BufferedWriter(fw);
				out.write(new String(rr.getFile()));
				out.close();
				
				OAPSBackend be = new OAPSBackend();
				boolean sendSuccess = be.sendMail(
								bean.getUserEmail(), 
					"Ihr OAPS-geprüftes Dokument",
					"Ihr Dokument wurde von OAPS geprüft. Das Überprüfungsergebnis erhalten sie im Anhang.",
					f
				);
				// delete temp file
				f.delete();
				
				// if mail successfully sent, delete job from OAPS (cleanup)
				if (sendSuccess) {
					System.out.println("Report erfolgreich gesendet");
					boolean deleteSuccess = mb.deleteJob(
						new Login(
							oapsProp.getProperty("absender_email"),
							OAPSBackend.hash(oapsProp.getProperty("absender_email_password"))
						), 
						String.valueOf(bean.getJobID())
					);
					if (deleteSuccess) { 
						System.out.println("Job erfolgreich gelöscht");
						this.status = "deleted";
					}
					else {
						System.out.println("Job konnte nicht gelöscht werden");
					}
				}
				else {
					System.out.println("Report konnte nicht gesendet werden");
				}
			}
		} 
			catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			String nachricht = e.getFaultString();
			System.out.println("AxisFault Nachricht: "+nachricht);
			if (nachricht.equals("Given JobID does not exist!")) {
				this.status = "notfound";
			}
			else {
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}
	}
	
	public void uploadFile(){   

        
        try {  
	        OapsAPI srv = new OapsAPILocator();
			URL url = new URL(srv.getOapsAPIPortAddress());
			OapsAPIBindingStub mb = new OapsAPIBindingStub(url.openConnection().getURL(), srv);
	
			
	        String hash = null; 
	        
	        hash = OAPSBackend.hash(oapsProp.getProperty("absender_email_password"));
	        
	        InputStream fileStream = file.getInputStream();
	        long size = file.getSize();
	        byte [] fileBuffer = new byte[(int) size];
	        fileStream.read(fileBuffer, 0, (int)size);
	        fileStream.close();
//	        System.err.println("output: ");
//	        System.out.println(new String(OAPSBackend.getBase64(fileBuffer)));
	        
	        String jobId = mb.startJob(new Login(oapsProp.getProperty("absender_email"), hash),/*OAPSBackend.getBase64(fileBuffer)*/ fileBuffer, bean.getLanguage());
	        
	        OAPSBackend backend = new OAPSBackend();
	        String jobMailHash = Base64.encodeBase64URLSafeString((jobId+","+bean.getUserEmail()).getBytes());
	        PropertyManager pm = new PropertyManager();
	        Properties p = pm.getOAPSProperties();
	        String serverPath = p.getProperty("webserver_path");
	        
	        Boolean mailSuccess = backend.sendMail(
	        				bean.getUserEmail(), 
	        	"Ihr Dokument wurde bei OAPS hochgeladen", 
	        	"Bitte besuchen Sie "+serverPath+"OAPSJob.xhtml?action=checkResults&code="+jobMailHash+" um den Bearbeitungsstatus Ihrer Anfrage einzusehen. Die Bearbeitung von großen Dokumenten kann einige Zeit dauern.", 
	        	null);
	       
	       if (mailSuccess) {
	    	   System.out.println("Mail erfolgreich versendet");
	    	   FacesContext.getCurrentInstance().addMessage("1", new FacesMessage("Ihr Dokument wurde an OAPS übermittelt. Sie erhalten eine Email mit einem Link, unter dem Sie den aktuellen Bearbeitungsstand Ihres Dokuments einsehen können."));
	       }
	       else {
	    	   System.out.println("Mail konnte nicht verschickt werden");
	       }
	        
	        
		} catch (AxisFault e) {
			String nachricht = e.getFaultString();
			System.out.println("AxisFault Nachricht: "+nachricht);
			if (nachricht.equals("Given JobID does not exist!")) {
				this.status = "notfound";
			}
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void checkResults() {
		if (bean.getJobID() == 0 || bean.getUserEmail().equals(null)) {
			this.errorMessage = "Dieser Link ist nicht gültig.";
			
		}
		OapsAPI srv = new OapsAPILocator();
		URL url;
		OapsAPIBindingStub mb;
		try {
			url = new URL(srv.getOapsAPIPortAddress());
			mb = new OapsAPIBindingStub(url.openConnection().getURL(), srv);
			
			System.out.println(oapsProp.getProperty("absender_email"));
			System.out.println(oapsProp.getProperty("absender_email_password"));
			System.out.println(bean.getJobID());
			System.out.println(mb == null);
			System.out.println(url);
			ReportResult rr = mb.getReport(new Login(oapsProp.getProperty("absender_email"), OAPSBackend.hash(oapsProp.getProperty("absender_email_password"))), Integer.toString(bean.getJobID()));
			this.status = rr.getStatus();
			System.out.println("aktueller Status: "+this.status);
//			if (this.status.equals("OK")) {
//				String hash = this.jobID+","+this.userEmail;
//				this.jobMailHash = Base64.encodeBase64URLSafeString(hash.getBytes());
//			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AxisFault e) {
			String nachricht = e.getFaultString();
			System.out.println("AxisFault Nachricht: "+nachricht);
			if (nachricht.equals("Given JobID does not exist!")) {
				this.status = "notfound";
			}
			else {
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
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
	
	public String getJobMailHash() {
		return jobMailHash;
	}

	public void setJobMailHash(String jobMailHash) {
		this.jobMailHash = jobMailHash;
	}

	public UploadedFile getFile() {
		return file;
	}
	public void setFile(UploadedFile file) {
		System.out.println("Writing file...");
		this.file = file;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public OAPSBean getBean() {
    	return bean;
    }

	public void setBean(OAPSBean bean) {
    	this.bean = bean;
    }

	public void setPropertyManager(PropertyManager propertyManager) {
    	this.propertyManager = propertyManager;
    }
	
	
}

