/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.DBAccess;
import de.dini.oanetzwerk.server.database.DBAccessInterface;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;

/**
 * @author Michael Kühn
 *
 */

public class ObjectEntry extends 
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (ObjectEntry.class);
	
	public ObjectEntry ( ) {
		
		super (ObjectEntry.class.getName ( ), RestKeyword.ObjectEntry);
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {

		this.rms = new RestMessage (RestKeyword.ObjectEntry);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);	
		
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * This method returns for a given internal ObjectID the key values for this Object.
	 * If the object does not exist "null" will be returned. 
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 3)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the internal object ID");
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("internal OID = " + path [2]);
		
		this.resultset = db.getObject (Integer.parseInt (path [2]));
		
		db.closeConnection ( );
		
		RestEntrySet res = new RestEntrySet ( );
		
		try {
			
			if (this.resultset.next ( )) {
				
				if (logger.isDebugEnabled ( )) 
					logger.debug ("DB returned: \n\tobject_id = " + this.resultset.getInt (1) +
							"\n\trepository_id = " + this.resultset.getInt (2) +
							"\n\tharvested = " + this.resultset.getDate (3).toString ( ) +
							"\n\trepository_datestamp = " + this.resultset.getDate (4).toString ( ) +
							"\n\trepository_identifier = " + this.resultset.getString (5));
				
				
				res.addEntry ("object_id", this.resultset.getBigDecimal ("object_id").toPlainString ( ));
				res.addEntry ("repository_id", this.resultset.getBigDecimal ("repository_id").toPlainString ( ));
				res.addEntry ("harvested", this.resultset.getDate ("harvested").toString ( ));
				res.addEntry ("repository_datestamp", this.resultset.getDate ("repository_datestamp").toString ( ));
				res.addEntry ("repository_identifier", this.resultset.getString ("repository_identifier"));
				res.addEntry ("testdata", Boolean.toString (this.resultset.getBoolean ("testdata")));
				res.addEntry ("failure_counter", Integer.toString (this.resultset.getInt ("failure_counter")));
				
				this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching ObjectEntry found");
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntry: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			this.rms.addEntrySet (res);
			this.resultset = null;
			res = null;
		}
				
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {
		
		BigDecimal repository_id = new BigDecimal (0);
		String repository_identifier = "";
		Date repository_datestamp = null;
		boolean testdata = true;
		int failureCounter = 0;
		
		this.rms = RestXmlCodec.decodeRestMessage (data);
		RestEntrySet res = this.rms.getListEntrySets ( ).get (0);

		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (key.equalsIgnoreCase ("repository_id")) {
				
				if (res.getValue (key) != null)
					repository_id = new BigDecimal (res.getValue (key));
				
				else repository_id = new BigDecimal (-1);
				
			} else if (key.equalsIgnoreCase ("repository_identifier")) {
				
				if (res.getValue (key) != null)
					repository_identifier = res.getValue (key);
				
				else repository_identifier = "";
				
			} else if (key.equalsIgnoreCase ("repository_datestamp")) {
				
				if (res.getValue (key) != null) {
					
					try {
						
						repository_datestamp = HelperMethods.extract_datestamp (res.getValue (key));
						
					} catch (ParseException ex) {
						
						logger.error (ex.getLocalizedMessage ( ));
						ex.printStackTrace ( );
					}
					
				} else repository_datestamp = null;
				
			} else if (key.equalsIgnoreCase ("testdata")) {
				
				testdata = new Boolean (res.getValue (key));
				
			} else if (key.equalsIgnoreCase ("failureCounter")) {
				
				failureCounter = new Integer (res.getValue (key));
				
			} else {
				
				logger.warn ("maybe I read a parameter which is not implemented! But I am continueing");
				continue;
			}
		}

		Date harvested = HelperMethods.today ( );
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		this.resultset = db.updateObject (repository_id, harvested, repository_datestamp, repository_identifier, testdata, failureCounter);
		
		db.closeConnection ( );
		
		this.rms = new RestMessage (RestKeyword.ObjectEntry);
		res = new RestEntrySet ( );
		
		try {
			
			if (this.resultset.next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: object_id = " + this.resultset.getInt (1));
				
				res.addEntry ("oid", Integer.toString (this.resultset.getInt (1)));
				this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching ObjectEntry found");
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			this.rms.addEntrySet (res);
			this.resultset = null;
			res = null;
		}
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 * This method inserts a new Object entry. The values for the new object will be extracted from the
	 * HTTP-Body's data.
	 * The internal ObjectID of the newly created Object will be returned.
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) {
		
		BigDecimal repository_id = new BigDecimal (0);
		String repository_identifier = "";
		Date repository_datestamp = null;
		boolean testdata = true;
		int failureCounter = 0;
		
		this.rms = RestXmlCodec.decodeRestMessage (data);
		RestEntrySet res = this.rms.getListEntrySets ( ).get (0);

		Iterator <String> it = res.getKeyIterator ( );
		String key = "";
		
		while (it.hasNext ( )) {
			
			key = it.next ( );
			
			if (key.equalsIgnoreCase ("repository_id")) {
				
				repository_id = new BigDecimal (res.getValue (key));
				
			} else if (key.equalsIgnoreCase ("repository_identifier")) {
				
				repository_identifier = res.getValue (key);
				
			} else if (key.equalsIgnoreCase ("repository_datestamp")) {
				
				try {
					
					repository_datestamp = HelperMethods.extract_datestamp (res.getValue (key));
					
				} catch (ParseException ex) {
					
					logger.error (ex.getLocalizedMessage ( ));
					ex.printStackTrace ( );
				}
				
			} else if (key.equalsIgnoreCase ("testdata")) {
				
				testdata = new Boolean (res.getValue (key));
				
			} else if (key.equalsIgnoreCase ("failureCounter")) {
				
				failureCounter = new Integer (res.getValue (key));
				
			} else {
				
				logger.warn ("maybe I read a parameter which is not implemented! But I am continueing");
				continue;
			}
		}

		Date harvested = HelperMethods.today ( );
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("The following values will be inserted:\n\tRepository ID = " + repository_id +
					"\n\tHarvested = " + harvested +
					"\n\tRepository Datestamp = " + repository_datestamp +
					"\n\texternal OID = " + repository_identifier);
		
		//resultset = db.insertObject (repository_id, harvested, repository_datestamp, repository_identifier);
		this.resultset = db.insertObject (repository_id, harvested, repository_datestamp, repository_identifier, testdata, failureCounter);
		
		db.closeConnection ( );
		
		this.rms = new RestMessage (RestKeyword.ObjectEntry);
		res = new RestEntrySet ( );
		
		try {
			
			if (this.resultset.next ( )) {
				
				if (logger.isDebugEnabled ( ))
					logger.debug ("DB returned: object_id = " + this.resultset.getInt (1));
				
				res.addEntry ("oid", Integer.toString (this.resultset.getInt (1)));
				this.rms.setStatus (RestStatusEnum.OK);
				
			} else {
				
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching ObjectEntry found");
			}
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			this.rms.addEntrySet (res);
			this.resultset = null;
			res = null;
		}
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
	
} // end of class
