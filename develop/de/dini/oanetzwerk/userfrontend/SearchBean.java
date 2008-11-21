package de.dini.oanetzwerk.userfrontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;

public class SearchBean implements Serializable {

	private static Logger logger = Logger.getLogger (HitlistBean.class);
	private Properties props = null;
	private String strOneSlot = "";
    private BigDecimal directOID = null; 
	private String strRepositoryFilterRID = null;
	private String strRepositoryFilterName = null;
	private String strRepositoryFilterURL = null;
	
    private HitlistBean hitlist = null;
    
    public SearchBean() throws InvalidPropertiesFormatException, FileNotFoundException, IOException  {
    	    	
		this.props = HelperMethods.loadPropertiesFromFile ("webapps/findnbrowse/WEB-INF/userfrontend_gui.xml");
    	
    	hitlist = new HitlistBean();
    	hitlist.setParentSearchBean(this);
    	
    	FacesContext context = FacesContext.getCurrentInstance();
    	HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
    	this.strRepositoryFilterRID = request.getParameter("filterForRID");
    	setupRepositoryFilterByRID(strRepositoryFilterRID);
    }
    
    
    ////////////// AUTO GENERATED ////////////////////
    
	public String getStrOneSlot() {
		return strOneSlot;
	}

	public void setStrOneSlot(String strOneSlot) {
		this.strOneSlot = strOneSlot;
	}
	
	public BigDecimal getDirectOID() {
		return directOID;
	}

	public void setDirectOID(BigDecimal directOID) {
		this.directOID = directOID;
	}
		
	public String getStrRepositoryFilterRID() {
		return strRepositoryFilterRID;
	}

	public void setStrRepositoryFilterRID(String strRepositoryFilterRID) {
		this.strRepositoryFilterRID = strRepositoryFilterRID;
	}
	
	public String getStrRepositoryFilterName() {
		return strRepositoryFilterName;
	}
	
	public void setStrRepositoryFilterName(String strRepositoryFilterName) {
		this.strRepositoryFilterName = strRepositoryFilterName;
	}

	public String getStrRepositoryFilterURL() {
		return strRepositoryFilterURL;
	}

	public void setStrRepositoryFilterURL(String strRepositoryFilterURL) {
		this.strRepositoryFilterURL = strRepositoryFilterURL;
	}

	public HitlistBean getHitlist() {
		return hitlist;
	}

	public void setHitlist(HitlistBean hitlist) {
		this.hitlist = hitlist;
	}
	
	//////////////// UTIL methods ///////////////////////
	
	private void parseOneSlotForDirectOID() {		
		if(strOneSlot != null && strOneSlot.length() > 0) {
			String strTest = new String(strOneSlot);
			strTest = strTest.toUpperCase();
			strTest = strTest.trim();
			if(strTest.startsWith("OID:")) {
				try {
					String[] oids = (strTest.substring(strTest.indexOf("OID:")+4)).split(":");
					ArrayList<BigDecimal> listOIDs = new ArrayList<BigDecimal>();
					if(oids != null && oids.length > 0) {
					    for(String strOid : oids) {
					    	BigDecimal oid = new BigDecimal(strOid);
					    	//this.directOID = oid;
					    	listOIDs.add(oid);
					    }
				    	this.hitlist.setListHitOID(listOIDs);
				    	this.hitlist.updateHitlistMetadata();
					} else {
						this.hitlist.fakefillListHitOID();
						this.hitlist.updateHitlistMetadata();											
					}
				} catch (NumberFormatException ex) {
					//this.directOID = null;
					this.hitlist.fakefillListHitOID();
					this.hitlist.updateHitlistMetadata();					
				}
			}
		}
	}
	
	private RestClient prepareRestTransmission (String resource) {
		
		return RestClient.createRestClient (new File (System.getProperty ("catalina.base") + this.props.getProperty ("restclientpropfile")), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
	}

	private void setupRepositoryFilterByRID(String strRID) {
						
		RestMessage rms = this.prepareRestTransmission ("Repository/" + strRID).sendGetRestMessage();	
		if (rms == null || rms.getListEntrySets().isEmpty() || rms.getStatus() != RestStatusEnum.OK) {			
			logger.warn ("received norepository data entry for rid " + strRID + ", status: " + rms.getStatus() + " : " + rms.getStatusDescription());
			this.strRepositoryFilterRID = "";
			return;
		}

		for (RestEntrySet res : rms.getListEntrySets ( )) {
			Iterator <String> it = res.getKeyIterator ( );
			String key = "";
			while (it.hasNext ( )) {
				key = it.next ( );
				if (key.equalsIgnoreCase ("name")) {
					this.strRepositoryFilterName = res.getValue (key);
				} else if(key.equalsIgnoreCase ("url")) {
					this.strRepositoryFilterURL = res.getValue (key);
				} else continue;
			}
		}						
	}
	
	//////////////// Action Methods /////////////////////
	
	public String actionSearchButton() {
		parseOneSlotForDirectOID();
		return "search_clicked";
	}
	
}
