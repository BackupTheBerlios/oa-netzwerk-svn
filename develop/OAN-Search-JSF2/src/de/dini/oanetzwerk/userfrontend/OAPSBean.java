/**
 * 
 */
package de.dini.oanetzwerk.userfrontend;

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
import java.util.Properties;

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

import de.dini.oanetzwerk.userfrontend.PropertyManager;

/**
 * @author Johannes Haubold
 * 
 */


public class OAPSBean implements Serializable {

	private String userEmail = null;
	private int jobID = 0;
	private int rating = 0;
	private String language = null;


	
	
	public OAPSBean() {

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