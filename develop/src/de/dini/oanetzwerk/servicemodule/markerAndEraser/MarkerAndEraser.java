package de.dini.oanetzwerk.servicemodule.markerAndEraser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.ServiceIDException;
import de.dini.oanetzwerk.utils.exceptions.ValueFromKeyException;

/**
 * @author Michael K&uuml;hn
 * @author Manuel Klatt-Kafemann
 *
 */

public class MarkerAndEraser {
	
	/**
	 * log4j-Logger
	 */
	
	private static Logger logger = Logger.getLogger (MarkerAndEraser.class);
	
	/**
	 * 
	 */
	
	private static Logger marknEraseStateLog = Logger.getLogger ("marknEraseStateLog");
	
	/**
	 * 
	 */
	
	private final BigDecimal repositoryID;
	
	/**
	 * 
	 */
	
	private Date LatestRepositoryHarvest;
	
	/**
	 * 
	 */
	
	private final LinkedList <ObjectEntry> objects = new LinkedList <ObjectEntry> ( );
	
	/**
	 * 
	 */
	
	private Properties props = new Properties ( );
	
	/**
	 * 
	 */
	
	private String propertyfile = "markereraserprop.xml";
	
	/**
	 * @param repositoryID
	 */
	
	public MarkerAndEraser (BigDecimal repositoryID) {
		
		this.repositoryID = repositoryID;
		
		try {
			
			this.props = HelperMethods.loadPropertiesFromFile (this.getPropertyfile ( ));
			
		} catch (InvalidPropertiesFormatException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			ex.printStackTrace ( );
			System.exit (1);
			
		} catch (FileNotFoundException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			ex.printStackTrace ( );
			System.exit (1);

		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			ex.printStackTrace ( );
			System.exit (1);
		}
	}
	
	/**
	 * 
	 */
	
	public void markAndErase ( ) {
		
		// 1. alle IDs des übergebenen Repositoriums holen und in lokaler Liste speichern
		
		// für jede dieser IDs ObjectEntry holen
		
		// schauen,
		
		this.getTestData ( );
		
		this.getData ( );
	}
	
	/**
	 * 
	 */
	
	private void getData ( ) {
		
		String ressource = "";
		
		BigDecimal serviceId ; 
		
		try {
			
			serviceId = HelperMethods.getServiceId ("MarkAndErase", props);
			
			logger.debug ("ServiceID: " + serviceId);
			
		} catch (ServiceIDException ex) {
			
			serviceId = new BigDecimal (3);
			ex.printStackTrace ( );
		}
		
		ressource = "WorkflowDB/" + serviceId;
		
		RestMessage rms = HelperMethods.prepareRestTransmission (ressource, this.getProps ( )).sendGetRestMessage ( );
		
//		String result = prepareRestTransmission ("AllOIDs/fromRepositoryID/" + this.repositoryID.toPlainString ( )).GetData ( );
		
		Iterator <RestEntrySet> it = rms.getListEntrySets ( ).listIterator ( );
		RestEntrySet key = null;
		
		if (!it.hasNext ( )) {
			
			logger.warn ("No OIDs found for this repository!");
			return;
		}
		
		BigDecimal oid;
		
		while (it.hasNext ( )) { //next
			
			key = it.next ( );
			
			try {
				
				oid = new BigDecimal (key.getValue ("object_id"));
				
			} catch (NumberFormatException ex) {
				
				logger.error ("Cannot get OID " + key.getValue ("object_id") + " ! Trying next one!");
				logger.error (ex.getLocalizedMessage ( ), ex);
				
				continue;
			}
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("oid: " + oid);
			
			try {
				
				logger.debug ("Harvested on " + this.getHarvestedDatestamp (oid));
				
				this.updateWorkflowDB (oid, serviceId);
				
			} catch (ValueFromKeyException ex1) {
				// TODO Auto-generated catch block
				ex1.printStackTrace();
			} catch (ParseException ex1) {
				// TODO Auto-generated catch block
				ex1.printStackTrace();
			}
			
			// if harvested older than config-weeks
			try {
				
//				this.deleteTestData (new BigDecimal (key.getValue ("oid")));
				
			} catch (NumberFormatException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
			}
		}
	}

