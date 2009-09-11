package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestEntrySet;
import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.codec.RestMessage;
import de.dini.oanetzwerk.codec.RestStatusEnum;
import de.dini.oanetzwerk.codec.RestXmlCodec;
import de.dini.oanetzwerk.server.database.DBAccessNG;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.UsageDataMonth;
import de.dini.oanetzwerk.utils.UsageDataOverall;
import de.dini.oanetzwerk.utils.exceptions.NotEnoughParametersException;
import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Robin Malitz
 *
 */

public class UsageData extends AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	private static Logger logger = Logger.getLogger (UsageData.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public UsageData ( ) {
		super (UsageData.class.getName ( ), RestKeyword.UsageData);
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
			
			this.rms = new RestMessage (RestKeyword.UsageData);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));
		MultipleStatementConnection stmtconn = null;
		
		this.rms = new RestMessage (RestKeyword.UsageData);
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection ( );
			
			/*stmtconn.loadStatement (DeleteFromDB.Interpolated_DDC_Classification(stmtconn.connection, object_id));
			this.result = stmtconn.execute ( );
			
			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
			
			if (this.result.getUpdateCount ( ) < 1) {
				// da eh nichts gelöscht wurde, entsprechende Daten wieder in Ursprungszustand versetzen
				stmtconn.rollback();
			} else {
				// gelöschte Daten als solche committen
				stmtconn.commit ( );
			}*/
			
			RestEntrySet res = new RestEntrySet ( );
			
			res.addEntry ("object_id", object_id.toPlainString ( ));
			
			this.rms.addEntrySet (res);
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Delete UsageData: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Delete UsageData: " + ex.getLocalizedMessage ( ), ex);
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
		Date dateLastMonth = HelperMethods.getFirstDayOfPreceedingMonth(new Date());
		
		if (path.length < 1)
			throw new NotEnoughParametersException ("This method needs at least 1 parameter: the internal object ID");
		
		BigDecimal object_id;
		
		try {
			
			object_id = new BigDecimal (path [0]);
			if (object_id.intValue() < 0) {
				
				logger.error (path [0] + " is NOT a valid number for this parameter!");
				
				this.rms = new RestMessage (RestKeyword.UsageData);
				this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
				this.rms.setStatusDescription (path [0] + " is NOT a valid number for this parameter!");
				
				return RestXmlCodec.encodeRestMessage (this.rms);				
			}

		} catch (NumberFormatException ex) {
			
			logger.error (path [0] + " is NOT a number!", ex);
			
			this.rms = new RestMessage (RestKeyword.UsageData);
			this.rms.setStatus (RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription (path [0] + " is NOT a number!");
			
			return RestXmlCodec.encodeRestMessage (this.rms);
		}
		
		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));
		MultipleStatementConnection stmtconn = null;
		
		try {
			
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();
			
			//////////////////////////////
			// Metriken-Namen auslesen
			//////////////////////////////
			
			Map<String,List<UsageDataMonth>> mapUsageDataMonths = new HashMap<String,List<UsageDataMonth>>();
			Map<String,UsageDataOverall> mapUsageDataOveralls = new HashMap<String,UsageDataOverall>();
			
			mapUsageDataMonths = getMapUsageDataMonth(stmtconn, object_id);
			mapUsageDataOveralls = getMapUsageDataOverall(stmtconn, object_id);
			
			/////////////////////////////////////
			// Monats- udn Gesamtwerte fuellen
			/////////////////////////////////////
			
			fillMapUsageDataMonths(stmtconn, mapUsageDataMonths, object_id);
			fillMapUsageDataOveralls(stmtconn, mapUsageDataOveralls, object_id);
			
			counter = 0;
			
			/////////////////////////
			// Response erzeugen
			/////////////////////////
			
			for (String strMetricsName : mapUsageDataOveralls.keySet()) {			
				
				UsageDataOverall udo = mapUsageDataOveralls.get(strMetricsName);
				if(udo != null) {
					counter ++;
					RestEntrySet entrySet = new RestEntrySet(); 
					entrySet.addEntry ("object_id", object_id.toPlainString());
					entrySet.addEntry ("metrics", strMetricsName);
					entrySet.addEntry ("last_update", "" + sdf.format(udo.getLastUpdate()));
					entrySet.addEntry ("count_overall", "" + udo.getCounter());
					this.rms.addEntrySet(entrySet);		
				}
				
			}
			
			for (String strMetricsName : mapUsageDataMonths.keySet()) {			
								
				for(UsageDataMonth udm : mapUsageDataMonths.get(strMetricsName)) { 
					counter ++;
					RestEntrySet entrySet = new RestEntrySet(); 
					entrySet.addEntry ("object_id", object_id.toPlainString());
					entrySet.addEntry ("metrics", strMetricsName);
					entrySet.addEntry ("relative_to_date", sdf.format(udm.getRelativeToDate()));
					entrySet.addEntry ("count_of_month", "" + udm.getCounter());
					this.rms.addEntrySet(entrySet);
				
				}
			}
			
			this.rms.setStatus (RestStatusEnum.OK);
			
			stmtconn.commit();
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get UsageData: " + ex.getLocalizedMessage ( ), ex);
			this.rms.setStatus (RestStatusEnum.SQL_ERROR);
			this.rms.setStatusDescription (ex.getLocalizedMessage ( ));
			
		} catch (WrongStatementException ex) {
			
			logger.error ("An error occured while processing Get UsageData: " + ex.getLocalizedMessage ( ), ex);
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
				this.rms.setStatusDescription ("No " + RestKeyword.UsageData + " found for object_id " + object_id + ".");
			}
		}
		
		return RestXmlCodec.encodeRestMessage (this.rms);
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) throws NotEnoughParametersException {
		
		BigDecimal object_id = null;

		@SuppressWarnings("unused")
		int number = 0;

		if (path.length < 1)
			throw new NotEnoughParametersException(
					"This method needs at least 1 parameter: the internal object ID");

		try {

			object_id = new BigDecimal(path[0]);

		} catch (NumberFormatException ex) {

			logger.error(path[0] + " is NOT a number!", ex);

			this.rms = new RestMessage(RestKeyword.UsageData);
			this.rms.setStatus(RestStatusEnum.WRONG_PARAMETER);
			this.rms.setStatusDescription(path[0] + " is NOT a number!");

			return RestXmlCodec.encodeRestMessage(this.rms);
		}

		this.rms = RestXmlCodec.decodeRestMessage(data);
		RestEntrySet res = null;

		DBAccessNG dbng = new DBAccessNG (super.getDataSource ( ));
		MultipleStatementConnection stmtconn = null;
		
		String key = "";

		try {
			boolean errorHappended = false; 
			stmtconn = (MultipleStatementConnection) dbng.getMultipleStatementConnection();

			Iterator<RestEntrySet> itSets = this.rms.getListEntrySets().iterator();
			while (itSets.hasNext()) {
				res = itSets.next();

				String metrics_name = null;	
				BigDecimal metrics_id = null;
		        Date last_update = new Date();
				long count_overall = -1;
		        Date relative_to_date = HelperMethods.getFirstDayOfPreceedingMonth(new Date());
				long count_of_month = -1;
				
				Iterator<String> it = res.getKeyIterator();			
				while (it.hasNext()) {
					key = it.next();
					if (logger.isDebugEnabled())
						logger.debug("key = " + key);

					if (key.equalsIgnoreCase("metrics"))
						metrics_name = res.getValue(key);
					else if (key.equalsIgnoreCase("last_update"))
						try {
						    last_update = sdf.parse(res.getValue(key));
						} catch (ParseException pex) {
							logger.error(pex);
						}
					else if (key.equalsIgnoreCase("count_overall"))
						count_overall = Long.parseLong(res.getValue(key));
					else if (key.equalsIgnoreCase("relative_to_date"))
						try {
						    relative_to_date = sdf.parse(res.getValue(key));
						    relative_to_date = HelperMethods.getFirstDayOfGivenMonth(relative_to_date);
						} catch (ParseException pex) {
							logger.error(pex);
						} 
					else if (key.equalsIgnoreCase("count_of_month"))
						count_of_month = Long.parseLong(res.getValue(key));
					else
						continue;
				}

				this.rms = new RestMessage(RestKeyword.UsageData);
				
				BigDecimal bdMetricsID = getMetricsId(stmtconn, metrics_name);

				this.rms = new RestMessage(RestKeyword.UsageData);
				this.rms.setStatus(RestStatusEnum.INCOMPLETE_ENTRYSET_ERROR);
				
				// Prüfen, ob überhaupt Daten übergeben wurden
				
				if (metrics_name == null) {				 
					
					this.rms.setStatusDescription("POST /UsageData/ needs a 'metrics_name' in an entry.");
  				    return RestXmlCodec.encodeRestMessage(this.rms);
  				    
				} else if(bdMetricsID == null) {
					
					this.rms.setStatusDescription("Metrics named '" + metrics_name + "' are unknown to database.");
					return RestXmlCodec.encodeRestMessage(this.rms);
				
				} else if(count_overall == -1 || count_of_month == -1) {
					
					this.rms.setStatusDescription("POST /UsageData/ needs either entry 'count_overall' or 'count_of_month' in an entry.");
					return RestXmlCodec.encodeRestMessage(this.rms);
					
				}
				

				res = new RestEntrySet();

				/*
				stmtconn.loadStatement(InsertIntoDB.UsageDataClassification(
						stmtconn.connection, object_id, ddc_value, percentage));
				this.result = stmtconn.execute();
				
				if (this.result.getWarning ( ) != null) 
					for (Throwable warning : result.getWarning ( ))
						logger.warn (warning.getLocalizedMessage ( ));
				
				if (this.result.getUpdateCount() < 1) {
					errorHappended = true;
				
					// warn, error, rollback, nothing????
				}
				*/
			}
			if (errorHappended == false) { 
				stmtconn.commit();
			    this.rms.setStatus (RestStatusEnum.OK);
			} else {
				stmtconn.rollback();
				this.rms.setStatus(RestStatusEnum.SQL_ERROR);
				this.rms.setStatusDescription("could not be inserted");
				return RestXmlCodec.encodeRestMessage(this.rms);
			}
			
		} catch (NumberFormatException ex) {

				logger.error(res.getValue(key) + " is NOT a number!", ex);
				this.rms = new RestMessage(RestKeyword.UsageData);
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

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	
	@Override
	protected String putKeyWord (String [ ] path, String data) throws NotEnoughParametersException {
		this.rms = new RestMessage (RestKeyword.UsageData);
		this.rms.setStatus (RestStatusEnum.NOT_IMPLEMENTED_ERROR);
		this.rms.setStatusDescription("PUT method is not implemented for ressource '"+RestKeyword.UsageData+"'.");
		return RestXmlCodec.encodeRestMessage (this.rms);
	}
	
	private Map<String,List<UsageDataMonth>> getMapUsageDataMonth(MultipleStatementConnection stmtconn, BigDecimal object_id) throws SQLException {
		
		Map<String,List<UsageDataMonth>> mapUsageDataMonths = new HashMap<String,List<UsageDataMonth>>();
		stmtconn.loadStatement (SelectFromDB.UsageData_Metrics_AllNames(stmtconn.connection));
		this.result = stmtconn.execute ( );
		
		if (this.result.getWarning ( ) != null) 
			for (Throwable warning : result.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));

		while (this.result.getResultSet().next()) {
			String strMetricsName = this.result.getResultSet().getString(1);
			List<UsageDataMonth> listUsageDataMonths = new ArrayList<UsageDataMonth>();
			mapUsageDataMonths.put(strMetricsName, listUsageDataMonths);			
		}

		return mapUsageDataMonths;
	}
	
	private Map<String,UsageDataOverall> getMapUsageDataOverall(MultipleStatementConnection stmtconn, BigDecimal object_id) throws SQLException {
		
		Map<String,UsageDataOverall> mapUsageDataOveralls = new HashMap<String,UsageDataOverall>();
		stmtconn.loadStatement (SelectFromDB.UsageData_Metrics_AllNames(stmtconn.connection));
		this.result = stmtconn.execute ( );
		
		if (this.result.getWarning ( ) != null) 
			for (Throwable warning : result.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));

		while (this.result.getResultSet().next()) {
			String strMetricsName = this.result.getResultSet().getString(1);			
			mapUsageDataOveralls.put(strMetricsName, null);
		}

		return mapUsageDataOveralls;
	}
	
	private void fillMapUsageDataMonths(MultipleStatementConnection stmtconn, Map<String,List<UsageDataMonth>> mapUsageDataMonths, BigDecimal object_id) throws SQLException {
		
		for(String strMetricsName : mapUsageDataMonths.keySet()) {
			
			stmtconn.loadStatement (SelectFromDB.UsageData_Months_ListForMetricsName(stmtconn.connection, object_id, strMetricsName));
			this.result = stmtconn.execute ( );

			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
		
			while (this.result.getResultSet().next()) {
				UsageDataMonth usageDataMonth = new UsageDataMonth(object_id, strMetricsName, 0, null);
				usageDataMonth.setCounter(this.result.getResultSet().getLong(3));
				try {
                    usageDataMonth.setRelativeToDate(HelperMethods.sql2javaDate(this.result.getResultSet().getDate(4)));
				} catch(ParseException pex) {
					logger.error(pex);
				}
				mapUsageDataMonths.get(strMetricsName).add(usageDataMonth);
			}
		}
	}
	
	private void fillMapUsageDataOveralls(MultipleStatementConnection stmtconn, Map<String,UsageDataOverall> mapUsageDataOveralls, BigDecimal object_id) throws SQLException {
		
		for(String strMetricsName : mapUsageDataOveralls.keySet()) {
			
			stmtconn.loadStatement (SelectFromDB.UsageData_Months_ListForMetricsName(stmtconn.connection, object_id, strMetricsName));
			this.result = stmtconn.execute ( );

			if (this.result.getWarning ( ) != null) 
				for (Throwable warning : result.getWarning ( ))
					logger.warn (warning.getLocalizedMessage ( ));
		
			if (this.result.getResultSet().next()) {
				UsageDataOverall usageDataOverall = new UsageDataOverall(object_id, strMetricsName, 0, null);
				usageDataOverall.setCounter(this.result.getResultSet().getLong(3));
				try {
					usageDataOverall.setLastUpdate(HelperMethods.sql2javaDate(this.result.getResultSet().getDate(4)));
				} catch(ParseException pex) {
					logger.error(pex);
				}
				mapUsageDataOveralls.put(strMetricsName, usageDataOverall);
			}
		}
	}
	
	private BigDecimal getMetricsId(MultipleStatementConnection stmtconn, String strMetricsName) throws SQLException {
		stmtconn.loadStatement (SelectFromDB.UsageData_Metrics(stmtconn.connection, strMetricsName));
		this.result = stmtconn.execute ( );

		if (this.result.getWarning ( ) != null) 
			for (Throwable warning : result.getWarning ( ))
				logger.warn (warning.getLocalizedMessage ( ));
	
		if (this.result.getResultSet().next()) {
		    return this.result.getResultSet().getBigDecimal(1);	
		}
		
		return null;
	}
	
}

