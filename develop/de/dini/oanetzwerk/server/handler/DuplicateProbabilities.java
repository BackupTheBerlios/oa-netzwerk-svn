package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Iterator;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.DeleteFromDB;
import de.dini.oanetzwerk.server.database.InsertIntoDB;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Manuel Klatt-Kafemann
 *
 */

public class DuplicateProbabilities extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	/**
	 * 
	 */
	
	public DuplicateProbabilities ( ) {

		super (DuplicateProbabilities.class.getName ( ), RestKeyword.DuplicateProbabilities);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the internal object ID");
		
		BigDecimal object_id;
		
		try {
			object_id = new BigDecimal (path [0]);
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.DuplicateProbabilities);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG ( );
		MultipleStatementConnection stmtconn = null;
		
		this.rms = new RestMessage (RestKeyword.DuplicateProbabilities);
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (DeleteFromDB.DuplicatePossibilities(stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getUpdateCount ( ) < 1) {
				// da eh nichts gelöscht wurde, entsprechende Daten wieder in Ursprungszustand versetzen
				stmtconn.rollback();
			} else {
				// gelöschte Daten als solche committen
				stmtconn.commit ( );
			}
			
			RestEntrySet res = new RestEntrySet ( );
			
			res.addEntry ("object_id", object_id.toPlainString ( ));
			
			this.rms.addEntrySet (res);
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Delete DuplicateProbabilities: " + ex.getLocalizedMessage ( ), ex);
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Delete DuplicateProbabilities: " + ex.getLocalizedMessage ( ), ex);
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
					logger.error (ex.getLocalizedMessage ( ), ex);
				}
			}
			
			this.result = null;
			dbng = null;
		}
				
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @throws NotEnoughParametersException 
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String getKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		int counter = 0;
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the Service ID");
		
		BigDecimal object_id;
		
		try {
			
			object_id = new BigDecimal (path [0]);
			if (object_id.intValue() < 0) {
				
				logger.error (path [0] + " is NOT a valid number for this parameter!");
				
				this.rms = new RestMessage (RestKeyword.DuplicateProbabilities);
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription (path [0] + " is NOT a valid number for this parameter!");
				
				return RestXmlCodec.encodeRestMessage (this.rms);				
				
			}

		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.DuplicateProbabilities);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG ( );
		SingleStatementConnection stmtconn = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			stmtconn.loadStatement (SelectFromDB.DuplicateProbabilities(stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			
			
			if (this.result.getWarning ( ) != null) {
				for (Throwable warning : result.getWarning ( )) {
					logger.warn (warning.getLocalizedMessage ( ));
				}
			}

			counter = 0;
			
			while (this.result.getResultSet ( ).next ( )) {
				
				counter ++;
				if (logger.isDebugEnabled ( )) 
					logger.debug ("DB returned: \n\tobject_id = " + this.result.getResultSet ( ).getBigDecimal (1));
				
				RestEntrySet entrySet = new RestEntrySet(); 
				entrySet.addEntry ("referToOID", this.result.getResultSet ( ).getBigDecimal (2).toPlainString ( ));
				entrySet.addEntry ("probability", this.result.getResultSet ( ).getBigDecimal (3).toPlainString ( ));
				entrySet.addEntry ("number", (new Integer(counter)).toString());
				this.rms.addEntrySet(entrySet);
			}
			
			this.rms.setStatus (RestStatusEnum.OK);
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get DuplicatePossibilities: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get DuplicatePossibilities: " + ex.getLocalizedMessage ( ));
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
			
			this.result = null;
			dbng = null;
		}
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {
		
		this.rms = new RestMessage (RestKeyword.DuplicateProbabilities);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) throws NotEnoughParametersException {
		
		BigDecimal object_id = null;
		BigDecimal duplicate_id = null;
		BigDecimal percentage = null;
		int number = 0;

		if (path.length < 1)
			throw new NotEnoughParametersException(
					"This method needs at least 2 parameters: the keyword and the Service ID");

		try {

			object_id = new BigDecimal(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a number!");

			this.rms = new RestMessage(RestKeyword.DuplicateProbabilities);
			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");

			return RestXmlCodec.encodeRestMessage(this.rms);
		}

		this.rms = RestXmlCodec.decodeRestMessage(data);
		RestEntrySet res = null;

		DBAccessNG dbng = new DBAccessNG();
		MultipleStatementConnection stmtconn = null;
		
		String key = "";

		try {
			boolean errorHappended = false; 
			stmtconn = (MultipleStatementConnection) dbng
					.getMultipleStatementConnection();

			Iterator<RestEntrySet> itSets = this.rms.getListEntrySets()
					.iterator();
			while (itSets.hasNext()) {
				res = itSets.next();

				Iterator<String> it = res.getKeyIterator();
				

					while (it.hasNext()) {
						key = it.next();

						if (logger.isDebugEnabled())
							logger.debug("key = " + key);

						if (key.equalsIgnoreCase("referToOID"))
							duplicate_id = new BigDecimal(res.getValue(key));

						else if (key.equalsIgnoreCase("probability"))
							percentage = new BigDecimal(res.getValue(key));

						else if (key.equalsIgnoreCase("number"))
							number = new Integer(res.getValue(key)).intValue();

						else
							continue;
					}

				this.rms = new RestMessage(RestKeyword.DuplicateProbabilities);

				// Prüfen, ob überhaupt Daten übergeben wurden
				if ((object_id == null) | (duplicate_id == null)
						| (percentage == null)) {
					// Fehlermeldung generieren und abbrechen
					this.rms = new RestMessage(
							RestKeyword.DuplicateProbabilities);
					this.rms
							.setStatus(RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
					this.rms
							.setStatusDescription("PUT /DuplicateProbabilities/ needs 2 entries in body: referToOID and probability");
					return RestXmlCodec.encodeRestMessage(this.rms);
				}

				res = new RestEntrySet();

				stmtconn.loadStatement(InsertIntoDB.DuplicatePossibilities(
						stmtconn.connection, object_id, duplicate_id,
						percentage));
				this.result = stmtconn.execute();

				if (this.result.getUpdateCount() < 1) {
					errorHappended = true;
				
					// warn, error, rollback, nothing????
				}
//				logger.debug("GetWarnings: " + stmtconn.connection.getWarnings().getNextWarning());

			}
			if (errorHappended == false) 
				stmtconn.commit();
			else {
				stmtconn.rollback();
				this.rms.setStatus(RestStatusEnum.SQL_ERROR);
				this.rms.setStatusDescription("could not be inserted");
			}
			this.rms.setStatus (RestStatusEnum.OK);
			
		} catch (NumberFormatException ex) {

				logger.error(res.getValue(key) + " is NOT a number!");
				this.rms = new RestMessage(
						RestKeyword.DuplicateProbabilities);
				this.rms.setStatusDescription(res.getValue(key)
						+ " is NOT a number!");
				return RestXmlCodec.encodeRestMessage(this.rms);

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage());
			ex.printStackTrace();
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage());
			ex.printStackTrace();
			this.rms.setStatus(RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} finally {

			if (stmtconn != null) {

				try {

					stmtconn.close();
					stmtconn = null;

				} catch (SQLException ex) {

					ex.printStackTrace();
					logger.error(ex.getLocalizedMessage());
				}
			}

			this.rms.addEntrySet(res);
			res = null;
			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage(this.rms);
	}
}
