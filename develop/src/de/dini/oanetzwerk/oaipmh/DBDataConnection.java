package de.dini.oanetzwerk.oaipmh;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.StatementConnection;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 * 
 */

class DBDataConnection extends DataConnection {

	/**
	 * 
	 */

	private static Logger logger = Logger.getLogger(DBDataConnection.class);

	/**
	 * 
	 */

	private DBAccessNG dbng;

	/**
	 * 
	 */

	public DBDataConnection() {

		this.dbng = new DBAccessNG();
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getEarliestDataStamp()
	 */

	@Override
	public String getEarliestDataStamp() {

		logger.info("br1");
		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;
		String eartliestDate = "1646-07-01";

		try {
			logger.info("br2");
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.OAIGetOldestDate(stmtconn.connection));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}
			logger.info("br3");
			if (queryresult.getResultSet().next())
				eartliestDate = queryresult.getResultSet().getDate(1).toString();
			logger.info("br4");
		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return eartliestDate;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#existsIdentifier(java.lang.String)
	 */

	@Override
	public boolean existsIdentifier(String identifier) {

		String id[] = identifier.split(":");

		if (id.length != 3)
			return false;

		if (!id[0].equals("oai") || !id[1].equals("oanet.de"))
			return false;

		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			return false;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.ObjectEntry(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			if (queryresult.getResultSet().next()) {

				if (!queryresult.getResultSet().getBigDecimal(1).equals(null) && queryresult.getResultSet().getBigDecimal(1).equals(bdID))
					return true;
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return false;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getSets()
	 */

	@Override
	public ArrayList<String[]> getSets() {

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		ArrayList<String[]> setList = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.OAIListSets(stmtconn.connection));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			setList = new ArrayList<String[]>();

			while (queryresult.getResultSet().next()) {

				setList.add(new String[] { queryresult.getResultSet().getString(1), queryresult.getResultSet().getString(2) });
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return setList;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getClassifications()
	 */

	@Override
	public ArrayList<String> getClassifications(String identifier) {

		ArrayList<String> classifications = new ArrayList<String>();
		String id[] = identifier.split(":");

		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

			return classifications;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.DDCClassification(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				classifications.add("ddc:" + queryresult.getResultSet().getString(2));
			}

			stmtconn.close();

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.DINISetClassification(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				classifications.add("dini:" + queryresult.getResultSet().getString(2));
				logger.debug(queryresult.getResultSet().getString(1) + " / " + queryresult.getResultSet().getString(2));
			}

			stmtconn.close();

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.DNBClassification(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				classifications.add("dnb:" + queryresult.getResultSet().getString(2));
			}

			stmtconn.close();

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.OtherClassification(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				classifications.add("other:" + queryresult.getResultSet().getString(2));
				logger.debug(queryresult.getResultSet().getString(1) + " / " + queryresult.getResultSet().getString(2));
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return classifications;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getDateStamp()
	 */

	@Override
	public String getDateStamp(String identifier) {

		String date = "1646-07-01";
		String id[] = identifier.split(":");
		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

			return date;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.ObjectEntry(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			if (queryresult.getResultSet().next()) {

				date = queryresult.getResultSet().getDate("repository_datestamp").toString();
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return date;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getCreators(java.lang.String)
	 */

	@Override
	public ArrayList<String> getCreators(String identifier) {

		ArrayList<String> creators = new ArrayList<String>();
		String id[] = identifier.split(":");

		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

			return creators;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.Authors(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				// TODO: more generic please, add email and institution as well,
				// when not null

				if (queryresult.getResultSet().getString(4) == null || queryresult.getResultSet().getString(4).equals(""))
					creators.add(queryresult.getResultSet().getString(2) + " " + queryresult.getResultSet().getString(3));

				else
					creators.add(queryresult.getResultSet().getString(4) + " " + queryresult.getResultSet().getString(2) + " "
							+ queryresult.getResultSet().getString(3));
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return creators;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getSubjects(java.lang.String)
	 */

	@Override
	public ArrayList<String> getSubjects(String identifier) {

		ArrayList<String> subjects = new ArrayList<String>();
		String id[] = identifier.split(":");

		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

			return subjects;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.Keywords(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				subjects.add(queryresult.getResultSet().getString(1));
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return subjects;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getTitles(java.lang.String)
	 */

	@Override
	public ArrayList<String> getTitles(String identifier) {

		ArrayList<String> titles = new ArrayList<String>();
		String id[] = identifier.split(":");

		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

			return titles;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.Title(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				titles.add(queryresult.getResultSet().getString(1));
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return titles;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getDates(java.lang.String)
	 */

	@Override
	public ArrayList<String> getDates(String identifier) {

		ArrayList<String> dates = new ArrayList<String>();
		String id[] = identifier.split(":");

		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

			return dates;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.DateValues(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				dates.add(queryresult.getResultSet().getDate("value").toString());
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return dates;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getDescriptions(java.lang.String)
	 */

	@Override
	public ArrayList<String> getDescriptions(String identifier) {

		ArrayList<String> descriptions = new ArrayList<String>();
		String id[] = identifier.split(":");

		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

			return descriptions;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.Description(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				descriptions.add(queryresult.getResultSet().getString("abstract"));
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return descriptions;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getFormats(java.lang.String)
	 */

	@Override
	public ArrayList<String> getFormats(String identifier) {

		ArrayList<String> formats = new ArrayList<String>();
		String id[] = identifier.split(":");

		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

			return formats;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.Format(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				formats.add(queryresult.getResultSet().getString("schema_f"));
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return formats;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getIdentifiers(java.lang.String)
	 */

	@Override
	public ArrayList<String> getIdentifiers(String identifier) {

		ArrayList<String> identifieres = new ArrayList<String>();
		String id[] = identifier.split(":");

		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

			return identifieres;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.Identifier(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				identifieres.add(queryresult.getResultSet().getString("identifier"));
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return identifieres;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getLanguages(java.lang.String)
	 */

	@Override
	public ArrayList<String> getLanguages(String identifier) {

		ArrayList<String> languages = new ArrayList<String>();
		String id[] = identifier.split(":");

		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

			return languages;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.Languages(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				languages.add(queryresult.getResultSet().getString("language"));
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return languages;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getPublishers(java.lang.String)
	 */

	@Override
	public ArrayList<String> getPublishers(String identifier) {

		ArrayList<String> publishers = new ArrayList<String>();
		String id[] = identifier.split(":");

		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

			return publishers;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.Publisher(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				publishers.add(queryresult.getResultSet().getString("name"));
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return publishers;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getTypes(java.lang.String)
	 */

	@Override
	public ArrayList<String> getTypes(String identifier) {

		ArrayList<String> types = new ArrayList<String>();
		String id[] = identifier.split(":");

		BigDecimal bdID;

		try {

			bdID = new BigDecimal(id[2]);

		} catch (NumberFormatException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

			return types;
		}

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.TypeValues(stmtconn.connection, bdID));

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			while (queryresult.getResultSet().next()) {

				types.add(queryresult.getResultSet().getString("value"));
			}

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return types;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getIdentifier(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */

	public LinkedList<Record> getIdentifierList(String from, String until, String set, BigInteger idOffset, int maxResults) {

		LinkedList<Record> recordList = new LinkedList<Record>();
		Date fromDate;
		Date untilDate;

		SingleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		try {

			if (from != null && !from.equals(""))
				fromDate = Date.valueOf(from);

			else
				fromDate = null;

			if (until != null && !until.equals(""))
				untilDate = Date.valueOf(until);

			else
				untilDate = null;

			if (fromDate != null && untilDate != null)

				if (untilDate.before(fromDate))
					throw new IllegalArgumentException("The 'until' value must not be before the 'from' value!");

		} catch (IllegalArgumentException ex) {

			logger.warn(ex.getLocalizedMessage());
			throw ex;
		}

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();

			stmtconn.loadStatement(SelectFromDB.AllOIDsByDate(stmtconn.connection, fromDate, untilDate, set, idOffset, maxResults, false));
			QueryResult idQueryResult = stmtconn.execute();
			
			List<BigDecimal> ids = new ArrayList<BigDecimal>();
			
			
			while (idQueryResult.getResultSet().next()) {
				ids.add(idQueryResult.getResultSet().getBigDecimal("object_id"));
			}
			
			logger.info("ID-Query results: " + ids.size());

			closeStatementConnection(stmtconn);
			
			// skip final query, if there have no ids been found for the query
			if (ids == null || ids.size() == 0) {
				return recordList;
			}
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.OAIListSetsbyID(stmtconn.connection, set, fromDate, untilDate, ids));			

			queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			Record record = new Record();
			BigDecimal oid = new BigDecimal(0);
			DCHeader h = record.getHeader();
			DCMetaData m = record.getMetaData();
			
			
//			logger.info("ID-Query fetches: " + queryresult.getResultSet().getFetchSize());
			
			
			while (queryresult.getResultSet().next()) {

				
				ResultSet rs = queryresult.getResultSet();
				oid = rs.getBigDecimal("object_id");

				if (!record.getHeader().getIdentifier().equals(oid.toPlainString())) {

					// new id, save last record

					if (recordList.size() == maxResults)
					{
						// enough results fetched, skip the rest
						break;
					}

					// if id of the last record is not empty (first object), save the last record
					if (!record.getHeader().getIdentifier().equals("")) {

						recordList.add(record);
						record = new Record();
						h = record.getHeader();
						m = record.getMetaData();
					}

					h.setIdentifier(safeValueOf(rs.getBigDecimal("object_id")));
					h.setDatestamp(safeValueOf(rs.getDate("repository_datestamp")));


					if (StringUtils.isNotEmpty(rs.getString("ddc")) )
						h.getSet().add(notNullValue("ddc:" + rs.getString("ddc")));
					if (StringUtils.isNotEmpty(rs.getString("dnb")) )
						h.getSet().add(notNullValue("dnb:" + rs.getString("dnb")));
					if (StringUtils.isNotEmpty(rs.getString("dini")) )
						h.getSet().add(notNullValue(rs.getString("dini")));
					
				} else {

					// additional data, --> aggregate
					if (StringUtils.isNotEmpty(rs.getString("ddc")) ) {
						if (!h.getSet().contains("ddc:" + rs.getString("ddc"))) {
							h.getSet().add(notNullValue("ddc:" + rs.getString("ddc")));
						}
					}
					if (StringUtils.isNotEmpty(rs.getString("dnb")) ) {
						if (!h.getSet().contains("dnb:" + rs.getString("dnb"))) {
							h.getSet().add(notNullValue("dnb:" + rs.getString("dnb")));
						}
					}
					if (StringUtils.isNotEmpty(rs.getString("dini")) ) {
						if (!h.getSet().contains(rs.getString("dini"))) {
							h.getSet().add(notNullValue(rs.getString("dini")));
						}
					}
				}
			}
			
			//store final record
			if (record != null && record.getHeader() != null && StringUtils.isNotEmpty(record.getHeader().getIdentifier()) ) {
				recordList.add(record);
			}
			
			closeStatementConnection(stmtconn);
			
		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} 
		finally {

			closeStatementConnection(stmtconn);
		}

		return recordList;
	}

	
	public Integer getRecordListSize(String from, String until, String set) {
		
		Date fromDate;
		Date untilDate;
		
		SingleStatementConnection stmtconn = null;

		try {

			if (StringUtils.isNotEmpty(from)) {
				fromDate = Date.valueOf(from);
			}
			else {
				fromDate = null;
			}
			if (StringUtils.isNotEmpty(until)) {
				untilDate = Date.valueOf(until);
			}
			else {
				untilDate = null;
			}
			if (fromDate != null && untilDate != null) {

				if (untilDate.before(fromDate)) {
					throw new IllegalArgumentException("The 'until' value must not be before the 'from' value!");
				}	
			}	
		} catch (IllegalArgumentException ex) {

			logger.warn(ex.getLocalizedMessage());
			throw ex;
		}

		Integer result = null;
		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();

			stmtconn.loadStatement(SelectFromDB.AllOIDsByDate(stmtconn.connection, fromDate, untilDate, set, BigInteger.valueOf(0), 0, true));
			QueryResult queryResult = stmtconn.execute();

			if (queryResult.getWarning() != null) {

				for (Throwable warning : queryResult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}
			if (queryResult.getResultSet().next())
			{
				result = queryResult.getResultSet().getInt("size");
			}


		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}
		return result;
	}
		
		
	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getRecordList(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */

	public LinkedList<Record> getRecordList(String from, String until, String set, BigInteger idOffset, int maxResults) {

		LinkedList<Record> recordList = new LinkedList<Record>();
		Date fromDate;
		Date untilDate;
		
		SingleStatementConnection stmtconn = null;

		try {

			if (StringUtils.isNotEmpty(from)) {
				fromDate = Date.valueOf(from);
			}
			else {
				fromDate = null;
			}
			if (StringUtils.isNotEmpty(until)) {
				untilDate = Date.valueOf(until);
			}
			else {
				untilDate = null;
			}
			if (fromDate != null && untilDate != null) {

				if (untilDate.before(fromDate)) {
					throw new IllegalArgumentException("The 'until' value must not be before the 'from' value!");
				}	
			}	
		} catch (IllegalArgumentException ex) {

			logger.warn(ex.getLocalizedMessage());
			throw ex;
		}

		try {

			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();

			stmtconn.loadStatement(SelectFromDB.AllOIDsByDate(stmtconn.connection, fromDate, untilDate, set, idOffset, maxResults, false));
			QueryResult idQueryResult = stmtconn.execute();
			
			List<BigDecimal> ids = new ArrayList<BigDecimal>();
			
			
			while (idQueryResult.getResultSet().next()) {
				ids.add(idQueryResult.getResultSet().getBigDecimal("object_id"));
			}
			
			logger.info("list size: " + ids.size());

			closeStatementConnection(stmtconn);
			
			// skip final query, if there have no ids been found for the query
			if (ids == null || ids.size() == 0) {
				return recordList;
			}
			
			stmtconn = (SingleStatementConnection) this.dbng.getSingleStatementConnection();
			stmtconn.loadStatement(SelectFromDB.OAIListAll(stmtconn.connection, set, fromDate, untilDate, ids));
			QueryResult queryresult = stmtconn.execute();

			if (queryresult.getWarning() != null) {

				for (Throwable warning : queryresult.getWarning()) {

					logger.warn(warning.getLocalizedMessage());
				}
			}

			Record record = new Record();
			BigDecimal oid = new BigDecimal(-1);
			DCHeader h = record.getHeader();
			DCMetaData m = record.getMetaData();



			while (queryresult.getResultSet().next()) {

				ResultSet rs = queryresult.getResultSet();
				oid = rs.getBigDecimal("object_id");

				if (!record.getHeader().getIdentifier().equals(oid.toPlainString())) {

					// new id, save last record

					if (recordList.size() == maxResults)
					{
						// enough results fetched, skip the rest
						break;
					}
					
					if (!record.getHeader().getIdentifier().equals("")) {

						recordList.add(record);
						record = new Record();
						h = record.getHeader();
						m = record.getMetaData();
					}

					h.setIdentifier(safeValueOf(rs.getBigDecimal("object_id")));
					h.setDatestamp(safeValueOf(rs.getDate("repository_datestamp")));

					if (rs.getString("title") != null)
						m.getTitle().add(rs.getString("title"));
					if (rs.getString("author_firstname") != null || rs.getString("author_lastname") != null)
						m.getCreator().add(rs.getString("author_firstname") + " " + rs.getString("author_lastname"));
					if (rs.getString("keyword") != null)
						m.getSubject().add(notNullValue(rs.getString("keyword")));
					if (rs.getString("abstract") != null)
						m.getDescription().add(notNullValue(rs.getString("abstract")));
					if (rs.getString("publisher") != null)
						m.getPublisher().add(notNullValue(rs.getString("publisher")));
					if (rs.getString("date") != null)
						m.getDate().add(safeValueOf(rs.getDate("date")));
					if (rs.getString("type") != null)
						m.getType().add(notNullValue(rs.getString("type")));
					if (rs.getString("mimeFormat") != null)
						m.getFormat().add(notNullValue(rs.getString("mimeformat")));
					if (rs.getString("link") != null)
						m.getIdentifier().add(notNullValue(rs.getString("link")));
					if (rs.getString("language") != null)
						m.getLanguage().add(notNullValue(rs.getString("language")));
					if (rs.getString("ddc") != null)
						h.getSet().add(notNullValue(rs.getString("ddc")));
				} else {

					// additional data, --> aggregate
					if (rs.getString("title") != null) {
						if (!m.getTitle().contains(rs.getString("title"))) {
							m.getTitle().add(notNullValue(rs.getString("title")));
						}
					}
					if (rs.getString("author_firstname") != null || rs.getString("author_lastname") != null) {
						if (!m.getCreator().contains(rs.getString("author_firstname") + " " + rs.getString("author_lastname"))) {
							m.getCreator().add(
									notNullValue(rs.getString("author_firstname")) + " " + notNullValue(rs.getString("author_lastname")));

						}
					}
					if (rs.getString("keyword") != null) {
						if (!m.getSubject().contains(rs.getString("keyword"))) {
							m.getSubject().add(notNullValue(rs.getString("keyword")));

						}
					}
					if (rs.getString("abstract") != null) {
						if (!m.getDescription().contains(rs.getString("abstract"))) {
							m.getDescription().add(notNullValue(rs.getString("abstract")));

						}
					}
					if (rs.getString("publisher") != null) {
						if (!m.getPublisher().contains(rs.getString("publisher"))) {
							m.getPublisher().add(notNullValue(rs.getString("publisher")));

						}
					}
					if (rs.getString("date") != null) {
						if (!m.getDate().contains(safeValueOf(rs.getDate("date")))) {
							m.getDate().add(safeValueOf((rs.getDate("date"))));

						}
					}
					if (rs.getString("type") != null) {
						if (!m.getType().contains(rs.getString("type"))) {
							m.getType().add(notNullValue(rs.getString("type")));

						}
					}
					if (rs.getString("mimeFormat") != null) {
						if (!m.getFormat().contains(rs.getString("mimeFormat"))) {
							m.getFormat().add(notNullValue(rs.getString("mimeFormat")));

						}
					}
					if (rs.getString("link") != null) {
						if (!m.getIdentifier().contains(rs.getString("link"))) {
							m.getIdentifier().add(notNullValue(rs.getString("link")));

						}
					}
					if (rs.getString("language") != null) {
						if (!m.getLanguage().contains(rs.getString("language"))) {
							m.getLanguage().add(notNullValue(rs.getString("language")));

						}
					}
					if (rs.getString("ddc") != null) {
						if (!h.getSet().contains(rs.getString("ddc"))) {
							h.getSet().add(notNullValue(rs.getString("ddc")));

						}
					}
				}
			}
			if (record != null && record.getHeader() != null && StringUtils.isNotEmpty(record.getHeader().getIdentifier()) ) {
				recordList.add(record);
			}
		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);

		} finally {

			closeStatementConnection(stmtconn);
		}

		return recordList;
	}
	
	
	private void closeStatementConnection(StatementConnection stmtconn) {
		
		try {

			if (stmtconn != null)
				stmtconn.close();

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
		}
	}

	private String safeValueOf(BigDecimal dec) {
		if (dec == null) {
			return null;
		}
		return dec.toPlainString();
	}

	private String safeValueOf(Date date) {
		if (date == null) {
			return null;
		}
		return date.toString();
	}

	private String notNullValue(String text) {
		if (text == null) {
			return "";
		}
		return text;
	}
}
