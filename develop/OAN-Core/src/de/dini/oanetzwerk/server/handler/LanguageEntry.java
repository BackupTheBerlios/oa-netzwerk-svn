package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
import de.dini.oanetzwerk.utils.ISO639LangNormalizer;
import de.dini.oanetzwerk.utils.exceptions.MethodNotImplementedException;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

public class LanguageEntry extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {

	private static Logger logger = Logger.getLogger(LanguageEntry.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private static final String OIDS_PARAM = "OIDS";

	public LanguageEntry() {
		super(LanguageEntry.class.getName(), RestKeyword.LanguageEntry);
	}

	@Override
	protected String deleteKeyWord(String[] path) throws MethodNotImplementedException, NotEnoughParametersException {

		BigDecimal object_id = null;

		if (path.length < 1)
			throw new NotEnoughParametersException("This method needs at least 1 parameter: the internal object ID");

		this.rms = new RestMessage(RestKeyword.LanguageEntry);

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

			// assume language code has been specified as second argument,
			// -> delete only the single oid-language relation
			if (path.length >= 2) {
				stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
				stmtconn.loadStatement(DBAccessNG.selectFromDB().Iso639LanguageByName(stmtconn.connection,
						ISO639LangNormalizer.getISO639_3(path[1])));
				this.result = stmtconn.execute();

				logWarnings();

				this.result.getResultSet().next();

				BigDecimal languageId = this.result.getResultSet().getBigDecimal("language_id");

				stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Object2Iso639Language(stmtconn.connection, object_id, languageId, true));
				this.result = stmtconn.execute();
			}
			// no ddc-category specified, -> delete all ddc-relations for the
			// given oid
			else if (path.length == 1) {
				stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();
				stmtconn.loadStatement(DBAccessNG.deleteFromDB().Object2Iso639Language(stmtconn.connection, object_id, true));
				this.result = stmtconn.execute();
			}

			logWarnings();

			int deleteCount = this.result.getUpdateCount();
			if (deleteCount == 0) {
				this.rms.setStatus(RestStatusEnum.ILLEGAL_ACCESS_ERROR);
				this.rms.setStatusDescription("The relation could not be deleted, remember, only generated ddc-entries are allowed to be deleted!");
			}

			// RestEntrySet res = new RestEntrySet ( );

		} catch (SQLException ex) {

			logger.error("An error occured while processing Delete LanguageEntry: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error("An error occured while processing Delete LanguageEntry: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.WRONG_STATEMENT);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} finally {

			if (stmtconn != null) {

				try {

					stmtconn.close();
					stmtconn = null;

				} catch (SQLException ex) {

					ex.printStackTrace();
					logger.error(ex.getLocalizedMessage(), ex);
				}
			}

			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage(this.rms);
	}

