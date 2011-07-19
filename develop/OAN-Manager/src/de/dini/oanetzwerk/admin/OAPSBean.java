/**
 * 
 */
package de.dini.oanetzwerk.admin;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.Service;

import org.apache.axis.AxisFault;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import OapsAPI_pkg.Login;
import OapsAPI_pkg.OapsAPI;
import OapsAPI_pkg.OapsAPIBindingStub;
import OapsAPI_pkg.OapsAPILocator;
import OapsAPI_pkg.ReportResult;




import de.dini.oanetzwerk.admin.utils.AbstractBean;

/**
 * @author Johannes Haubold
 * 
 */

@ManagedBean(name="OAPSConnector")
@RequestScoped 
public class OAPSBean extends AbstractBean implements Serializable {

	private String userEmail = null;
	private int jobID = 0;
	private UploadedFile file;
	private String status = null;
	private int rating = 0;
	private String language = null;
	private String errorMessage = null;
	private String pass = OAPSBackend.getPassword();
	private String mail = OAPSBackend.getAbsender_email();
	private String jobMailHash = null;
	private String action = null;
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	HttpSession session = (HttpSession) ctx.getExternalContext().getSession(false);
	
	public OAPSBean() {
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		this.jobMailHash = request.getParameter("code");
		this.action = request.getParameter("action");
		if (this.jobMailHash != null) {
			String code2 = new String(Base64.decodeBase64(this.jobMailHash.getBytes()));
			String[] codearray = code2.split(",");
			this.jobID = Integer.parseInt(codearray[0]);
			this.userEmail = codearray[1];
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
		if (this.jobID == 0 || this.userEmail.equals(null)) {
			this.errorMessage = "Dieser Link ist nicht gültig.";
			
		}
		
		System.out.println("JobID = "+this.jobID);
		System.out.println("receiveReport angestoßen");
		OapsAPI srv = new OapsAPILocator();
		URL url;
		OapsAPIBindingStub mb;
		try {
			url = new URL(srv.getOapsAPIPortAddress());
			mb = new OapsAPIBindingStub(url.openConnection().getURL(), srv);
			
			
			ReportResult rr = mb.getReport(new Login(this.mail, OAPSBackend.hash(this.pass)), Integer.toString(this.jobID));
			this.status = rr.getStatus();
			
			if (this.status.equals("OK")) {
				// prepare temp file for the results
				File f = new File(jobID+".html");
				if (!f.exists()) {
					f.createNewFile();
				}
				FileWriter fw = new FileWriter(jobID+".html");
				BufferedWriter out = new BufferedWriter(fw);
				out.write(new String(rr.getFile()));
				out.close();
				
				OAPSBackend be = new OAPSBackend();
				boolean sendSuccess = be.sendMail(
					this.userEmail, 
					"Ihr OAPS-geprüftes Dokument",
					"Ihr Dokument wurde von OAPS geprüft. Das Überprüfungsergebnis erhalten sie im Anhang.",
					f
				);
				// delete temp file
				f.delete();
				
				// if mail successfully sent, delete job from OAPS (cleanup)
				if (sendSuccess) {
					System.out.println("Report erfolgreich gesendet");
					boolean deleteSuccess = mb.deleteJob(new Login(this.mail, OAPSBackend.hash(this.pass)), String.valueOf(this.jobID));
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
	        
	        hash = OAPSBackend.hash(pass);
	        
	        InputStream fileStream = file.getInputStream();
	        long size = file.getSize();
	        byte [] fileBuffer = new byte[(int) size];
	        fileStream.read(fileBuffer, 0, (int)size);
	        fileStream.close();
//	        System.err.println("output: ");
//	        System.out.println(new String(OAPSBackend.getBase64(fileBuffer)));
	        
	        String jobId = mb.startJob(new Login(mail, hash),/*OAPSBackend.getBase64(fileBuffer)*/ fileBuffer, this.language);
	        
	        OAPSBackend backend = new OAPSBackend();
	        String jobMailHash = Base64.encodeBase64URLSafeString((jobId+","+userEmail).getBytes());
	        Boolean mailSuccess = backend.sendMail(
	        	userEmail, 
	        	"Ihr Dokument wurde bei OAPS hochgeladen", 
	        	"Bitte besuchen Sie https://localhost:8443/oanadmin/pages/OAPSJob.xhtml?action=checkResults&code="+jobMailHash+" um den Bearbeitungsstatus Ihrer Anfrage einzusehen. Die Bearbeitung von großen Dokumenten kann einige Zeit dauern.", 
	        	null);
	       
	       if (mailSuccess) {
	    	   System.out.println("Mail erfolgreich versendet");
	    	   ctx.addMessage("1", new FacesMessage("Ihr Dokument wurde an OAPS übermittelt. Sie erhalten eine Email mit einem Link, unter dem Sie den aktuellen Bearbeitungsstand Ihres Dokuments einsehen können."));
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
		if (this.jobID == 0 || this.userEmail.equals(null)) {
			this.errorMessage = "Dieser Link ist nicht gültig.";
			
		}
		OapsAPI srv = new OapsAPILocator();
		URL url;
		OapsAPIBindingStub mb;
		try {
			url = new URL(srv.getOapsAPIPortAddress());
			mb = new OapsAPIBindingStub(url.openConnection().getURL(), srv);
			
			
			ReportResult rr = mb.getReport(new Login(this.mail, OAPSBackend.hash(this.pass)), Integer.toString(this.jobID));
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
	
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public int getJobID() {
		return jobID;
	}
	public void setJobID(int jobID) {
		this.jobID = jobID;
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
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
}