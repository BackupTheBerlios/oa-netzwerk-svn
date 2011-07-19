/**
 * 
 */
package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.sybase.InsertIntoDBSybase;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.server.database.sybase.UpdateInDBSybase;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 * @author Sammy David
 */

public class Repository extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {

	private static final String PATH_MARKEDTODAY = "markedtoday";
	private static final String PATH_HARVESTEDTODAY = "harvestedtoday";
	/**
	 * 
	 */

	private static Logger logger = Logger.getLogger(Repository.class);

	/**
	 * 
	 */

	public Repository() {

		super(Repository.class.getName(), RestKeyword.Repository);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */

	@Override
	protected String deleteKeyWord(String[] path) {

		this.rms = new RestMessage(RestKeyword.Repository);
		this.rms.setStatus(RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("DELETE-method is not implemented for ressource '" + RestKeyword.Repository + "'.");
		return RestXmlCodec.encodeRestMessage(this.rms);
	}

	/**
	 * @throws NotEnoughParametersException
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 * 
	 */

	@Override
	protected String getKeyWord(String[] path) throws NotEnoughParametersException {

		BigDecimal repositoryID = null;

		if (path.length == 1) {

			try {

				repositoryID = new BigDecimal(path[0]);

			} catch (NumberFormatException ex) {

				logger.error(path[0] + " is NOT a number!");

				this.rms = new RestMessage(RestKeyword.Repository);
				this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription(path[0] + " is NOT a number!");

				return RestXmlCodec.encodeRestMessage(this.rms);
			}
		}

		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;

		try {

			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();

			if (repositoryID != null) {

				stmtconn.loadStatement(DBAccessNG.selectFromDB().Repository(stmtconn.connection, repositoryID));

			} else {

				stmtconn.loadStatement(DBAccessNG.selectFromDB().Repository(stmtconn.connection));
			}

			this.result = stmtconn.execute();

			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage());

			if (repositoryID != null) {

				if (this.result.getResultSet().next()) {

					if (logger.isDebugEnabled())
						logger.debug("DB returned: \n\tRepository name = " + this.result.getResultSet().getString("name")
						                + "\n\tRepository URL = " + this.result.getResultSet().getString("url")
						                + "\n\tRepository OAI-URL = " + this.result.getResultSet().getString("oai_url")
						                + "\n\tTest Data = " + this.result.getResultSet().getBoolean("test_data") + "\n\tAmount = "
						                + this.result.getResultSet().getInt("harvest_amount") + "\n\tSleep = "
						                + this.result.getResultSet().getInt("harvest_pause"));

					RestEntrySet res = new RestEntrySet();

					res.addEntry("name", this.result.getResultSet().getString("name"));
					res.addEntry("url", this.result.getResultSet().getString("url"));
					res.addEntry("oai_url", this.result.getResultSet().getString("oai_url"));
					res.addEntry("test_data", new Boolean(this.result.getResultSet().getBoolean("test_data")).toString());
					res.addEntry("harvest_amount", Integer.toString(this.result.getResultSet().getInt("harvest_amount")));
					res.addEntry("harvest_pause", Integer.toString(this.result.getResultSet().getInt("harvest_pause")));
					res.addEntry("active", new Boolean(this.result.getResultSet().getBoolean("active")).toString());

					Date lastFullHarvestBegin = this.result.getResultSet().getDate("last_full_harvest_begin");
					res.addEntry("last_full_harvest_begin", lastFullHarvestBegin == null ? null : lastFullHarvestBegin.toString());
					Date lastMarkerEraserBegin = this.result.getResultSet().getDate("last_markereraser_begin");
					res.addEntry("last_markereraser_begin", lastMarkerEraserBegin == null ? null : lastMarkerEraserBegin.toString());
					this.rms.setStatus(RestStatusEnum.OK);
					this.rms.addEntrySet(res);

				} else {

					logger.warn("Nothing found!");
					this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
					this.rms.setStatusDescription("No matching Repository found");
				}

			} else {

				this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				RestEntrySet res;

				while (this.result.getResultSet().next()) {

					this.rms.setStatus(RestStatusEnum.OK);

					if (logger.isDebugEnabled())
						logger.debug("DB returned: \n\tRepository name = " + this.result.getResultSet().getString("name")
						                + "\n\tRepository URL = " + this.result.getResultSet().getString("url")
						                + "\n\tRepository OAI-URL = " + this.result.getResultSet().getString("oai_url")
						                + "\n\tTest Data = " + this.result.getResultSet().getBoolean("test_data") + "\n\tAmount = "
						                + this.result.getResultSet().getInt("harvest_amount") + "\n\tSleep = "
						                + this.result.getResultSet().getInt("harvest_pause"));

					res = new RestEntrySet();

					res.addEntry("repository_id", this.result.getResultSet().getBigDecimal("repository_id").toPlainString());
					res.addEntry("name", this.result.getResultSet().getString("name"));
					res.addEntry("url", this.result.getResultSet().getString("url"));
					res.addEntry("oai_url", this.result.getResultSet().getString("oai_url"));
					res.addEntry("test_data", new Boolean(this.result.getResultSet().getBoolean("test_data")).toString());
					res.addEntry("harvest_amount", Integer.toString(this.result.getResultSet().getInt("harvest_amount")));
					res.addEntry("harvest_pause", Integer.toString(this.result.getResultSet().getInt("harvest_pause")));
					res.addEntry("active", new Boolean(this.result.getResultSet().getBoolean("active")).toString());
					Date lastFullHarvestBegin = this.result.getResultSet().getDate("last_full_harvest_begin");
					res.addEntry("last_full_harvest_begin", lastFullHarvestBegin == null ? null : lastFullHarvestBegin.toString());
					Date lastMarkerEraserBegin = this.result.getResultSet().getDate("last_markereraser_begin");
					res.addEntry("last_markereraser_begin", lastMarkerEraserBegin == null ? null : lastMarkerEraserBegin.toString());
					
					this.rms.addEntrySet(res);
				}

				if (this.rms.getStatus().equals(RestStatusEnum.NO_OBJECT_FOUND_ERROR)) {

					this.rms.setStatusDescription("No Repositories found");
					logger.warn("No Repositories found");
				}
			}

		} catch (SQLException ex) {

			logger.error("An error occured while processing Get Repository: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error("An error occured while processing Get Repository: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.WRONG_STATEMENT);
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

			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage(this.rms);
	}

	/**
	 * @throws NotEnoughParametersException
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[],
	 *      java.lang.String)
	 */

	@Override
	protected String postKeyWord(String[] path, String data) throws NotEnoughParametersException {

		if (path.length < 1)
			throw new NotEnoughParametersException("This method needs at least 2 parameters: the keyword and the repository id!");

		BigDecimal repositoryID;

		try {

			repositoryID = new BigDecimal(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a number!");

			this.rms = new RestMessage(RestKeyword.Repository);
			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");

			return RestXmlCodec.encodeRestMessage(this.rms);
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;

		
		
		// case: update repository info
		if (path.length == 1) {
	
			RestMessage msg = RestXmlCodec.decodeRestMessage(data);
	
			String name 		= null;
			String owner 		= "";
			String ownerEmail  	= "";
			String url 			= null;
			String oaiUrl 		= null;
			String harvestAmount = "10";
			String harvestPause = "5000";
			boolean testData 	= true;
			boolean listRecords = true;
			boolean active 		= false;
	
			List<RestEntrySet> entries = msg.getListEntrySets();
			Iterator<RestEntrySet> iterator = entries.iterator();
			RestEntrySet res;
	
			String key;
			while (iterator.hasNext()) {
				res = iterator.next();
	
				Iterator<String> it = res.getKeyIterator();
				while (it.hasNext()) {
					key = it.next();
	
					if (key.equalsIgnoreCase("name")) {
						name = res.getValue(key);
					} else if (key.equalsIgnoreCase("repoId")) {
						url = res.getValue(key);
					} else if (key.equalsIgnoreCase("url")) {
						url = res.getValue(key);
					} else if (key.equalsIgnoreCase("oaiUrl")) {
						oaiUrl = res.getValue(key);
					} else if (key.equalsIgnoreCase("owner")) {
						owner = res.getValue(key);
					} else if (key.equalsIgnoreCase("ownerEmail")) {
						ownerEmail = res.getValue(key);
					} else if (key.equalsIgnoreCase("harvestAmount")) {
						harvestAmount = res.getValue(key);
					} else if (key.equalsIgnoreCase("harvestPause")) {
						harvestPause = res.getValue(key);
					} else if (key.equalsIgnoreCase("testData")) {
						testData = Boolean.parseBoolean(res.getValue(key));
					} else if (key.equalsIgnoreCase("active")) {
						active = Boolean.parseBoolean(res.getValue(key));
					} else if (key.equalsIgnoreCase("listRecords")) {
						listRecords = Boolean.parseBoolean(res.getValue(key));
					}
	
				}
			}
	
			try {
				if (name == null || name.length() == 0 || url == null || url.length() == 0 || oaiUrl == null || oaiUrl.length() == 0) {
					logger.error("An error occured while processing Put Repository: Name, Url and OAI-Url must be provided!");
					this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
					this.rms.setStatusDescription("Name, Url and OAI-Url must be provided!");
					return RestXmlCodec.encodeRestMessage(this.rms);
				}
	
				stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
				stmtconn.loadStatement(DBAccessNG.updateInDB().Repository(stmtconn.connection, repositoryID, name, url, oaiUrl, owner, ownerEmail,
				                Integer.parseInt(harvestAmount), Integer.parseInt(harvestPause), listRecords, testData, active));
	
				this.result = stmtconn.execute();
	
				if (this.result.getWarning() != null)
					for (Throwable warning : result.getWarning())
						logger.warn(warning.getLocalizedMessage(), warning);
	
			} catch (SQLException ex) {
	
				logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
				this.rms.setStatus(RestStatusEnum.SQL_ERROR);
				this.rms.setStatusDescription(ex.getLocalizedMessage());
	
			} catch (WrongStatementException ex) {
	
				logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
				this.rms.setStatus(RestStatusEnum.WRONG_STATEMENT);
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
	
				this.result = null;
				dbng = null;
			}
	
			return RestXmlCodec.encodeRestMessage(this.rms);
		}
	
		// case: setting active property of reposiotry
		if (path.length == 2 && "deactivate".equals(path[1])) {
			
			RestMessage msg = RestXmlCodec.decodeRestMessage(data);
			boolean active 		= false;
		
			try {
	
				stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
				stmtconn.loadStatement(DBAccessNG.updateInDB().Repository(stmtconn.connection, repositoryID, false));
	
				this.result = stmtconn.execute();
	
				if (this.result.getWarning() != null)
					for (Throwable warning : result.getWarning())
						logger.warn(warning.getLocalizedMessage(), warning);
	
			} catch (SQLException ex) {
	
				logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
				this.rms.setStatus(RestStatusEnum.SQL_ERROR);
				this.rms.setStatusDescription(ex.getLocalizedMessage());
	
			} catch (WrongStatementException ex) {
	
				logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
				this.rms.setStatus(RestStatusEnum.WRONG_STATEMENT);
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
	
				this.result = null;
				dbng = null;
			}
	
			return RestXmlCodec.encodeRestMessage(this.rms);
		}
		
		
		
		// case: check for /harvestedtoday/ and /markedtoday/ request
		if (path.length == 2 && (path[1].equals(PATH_HARVESTEDTODAY) || path[1].equals(PATH_MARKEDTODAY))) {
			if (data != null) {
				logger.info("POST-Request to Repository/" + path[0]
				                + "/harvestedtoday/ or /markedtoday/ should not contain any data in body! Ignoring data: \n" + data);
			}

			try {

				stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();

				if (path[1].equals(PATH_HARVESTEDTODAY)) {

					stmtconn.loadStatement(DBAccessNG.updateInDB().Repository(stmtconn.connection, repositoryID, HelperMethods.today(),
					                "last_full_harvest_begin"));

				} else if (path[1].equals(PATH_MARKEDTODAY)) {

					stmtconn.loadStatement(DBAccessNG.updateInDB().Repository(stmtconn.connection, repositoryID, HelperMethods.today(),
					                "last_markereraser_begin"));
				}

				this.result = stmtconn.execute();

				if (this.result.getWarning() != null)
					for (Throwable warning : result.getWarning())
						logger.warn(warning.getLocalizedMessage(), warning);

			} catch (SQLException ex) {

				logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
				this.rms.setStatus(RestStatusEnum.SQL_ERROR);
				this.rms.setStatusDescription(ex.getLocalizedMessage());

			} catch (WrongStatementException ex) {

				logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
				this.rms.setStatus(RestStatusEnum.WRONG_STATEMENT);
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

				this.result = null;
				dbng = null;
			}
		}

		return RestXmlCodec.encodeRestMessage(this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[],
	 *      java.lang.String)
	 */

	@Override
	protected String putKeyWord(String[] path, String data) {

		if (data == null) {
			logger.warn("Request contained no data section. Cannot proceed!");
		}

		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;

		RestMessage msg = RestXmlCodec.decodeRestMessage(data);

		String name 		= null;
		String owner 		= "";
		String ownerEmail  	= "";
		String url 			= null;
		String oaiUrl 		= null;
		String harvestAmount = "10";
		String harvestPause = "5000";
		boolean testData 	= true;
		boolean listRecords = true;
		boolean active 		= false;

		List<RestEntrySet> entries = msg.getListEntrySets();
		Iterator<RestEntrySet> iterator = entries.iterator();
		RestEntrySet res;

		String key;
		while (iterator.hasNext()) {
			res = iterator.next();

			Iterator<String> it = res.getKeyIterator();
			while (it.hasNext()) {
				key = it.next();

				if (key.equalsIgnoreCase("name")) {
					name = res.getValue(key);
				} else if (key.equalsIgnoreCase("url")) {
					url = res.getValue(key);
				} else if (key.equalsIgnoreCase("oaiUrl")) {
					oaiUrl = res.getValue(key);
				} else if (key.equalsIgnoreCase("owner")) {
					owner = res.getValue(key);
				} else if (key.equalsIgnoreCase("ownerEmail")) {
					ownerEmail = res.getValue(key);
				} else if (key.equalsIgnoreCase("harvestAmount")) {
					harvestAmount = res.getValue(key);
				} else if (key.equalsIgnoreCase("harvestPause")) {
					harvestPause = res.getValue(key);
				} else if (key.equalsIgnoreCase("testData")) {
					testData = Boolean.parseBoolean(res.getValue(key));
				} else if (key.equalsIgnoreCase("active")) {
					active = Boolean.parseBoolean(res.getValue(key));
				} else if (key.equalsIgnoreCase("listRecords")) {
					listRecords = Boolean.parseBoolean(res.getValue(key));
				}

			}
		}

		try {
			if (name == null || name.length() == 0 || url == null || url.length() == 0 || oaiUrl == null || oaiUrl.length() == 0) {
				logger.error("An error occured while processing Put Repository: Name, Url and OAI-Url must be provided!");
				this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription("Name, Url and OAI-Url must be provided!");
				return RestXmlCodec.encodeRestMessage(this.rms);
			}

			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
			stmtconn.loadStatement(DBAccessNG.insertIntoDB().Repository(stmtconn.connection, name, url, oaiUrl, owner, ownerEmail,
			                Integer.parseInt(harvestAmount), Integer.parseInt(harvestPause), listRecords, testData, active));

			this.result = stmtconn.execute();

			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage(), warning);

		} catch (SQLException ex) {

			logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error("An error occured while processing Post Repository: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.WRONG_STATEMENT);
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

			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage(this.rms);

		// this.rms = new RestMessage (RestKeyword.Repository);
		// this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		// this.rms.setStatusDescription("PUT method is not implemented for ressource '"+RestKeyword.Repository+"'.");
		// return RestXmlCodec.encodeRestMessage (this.rms);
	}
} // end of class