	@Override
	protected String getKeyWord(String[] path) throws NotEnoughParametersException {
		BigDecimal object_id = null;

		if (path.length < 1)
			throw new NotEnoughParametersException("This method needs at least 1 parameter: the internal object ID");

		this.rms = new RestMessage(RestKeyword.LanguageEntry);

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

			stmtconn.loadStatement(DBAccessNG.selectFromDB().Object2Iso639Language(stmtconn.connection, object_id, null));
			this.result = stmtconn.execute();

			logWarnings();

			boolean foundOne = false;
			while (this.result.getResultSet().next()) {
				foundOne = true;
				res = new RestEntrySet();

				res.addEntry("language", ISO639LangNormalizer.getISO639_2fromISO639_3(this.result.getResultSet().getString("language")));
				res.addEntry("generated", Boolean.toString(this.result.getResultSet().getBoolean("generated")));

				this.rms.setStatus(RestStatusEnum.OK);
				this.rms.addEntrySet(res);

			}

			if (!foundOne) {

				this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription("No matching language-entry found");
			}

		} catch (SQLException ex) {

			logger.error("An error occured while processing Get LanguageEntry: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error("An error occured while processing Get LanguageEntry: " + ex.getLocalizedMessage(), ex);
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

			this.rms = new RestMessage(RestKeyword.LanguageEntry);
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
	protected String postKeyWord(String[] path, String data) throws MethodNotImplementedException, NotEnoughParametersException {
		this.rms = new RestMessage(RestKeyword.LanguageEntry);
		this.rms.setStatus(RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("POST method is not implemented for ressource '" + RestKeyword.LanguageEntry + "'.");
		return RestXmlCodec.encodeRestMessage(this.rms);
	}

	// @Override
	// protected String postKeyWord(String[] path, String data) throws
	// NotEnoughParametersException, MethodNotImplementedException {
	// BigDecimal object_id = null;
	//
	// if (path.length < 1)
	// throw new
	// NotEnoughParametersException("This method needs at least 1 parameter: the internal object ID");
	//
	// this.rms = new RestMessage(RestKeyword.LanguageEntry);
	//
	// try {
	//
	// object_id = new BigDecimal(path[0]);
	//
	// } catch (NumberFormatException ex) {
	//
	// logger.error(path[0] + " is NOT a numeric value!", ex);
	//
	// this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
	// this.rms.setStatusDescription(path[0] + " is NOT a number!");
	// return RestXmlCodec.encodeRestMessage(this.rms);
	// }
	//
	// this.rms = RestXmlCodec.decodeRestMessage(data);
	// List<RestEntrySet> requestEntrySets = this.rms.getListEntrySets();
	//
	// DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
	// MultipleStatementConnection stmtconn = null;
	//
	// try {
	// stmtconn = (MultipleStatementConnection)
	// dbng.getMultipleStatementConnection();
	//
	// // assume ddc-category has been specified as second argument,
	// // -> delete only the single oid-category relation
	// if (path.length >= 2) {
	//
	// // delete all relations first
	// stmtconn.loadStatement
	// (DBAccessNG.deleteFromDB().DDC_Classification(stmtconn.connection,
	// object_id, path[1], true));
	// this.result = stmtconn.execute();
	//
	//
	// // insert new relations
	// insertRelation(dbng, stmtconn, requestEntrySets, object_id);
	// }
	// // no ddc-category specified, -> delete all ddc-relations for the given
	// oid
	// else if (path.length == 1) {
	// // delete all relations first
	// stmtconn.loadStatement
	// (DBAccessNG.deleteFromDB().DDC_Classification(stmtconn.connection,
	// object_id, true));
	// this.result = stmtconn.execute();
	//
	// // insert new relations
	// insertRelation(dbng, stmtconn, requestEntrySets, object_id);
	// }
	//
	//
	// // this.result = stmtconn.execute ( );
	//
	// // if (this.result.getWarning ( ) != null)
	// // for (Throwable warning : result.getWarning ( ))
	// // logger.warn (warning.getLocalizedMessage ( ), warning);
	// //
	//
	// RestEntrySet res = new RestEntrySet ( );
	//
	// } catch (SQLException ex) {
	//
	// logger.error ("An error occured while processing Delete LanguageEntry: "
	// + ex.getLocalizedMessage ( ), ex);
	// this.rms.setStatus (RestStatusEnum.SQL_ERROR);
	// this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
	//
	// } catch (WrongStatementException ex) {
	//
	// logger.error ("An error occured while processing Delete LanguageEntry: "
	// + ex.getLocalizedMessage ( ), ex);
	// this.rms.setStatus (RestStatusEnum.WRONG_STATEMENT);
	// this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
	//
	// } finally {
	//
	// if (stmtconn != null) {
	//
	// try {
	//
	// stmtconn.close ( );
	// stmtconn = null;
	//
	// } catch (SQLException ex) {
	//
	// ex.printStackTrace ( );
	// logger.error (ex.getLocalizedMessage ( ), ex);
	// }
	// }
	//
	// this.result = null;
	// dbng = null;
	// }
	//
	// return RestXmlCodec.encodeRestMessage (this.rms);
	// }

	private String insertRelation(DBAccessNG dbng, MultipleStatementConnection stmtconn, List<RestEntrySet> requestEntrySets,
			BigDecimal object_id) {

		String key = "";
		RestEntrySet res = null;

		try {
			boolean errorHappended = false;
			if (stmtconn == null)
				stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();

			Iterator<RestEntrySet> itSets = requestEntrySets.iterator();
			Set<String> languages = new HashSet<String>();

			while (itSets.hasNext()) {
				res = itSets.next();

				String language = null;

				Iterator<String> it = res.getKeyIterator();

				while (it.hasNext()) {
					key = it.next();
					if (logger.isDebugEnabled())
						logger.debug("key = " + key);

					if (key.equalsIgnoreCase("language")) {
						language = res.getValue(key);
					} else
						continue;

					language = ISO639LangNormalizer.getISO639_3(language);

					if (language == null || language.trim().length() != 3) {
						this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
						this.rms.setListEntrySets(requestEntrySets);
						this.rms.setStatusDescription("Invalid language name supplied!' ('" + language + "')");
						res.addEntry("ERROR_OCCURED_HERE", "Invalid language name supplied!' (" + language + "')");
						return RestXmlCodec.encodeRestMessage(this.rms);
					}
					System.out.println("lang: " + language);
					languages.add(language);
				}

				this.rms = new RestMessage(RestKeyword.LanguageEntry);

				// Prüfen, ob überhaupt Daten übergeben wurden

				if (language == null) {

					this.rms.setStatus(RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
					this.rms.setListEntrySets(requestEntrySets);
					this.rms.setStatusDescription("No language code supplied!'");
					res.addEntry("ERROR_OCCURED_HERE", "No language name supplied! Please specify a language value for the 'language' key.");
					return RestXmlCodec.encodeRestMessage(this.rms);
				}

			}

			// check if languages exist already, if not create
			for (String lang : languages) {
				stmtconn.loadStatement(DBAccessNG.selectFromDB().Iso639LanguageByName(stmtconn.connection, lang));
				this.result = stmtconn.execute();

				logWarnings();

				boolean hasNext = result.getResultSet().next();
				BigDecimal language_id = null;

				if (!hasNext) {

					// create language entry first, in order to relate it to the
					// specified oid in the next step

					stmtconn.loadStatement(DBAccessNG.insertIntoDB().Iso639Language(stmtconn.connection, lang));
					this.result = stmtconn.execute();

					logWarnings();

					stmtconn.loadStatement(DBAccessNG.selectFromDB().Iso639LanguageByName(stmtconn.connection, lang));
					this.result = stmtconn.execute();

					result.getResultSet().next();

					logWarnings();
				}

				language_id = result.getResultSet().getBigDecimal("language_id");

				// check if relation exists already
				stmtconn.loadStatement(DBAccessNG.selectFromDB().Object2Iso639Language(stmtconn.connection, object_id, lang));
				result = stmtconn.execute();

				hasNext = result.getResultSet().next();

				// if relation doesn't exist, create it
				if (!hasNext) {

					// connect categories with md-object
					stmtconn.loadStatement(DBAccessNG.insertIntoDB().Object2Iso639Language(stmtconn.connection, object_id, language_id, 1,
							true));
					stmtconn.execute();
				}

			}

			stmtconn.connection.commit();

			logger.info("DDC-Entries successfully processed!");
			this.rms.setStatus(RestStatusEnum.OK);
			this.rms.setListEntrySets(requestEntrySets);
		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setKeyword(RestKeyword.LanguageEntry);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.WRONG_STATEMENT);
			this.rms.setKeyword(RestKeyword.LanguageEntry);
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
