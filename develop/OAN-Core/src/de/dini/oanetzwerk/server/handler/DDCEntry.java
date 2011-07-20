package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

public class DDCEntry  extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {

	private static Logger logger = Logger.getLogger(DDCEntry.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private static final String OIDS_PARAM = "OIDS";

	public DDCEntry() {
		super(DDCEntry.class.getName(), RestKeyword.DDCEntry);
	}

	@Override
	protected String deleteKeyWord(String[] path) throws MethodNotImplementedException, NotEnoughParametersException {

		BigDecimal object_id = null;
		
		if (path.length < 1)
			throw new NotEnoughParametersException("This method needs at least 1 parameter: the internal object ID");

		this.rms = new RestMessage(RestKeyword.DDCEntry);

		try {

			object_id = new BigDecimal(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a numeric value!", ex);

			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");
			return RestXmlCodec.encodeRestMessage(this.rms);
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;
				
		try {
			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection ( );
			
			// assume ddc-category has been specified as second argument, 
			// -> delete only the single oid-category relation
			if (path.length >= 2) {
				
				stmtconn.loadStatement (DBAccessNG.deleteFromDB().DDC_Classification(stmtconn.connection, object_id, path[1], true));
			} 
			// no ddc-category specified, -> delete all ddc-relations for the given oid
			else if (path.length == 1) { 
				
				stmtconn.loadStatement (DBAccessNG.deleteFromDB().DDC_Classification(stmtconn.connection, object_id, true));
			}
			
			
			this.result = stmtconn.execute ( );
					
			if (this.result.getWarning ( ) != null)
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ), warning);
						
			int deleteCount =  this.result.getUpdateCount();
			if (deleteCount == 0)
			{
				this.rms.setStatus (RestStatusEnum.ILLEGAL_ACCESS_ERROR);
				this.rms.setStatusDescription ("The relation could not be deleted, remember, only generated ddc-entries are allowed to be deleted!");
			}
			
//			RestEntrySet res = new RestEntrySet ( );
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Delete DDCEntry: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Delete DDCEntry: " + ex.getLocalizedMessage ( ), ex);
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
	
	@Override
	protected String getKeyWord(String[] path) throws NotEnoughParametersException {
		BigDecimal object_id = null;

		if (path.length < 1)
			throw new NotEnoughParametersException("This method needs at least 1 parameter: the internal object ID");

		this.rms = new RestMessage(RestKeyword.DDCEntry);

		try {

			object_id = new BigDecimal(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a numeric value!", ex);

			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");
			return RestXmlCodec.encodeRestMessage(this.rms);
		}

		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;
		RestEntrySet res;

		try {

			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();

			stmtconn.loadStatement(DBAccessNG.selectFromDB().DDCClassification2(stmtconn.connection, object_id));
			this.result = stmtconn.execute();

			if (this.result.getWarning() != null) {

				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage(), warning);
			}

			boolean foundOne = false;
			while (this.result.getResultSet().next()) {
				foundOne = true;
				res = new RestEntrySet();

				res.addEntry("ddc_category", this.result.getResultSet().getString("DDC_Categorie"));
				res.addEntry("generated", Boolean.toString(this.result.getResultSet().getBoolean("generated")));
				
				this.rms.setStatus(RestStatusEnum.OK);
				this.rms.addEntrySet(res);

			}

			if (!foundOne) {

				this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription("No matching DDC-entry found");
			}

		} catch (SQLException ex) {

			logger.error("An error occured while processing Get DDCEntry: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error("An error occured while processing Get DDCEntry: " + ex.getLocalizedMessage(), ex);
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

			res = null;
			this.result = null;
			dbng = null;
		}		
		
		return RestXmlCodec.encodeRestMessage(this.rms);
	}


	@Override
	protected String putKeyWord(String[] path, String data) throws NotEnoughParametersException {

		BigDecimal object_id = null;

		@SuppressWarnings("unused")
		int number = 0;

		if (path.length < 1)
			throw new NotEnoughParametersException("This method needs at least 1 parameter: the internal object ID");

		try {

			object_id = new BigDecimal(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a number!", ex);

			this.rms = new RestMessage(RestKeyword.DDCEntry);
			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");

			return RestXmlCodec.encodeRestMessage(this.rms);
		}

		this.rms = RestXmlCodec.decodeRestMessage(data);
		List<RestEntrySet> requestEntrySets = this.rms.getListEntrySets();

		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;

		return insertRelation(dbng, stmtconn, requestEntrySets, object_id);
	}
	
	@Override
    protected String postKeyWord(String[] path, String data) throws NotEnoughParametersException, MethodNotImplementedException {
		BigDecimal object_id = null;
		
		if (path.length < 1)
			throw new NotEnoughParametersException("This method needs at least 1 parameter: the internal object ID");

		this.rms = new RestMessage(RestKeyword.DDCEntry);

		try {

			object_id = new BigDecimal(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a numeric value!", ex);

			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");
			return RestXmlCodec.encodeRestMessage(this.rms);
		}
		
		this.rms = RestXmlCodec.decodeRestMessage(data);
		List<RestEntrySet> requestEntrySets = this.rms.getListEntrySets();
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;
		
		try {
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();
			
			// assume ddc-category has been specified as second argument, 
			// -> delete only the single oid-category relation
			if (path.length >= 2) {
				
				// delete all relations first				
				stmtconn.loadStatement (DBAccessNG.deleteFromDB().DDC_Classification(stmtconn.connection, object_id, path[1], true));
				this.result = stmtconn.execute();
				
				
				// insert new relations
				insertRelation(dbng, stmtconn, requestEntrySets, object_id);
			} 
			// no ddc-category specified, -> delete all ddc-relations for the given oid
			else if (path.length == 1) { 
				// delete all relations first
				stmtconn.loadStatement (DBAccessNG.deleteFromDB().DDC_Classification(stmtconn.connection, object_id, true));
				this.result = stmtconn.execute();
				
				// insert new relations
				insertRelation(dbng, stmtconn, requestEntrySets, object_id);
			}
			
			
//			this.result = stmtconn.execute ( );
						
//			if (this.result.getWarning ( ) != null)
//				for (Throwable warning : result.getWarning ( ))
//					logger.warn (warning.getLocalizedMessage ( ), warning);
//						
			
			RestEntrySet res = new RestEntrySet ( );
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Delete DDCEntry: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Delete DDCEntry: " + ex.getLocalizedMessage ( ), ex);
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
	
	private String insertRelation(DBAccessNG dbng, MultipleStatementConnection stmtconn, List<RestEntrySet> requestEntrySets, BigDecimal object_id) {

		String key = "";
		RestEntrySet res = null;
		
		try {
			boolean errorHappended = false;
			if (stmtconn == null)
				stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();

			Iterator<RestEntrySet> itSets = requestEntrySets.iterator();
			Set<String> categories = new HashSet<String>();
			
			while (itSets.hasNext()) {
				res = itSets.next();

				String ddc_category = null;


				Iterator<String> it = res.getKeyIterator();
				
				try {
					while (it.hasNext()) {
						key = it.next();
						if (logger.isDebugEnabled())
							logger.debug("key = " + key);
	
						if (key.equalsIgnoreCase("ddc_category")) {
							ddc_category = res.getValue(key);
						} 
						else
							continue;
						
						
						if (ddc_category != null && ddc_category.trim().length() == 3 ) {
							Integer.parseInt(ddc_category);
							
						} else {
							this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
							this.rms.setListEntrySets(requestEntrySets);
							this.rms.setStatusDescription("Invalid DDC category name supplied!' ('"+ ddc_category +"')");
							res.addEntry("ERROR_OCCURED_HERE", "Invalid DDC category name supplied!' ('"+ ddc_category +"')");
							return RestXmlCodec.encodeRestMessage(this.rms);
						}
						
						categories.add(ddc_category);
					}
				}catch (NumberFormatException e) {
					this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
					this.rms.setListEntrySets(requestEntrySets);
					this.rms.setStatusDescription("Invalid DDC category name supplied!' ('"+ ddc_category +"')");
					res.addEntry("ERROR_OCCURED_HERE", "Invalid DDC category name supplied!' ('"+ ddc_category +"')");
					return RestXmlCodec.encodeRestMessage(this.rms);
				}

				this.rms = new RestMessage(RestKeyword.DDCEntry);

				
				// Prüfen, ob überhaupt Daten übergeben wurden

				if (ddc_category == null) {

					this.rms.setStatus(RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
					this.rms.setListEntrySets(requestEntrySets);
					this.rms.setStatusDescription("No DDC category name supplied!'");
					res.addEntry("ERROR_OCCURED_HERE", "No DDC category name supplied! Please specify a DDC category for the 'ddc_category' key.");
					return RestXmlCodec.encodeRestMessage(this.rms);
				} 				

		
			}
			
			// check if categories exist already, if not create
			for (String category : categories) {
				stmtconn.loadStatement(DBAccessNG.selectFromDB().DDCCategoriesByCategorie(stmtconn.connection, category));
				this.result = stmtconn.execute();
	
				if (this.result.getWarning() != null) {
	
					for (Throwable warning : result.getWarning())
						logger.warn(warning.getLocalizedMessage(), warning);
				}
				
				result.getResultSet().next();
				int row = result.getResultSet().getRow();

				if (row == 0) {

					// create ddc entry first, in order to relate it to the specified oid in the next step

					stmtconn.loadStatement(DBAccessNG.insertIntoDB().DDCCategory(stmtconn.connection, category, "", ""));
					this.result = stmtconn.execute();
		
					if (this.result.getWarning() != null) {
		
						for (Throwable warning : result.getWarning())
							logger.warn(warning.getLocalizedMessage(), warning);
					}
				}
			
				// check if relation exists already
				stmtconn.loadStatement(DBAccessNG.selectFromDB().DDCClassification(stmtconn.connection, object_id, category));
				result = stmtconn.execute();
				
				result.getResultSet().next();
				row = result.getResultSet().getRow();

				// if relation doesn't exist, create it
				if (row == 0) {
				
					// connect categories with md-object
					stmtconn.loadStatement(DBAccessNG.insertIntoDB().DDCClassification(stmtconn.connection, object_id, category, true));
					stmtconn.execute();
				}
			
			}

			stmtconn.connection.commit();
			
			logger.info("DDC-Entries successfully processed!");
			this.rms.setStatus (RestStatusEnum.OK);
			this.rms.setListEntrySets(requestEntrySets);
		}  catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setKeyword(RestKeyword.DDCEntry);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.WRONG_STATEMENT);
			this.rms.setKeyword(RestKeyword.DDCEntry);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} finally {

			if (stmtconn != null) {

				try {

					stmtconn.close();
					stmtconn = null;

				} catch (SQLException ex) {

					logger.error(ex.getLocalizedMessage(), ex);
				}
			}

			res = null;
			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage(this.rms);
		
	}
}
