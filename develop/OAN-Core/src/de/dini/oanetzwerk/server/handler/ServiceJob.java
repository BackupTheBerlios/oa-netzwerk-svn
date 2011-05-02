package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.InsertIntoDB;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.server.database.SingleStatementConnection;
import de.dini.oanetzwerk.server.database.UpdateInDB;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Sammy David
 * 
 */

public class ServiceJob extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {

	/**
	 * 
	 */

	private static Logger logger = Logger.getLogger(ServiceJob.class);

	/**
	 * 
	 */

	public ServiceJob() {

		super(ServiceJob.class.getName(), RestKeyword.Services);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */

	@Override
	protected String deleteKeyWord(String[] path) {

		this.rms = new RestMessage(RestKeyword.ServiceJob);
		this.rms.setStatus(RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("DELETE-method is not implemented for ressource '" + RestKeyword.Services + "'.");
		return RestXmlCodec.encodeRestMessage(this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */

	@Override
	protected String getKeyWord(String[] path) throws NotEnoughParametersException {

		if (path.length < 1) {

			int jobId;

			try {

				jobId = Integer.parseInt(path[0]);

			} catch (NumberFormatException ex) {

				logger.error(path[0] + " is NOT a number!");

				this.rms = new RestMessage(RestKeyword.ServiceJob);
				this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription(path[0] + " is NOT a number!");

				return RestXmlCodec.encodeRestMessage(this.rms);
			}

			DBAccessNG dbng = new DBAccessNG(super.getDataSource());
			SingleStatementConnection stmtconn = null;
			RestEntrySet res = new RestEntrySet();

			try {

				stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();

				stmtconn.loadStatement(SelectFromDB.ServicesScheduling(stmtconn.connection, jobId));
				this.result = stmtconn.execute();

				if (this.result.getWarning() != null) {

					for (Throwable warning : result.getWarning())
						logger.warn(warning.getLocalizedMessage(), warning);
				}

				boolean foundOne = false;
				while (this.result.getResultSet().next()) {
					foundOne = true;
					RestEntrySet entrySet = new RestEntrySet();

					if (logger.isDebugEnabled())
						logger.debug("DB returned: \n\tjob_id = " + this.result.getResultSet().getInt(1) + "\n\tname = "
								+ this.result.getResultSet().getString(2) + "\n\tservice_id = "
								+ this.result.getResultSet().getBigDecimal(3).toString() + "\n\tstatus = "
								+ this.result.getResultSet().getString(4).toString() + "\n\tinfo = "
								+ this.result.getResultSet().getString(5));

					res.addEntry("job_id", Integer.toString(this.result.getResultSet().getInt("job_id")));
					res.addEntry("name", this.result.getResultSet().getString("name"));
					res.addEntry("service_id", this.result.getResultSet().getBigDecimal("service_id").toPlainString());
					res.addEntry("status", this.result.getResultSet().getString("status"));
					res.addEntry("info", this.result.getResultSet().getString("info"));
					res.addEntry("periodic", Boolean.toString(this.result.getResultSet().getBoolean("periodic")));
					res.addEntry("nonperiodic_date", this.result.getResultSet().getDate("nonperiodic_date").toString());
					res.addEntry("periodic_interval_type", this.result.getResultSet().getString("periodic_interval_type"));
					res.addEntry("periodic_interval_days", Integer.toString(this.result.getResultSet().getInt("periodic_interval_days")));

					this.rms.setStatus(RestStatusEnum.OK);
					this.rms.addEntrySet(res);

				}

				if (!foundOne) {

					this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
					this.rms.setStatusDescription("No matching ObjectEntry found");
				}

			} catch (SQLException ex) {

				logger.error("An error occured while processing Get ServiceJob: " + ex.getLocalizedMessage(), ex);
				this.rms.setStatus(RestStatusEnum.SQL_ERROR);
				this.rms.setStatusDescription(ex.getLocalizedMessage());

			} catch (WrongStatementException ex) {

				logger.error("An error occured while processing Get ObjectEntry: " + ex.getLocalizedMessage(), ex);
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
		// //////////////////////////

		//
		// if (path.length < 1)
		// throw new
		// NotEnoughParametersException("This method needs at least 2 parameters: the keyword and the internal object ID");

		int jobId;

		try {

			jobId = Integer.parseInt(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a number!");

			this.rms = new RestMessage(RestKeyword.ServiceJob);
			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");

			return RestXmlCodec.encodeRestMessage(this.rms);
		}

		DBAccessNG dbng = new DBAccessNG(super.getDataSource());
		SingleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet();

		try {

			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();

			stmtconn.loadStatement(SelectFromDB.ServicesScheduling(stmtconn.connection, jobId));
			this.result = stmtconn.execute();

			if (this.result.getWarning() != null) {

				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage(), warning);
			}

			if (this.result.getResultSet().next()) {

				if (logger.isDebugEnabled())
					logger.debug("DB returned: \n\tjob_id = " + this.result.getResultSet().getInt(1) + "\n\tname = "
							+ this.result.getResultSet().getString(2) + "\n\tservice_id = "
							+ this.result.getResultSet().getBigDecimal(3).toString() + "\n\tstatus = "
							+ this.result.getResultSet().getString(4).toString() + "\n\tinfo = " + this.result.getResultSet().getString(5));

				res.addEntry("job_id", Integer.toString(this.result.getResultSet().getInt("job_id")));
				res.addEntry("name", this.result.getResultSet().getString("name"));
				res.addEntry("service_id", this.result.getResultSet().getBigDecimal("service_id").toPlainString());
				res.addEntry("status", this.result.getResultSet().getString("status"));
				res.addEntry("info", this.result.getResultSet().getString("info"));
				res.addEntry("periodic", Boolean.toString(this.result.getResultSet().getBoolean("periodic")));
				res.addEntry("nonperiodic_date", this.result.getResultSet().getDate("nonperiodic_date").toString());
				res.addEntry("periodic_interval_type", this.result.getResultSet().getString("periodic_interval_type"));
				res.addEntry("periodic_interval_days", Integer.toString(this.result.getResultSet().getInt("periodic_interval_days")));

				this.rms.setStatus(RestStatusEnum.OK);
				this.rms.addEntrySet(res);

			} else {

				this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription("No matching ObjectEntry found");
			}

		} catch (SQLException ex) {

			logger.error("An error occured while processing Get ServiceJob: " + ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error("An error occured while processing Get ObjectEntry: " + ex.getLocalizedMessage(), ex);
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

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[],
	 *      java.lang.String)
	 */

	@Override
	protected String postKeyWord(String[] path, String data) throws NotEnoughParametersException {

		if (path.length < 1)
			throw new NotEnoughParametersException("This method needs at least 2 parameters: the keyword and the internal job ID");

		if (logger.isDebugEnabled())
			logger.debug("doing POST ServiceJob");

		int jobId;

		try {

			jobId = Integer.parseInt(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a number!");

			this.rms = new RestMessage(RestKeyword.ServiceJob);
			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");

			return RestXmlCodec.encodeRestMessage(this.rms);
		}

		String name = null;
		BigDecimal serviceId = null;
		String status = null;
		String info = null;
		boolean periodic = false;
		Date nonperiodicTimestamp = null;
		String periodicInterval = null;
		int periodicDays = -1;

		this.rms = RestXmlCodec.decodeRestMessage(data);
		RestEntrySet res = this.rms.getListEntrySets().get(0);

		Iterator<String> it = res.getKeyIterator();
		String key = "";

		while (it.hasNext()) {

			key = it.next();

			if (key.equalsIgnoreCase("name")) {

				name = res.getValue(key);

				if (logger.isDebugEnabled())
					logger.debug("name: " + name);

			} else if (key.equalsIgnoreCase("service_id")) {

				serviceId = new BigDecimal(res.getValue(key));

				if (logger.isDebugEnabled())
					logger.debug("service-ID: " + serviceId);

			} else if (key.equalsIgnoreCase("status")) {

				status = res.getValue(key);

				if (logger.isDebugEnabled())
					logger.debug("status: " + status);

			} else if (key.equalsIgnoreCase("info")) {

				info = res.getValue(key);

				if (logger.isDebugEnabled())
					logger.debug("info: " + info);

			} else if (key.equalsIgnoreCase("nonperiodic_date")) {

				try {

					nonperiodicTimestamp = HelperMethods.extract_datestamp(res.getValue(key));

				} catch (ParseException ex) {

					logger.error(ex.getLocalizedMessage());
					ex.printStackTrace();
				}

				if (logger.isDebugEnabled())
					logger.debug("nonperiodic_date: " + nonperiodicTimestamp);

			} else if (key.equalsIgnoreCase("periodic")) {

				periodic = new Boolean(res.getValue(key));

			} else if (key.equalsIgnoreCase("periodic_days")) {

				periodicDays = new Integer(res.getValue(key));

			} else if (key.equalsIgnoreCase("periodic_interval")) {

				periodicInterval = res.getValue(key);

				if (logger.isDebugEnabled())
					logger.debug("periodic_interval: " + periodicInterval);

			} else {

				logger.warn("Maybe I read a parameter which is not implemented! Continuing anyways...");
				logger.debug(key + " found with value: " + res.getValue(key));
				continue;
			}
		}

		DBAccessNG dbng = new DBAccessNG(super.getDataSource());
		MultipleStatementConnection stmtconn = null;

		this.rms = new RestMessage(RestKeyword.ServiceJob);
		res = new RestEntrySet();

		try {

			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();

			stmtconn.loadStatement(UpdateInDB.ServicesScheduling(stmtconn.connection, name, serviceId, status, info, periodic,
					nonperiodicTimestamp, periodicInterval, periodicDays, jobId));
			this.result = stmtconn.execute();

			System.out.println("Update Count: " + this.result.getUpdateCount());
			if (this.result.getUpdateCount() < 1) {

				this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription("No matching ObjectEntry found");
			}

			stmtconn.commit();
			// stmtconn.loadStatement(SelectFromDB
			// .ObjectEntry(stmtconn.connection, , repository_datestamp,
			// repository_identifier));
			// this.result = stmtconn.execute();

			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage(), warning);

			// if (this.result.getResultSet().next()) {
			//
			// if (logger.isDebugEnabled())
			// logger.debug("DB returned: object_id = " +
			// this.result.getResultSet().getBigDecimal(1));
			//
			// res.addEntry("oid",
			// this.result.getResultSet().getBigDecimal(1).toPlainString());
			// stmtconn.commit();

			this.rms.setStatus(RestStatusEnum.OK);

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
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

			this.rms.addEntrySet(res);
			res = null;
			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage(this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[],
	 *      java.lang.String)
	 */
	@Override
	protected String putKeyWord(String[] path, String data) {

		// String name;
		//
		// this.rms = RestXmlCodec.decodeRestMessage(data);
		// RestEntrySet entrySet = this.rms.getListEntrySets().get(0);
		//
		// this.rms = new RestMessage();
		// StringBuffer sbPath = new StringBuffer();
		// for(String s : path) sbPath.append(s + "/");
		// this.rms.setRestURL(sbPath.toString());
		// this.rms.setKeyword(RestKeyword.ServiceJob);
		//
		// String strSID = entrySet.getValue("service_id");
		//
		// if (strSID != null)
		// new Integer (strSID);
		//
		// else {
		//
		// // SID is missing!
		// this.rms.setStatus(RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
		// this.rms.setStatusDescription("no 'service_id' entry given in request body");
		// return RestXmlCodec.encodeRestMessage(this.rms);
		// }
		//
		// name = entrySet.getValue("name");
		// if(name == null) {
		//
		// // name is missing!
		// this.rms.setStatus(RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
		// this.rms.setStatusDescription("no 'name' entry given in request body");
		// return RestXmlCodec.encodeRestMessage(this.rms);
		// }
		//

		if (logger.isDebugEnabled())
			logger.debug("doing Put ObjectEntry");

		String name = null;
		BigDecimal serviceId = null;
		String status = null;
		String info = null;
		boolean periodic = false;
		Date nonperiodicTimestamp = null;
		String periodicInterval = null;
		int periodicDays = -1;

		this.rms = RestXmlCodec.decodeRestMessage(data);
		RestEntrySet res = this.rms.getListEntrySets().get(0);

		Iterator<String> it = res.getKeyIterator();
		String key = "";

		while (it.hasNext()) {

			key = it.next();

			if (key.equalsIgnoreCase("name")) {

				name = res.getValue(key);

				if (logger.isDebugEnabled())
					logger.debug("name: " + name);

			} else if (key.equalsIgnoreCase("service_id")) {

				serviceId = new BigDecimal(res.getValue(key));

				if (logger.isDebugEnabled())
					logger.debug("service-ID: " + serviceId);

			} else if (key.equalsIgnoreCase("status")) {

				status = res.getValue(key);

				if (logger.isDebugEnabled())
					logger.debug("status: " + status);

			} else if (key.equalsIgnoreCase("info")) {

				info = res.getValue(key);

				if (logger.isDebugEnabled())
					logger.debug("info: " + info);

			} else if (key.equalsIgnoreCase("nonperiodic_date")) {

				try {

					nonperiodicTimestamp = HelperMethods.extract_datestamp(res.getValue(key));

				} catch (ParseException ex) {

					logger.error(ex.getLocalizedMessage());
					ex.printStackTrace();
				}

				if (logger.isDebugEnabled())
					logger.debug("nonperiodic_date: " + nonperiodicTimestamp);

			} else if (key.equalsIgnoreCase("periodic")) {

				periodic = new Boolean(res.getValue(key));

			} else if (key.equalsIgnoreCase("periodic_days")) {

				periodicDays = new Integer(res.getValue(key));

			} else if (key.equalsIgnoreCase("periodic_interval")) {

				periodicInterval = res.getValue(key);

				if (logger.isDebugEnabled())
					logger.debug("periodic_interval: " + periodicInterval);

			} else {

				logger.warn("Maybe I read a parameter which is not implemented! Continuing anyways...");
				logger.debug(key + " found with value: " + res.getValue(key));
				continue;
			}
		}

		DBAccessNG dbng = new DBAccessNG(super.getDataSource());
		MultipleStatementConnection stmtconn = null;

		this.rms = new RestMessage(RestKeyword.ServiceJob);
		res = new RestEntrySet();

		try {

			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();

			stmtconn.loadStatement(InsertIntoDB.ServicesScheduling(stmtconn.connection, name, serviceId, status, info, periodic,
					nonperiodicTimestamp, periodicInterval, periodicDays));
			this.result = stmtconn.execute();

			System.out.println("Update Count: " + this.result.getUpdateCount());
			if (this.result.getUpdateCount() < 1) {

				this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription("No matching ObjectEntry found");
			}

			stmtconn.commit();
			// stmtconn.loadStatement(SelectFromDB
			// .ObjectEntry(stmtconn.connection, , repository_datestamp,
			// repository_identifier));
			// this.result = stmtconn.execute();

			if (this.result.getWarning() != null)
				for (Throwable warning : result.getWarning())
					logger.warn(warning.getLocalizedMessage(), warning);

			// if (this.result.getResultSet().next()) {
			//
			// if (logger.isDebugEnabled())
			// logger.debug("DB returned: object_id = " +
			// this.result.getResultSet().getBigDecimal(1));
			//
			// res.addEntry("oid",
			// this.result.getResultSet().getBigDecimal(1).toPlainString());
			// stmtconn.commit();

			this.rms.setStatus(RestStatusEnum.OK);

		} catch (SQLException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
			this.rms.setStatus(RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription(ex.getLocalizedMessage());

		} catch (WrongStatementException ex) {

			logger.error(ex.getLocalizedMessage(), ex);
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

			this.rms.addEntrySet(res);
			res = null;
			this.result = null;
			dbng = null;
		}

		return RestXmlCodec.encodeRestMessage(this.rms);
	}
}
