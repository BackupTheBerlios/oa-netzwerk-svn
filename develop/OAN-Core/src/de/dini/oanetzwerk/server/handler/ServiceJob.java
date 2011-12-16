package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import de.dini.oanetzwerk.server.database.sybase.InsertIntoDBSybase;
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;
import de.dini.oanetzwerk.server.database.sybase.UpdateInDBSybase;
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

		super(ServiceJob.class.getName(), RestKeyword.ServiceJob);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */

	@Override
	protected String deleteKeyWord(String[] path) throws NotEnoughParametersException {

		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 2 parameters: the keyword and the job ID");
		
		BigDecimal jobId;
		
		try {
			
			jobId = new BigDecimal (path [0]);
			
		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!");
			
			this.rms = new RestMessage (RestKeyword.ServiceJob);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;
		
		this.rms = new RestMessage (RestKeyword.ServiceJob);
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			stmtconn.loadStatement (DBAccessNG.deleteFromDB().ServicesScheduling(stmtconn.connection, jobId));
			this.result = stmtconn.execute ( );
						
			logWarnings();
						
			if (this.result.getUpdateCount ( ) < 1) {
				
				stmtconn.rollback ( );
				throw new SQLException ("Job could not be deleted");
				
			} else {
				
				stmtconn.commit ( );
			}
			
			RestEntrySet res = new RestEntrySet ( );
			
			res.addEntry ("job_id", jobId.toPlainString ( ));
			
			this.rms.addEntrySet (res);
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Delete ServiceJob: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Delete ServiceJob: " + ex.getLocalizedMessage ( ), ex);
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
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */

	@Override
	protected String getKeyWord(String[] path) throws NotEnoughParametersException {

		if (path.length < 1) {

			DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
			SingleStatementConnection stmtconn = null;
			RestEntrySet res;

			try {

				stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();

				stmtconn.loadStatement(DBAccessNG.selectFromDB().ServicesScheduling(stmtconn.connection));
				this.result = stmtconn.execute();

				logWarnings();

				boolean foundOne = false;
				while (this.result.getResultSet().next()) {
					foundOne = true;
					res = new RestEntrySet();

					if (logger.isDebugEnabled())
						logger.debug("DB returned: \n\tjob_id = " + this.result.getResultSet().getInt(1) + "\n\tname = "
								+ this.result.getResultSet().getString(3) + "\n\tservice_id = "
								+ this.result.getResultSet().getBigDecimal(2).toString() + "\n\tstatus = "
								+ this.result.getResultSet().getString(4).toString() + "\n\tinfo = "
								+ this.result.getResultSet().getString(5));

					res.addEntry("job_id", Integer.toString(this.result.getResultSet().getInt("job_id")));
					res.addEntry("name", this.result.getResultSet().getString("name"));
					res.addEntry("service_id", this.result.getResultSet().getBigDecimal("service_id").toPlainString());
					res.addEntry("status", this.result.getResultSet().getString("status"));
					res.addEntry("info", this.result.getResultSet().getString("info"));
					res.addEntry("periodic", Boolean.toString(this.result.getResultSet().getBoolean("periodic")));
					res.addEntry("nonperiodic_date", this.result.getResultSet().getTimestamp("nonperiodic_date").toString());
					res.addEntry("periodic_interval_type", this.result.getResultSet().getString("periodic_interval_type"));
					res.addEntry("periodic_interval_days", Integer.toString(this.result.getResultSet().getInt("periodic_interval_days")));

					this.rms.setStatus(RestStatusEnum.OK);
					this.rms.addEntrySet(res);

				}

				if (!foundOne) {

					this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
					this.rms.setStatusDescription("No matching ServiceJob found");
				}

			} catch (SQLException ex) {

				logger.error("An error occured while processing Get ServiceJob: " + ex.getLocalizedMessage(), ex);
				this.rms.setStatus(RestStatusEnum.SQL_ERROR);
				this.rms.setStatusDescription(ex.getLocalizedMessage());

			} catch (WrongStatementException ex) {

				logger.error("An error occured while processing Get ServiceJob: " + ex.getLocalizedMessage(), ex);
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

		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		SingleStatementConnection stmtconn = null;
		RestEntrySet res = new RestEntrySet();

		try {

			stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();

			stmtconn.loadStatement(DBAccessNG.selectFromDB().ServicesScheduling(stmtconn.connection, jobId));
			this.result = stmtconn.execute();

			logWarnings();

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
				res.addEntry("nonperiodic_date", this.result.getResultSet().getTimestamp("nonperiodic_date").toString());
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

		long jobName;

		try {

			jobName = Long.parseLong(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a number!");

			this.rms = new RestMessage(RestKeyword.ServiceJob);
			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");

			return RestXmlCodec.encodeRestMessage(this.rms);
		}
		
		// case of status update
		if (path.length > 1) {
			
			String status = path[1];
			if (status != null) {
				String updatedStatus = null;
				if (status.toLowerCase().equals("finished")) {
					updatedStatus = "Finished";
				}
				else if (status.toLowerCase().equals("working")) {
					updatedStatus = "Working";
				}
				if (updatedStatus != null) {
					DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
					SingleStatementConnection stmtconn = null;
					
					this.rms = new RestMessage(RestKeyword.ServiceJob);
					RestEntrySet res = new RestEntrySet();

					try {
						
						stmtconn = (SingleStatementConnection) dbng.getSingleStatementConnection();

						stmtconn.loadStatement(DBAccessNG.updateInDB().ServicesScheduling(stmtconn.connection, Long.toString(jobName), status));
						this.result = stmtconn.execute();

						if (this.result.getUpdateCount() < 1) {

							this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
							this.rms.setStatusDescription("No matching Service job found for id " + jobName);
						}

						logWarnings();

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
		}
		
		// case update job as a whole

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
		int periodicDays = 0;

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

					nonperiodicTimestamp = new Date(new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(res.getValue(key)).getTime());

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

		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;

		this.rms = new RestMessage(RestKeyword.ServiceJob);
		res = new RestEntrySet();

		try {

			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();

			stmtconn.loadStatement(DBAccessNG.updateInDB().ServicesScheduling(stmtconn.connection, name, serviceId, status, info, periodic,
					nonperiodicTimestamp, periodicInterval, periodicDays, jobId));
			this.result = stmtconn.execute();

			logger.info("Update Count: " + this.result.getUpdateCount());
			if (this.result.getUpdateCount() < 1) {

				this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription("No matching ObjectEntry found");
			}

			stmtconn.commit();
			// stmtconn.loadStatement(SelectFromDB
			// .ObjectEntry(stmtconn.connection, , repository_datestamp,
			// repository_identifier));
			// this.result = stmtconn.execute();

			logWarnings();

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
		Timestamp nonperiodicTimestamp = null;
		String periodicInterval = null;
		int periodicDays = 0;

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
					nonperiodicTimestamp = new Timestamp(new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(res.getValue(key)).getTime());

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

		DBAccessNG dbng = DBAccessNG.getInstance(super.getDataSource());
		MultipleStatementConnection stmtconn = null;

		this.rms = new RestMessage(RestKeyword.ServiceJob);
		res = new RestEntrySet();

		try {

			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();

			stmtconn.loadStatement(DBAccessNG.insertIntoDB().ServicesScheduling(stmtconn.connection, name, serviceId, status, info, periodic,
					nonperiodicTimestamp, periodicInterval, periodicDays));
			
			logger.info("DB-Insert: " + name + "   " + serviceId + "   " + status + "   " + info + "   " + periodic
					 + "   " + nonperiodicTimestamp  + "   " + periodicInterval  + "   " +  periodicDays);
			
			this.result = stmtconn.execute();

			logger.info("Update Count: " + this.result.getUpdateCount());
			if (this.result.getUpdateCount() < 1) {

				this.rms.setStatus(RestStatusEnum.NO_OBJECT_FOUND_ERROR);
				this.rms.setStatusDescription("No matching ObjectEntry found");
			}

			stmtconn.commit();
			// stmtconn.loadStatement(SelectFromDB
			// .ObjectEntry(stmtconn.connection, , repository_datestamp,
			// repository_identifier));
			// this.result = stmtconn.execute();

			logWarnings();

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
