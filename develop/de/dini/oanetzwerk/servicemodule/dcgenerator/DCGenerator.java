package de.dini.oanetzwerk.servicemodule.dcgenerator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;

/**
 * @author Michael K&uuml;hn
 *
 */

public class DCGenerator {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (DCGenerator.class);
	
	/**
	 * 
	 */
	
	private ArrayList <BigDecimal> oidList = new ArrayList <BigDecimal> ( );
	
	/**
	 * 
	 */
	
	private BigDecimal serviceID = new BigDecimal (6);
	
	/**
	 * 
	 */
	
	private String propertyfile = "harvesterprop.xml";
	
	/**
	 * 
	 */
	
	private Properties props;
	
	/**
	 * 
	 */
	
	public void init ( ) {
		
		try {
			
			this.props = HelperMethods.loadPropertiesFromFile (this.propertyfile);
			
		} catch (InvalidPropertiesFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (FileNotFoundException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
		}
		
		this.initConnection ( );
		
		RestMessage getServicesResponse = prepareRestTransmission ("Services/byName/DCGenerator/").sendGetRestMessage ( );
		
		if (getServicesResponse == null || getServicesResponse.getListEntrySets ( ).isEmpty ( ) || getServicesResponse.getStatus ( ) != RestStatusEnum.OK) {
			
			String description = RestStatusEnum.UNKNOWN_ERROR.toString ( );
			
			if (getServicesResponse != null)
				description = getServicesResponse.getStatusDescription ( );
			
			if (getServicesResponse == null || getServicesResponse.getStatus ( ) != RestStatusEnum.SQL_WARNING) {
				
				logger.error ("Could NOT get DCGenerator-Service-ID from database" + "! " + description);
				return;
				
			} else {
				
				logger.warn ("SQL_WARNING: " + description);
			}
		}
		
		RestEntrySet res = getServicesResponse.getListEntrySets ( ).get (0);
		
		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		
		this.serviceID = new BigDecimal (0);
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("key: " + key + " value: " + res.getValue (key));
			
			if (key.equalsIgnoreCase ("service_id")) {
				
				this.serviceID = new BigDecimal (res.getValue (key));
				continue;
				
			} else if (key.equalsIgnoreCase ("name")) {
				
				if (!res.getValue (key).equalsIgnoreCase ("DCGenerator"))
					logger.warn ("Should read DCGenerator and read " + res.getValue (key) + " instead!");
				
				continue;
				
			} else {
				
				logger.warn ("Unknown RestMessage key received: " + res.getValue (key));
				continue;
			}
		}
	}

	/**
	 * 
	 */
	
	public void run ( ) {
		
		this.getOIDs ( );
		this.createDC ( );
		this.storeDC ( );
	}

	/**
	 * 
	 */
	
	private void initConnection ( ) {
		
	}
	
	/**
	 * 
	 */
	
	private void getOIDs ( ) {
		
//		RestMessage getWorkflowDBResponse = prepareRestTransmission ("WorkflowDB/" + this.serviceID.toPlainString ( )).sendGetRestMessage ( );
		RestMessage getWorkflowDBResponse = prepareRestTransmission ("WorkflowDB/4/").sendGetRestMessage ( );
		
		if (getWorkflowDBResponse == null || getWorkflowDBResponse.getListEntrySets ( ).isEmpty ( ) || getWorkflowDBResponse.getStatus ( ) != RestStatusEnum.OK) {
			
			String description = RestStatusEnum.UNKNOWN_ERROR.toString ( );
			
			if (getWorkflowDBResponse != null)
				description = getWorkflowDBResponse.getStatusDescription ( );
			
			if (getWorkflowDBResponse == null || getWorkflowDBResponse.getStatus ( ) != RestStatusEnum.SQL_WARNING) {
				
				logger.error ("Could NOT get OID List from database" + "! " + description);
				return;
				
			} else {
				
				logger.warn ("SQL_WARNING: " + description);
			}
		}
		
		for (RestEntrySet entrySet : getWorkflowDBResponse.getListEntrySets ( )) {
			
			this.oidList.add (new BigDecimal (entrySet.getValue ("object_id")));
		}
	}
	
	/**
	 * 
	 */
	
	private void createDC ( ) {
		
		for (BigDecimal oid : this.oidList) {
			
			RestMessage getCMFDBResponse = prepareRestTransmission ("CompleteMetadataEntry/" + oid.toPlainString ( )).sendGetRestMessage ( );
			
			if (getCMFDBResponse == null || getCMFDBResponse.getListEntrySets ( ).isEmpty ( ) || getCMFDBResponse.getStatus ( ) != RestStatusEnum.OK) {
				
				String description = RestStatusEnum.UNKNOWN_ERROR.toString ( );
				
				if (getCMFDBResponse != null)
					description = getCMFDBResponse.getStatusDescription ( );
				
				if (getCMFDBResponse == null || getCMFDBResponse.getStatus ( ) != RestStatusEnum.SQL_WARNING) {
					
					logger.error ("Could NOT get CMF from database" + "! " + description);
					return;
					
				} else {
					
					logger.warn ("SQL_WARNING: " + description);
				}
			}

			System.out.println (getCMFDBResponse.getListEntrySets ( ).get (0).getValue ("completemetadata"));
		}
		
	}
	
	/**
	 * 
	 */
	
	private void storeDC ( ) {
		
	}
	
	/**
	 * @param resource
	 * @return
	 */
	
	private RestClient prepareRestTransmission (String resource) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("prepareRestTransmission");
		
		return RestClient.createRestClient (this.getProps ( ).getProperty ("host"), resource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
	}
	
	/**
	 * 
	 */
	
	public void shutdown ( ) { }
	
	/**
	 * @return the props
	 */
	
	protected final Properties getProps ( ) {
	
		return this.props;
	}
}
