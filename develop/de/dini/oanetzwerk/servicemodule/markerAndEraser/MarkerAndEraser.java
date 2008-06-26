package de.dini.oanetzwerk.servicemodule.markerAndEraser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.servicemodule.RestClient;
import de.dini.oanetzwerk.utils.HelperMethods;

public class MarkerAndEraser {
	
	static Logger logger = Logger.getLogger (MarkerAndEraser.class); 
	
	private final BigDecimal repositoryID;
	private Date LatestRepositoryHarvest;
	private final LinkedList <ObjectEntry> objects = new LinkedList <ObjectEntry> ( );

	private Properties props = new Properties ( );

	private String propertyfile = "markereraserprop.xml";
	
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
	
	public void markAndErase ( ) {
		
		this.getTestData ( );
	}
	
	public void eraseTestOnlyData ( ) {
		
		this.getTestData ( );
	}
	
	protected void getLatestHarvestTimeStampforRepository ( ) {
		
		this.LatestRepositoryHarvest = null; //From DB
	}
	
	protected void getAllObjectsFromCurrentRepoitory ( ) {
		
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
		
	protected void findPeculiarObjects ( ) {
		
		for (ObjectEntry objectEntry : this.objects) {
			
			if (objectEntry.getHarvested ( ).before (this.LatestRepositoryHarvest)) {
				
				markAsPeculiar (objectEntry);
				
			}
		}
	}
	
	protected void markAsPeculiar (ObjectEntry objectEntry) {
		
		// increase pecliarCounter
		
		// if object has been marked more than 3 times, mark it official as peculiar
		// if object has been marked more than 6 times, mark it official as outdated and remove peculiar mark
	}
	
	protected void markAsOutdated ( ) {
		
		
	}
	
	protected void getTestData ( ) {
		
		String result = prepareRestTransmission ("AllOIDs/fromRepositoryID/" + this.repositoryID.toPlainString ( ) + "/markedAs/test").GetData ( );
		
		RestMessage rms = RestXmlCodec.decodeRestMessage (result);
		//RestEntrySet res = rms.getListEntrySets ( ).get (0);
		
		Iterator <RestEntrySet> it = rms.getListEntrySets ( ).listIterator ( );
		//Iterator <String> it = res.getKeyIterator ( );
		RestEntrySet key = null;
		
		if (!it.hasNext ( ))
			;//TODO: Meckern!!!
		
		while (it.hasNext ( )) { //next
			
			key = it.next ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("oid: " + key.getValue ("oid"));
			
			// if harvested older than config-weeks
			try {
				
				deleteTestData (new BigDecimal (key.getValue ("oid")));
				
			} catch (NumberFormatException ex) {
				
				logger.error (ex.getLocalizedMessage ( ), ex);
				ex.printStackTrace ( );
			}
		}
	}
	
	protected void deleteTestData (BigDecimal oid) {
		
		String result = prepareRestTransmission ("ObjectEntry/" + oid.toPlainString ( )).DeleteData ( );
	}
	
	public final Properties getProps ( ) {
		
		return this.props;
	}
	
	public final String getPropertyfile ( ) {
		
		return this.propertyfile ;
	}
	
	public final void setPropertyfile (String propertyfile) {
		
		this.propertyfile = propertyfile;
	}
	
	private RestClient prepareRestTransmission (String resource) {
		
		logger.debug (this.getProps ( ).getProperty ("host") + " "  + resource + " " +this.getProps ( ).getProperty ("username"));
		
		return RestClient.createRestClient (this.getProps ( ).getProperty ("host"), resource, this.getProps ( ).getProperty ("username"), this.getProps ( ).getProperty ("password"));
	}
}

class ObjectEntry {
	
	private final BigDecimal objectID;
	private BigDecimal repositoryID;
	private Date harvested;
	private Date repositoryDateStamp;
	private String repositoryIdentifier;
	private boolean testData;
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
