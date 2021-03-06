package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Manuel Klatt-Kafemann
 *
 */

public class ObjectServiceStatus extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (ObjectServiceStatus.class); 
	
	public static final int ALL = 0;
//	public static final int FLAG = 1;
//	public static final int FROMREPO = 2;
//	public static final int FROMREPO_AND_FLAG = 3;	
	public static final int OBJECTID = 4;
	
//	private BigDecimal repositoryID = null;
//	private String strFlag = null;
	private BigDecimal objectEntryID = null;
	
	/**
	 * 
	 */
	
	public ObjectServiceStatus ( ) {
		
		super (ObjectServiceStatus.class.getName ( ), RestKeyword.ObjectServiceStatus);
	}	

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * This method returns 
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		// no parameters -- return ERROR
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the internal object ID");

		// first parameter = ALL -- return all OIDs and their respective service status
		if (path[0].equals("ALL")) {
			return RestXmlCodec.encodeRestMessage (getOIDsRestMessage(ALL));
		}
		
		// specific object_id -- return service status of that object
		try {
			
			objectEntryID = new BigDecimal (path [0]);
			
			if (objectEntryID.intValue() < 0) {
				
				logger.error (path [0] + " is NOT a valid number for this parameter!");
				
				this.rms = new RestMessage (RestKeyword.ObjectServiceStatus);
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription (path [0] + " is NOT a valid number for this parameter!");
				
				return RestXmlCodec.encodeRestMessage (this.rms);				
				
			}
			return RestXmlCodec.encodeRestMessage (getOIDsRestMessage(OBJECTID));
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.ObjectServiceStatus);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
//		// filter by marking flag -- return all fitting OIDs
//		if ("markedAs".equals(path[0])) {
//			
//			/*
//			 * falls wir uns entscheiden, eine separate, generische Markierungstabelle aus OID und FlagID
//			 * einzuführen, wobei FlagID auf eine FlagTabelle mit sprechendem Namen für das Flag verlinkt,
//			 * müssen wir die KeywordLogik nicht ändern
//			 * 
//			 */
//			
//			if (path.length < 2)
//				throw new NotEnoughParametersException ("This method used with first parameter \"markedAs\" needs a second parameter to specify the marking flag.");			
//			
//			if ("test".equals(path[1]) 
//				/*|| "weitere Markierung".equals(path[1])*/
//			   ) {
//				
//				strFlag = path[1];
//				
//				// return all marked with a specific flag
//				return RestXmlCodec.encodeRestMessage (getOIDsRestMessage(FLAG));
//				
//			} else {
//				
//				logger.error (path [1] + " is an unknown marking flag!");
//							
//				this.rms = new RestMessage (RestKeyword.AllOIDs);
//				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
//				this.rms.setStatusDescription (path [1] + " is an unknown marking flag!");
//				return RestXmlCodec.encodeRestMessage (this.rms);
//				
//			}
//			
//		} 
//		
//		// filter by repository ID -- return all fitting OIDs 
//		if("fromRepositoryID".equals(path[0])) {
//
//			if (path.length < 2)
//				throw new NotEnoughParametersException ("This method used with first parameter \"fromRepositoryID\" needs a second parameter to specify the repository ID.");			
//			
//			
//			try {
//				repositoryID = new BigDecimal (path [1]);
//			} catch (NumberFormatException ex) {
//				
//				logger.error (path [1] + " is NOT a number and cannot be the expected repository ID!");
//				
//				this.rms = new RestMessage (RestKeyword.AllOIDs);
//				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
//				this.rms.setStatusDescription (path [1] + " is NOT a number and cannot be the expected repository ID!");
//
//				return RestXmlCodec.encodeRestMessage (this.rms);
//			}
//			
//			if(path.length < 4) {
//							
//				// return all from a repository ID
//			    return RestXmlCodec.encodeRestMessage (getOIDsRestMessage(FROMREPO));
//			    
//			} else {
//				
//				// almost same code as above with markedAs only !!!
//				
//                // filter by marking flag -- return all fitting OIDs
//				if ("markedAs".equals(path[2])) {
//					
//					if (path.length < 2)
//						throw new NotEnoughParametersException ("This method used with first parameter \"markedAs\" needs a second parameter to specify the marking flag.");			
//					
//					if ("test".equals(path[3]) 
//						/*|| "weitere Markierung".equals(path[1])*/
//					   ) {
//						
//						strFlag = path[3];
//						
//						// return all from a repository ID marked with a specific flag
//						return RestXmlCodec.encodeRestMessage (getOIDsRestMessage(FROMREPO_AND_FLAG));
//						
//					} else {
//						
//						logger.warn (path [3] + " is an unknown marking flag!");
//									
//						this.rms = new RestMessage (RestKeyword.AllOIDs);
//						this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
//						this.rms.setStatusDescription (path [3] + " is an unknown marking flag!");
//						return RestXmlCodec.encodeRestMessage (this.rms);
//						
//					}
//					
//				} else {
//					
//					logger.warn (path [2] + " is an unknown parameter!");
//					
//					this.rms = new RestMessage (RestKeyword.AllOIDs);
//					this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
//					this.rms.setStatusDescription (path [2] + " is an unknown parameter!");
//					return RestXmlCodec.encodeRestMessage (this.rms);
//				}
//				
//			}
//			
//		}
//		
//		// other convenience filters go HERE
//		   // by time of creation, ...		
//		
//		// return general parameter usage error
//		
//		logger.warn (path [0] + " is an unknown parameter!");
//		
//		this.rms = new RestMessage (RestKeyword.AllOIDs);
//		this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
//		this.rms.setStatusDescription (path [0] + " is an unknown parameter!");
//		return RestXmlCodec.encodeRestMessage (this.rms);

	}

	/**
	 * @param flag
	 * @return
	 */
	
	protected RestMessage getOIDsRestMessage(int flag) {
		
		this.rms = new RestMessage (RestKeyword.ObjectServiceStatus);
		RestEntrySet entrySet = new RestEntrySet ( );

		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;
		
		try {
			
			// fetch and execute specific statement 
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );						
			switch (flag) {
				case ALL:
					stmtconn.loadStatement (DBAccessNG.selectFromDB().ObjectServiceStatusAll(stmtconn.connection));	
					break;
				case OBJECTID:
					stmtconn.loadStatement (DBAccessNG.selectFromDB().ObjectServiceStatusID(stmtconn.connection, this.objectEntryID));	
					break;
//				case FLAG:
//					if("test".equals(strFlag))stmtconn.loadStatement (SelectFromDB.AllOIDsMarkAsTest(stmtconn.connection));	
//					break;					
//				case FROMREPO:
//					stmtconn.loadStatement (SelectFromDB.AllOIDsFromRepositoryID(stmtconn.connection, repositoryID));	
//					break;
//				case FROMREPO_AND_FLAG:
//					if("test".equals(strFlag))stmtconn.loadStatement (SelectFromDB.AllOIDsFromRepositoryIDMarkAsTest(stmtconn.connection, repositoryID));	
//					break;										
			}						
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
			// extract oids from db response
			while(this.result.getResultSet ( ).next ( )) {
				entrySet = new RestEntrySet();
				entrySet.addEntry ("object_id", Integer.toString (this.result.getResultSet ( ).getInt ("object_id")));	
				entrySet.addEntry ("time", (this.result.getResultSet ( ).getTimestamp("time")).toString());
				entrySet.addEntry ("service_id", Integer.toString (this.result.getResultSet ( ).getInt ("service_id")));
				this.rms.addEntrySet (entrySet);								
			} 
			this.rms.setStatus (RestStatusEnum.OK);
			
			// error if no oids at all
			if(entrySet.getEntryHashMap().isEmpty()) {				
				if (logger.isDebugEnabled ( ))
					logger.debug ("No matching internal objectID found");
				
				entrySet.addEntry ("oid", null);
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No matching internal objectID found");
			}
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get ObjectServiceStatus: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get ObjectServiceStatus: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					logger.error (ex.getLocalizedMessage ( ), ex);
				}
			}
			
			entrySet = null;
			this.result = null;
			dbng = null;
		}		
		
		return this.rms;
		
	}
	
	
	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now. 
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.ObjectServiceStatus);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("POST method is not implemented for ressource '"+RestKeyword.ObjectServiceStatus+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.ObjectServiceStatus);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("PUT method is not implemented for ressource '"+RestKeyword.ObjectServiceStatus+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
	
	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.ObjectServiceStatus);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("DELETE-method is not implemented for ressource '"+RestKeyword.ObjectServiceStatus+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}	
	
	
}
