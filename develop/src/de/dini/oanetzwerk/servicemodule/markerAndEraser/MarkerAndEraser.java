package de.dini.oanetzwerk.servicemodule.markerAndEraser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
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
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.ServiceIDException;
import de.dini.oanetzwerk.utils.exceptions.ValueFromKeyException;

/**
 * @author Michael K&uuml;hn
 * @author Manuel Klatt-Kafemann
 * @author Sammy David
 * 
 */

public class MarkerAndEraser {

	private static final int LIMIT_PECULIAR = 3;

	private static final int LIMIT_OUTDATED = 6;

	/**
	 * log4j-Logger
	 */

	private static Logger logger = Logger.getLogger(MarkerAndEraser.class);

	/**
	 * 
	 */

	private static Logger marknEraseStateLog = Logger
			.getLogger("marknEraseStateLog");

	/**
	 * 
	 */

	private final BigDecimal repositoryID;

	/**
	 * 
	 */

	private Date lastRepositoryHarvestDate;
	
	
	private Date lastRepositoryMarkAndEraseDate;


	
//	private final int REQUEST_BATCH_SIZE = 1000;
	
	/**
	 * 
	 */

	private Properties props = new Properties();

	/**
	 * 
	 */

	private String propertyfile = "markereraserprop.xml";

	/**
	 * @param repositoryID
	 */

