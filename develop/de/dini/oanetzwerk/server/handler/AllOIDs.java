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
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Robin Malitz
 *
 */

public class AllOIDs extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (AllOIDs.class);
	
	public static final int ALL = 0;
	public static final int FLAG = 1;
	public static final int FROMREPO = 2;
	public static final int FROMREPO_AND_FLAG = 3;	
	
	private BigDecimal repositoryID = null;
	private String strFlag = null;
	
	/**
	 * 
	 */
	
	public AllOIDs ( ) {
		
		super (AllOIDs.class.getName ( ), RestKeyword.AllOIDs);
	}	

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * This method returns 
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		// no parameters -- return all existing OIDs
		if (path.length == 0) {
			
			return RestXmlCodec.encodeRestMessage (getOIDsRestMessage(ALL));
			
		}
		
		// filter by marking flag -- return all fitting OIDs
		if ("markedAs".equals(path[0])) {
			
			/*
			 * falls wir uns entscheiden, eine separate, generische Markierungstabelle aus OID und FlagID
			 * einzuführen, wobei FlagID auf eine FlagTabelle mit sprechendem Namen für das Flag verlinkt,
			 * müssen wir die KeywordLogik nicht ändern
			 * 
			 */
			
			if (path.length < 2)
				throw new NotEnoughParametersException ("This method used with first parameter \"markedAs\" needs a second parameter to specify the marking flag.");			
			
			if ("test".equals(path[1]) 
			    || "notTest".equals(path[1])
			    || "productive".equals(path[1])
			    || "hasFulltextlink".equals(path[1])
				/*|| "weitere Markierung".equals(path[1])*/
			   ) {
				
				strFlag = path[1];
				
				// return all marked with a specific flag
				return RestXmlCodec.encodeRestMessage (getOIDsRestMessage(FLAG));
				
			} else {
				
				logger.error (path [1] + " is an unknown marking flag!");
							
				this.rms = new RestMessage (RestKeyword.AllOIDs);
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription (path [1] + " is an unknown marking flag!");
				return RestXmlCodec.encodeRestMessage (this.rms);
				
			}
			
		} 
		
		// filter by repository ID -- return all fitting OIDs 
		if("fromRepositoryID".equals(path[0])) {

			if (path.length < 2)
				throw new NotEnoughParametersException ("This method used with first parameter \"fromRepositoryID\" needs a second parameter to specify the repository ID.");			
			
			
			try {
				repositoryID = new BigDecimal (path [1]);
			} catch (NumberFormatException ex) {
				
				logger.error (path [1] + " is NOT a number and cannot be the expected repository ID!");
				
				this.rms = new RestMessage (RestKeyword.AllOIDs);
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription (path [1] + " is NOT a number and cannot be the expected repository ID!");

				return RestXmlCodec.encodeRestMessage (this.rms);
			}
			
			if(path.length < 4) {
							
				// return all from a repository ID
			    return RestXmlCodec.encodeRestMessage (getOIDsRestMessage(FROMREPO));
			    
			} else {
				
				// almost same code as above with markedAs only !!!
				
                // filter by marking flag -- return all fitting OIDs
				if ("markedAs".equals(path[2])) {
					
					if (path.length < 2)
						throw new NotEnoughParametersException ("This method used with first parameter \"markedAs\" needs a second parameter to specify the marking flag.");			
					
					if ("test".equals(path[3]) 
						//|| "notTest".equals(path[3])
						//|| "productive".equals(path[3])
						/*|| "weitere Markierung".equals(path[3])*/
					   ) {
						
						strFlag = path[3];
						
						// return all from a repository ID marked with a specific flag
						return RestXmlCodec.encodeRestMessage (getOIDsRestMessage(FROMREPO_AND_FLAG));
						
					} else {
						
						logger.warn (path [3] + " is an unknown marking flag!");
									
						this.rms = new RestMessage (RestKeyword.AllOIDs);
						this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
						this.rms.setStatusDescription (path [3] + " is an unknown marking flag!");
						return RestXmlCodec.encodeRestMessage (this.rms);
						
					}
					
				} else {
					
					logger.warn (path [2] + " is an unknown parameter!");
					
					this.rms = new RestMessage (RestKeyword.AllOIDs);
					this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
					this.rms.setStatusDescription (path [2] + " is an unknown parameter!");
					return RestXmlCodec.encodeRestMessage (this.rms);
				}
				
			}
			
		}
		
		// other convenience filters go HERE
		   // by time of creation, ...		
		
		// return general parameter usage error
		
		logger.warn (path [0] + " is an unknown parameter!");
		
		this.rms = new RestMessage (RestKeyword.AllOIDs);
		this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
		this.rms.setStatusDescription (path [0] + " is an unknown parameter!");
		return RestXmlCodec.encodeRestMessage (this.rms);

	}
	
	/**
	 * @param flag
	 * @return
	 */
	
	protected RestMessage getOIDsRestMessage(int flag) {
		
		this.rms = new RestMessage (RestKeyword.AllOIDs);
		RestEntrySet entrySet = new RestEntrySet ( );

		DBAccessNG dbng = new DBAccessNG ( );
		SingleStatementConnection stmtconn = null;
		
		try {
			
			// fetch and execute specific statement 
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );						
			switch (flag) {
				case ALL:
					stmtconn.loadStatement (SelectFromDB.AllOIDs(stmtconn.connection));	
					break;
				case FLAG:
					if("test".equals(strFlag))stmtconn.loadStatement (SelectFromDB.AllOIDsMarkAsTest(stmtconn.connection));	
					if("notTest".equals(strFlag) || "productive".equals(strFlag))stmtconn.loadStatement (SelectFromDB.AllOIDsMarkAsNotTest(stmtconn.connection));
					if("hasFulltextlink".equals(strFlag))stmtconn.loadStatement (SelectFromDB.AllOIDsMarkAsHasFulltextlink(stmtconn.connection));
					break;					
				case FROMREPO:
					stmtconn.loadStatement (SelectFromDB.AllOIDsFromRepositoryID(stmtconn.connection, repositoryID));	
					break;
				case FROMREPO_AND_FLAG:
					if("test".equals(strFlag))stmtconn.loadStatement (SelectFromDB.AllOIDsFromRepositoryIDMarkAsTest(stmtconn.connection, repositoryID));	
					break;										
			}						
			this.result = stmtconn.execute ( );
			
			// log warnings
			if (this.result.getWarning ( ) != null) {
				for (Throwable warning : result.getWarning ( )) {
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}
			
			// extract oids from db response
			while(this.result.getResultSet ( ).next ( )) {
				entrySet = new RestEntrySet();
				entrySet.addEntry ("oid", Integer.toString (this.result.getResultSet ( ).getInt ("object_id")));	
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
			
			logger.error ("An error occured while processing Get AllOIDs: " + ex.getLocalizedMessage ( ));
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get AllOIDs: " + ex.getLocalizedMessage ( ));
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

		this.rms = new RestMessage (RestKeyword.AllOIDs);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.AllOIDs);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
	
	/**
	 * @throws MethodNotImplementedException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws MethodNotImplementedException {

		this.rms = new RestMessage (RestKeyword.AllOIDs);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		return RestXmlCodec.encodeRestMessage (this.rms);
	}	
}