	/**
	 * 
	 */
	private void updateWorkflowDB (BigDecimal oid, BigDecimal serviceId) {

		logger.debug("set marked completed for " + oid);
		RestMessage rms;
		RestEntrySet res;
		RestMessage result = null;
		
		rms = new RestMessage ( );
		
		rms.setKeyword (RestKeyword.WorkflowDB);
		rms.setStatus (RestStatusEnum.OK);
		
		res = new RestEntrySet ( );
		
		res.addEntry ("object_id",  oid.toPlainString ( ));
		res.addEntry ("service_id", serviceId.toPlainString ( ));
		res.addEntry ("time", HelperMethods.today ( ).toString ( ) + " 00:00:00.1");
		rms.addEntrySet (res);
		
		RestEntrySet resout = rms.getListEntrySets ( ).get (0);
		
		logger.debug ("object_id" + resout.getValue ("object_id"));
		logger.debug ("service_id" + resout.getValue ("service_id"));
		logger.debug ("time" + resout.getValue ("time"));
		
		//restclient = RestClient.createRestClient (this.getProps ( ).getProperty ("host"), resource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
		
		try {
			
			if (logger.isDebugEnabled()) logger.debug("BEFORE PUT WorkflowDB/"+oid);
			result = HelperMethods.prepareRestTransmission ("WorkflowDB/", this.getProps ( )).sendPutRestMessage (rms);
			if (logger.isDebugEnabled()) logger.debug("AFTER PUT WorkflowDB/"+oid);

			//result = restclient.PutData (requestxml);
			
		} catch (UnsupportedEncodingException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		rms = null;
		res = null;
		//restclient = null;
		
		String value = null;
		
		try {
			
			value = HelperMethods.getValueFromKey (result, "workflow_id");
			
		} catch (ValueFromKeyException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		if (value == null) {
			
			logger.error ("I cannot the WORKFLOW-DB entry for this id, an error has occured. Skipping this object and continue with next.");
			return;
		}
		
		int workflowid = new Integer (value);
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("workflow_id is: " + workflowid);
	}

	/**
	 * 
	 */
	
	public void eraseTestOnlyData ( ) {
		
		this.getTestData ( );
	}
	
	/**
	 * 
	 */
	
	protected void getLatestHarvestTimeStampforRepository ( ) {
		
		this.LatestRepositoryHarvest = null; //From DB
	}
	
	/**
	 * 
	 */
	
	protected void getAllObjectsFromCurrentRepository ( ) {
		
		//get all Objects
		
		while (true) { //next
			
			ObjectEntry objectentry = new ObjectEntry (new BigDecimal (0));
			objectentry.setRepositoryID (new BigDecimal (0));
			objectentry.setHarvested (new Date ( ));
			objectentry.setRepositoryDateStamp (new Date ( ));
			objectentry.SetRepositoryIdentifier (new String ( ));
			objectentry.setTestData (false);
			objectentry.setFailureCounter (0);
			
			this.objects.add (objectentry);
		}
	}
		
	/**
	 * 
	 */
	
	protected void findPeculiarObjects ( ) {
		
		for (ObjectEntry objectEntry : this.objects) {
			
			if (objectEntry.getHarvested ( ).before (this.LatestRepositoryHarvest)) {
				
				markAsPeculiar (objectEntry);
				
			}
		}
	}
	
	/**
	 * @param objectEntry
	 */
	
	protected void markAsPeculiar (ObjectEntry objectEntry) {
		
		// increase pecliarCounter
		
		// if object has been marked more than 3 times, mark it official as peculiar
		// if object has been marked more than 6 times, mark it official as outdated and remove peculiar mark
	}
	
	/**
	 * 
	 */
	
	protected void markAsOutdated ( ) {
		
		
	}
	
	/**
	 * 
	 */
	
	protected void getTestData ( ) {
		
		String result = HelperMethods.prepareRestTransmission ("AllOIDs/fromRepositoryID/" + this.repositoryID.toPlainString ( ) + "/markedAs/test", this.getProps ( )).GetData ( );
		
		RestMessage rms = RestXmlCodec.decodeRestMessage (result);
		
		Iterator <RestEntrySet> it = rms.getListEntrySets ( ).listIterator ( );
		RestEntrySet key = null;
		
		if (!it.hasNext ( )) {
			
			logger.warn ("No OIDs marked as test found!");
			return;
		}
		
		BigDecimal oid;
		
		while (it.hasNext ( )) { //next
			
			key = it.next ( );
			
			try {
			
				oid = new BigDecimal (key.getValue ("oid"));
				
			} catch (NumberFormatException ex) {
				
				logger.error ("Cannot get OID " + key.getValue ("oid") + " ! Trying next one!");
				logger.error (ex.getLocalizedMessage ( ), ex);
				
				continue;
			}
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("oid: " + oid);
			
			try {
				
				if (this.getHarvestedDatestamp (oid).before (HelperMethods.twoWeeksBefore ( )))
					this.deleteTestData (oid);
					
				else
					logger.info ("OID " + oid + " ist marked as tested an will be deleted within the next two weeks!");
				
			} catch (Exception ex) { 
				
				continue;
			}
		}
	}
	
	/**
	 * @param oid
	 * @return
	 * @throws ValueFromKeyException 
	 * @throws ParseException 
	 */
	
	private Date getHarvestedDatestamp (BigDecimal oid) throws ValueFromKeyException, ParseException {
		
		RestMessage objectEntryResponse = HelperMethods.prepareRestTransmission ("ObjectEntry/" + oid.toPlainString ( ) + "/", this.getProps ( )).sendGetRestMessage ( );
		
		String objectEntryDatestamp;
		Date objectEntryDate = null;
		
		try {
			
			objectEntryDatestamp = HelperMethods.getValueFromKey (objectEntryResponse, "repository_datestamp");
			objectEntryDate = new SimpleDateFormat ("yyyy-MM-dd").parse (objectEntryDatestamp);
			
		} catch (ValueFromKeyException ex) {
			
			marknEraseStateLog.error (ex.getLocalizedMessage ( ));
			throw ex;
			
		} catch (ParseException ex) {
			
			marknEraseStateLog.error (ex.getLocalizedMessage ( ), ex);
			throw ex;
		}
		
		return objectEntryDate;
	}

	/**
	 * @param oid
	 */
	
	protected void deleteTestData (BigDecimal oid) {
		
		logger.info ("Deleting object " + oid.toPlainString ( ) + " marked as test data");
		
		@SuppressWarnings("unused")
		String result = HelperMethods.prepareRestTransmission ("ObjectEntry/" + oid.toPlainString ( ), this.getProps ( )).DeleteData ( );
	}
	
	/**
	 * @return
	 */
	
	public final Properties getProps ( ) {
		
		return this.props;
	}
	
	/**
	 * @return
	 */
	
	public final String getPropertyfile ( ) {
		
		return this.propertyfile ;
	}
	
	/**
	 * @param propertyfile
	 */
	
	public final void setPropertyfile (String propertyfile) {
		
		this.propertyfile = propertyfile;
	}
}

class ObjectEntry {
	
	@SuppressWarnings("unused")
	private final BigDecimal objectID;
	@SuppressWarnings("unused")
	private BigDecimal repositoryID;
	@SuppressWarnings("unused")
	private Date harvested;
	@SuppressWarnings("unused")
	private Date repositoryDateStamp;
	@SuppressWarnings("unused")
	private String repositoryIdentifier;
	@SuppressWarnings("unused")
	private boolean testData;
	@SuppressWarnings("unused")
	private int failureCounter;
	
	public ObjectEntry (BigDecimal objectID) {
		
		this.objectID = objectID;
	}

	public void setFailureCounter (int failureCounter) {
		
		this.failureCounter = failureCounter;
	}

	public void setTestData (boolean testData) {
		
		this.testData = testData;
	}

	public void SetRepositoryIdentifier (String repositoryIdentifier) {
		
		this.repositoryIdentifier = repositoryIdentifier;
	}

	public void setRepositoryDateStamp (Date repositoryDateStamp) {
		
		this.repositoryDateStamp = repositoryDateStamp;
	}

	public void setHarvested (Date harvested) {
		
		this.harvested = harvested;
	}

	public Date getHarvested ( ) {
		
		return this.getHarvested ( );
	}
	
	public void setRepositoryID (BigDecimal repositoryID) {
		
		this.repositoryID = repositoryID;
	}
}
