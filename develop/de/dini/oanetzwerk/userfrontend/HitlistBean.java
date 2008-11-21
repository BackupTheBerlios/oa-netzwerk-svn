package de.dini.oanetzwerk.userfrontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.CompleteMetadataJAXBMarshaller;

public class HitlistBean implements Serializable {
	
	private static Logger logger = Logger.getLogger (HitlistBean.class);
	private Properties props = null;
	private CompleteMetadataJAXBMarshaller cmMarsh = null;
	
	private SearchBean parentSearchBean = null;
		
	private List<BigDecimal> listHitOID;
	private HashMap<BigDecimal, HitBean> mapHitBean;
	private BigDecimal selectedDetailsOID = null;
	
	public HitlistBean() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		
		this.props = HelperMethods.loadPropertiesFromFile ("webapps/findnbrowse/WEB-INF/userfrontend_gui.xml");
		this.cmMarsh = CompleteMetadataJAXBMarshaller.getInstance();
		
		this.listHitOID = new ArrayList<BigDecimal>();
		this.mapHitBean = new HashMap<BigDecimal, HitBean>();	
		
		this.fakefillListHitOID();
		this.updateHitlistMetadata();
		
	}
	
	///////////////////////////// AUTO GENERATED ///////////////////////////////
	
	public List<BigDecimal> getListHitOID() {
		return listHitOID;
	}

	public void setListHitOID(List<BigDecimal> listHitOID) {
		this.listHitOID = listHitOID;
	}

	public HashMap<BigDecimal, HitBean> getMapHitBean() {
		return mapHitBean;
	}

	public void setMapHitBean(
			HashMap<BigDecimal, HitBean> mapHitBean) {
		this.mapHitBean = mapHitBean;
	}

		
	public SearchBean getParentSearchBean() {
		return parentSearchBean;
	}

	public void setParentSearchBean(SearchBean parentSearchBean) {
		this.parentSearchBean = parentSearchBean;
	}

	public BigDecimal getSelectedDetailsOID() {
		return selectedDetailsOID;
	}

	public void setSelectedDetailsOID(BigDecimal selectedDetailsOID) {
		this.selectedDetailsOID = selectedDetailsOID;
	}

	//////////////////////////////////////////////////////////////////////////////

	public int getSizeListHitOID() {
		return listHitOID.size();
	}
	
	public void fakefillListHitOID() {
		listHitOID = new ArrayList<BigDecimal>();
		
		listHitOID.add(new BigDecimal("1609"));
//		listHitOID.add(new BigDecimal("2063"));
//		listHitOID.add(new BigDecimal("2083"));
//		listHitOID.add(new BigDecimal("11000"));
		listHitOID.add(new BigDecimal("11100"));
		listHitOID.add(new BigDecimal("11200"));
		listHitOID.add(new BigDecimal("11300"));
		listHitOID.add(new BigDecimal("11400"));
		listHitOID.add(new BigDecimal("11500"));
		listHitOID.add(new BigDecimal("11600"));
		listHitOID.add(new BigDecimal("11700"));
		listHitOID.add(new BigDecimal("11800"));
		listHitOID.add(new BigDecimal("11900"));
		listHitOID.add(new BigDecimal("12000"));
		listHitOID.add(new BigDecimal("12100"));
		listHitOID.add(new BigDecimal("12200"));
		listHitOID.add(new BigDecimal("12300"));
		listHitOID.add(new BigDecimal("12400"));
		listHitOID.add(new BigDecimal("12500"));
		listHitOID.add(new BigDecimal("12600"));
		listHitOID.add(new BigDecimal("12700"));
		listHitOID.add(new BigDecimal("12800"));
		listHitOID.add(new BigDecimal("12900"));
	}
	
	public void updateHitlistMetadata() {
		mapHitBean = new HashMap<BigDecimal, HitBean>();	
		for(BigDecimal oid : listHitOID) {
			//CompleteMetadata cm = CompleteMetadata.createDummy();
			CompleteMetadata cm = fetchCompleteMetadataByOID(oid);
			if (cm == null) {
				cm = CompleteMetadata.createDummy();
				cm.setOid(oid);
			}
			HitBean hb = new HitBean();
			hb.setParentHitlistBean(this);
			hb.setCompleteMetadata(cm);
			mapHitBean.put(oid, hb);
		}
	}
	
	private RestClient prepareRestTransmission (String resource) {
		
		return RestClient.createRestClient (new File (System.getProperty ("catalina.base") + this.props.getProperty ("restclientpropfile")), resource, this.props.getProperty ("username"), this.props.getProperty ("password"));
	}

	private CompleteMetadata fetchCompleteMetadataByOID(BigDecimal oid) {
						
		RestMessage rms = this.prepareRestTransmission ("CompleteMetadataEntry/" + oid).sendGetRestMessage();
		CompleteMetadata cm = new CompleteMetadata();
		cm.setOid(oid);
		
		if (rms == null || rms.getListEntrySets ( ).isEmpty ( )) {
			
			logger.error ("received no complete metadata entry for oid " + oid);
			return null;
		}
		
		logger.debug("resultset for oid " + oid);		
		
		for (RestEntrySet res : rms.getListEntrySets ( )) {
			
			Iterator <String> it = res.getKeyIterator ( );
			String key = "";
			
			while (it.hasNext ( )) {
				
				key = it.next ( );
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("key: " + key + " value: " + res.getValue (key));
				
				if (key.equalsIgnoreCase ("completemetadata")) {
					
					String strXML = res.getValue (key);
					
					cm = cmMarsh.unmarshall(strXML); 

				} else
					continue;
			}
			
		}
		
		return cm;
		
	}
	
}
