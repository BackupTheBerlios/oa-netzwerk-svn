package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.sybase.DeleteFromDBSybase;
import de.dini.oanetzwerk.server.database.sybase.InsertIntoDBSybase;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Robin Malitz
 *
 */

public class InterpolatedDDC extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	private static Logger logger = Logger.getLogger (InterpolatedDDC.class);
	public InterpolatedDDC ( ) {

		super (InterpolatedDDC.class.getName ( ), RestKeyword.InterpolatedDDC);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) throws NotEnoughParametersException {
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 1 parameter: the internal object ID");
		
		BigDecimal object_id;
		
		try {
			object_id = new BigDecimal (path [0]);
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!", ex);
			
			this.rms = new RestMessage (RestKeyword.InterpolatedDDC);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;
		
		this.rms = new RestMessage (RestKeyword.InterpolatedDDC);
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().Interpolated_DDC_Classification(stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();
			
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
			
			logger.error ("An error occured while processing Delete InterpolatedDDC: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Delete InterpolatedDDC: " + ex.getLocalizedMessage ( ), ex);
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
			throw new NotEnoughParametersException ("This method needs at least 1 parameter: the internal object ID");
		
		BigDecimal object_id;
		
		try {
			
			object_id = new BigDecimal (path [0]);
			if (object_id.intValue() < 0) {
				
				logger.error (path [0] + " is NOT a valid number for this parameter!");
				
				this.rms = new RestMessage (RestKeyword.InterpolatedDDC);
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription (path [0] + " is NOT a valid number for this parameter!");
				
				return RestXmlCodec.encodeRestMessage (this.rms);				
			}

		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!", ex);
			
			this.rms = new RestMessage (RestKeyword.InterpolatedDDC);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;
		
		try {
			
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			stmtconn.loadStatement (DBAccessNG.selectFromDB().InterpolatedDDCClassification(stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			logWarnings();

			counter = 0;
			
			while (this.result.getResultSet ( ).next ( )) {
				
				counter ++;
				if (logger.isDebugEnabled ( )) 
					logger.debug ("DB returned: \n\tobject_id = " + this.result.getResultSet ( ).getBigDecimal (1));
				
				RestEntrySet entrySet = new RestEntrySet(); 
				entrySet.addEntry ("object_id", this.result.getResultSet ( ).getBigDecimal (1).toPlainString ( ));
				entrySet.addEntry ("ddc_value", this.result.getResultSet ( ).getString (2));
				entrySet.addEntry ("probability", this.result.getResultSet ( ).getBigDecimal (3).toPlainString ( ));
				this.rms.addEntrySet(entrySet);
			}
			
			this.rms.setStatus (RestStatusEnum.OK);
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get InterpolatedDDC: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get InterpolatedDDC: " + ex.getLocalizedMessage ( ), ex);
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
			
			this.result = null;
			dbng = null;
			
			if(counter == 0) {
				this.rms.setStatus (RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription ("No " + RestKeyword.InterpolatedDDC + " found for object_id " + object_id + ".");
			}
		}
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {
		
		this.rms = new RestMessage (RestKeyword.InterpolatedDDC);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("POST method is not implemented for ressource '"+RestKeyword.InterpolatedDDC+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) throws NotEnoughParametersException {
		
		BigDecimal object_id = null;
        String ddc_value = null;
		BigDecimal percentage = null;

		@SuppressWarnings("unused")
		int number = 0;

		if (path.length < 1)
			throw new NotEnoughParametersException(
					"This method needs at least 1 parameter: the internal object ID");

		try {

			object_id = new BigDecimal(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a number!", ex);

			this.rms = new RestMessage(RestKeyword.InterpolatedDDC);
			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");

			return RestXmlCodec.encodeRestMessage(this.rms);
		}

		this.rms = RestXmlCodec.decodeRestMessage(data);
		RestEntrySet res = null;

		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
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

						if (key.equalsIgnoreCase("probability"))
							percentage = new BigDecimal(res.getValue(key));
						else if (key.equalsIgnoreCase("ddc_value"))
							ddc_value = res.getValue(key);
						else
							continue;
					}

				this.rms = new RestMessage(RestKeyword.InterpolatedDDC);
				
				// Prüfen, ob überhaupt Daten übergeben wurden
				if ((object_id == null) || (percentage == null) || (ddc_value == null)) {
					// Fehlermeldung generieren und abbrechen
					this.rms = new RestMessage(RestKeyword.InterpolatedDDC);
					this.rms.setStatus(RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
					this.rms.setStatusDescription("PUT /InterpolatedDDC/ needs 2 entries in body: probability and ddc_value");
					return RestXmlCodec.encodeRestMessage(this.rms);
				}

				res = new RestEntrySet();

				stmtconn.loadStatement(DBAccessNG.insertIntoDB().InterpolatedDDCClassification(
						stmtconn.connection, object_id, ddc_value, percentage));
				this.result = stmtconn.execute();
				
				logWarnings();
				
				if (this.result.getUpdateCount() < 1) {
					errorHappended = true;
				
					// warn, error, rollback, nothing????
				}
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

				logger.error(res.getValue(key) + " is NOT a number!", ex);
				this.rms = new RestMessage(RestKeyword.InterpolatedDDC);
				this.rms.setStatusDescription(res.getValue(key) + " is NOT a number!");
				return RestXmlCodec.encodeRestMessage(this.rms);

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus(RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} finally {

			if (stmtconn != null) {

				try {

					stmtconn.close();
					stmtconn = null;

				} catch (SQLException ex) {

					logger.error(ex.getLocalizedMessage ( ), ex);
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
