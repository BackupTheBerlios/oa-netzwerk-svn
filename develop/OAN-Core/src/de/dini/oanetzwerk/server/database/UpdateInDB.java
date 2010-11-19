/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @author Michael K&uuml;hn
 *
 */

public class UpdateInDB {

	static Logger logger = Logger.getLogger (UpdateInDB.class);

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
	
	public static PreparedStatement Object (Connection connection,
			BigDecimal object_id, BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier,
			boolean testdata, int failureCounter, boolean peculiar, boolean outdated, int peculiarCounter) throws SQLException {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Updating Object: UPDATE dbo.Object SET harvested = " + harvested +
					", repository_datestamp = " + repository_datestamp + ", testdata = " + testdata +
					", failure_counter = " + failureCounter + " WHERE object_id = " + object_id);
		}
		
		PreparedStatement preparedstmt = connection.prepareStatement ("UPDATE dbo.Object SET harvested = ?, repository_datestamp = ?, testdata = ?, failure_counter = ?, peculiar = ?, outdated = ?, peculiar_counter = ? WHERE object_id = ? AND repository_id = ?");
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
	
	public static PreparedStatement PrecleanedData (Connection connection,
			BigDecimal object_id, Date repositoryDateStamp, String data) throws SQLException {
		
		return PrecleanedData (connection, object_id, repositoryDateStamp, "oai_dc", data);
	}
	
	public static PreparedStatement PrecleanedData (Connection connection,
			BigDecimal object_id, Date repositoryDateStamp, String metaDataFormat, String data) throws SQLException {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Updating PrecleanedData: UPDATE dbo.RawData SET precleaned_data = <data>" +
					" WHERE object_id = " + object_id + " AND repository_timestamp = " + repositoryDateStamp +
					" AND MetaDataFormat = " + metaDataFormat);
		}
		
		PreparedStatement preparedstmt = connection.prepareStatement ("UPDATE dbo.RawData SET precleaned_data = ? WHERE object_id = ? AND repository_timestamp = ? AND MetaDataFormat = ?");
		preparedstmt.setString (1, data);
		preparedstmt.setBigDecimal (2, object_id);
		preparedstmt.setDate(3, repositoryDateStamp);
		preparedstmt.setString(4, metaDataFormat);
		
		return preparedstmt;
	}
	
	
	public static PreparedStatement LoginData (Connection connection,
			String name, String password, String email) throws SQLException {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Updating PrecleanedData: UPDATE dbo.LoginData SET password = " + password + 
					" WHERE name = " + name + " AND email = " + email);
		}
		
		PreparedStatement preparedstmt = connection.prepareStatement ("UPDATE dbo.LoginData SET password = ? WHERE name = ? AND email = ?");

		preparedstmt.setString (1, password);
		preparedstmt.setString (2, name);
		preparedstmt.setString (3, email);
		
		return preparedstmt;
	}
	
	public static PreparedStatement Repository (final Connection connection,
			final BigDecimal repository_id, final Date dateToSet, final String dateField) throws SQLException {
		
		if (logger.isDebugEnabled ( )) {
			
			logger.debug ("Updating Repository: UPDATE dbo.Repositories SET " + dateField + " = " + dateToSet +
					" WHERE repository_id = " + repository_id);
		}

		PreparedStatement preparedstmt = connection.prepareStatement ("UPDATE dbo.Repositories SET " + dateField + " = ? WHERE repository_id = ?");
		preparedstmt.setDate(1, dateToSet);
		preparedstmt.setBigDecimal(2, repository_id);
		
		return preparedstmt;
	}
	
}