	public MarkerAndEraser(BigDecimal repositoryID) {

		this.repositoryID = repositoryID;

		try {

			this.props = HelperMethods.loadPropertiesFromFile(this
					.getPropertyfile());

		} catch (InvalidPropertiesFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			ex.printStackTrace();
			System.exit(1);

		} catch (FileNotFoundException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			ex.printStackTrace();
			System.exit(1);

		} catch (IOException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			ex.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * 
	 */

	public void markAndErase() {

		// first of all retrieve the repository information
		getRepositoryInfo();
		
		// check for data that has not been collected during the last full harvest
		this.checkForPeculiarObjects();
		
		//this.getData();
		
		try {	
			setMarkerEraserDateStampForRepository();
		
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getLocalizedMessage());
			logger.error("Could not update marker&eraser datestamp for repository with id " + this.repositoryID);
		}
	}

	/**
	 * Retrieves service ID for MarkAndErase Operation, retrieves all workflow
	 * entries with the according processing status
	 */

	private void getData() {

		String ressource = "";

		BigDecimal serviceId;

		try {

			serviceId = HelperMethods.getServiceId("MarkAndErase", props);

			logger.debug("ServiceID: " + serviceId);

		} catch (ServiceIDException ex) {

			serviceId = new BigDecimal(3);
			ex.printStackTrace();
		}

		ressource = "WorkflowDB/" + serviceId;

		RestMessage rms = HelperMethods.prepareRestTransmission(ressource,
				this.getProps()).sendGetRestMessage();

		if (rms.getStatus() != RestStatusEnum.OK) {

			logger.error("WorkflowDB response failed: " + rms.getStatus() + "("
					+ rms.getStatusDescription() + ")");
			return;
		}

		// String result = prepareRestTransmission ("AllOIDs/fromRepositoryID/"
		// + this.repositoryID.toPlainString ( )).GetData ( );

		Iterator<RestEntrySet> it = rms.getListEntrySets().listIterator();
		RestEntrySet key = null;

		if (!it.hasNext()) {

			logger.warn("No OIDs found for this repository!");
			return;
		}

		BigDecimal oid;

		int i = 0;

		while (it.hasNext()) { // next

			if (i++ >= 1) {
				// break;
				System.out.println("break...");
				break;
			}

			key = it.next();

			try {

				oid = new BigDecimal(key.getValue("object_id"));

			} catch (NumberFormatException ex) {

				logger.error("Cannot get OID " + key.getValue("object_id")
						+ " ! Trying next one!");
				logger.error(ex.getLocalizedMessage(), ex);

				continue;
			}

			if (logger.isDebugEnabled())
				logger.debug("oid: " + oid);

//			try {

//				logger.debug("Harvested on " + this.getHarvestedDatestamp(oid));

				this.updateWorkflowDB(oid, serviceId);

//			} catch (ValueFromKeyException ex1) {
//				// TODO Auto-generated catch block
//				ex1.printStackTrace();
//			} catch (ParseException ex1) {
//				// TODO Auto-generated catch block
//				ex1.printStackTrace();
//			}

			// if harvested older than config-weeks
			try {

				// this.deleteTestData (new BigDecimal (key.getValue ("oid")));

			} catch (NumberFormatException ex) {

				logger.error(ex.getLocalizedMessage(), ex);
			}
		}
	}

	/**
	 * Sends an object update to the workflow DB
	 */
	private void updateWorkflowDB(BigDecimal oid, BigDecimal serviceId) {

		logger.debug("set marked completed for " + oid);
		RestMessage rms;
		RestEntrySet res;
		RestMessage result = null;

		rms = new RestMessage();

		rms.setKeyword(RestKeyword.WorkflowDB);
		rms.setStatus(RestStatusEnum.OK);

		res = new RestEntrySet();

		res.addEntry("object_id", oid.toPlainString());
		res.addEntry("service_id", serviceId.toPlainString());
		res.addEntry("time", HelperMethods.today().toString() + " 00:00:00.1");
		rms.addEntrySet(res);

		RestEntrySet resout = rms.getListEntrySets().get(0);

		logger.debug("object_id" + resout.getValue("object_id"));
		logger.debug("service_id" + resout.getValue("service_id"));
		logger.debug("time" + resout.getValue("time"));

		// restclient = RestClient.createRestClient (this.getProps (
		// ).getProperty ("host"), resource, this.getProps ( ).getProperty
		// ("username"), this.getProps ( ).getProperty ("password"));

		try {

			if (logger.isDebugEnabled())
				logger.debug("BEFORE PUT WorkflowDB/" + oid);
			result = HelperMethods.prepareRestTransmission("WorkflowDB/",
					this.getProps()).sendPutRestMessage(rms);
			if (logger.isDebugEnabled())
				logger.debug("AFTER PUT WorkflowDB/" + oid);

			// result = restclient.PutData (requestxml);

		} catch (UnsupportedEncodingException ex) {

			logger.error(ex.getLocalizedMessage());
			ex.printStackTrace();
		}

		rms = null;
		res = null;
		// restclient = null;

		String value = null;

		try {

			value = HelperMethods.getValueFromKey(result, "workflow_id");

		} catch (ValueFromKeyException ex) {

			ex.printStackTrace();
		}

		if (value == null) {

			logger
					.error("I cannot the WORKFLOW-DB entry for this id, an error has occured. Skipping this object and continue with next.");
			return;
		}

		int workflowid = new Integer(value);

		if (logger.isDebugEnabled())
			logger.debug("workflow_id is: " + workflowid);
	}

	/**
	 * 
	 */

	public void eraseTestOnlyData() {

		this.getTestData();
	}

	/**
	 * 
	 */

	private void getRepositoryInfo() {

		this.lastRepositoryHarvestDate = null; // From DB
		
		String resource = "Repository/" + this.repositoryID.toString();
		
		RestMessage rms = HelperMethods.prepareRestTransmission(resource,
				this.getProps()).sendGetRestMessage();
		
		if (rms == null || rms.getStatus() != RestStatusEnum.OK) {
			
			logger.error("Repository response failed: " + rms.getStatus() + "("
					+ rms.getStatusDescription() + ")");
			return;
		}

		Iterator<RestEntrySet> it = rms.getListEntrySets().listIterator();
		RestEntrySet key = null;

		if (!it.hasNext()) {

			logger.error("No information for repository with ID " + this.repositoryID + " available!");
			return;
		
		}


		if (it.hasNext()) {

			key = it.next();
			final String lastFullHarvestDate = key.getValue("last_full_harvest_begin");
			final String lastMarkerEraserDate = key.getValue("last_markereraser_begin");
						
			try {
				
				this.lastRepositoryHarvestDate 		= (lastFullHarvestDate == null) ? null : new SimpleDateFormat("yyyy-mm-dd").parse(lastFullHarvestDate);
				this.lastRepositoryMarkAndEraseDate 	= (lastMarkerEraserDate == null) ? null : new SimpleDateFormat("yyyy-mm-dd").parse(lastMarkerEraserDate);
				
				logger.info("last repository harvest date: " + this.lastRepositoryHarvestDate + "   last repository mark&erase date: " + lastMarkerEraserDate);
			} catch (ParseException e) {
				e.printStackTrace();
				logger.warn("Latest Repository Harvest datestamp and/or latest repository mark&erase datestamp not properly set for repository with id " + this.repositoryID + "! Make sure to run Harvester/Marker&Eraser in advance!");
			}

			if (logger.isInfoEnabled())
				logger.info("Repository info received for repository with ID " + this.repositoryID);
			
		}			
	}
	
	
	/**
	 * 
	 */

	protected void checkForPeculiarObjects() {
		
		BigDecimal oidOffset = new BigDecimal(0);
		boolean lastBatch = false;
			

		if (this.lastRepositoryHarvestDate == null)
		{
			logger.warn("Could not get a valid lastFullHarvestDatestamp for the repository with id " + this.repositoryID + "! Canceling Marker&Eraser operation for this repository!");
			return;
		}
		
		// check if there has been a full harvest cycle for the specified repository since the last mark&Erase run
		// if not, skip mark&Erase procedure
		if (this.lastRepositoryMarkAndEraseDate != null && this.lastRepositoryHarvestDate.before(lastRepositoryMarkAndEraseDate)) {
			
			logger.info("There has been no full harvest cycle for repository with id " + repositoryID + "! Skipping marker procedure for this repository!");
			return;
		}
		
		
		System.out.println("checking2");		
		// send multiple requests to retrieve batches of ObjectEntries for the current repository
		while (!lastBatch)
		{
		
			String resource = "ObjectEntry/fromRepositoryID/" + repositoryID + "/oidOffset/" + oidOffset;
			
			RestMessage rms = HelperMethods.prepareRestTransmission(resource,
					this.getProps()).sendGetRestMessage();
	
			if (rms.getStatus() != RestStatusEnum.OK) {
				logger.error("ObjectEntry/ response failed: " + rms.getStatus() + "("
						+ rms.getStatusDescription() + ")");
				return;
			}
	
			Iterator<RestEntrySet> it = rms.getListEntrySets().listIterator();
			RestEntrySet res = null;
	
			if (!it.hasNext()) {
	
				logger.warn("No Object entries found for this repository!");
				return;
			}
			
			while (it.hasNext()) { // next

				res = it.next();
				Date harvestedTimestamp = null;
				
				try {
					
					harvestedTimestamp = new SimpleDateFormat("yyyy-mm-dd").parse(res.getValue("harvested"));
				
				} catch (ParseException e) {
					
					logger.error("Error while parsing object entry, skipping current object entry!");
					continue;
				}
				
				if(logger.isDebugEnabled()) {
					logger.debug("Comparing datestamps " + harvestedTimestamp + " and " + lastRepositoryHarvestDate + "for object with id " + res.getValue("object_id"));
				}
				
				if (harvestedTimestamp.before(lastRepositoryHarvestDate)) {
					
					System.out.println("found object to mark");
					// mark and increase peculiar counter
					markAndIncreasePeculiarCounter(res);
					
					System.out.println(res.getValue("outdated"));
					System.out.println(res.getValue("peculiar"));

					// update object entry
					RestMessage updateResponse = null;
					BigDecimal objectId = null;
						
					try {

						objectId = new BigDecimal(res.getValue("object_id"));
						String updateResource = "ObjectEntry/" + objectId;
						
						RestClient restclient = RestClient.createRestClient (this.props.getProperty ("host"), updateResource, this.props.getProperty ("username"), this.props.getProperty ("password"));
						RestMessage updateRequest = new RestMessage(RestKeyword.ObjectEntry);
						updateRequest.addEntrySet (res);
						
						System.out.println("counter " + res.getValue("peculiar_counter"));

						logger.debug("object entry to send via POST: " + objectId);
						updateResponse = restclient.sendPostRestMessage(updateRequest);
						
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						HelperMethods.getRestFailureMessage(RestKeyword.ObjectEntry, RestStatusEnum.UNSUPPORTED_ENCODING, 
								"Encoding problem trying to update object entry with id " + objectId + "! Ignoring request...");
					}
					
					if (updateResponse == null || !RestStatusEnum.OK.equals(updateResponse.getStatus())) {
					
						logger.error("REST-Transmission failed: " + updateResponse);
						logger.error("Error while sending POST request to update object entry with id " + objectId + 
								", server responded with error:\n" + updateResponse.getStatus() + " - " + updateResponse.getStatusDescription());

					} else {
						
						logger.info("success updating object entry with oid " + objectId);
					}
					
					
				} // endif					
			} //endwhile
			
			// set oid offset to fetch next batch 
			oidOffset = new BigDecimal(res.getValue("object_id"));

			if (rms.getListEntrySets().size() < 1000) {
				lastBatch = true;
				System.out.println("lastBatch");
			} 			
		}
		
		

	}

	private void markAndIncreasePeculiarCounter(RestEntrySet res) {
		
		Integer peculiarCounter = 0;
		boolean peculiar = false;
		boolean outdated = false;
		
		try {
			peculiarCounter = Integer.parseInt(res.getValue("peculiar_counter"));
			peculiar = Boolean.parseBoolean(res.getValue("peculiar"));
			outdated = Boolean.parseBoolean(res.getValue("outdated"));
			
		}catch (NumberFormatException e) {
			
			logger.warn("Retrieved EntrySet seems to be defect! Skipping it.");
		}
		
		// increase the peculiarity counter
		peculiarCounter++;
		
		//mark as outdated if limit is reached
		if (peculiarCounter >= LIMIT_OUTDATED)
		{
			//mark as autdated
			outdated = true;
			res.remove("outdated");
			res.addEntry("outdated", Boolean.toString(outdated));
		}
		
		//mark as peculiar if limit is reached
		else if(peculiarCounter >= LIMIT_PECULIAR )
		{
			
			// mark as peculiar
			peculiar = true;
			res.remove("peculiar");
			res.addEntry("peculiar", Boolean.toString(peculiar));
		} 

		// set new peculiar counter
		res.remove("peculiar_counter");
		res.addEntry("peculiar_counter", Integer.toString(peculiarCounter));
		
	}


	/**
	 * Retrieves all testdata entries and deletes testdata entries older than 2
	 * weeks
	 */

	protected void getTestData() {

		String result = HelperMethods.prepareRestTransmission(
				"AllOIDs/fromRepositoryID/" + this.repositoryID.toPlainString()
						+ "/markedAs/test", this.getProps()).GetData();

		RestMessage rms = RestXmlCodec.decodeRestMessage(result);

		Iterator<RestEntrySet> it = rms.getListEntrySets().listIterator();
		RestEntrySet key = null;

		if (!it.hasNext()) {

			logger.warn("No OIDs marked as test found!");
			return;
		}

		BigDecimal oid;

		while (it.hasNext()) { // next

			key = it.next();

			try {

				oid = new BigDecimal(key.getValue("oid"));

			} catch (NumberFormatException ex) {

				logger.error("Cannot get OID " + key.getValue("oid")
						+ " ! Trying next one!");
				logger.error(ex.getLocalizedMessage(), ex);

				continue;
			}

			if (logger.isDebugEnabled())
				logger.debug("oid: " + oid);

			try {

				if (this.getHarvestedDatestamp(oid).before(
						HelperMethods.twoWeeksBefore()))
					this.deleteTestData(oid);

				else
					logger
							.info("OID "
									+ oid
									+ " ist marked as tested an will be deleted within the next two weeks!");

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

	private Date getHarvestedDatestamp(BigDecimal oid)
			throws ValueFromKeyException, ParseException {

		RestMessage objectEntryResponse = HelperMethods
				.prepareRestTransmission(
						"ObjectEntry/" + oid.toPlainString() + "/",
						this.getProps()).sendGetRestMessage();

		String objectEntryDatestamp;
		Date objectEntryDate = null;

		try {
			objectEntryDatestamp = HelperMethods.getValueFromKey(
					objectEntryResponse, "harvested");
			// objectEntryDatestamp = HelperMethods.getValueFromKey
			// (objectEntryResponse, "repository_datestamp");
			objectEntryDate = new SimpleDateFormat("yyyy-MM-dd")
					.parse(objectEntryDatestamp);

		} catch (ValueFromKeyException ex) {

			marknEraseStateLog.error(ex.getLocalizedMessage());
			throw ex;

		} catch (ParseException ex) {

			marknEraseStateLog.error(ex.getLocalizedMessage(), ex);
			throw ex;
		}

		return objectEntryDate;
	}

	/**
	 * @param oid
	 */

	protected void deleteTestData(BigDecimal oid) {

		logger.info("Deleting object " + oid.toPlainString()
				+ " marked as test data");

		@SuppressWarnings("unused")
		String result = HelperMethods.prepareRestTransmission(
				"ObjectEntry/" + oid.toPlainString(), this.getProps())
				.DeleteData();
	}

	
	private void setMarkerEraserDateStampForRepository ( ) throws UnsupportedEncodingException {
		
		String resource = "Repository/" + this.repositoryID + "/markedtoday/";
		
		String postRepositories = RestClient.createRestClient (this.getProps ( ).getProperty ("host"), resource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"))
				.PostData("");
		
		RestMessage postRepositoriesmsg = RestXmlCodec.decodeRestMessage (postRepositories);
		
		if (postRepositoriesmsg == null || postRepositoriesmsg.getStatus ( ) != RestStatusEnum.OK) {
			
			String description = RestStatusEnum.UNKNOWN_ERROR.toString ( );
			
			if (postRepositoriesmsg != null) {
				
				description = postRepositoriesmsg.getStatusDescription ( );
			
				if (postRepositoriesmsg.getStatus ( ) == RestStatusEnum.SQL_WARNING) {
					
					logger.warn ("SQL_WARNING: " + description);

					//					harvStateLog.warn ("SQL_WARNING: " + description);
					// TODO: create markereraser log file
					
					return;
					
				} else;
				
			} else {
				
				logger.error ("Could NOT post Repositories FullHarvest-DateStamp into the database for repository No " + this.repositoryID + "! " + description);
				// TODO: harvStateLog.error ("Could NOT post Repositories FullHarvest-DateStamp into the database for repository No " + this.getRepositoryID ( ) + "! Cause: " + description);
				
				return;
			}
		}
	}
	
	
	/**
	 * @return
	 */

	public final Properties getProps() {

		return this.props;
	}

	/**
	 * @return
	 */

	public final String getPropertyfile() {

		return this.propertyfile;
	}

	/**
	 * @param propertyfile
	 */

	public final void setPropertyfile(String propertyfile) {

		this.propertyfile = propertyfile;
	}
}

//class ObjectEntry {
//
//	@SuppressWarnings("unused")
//	private final BigDecimal objectID;
//	@SuppressWarnings("unused")
//	private BigDecimal repositoryID;
//	@SuppressWarnings("unused")
//	private Date harvested;
//	@SuppressWarnings("unused")
//	private Date repositoryDateStamp;
//	@SuppressWarnings("unused")
//	private String repositoryIdentifier;
//	@SuppressWarnings("unused")
//	private boolean testData;
//	@SuppressWarnings("unused")
//	private int failureCounter;
//
//	private boolean peculiar;
//	private boolean outdated;
//	private int peculiarCounter;
//
//	public ObjectEntry(BigDecimal objectID) {
//
//		this.objectID = objectID;
//	}
//
//	public void setFailureCounter(int failureCounter) {
//
//		this.failureCounter = failureCounter;
//	}
//
//	public void setTestData(boolean testData) {
//
//		this.testData = testData;
//	}
//
//	public void SetRepositoryIdentifier(String repositoryIdentifier) {
//
//		this.repositoryIdentifier = repositoryIdentifier;
//	}
//
//	public void setRepositoryDateStamp(Date repositoryDateStamp) {
//
//		this.repositoryDateStamp = repositoryDateStamp;
//	}
//
//	public void setHarvested(Date harvested) {
//
//		this.harvested = harvested;
//	}
//
//	public Date getHarvested() {
//
//		return this.getHarvested();
//	}
//
//	public void setRepositoryID(BigDecimal repositoryID) {
//
//		this.repositoryID = repositoryID;
//	}
//	
//	public BigDecimal getRepositoryID() {
//	
//		
//		return repositoryID;
//	}
//
//	public void setPeculiar(boolean peculiar) {
//		
//		this.peculiar = peculiar;
//	}
//
//	public void setOutdated(boolean outdated) {
//		
//		this.outdated = outdated;
//	}
//
//	public void setPeculiarCounter(int peculiarCounter) {
//		
//		this.peculiarCounter = peculiarCounter;
//	}
//
//}
