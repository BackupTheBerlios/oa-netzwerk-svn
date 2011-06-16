package de.dini.oanetzwerk.server.database.postgres;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.UpdateFromDB;

public class UpdateInDBPostgres implements UpdateFromDB {
	static Logger logger = Logger.getLogger (UpdateInDBPostgres.class);

	/**
	 * @param connection
	 * @param repository_id
	 * @param harvested
	 * @param repository_datestamp
	 * @param repository_identifier
	 * @param testdata
	 * @param failureCounter
	 * @return
	 * @throws SQLException 
	 */
	
	public PreparedStatement Object (Connection connection,
			BigDecimal object_id, BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier,
			boolean testdata, int failureCounter, boolean peculiar, boolean outdated, int peculiarCounter) throws SQLException {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Updating Object: UPDATE \"Object\" SET harvested = " + harvested +
					", repository_datestamp = " + repository_datestamp + ", testdata = " + testdata +
					", failure_counter = " + failureCounter + " WHERE object_id = " + object_id);
		}
		
		PreparedStatement preparedstmt = connection.prepareStatement (
				"UPDATE \"Object\" " +
				"SET " +
				"	harvested = ?, " +
				"	repository_datestamp = ?, " +
				"	testdata = ?, " +
				"	failure_counter = ?, " +
				"	peculiar = ?, " +
				"	outdated = ?, " +
				"	peculiar_counter = ? " +
				"WHERE " +
				"		object_id = ? " +
				"	AND " +
				"		repository_id = ?");
		preparedstmt.setDate (1, harvested);
		preparedstmt.setDate (2, repository_datestamp);
		preparedstmt.setBoolean (3, testdata);
		preparedstmt.setInt (4, failureCounter);
		preparedstmt.setBoolean (5, peculiar);
		preparedstmt.setBoolean (6, outdated);
		preparedstmt.setInt (7, peculiarCounter);
		preparedstmt.setBigDecimal (8, object_id);
		preparedstmt.setBigDecimal (9, repository_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @param data
	 * @return
	 * @throws SQLException 
	 */
	
	public PreparedStatement PrecleanedData (Connection connection,
			BigDecimal object_id, Date repositoryDateStamp, String data) throws SQLException {
		
		return PrecleanedData (connection, object_id, repositoryDateStamp, "oai_dc", data);
	}
	
	public PreparedStatement PrecleanedData (Connection connection,
			BigDecimal object_id, Date repositoryDateStamp, String metaDataFormat, String data) throws SQLException {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Updating PrecleanedData: UPDATE \"RawData\" SET precleaned_data = <data>" +
					" WHERE object_id = " + object_id + " AND repository_timestamp = " + repositoryDateStamp +
					" AND MetaDataFormat = " + metaDataFormat);
		}
		
		PreparedStatement preparedstmt = connection.prepareStatement (
				"UPDATE \"RawData\" " +
				"SET " +
				"	precleaned_data = ? " +
				"WHERE " +
				"		object_id = ? " +
				"	AND " +
				"		repository_timestamp = ? " +
				"	AND " +
				"		\"MetaDataFormat\" = ?");
		preparedstmt.setString (1, data);
		preparedstmt.setBigDecimal (2, object_id);
		preparedstmt.setDate(3, repositoryDateStamp);
		preparedstmt.setString(4, metaDataFormat);
		
		return preparedstmt;
	}
	
	
	public PreparedStatement LoginData (Connection connection,
			String name, String password, String email) throws SQLException {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Updating PrecleanedData: UPDATE \"LoginData\" SET password = " + password + 
					" WHERE name = " + name + " AND email = " + email);
		}
		
		PreparedStatement preparedstmt = connection.prepareStatement (
				"UPDATE \"LoginData\" " +
				"SET " +
				"	password = ? " +
				"WHERE " +
				"		name = ? " +
				"	AND " +
				"		email = ?");

		preparedstmt.setString (1, password);
		preparedstmt.setString (2, name);
		preparedstmt.setString (3, email);
		
		return preparedstmt;
	}
	
	public PreparedStatement Repository (final Connection connection,
			final BigDecimal repository_id, final Date dateToSet, final String dateField) throws SQLException {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Updating Repository: UPDATE \"Repositories\" SET " + dateField + " = " + dateToSet +
					" WHERE repository_id = " + repository_id);
		}

		PreparedStatement preparedstmt = connection.prepareStatement (
			"UPDATE \"Repositories\" " +
			"SET " + dateField + " = ? WHERE repository_id = ?");
		preparedstmt.setDate(1, dateToSet);
		preparedstmt.setBigDecimal(2, repository_id);
		
		return preparedstmt;
	}
	
	public PreparedStatement Repository (final Connection connection,
			final Long repository_id, final boolean active) throws SQLException {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Updating Repository: UPDATE \"Repositories\" SET active = " + active +
					" WHERE repository_id = " + repository_id);
		}

		PreparedStatement preparedstmt = connection.prepareStatement (
			"UPDATE \"Repositories\" " +
			"SET active = ? WHERE repository_id = ?");
		preparedstmt.setBoolean(1, active);
		preparedstmt.setLong(2, repository_id);
		
		return preparedstmt;
	}
	
	
	@Override
	public PreparedStatement ServicesScheduling(Connection connection, String name, BigDecimal service_id, String status, String info, boolean periodic, Date nonperiodicDate, String periodicInterval, int periodicDays, int jobId) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Updating ServicesScheduling: UPDATE \"ServicesScheduling\" (name, service_id, status, info, periodic, nonperiodic_date, periodic_interval_type, periodic_interval_days) VALUES " + "(" + name + ", " + service_id
			                + ", " + status + info + ", " + periodic + ", " + nonperiodicDate + ", " + periodicInterval + ", " + periodicDays + ")");
		}

		PreparedStatement preparedstmt = connection.prepareStatement("UPDATE \"ServicesScheduling\" SET name = ?, service_id = ?, status = ?, info = ?, periodic = ?, nonperiodic_date = ?, periodic_interval_type = ?, periodic_interval_days = ? WHERE job_id = ?");
		preparedstmt.setString(1, name);
		preparedstmt.setBigDecimal(2, service_id);
		preparedstmt.setString(3, status);
		preparedstmt.setString(4, info);
		preparedstmt.setBoolean(5, periodic);
		preparedstmt.setDate(6, nonperiodicDate);
		preparedstmt.setString(7, periodicInterval);
		preparedstmt.setInt(8, periodicDays);
		preparedstmt.setInt(9, jobId);

		return preparedstmt;
	}
	
	
	@Override
	public PreparedStatement ServicesScheduling(Connection connection, String name, String status) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("UPDATE \"ServicesScheduling\" SET status = " + status + " WHERE name = " + name);
		}

		PreparedStatement preparedstmt = connection.prepareStatement("UPDATE \"ServicesScheduling\" SET status = ? WHERE name = ?");
		preparedstmt.setString(1, status);
		preparedstmt.setString(2, name);


		return preparedstmt;
	}
}
