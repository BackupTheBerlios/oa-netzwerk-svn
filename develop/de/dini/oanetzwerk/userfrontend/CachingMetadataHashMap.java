package de.dini.oanetzwerk.userfrontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.imf.CompleteMetadata;
import de.dini.oanetzwerk.utils.imf.CompleteMetadataJAXBMarshaller;
import de.dini.oanetzwerk.utils.imf.DuplicateProbability;
import de.dini.oanetzwerk.utils.imf.FullTextLink;

public class CachingMetadataHashMap extends HashMap<BigDecimal,CompleteMetadata> {

	private static Logger logger = Logger.getLogger (CachingMetadataHashMap.class);
	private Properties props = null;
	private CompleteMetadataJAXBMarshaller cmMarsh = null;
	
	public CachingMetadataHashMap() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		super();
		
		this.props = HelperMethods.loadPropertiesFromFile ("webapps/findnbrowse/WEB-INF/userfrontend_gui.xml");
		this.cmMarsh = CompleteMetadataJAXBMarshaller.getInstance();
		
	}
	
	public CompleteMetadata get(BigDecimal bdOID) {
		
		CompleteMetadata cm = null;
		
		cm = super.get(bdOID);
		
		if(cm == null) {
			logger.debug("try to fetch cm for oid " + bdOID + " via REST");
			cm = fetchCompleteMetadataByOID(bdOID);
			if(cm.getDuplicateProbabilityList() == null) cm.setDuplicateProbabilityList(new ArrayList<DuplicateProbability>());			
			if(cm.getFullTextLinkList() == null) cm.setFullTextLinkList(new ArrayList<FullTextLink>());			
			super.put(bdOID, cm);
		}
		
		return cm; 
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
		
		if (rms.getStatus() != RestStatusEnum.OK) {
			
			logger.error ("received status " + rms.getStatus() + " (" + rms.getStatusDescription() + ") for complete metadata entry for oid " + oid);
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
