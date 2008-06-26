/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.*;
import de.dini.oanetzwerk.codec.*;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Robin Malitz
 *
 */

public class AllOIDs extends 
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (AllOIDs.class);
	
	public static final int ALL = 0;
	public static final int TEST = 1;
	public static final int FROMREPO = 2;
	
	private BigDecimal repositoryID = null;
	
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
			
			if ("test".equals(path[1])) {
				
				// return all marked as "test"
				return RestXmlCodec.encodeRestMessage (getOIDsRestMessage(TEST));
				
			} else {
				
				logger.error (path [1] + " is an unknown marking flag!");
							
				this.rms = new RestMessage (RestKeyword.AllOIDs);
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription (path [1] + " is an unknown marking flag!");
				return RestXmlCodec.encodeRestMessage (this.rms);
				
			}
			
		} 
		
//		 filter by repository ID -- return all fitting OIDs 
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

			// return all from a repository ID
			return RestXmlCodec.encodeRestMessage (getOIDsRestMessage(FROMREPO));
			
		}
		
		// other convenience filters go HERE
		   // by time of creation, ...		
		
		// return general parameter usage error
		
		logger.error (path [0] + " is an unknown parameter!");
		
		this.rms = new RestMessage (RestKeyword.AllOIDs);
		this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
		this.rms.setStatusDescription (path [0] + " is an unknown parameter!");
		return RestXmlCodec.encodeRestMessage (this.rms);

	}

	
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
				case TEST:
					stmtconn.loadStatement (SelectFromDB.AllOIDsMarkAsTest(stmtconn.connection));	
					break;
				case FROMREPO:
					stmtconn.loadStatement (SelectFromDB.AllOIDsFromRepositoryID(stmtconn.connection, repositoryID));	
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
			
			logger.error ("An error occured while processing Get ObjectEntryID: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntryID: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} finally {
			
			if (stmtconn != null) {
				
				try {
					
					stmtconn.close ( );
					stmtconn = null;
					
				} catch (SQLException ex) {
					
					ex.printStackTrace ( );
					logger.error (ex.getLocalizedMessage ( ));
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
