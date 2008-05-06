/**
 * 
 */

package de.dini.oanetzwerk.server.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * @author Michael K&uuml;hn
 *
 */

public class InsertIntoDB {

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
			BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier,
			boolean testdata, int failureCounter) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.Object (repository_id, harvested, repository_datestamp, repository_identifier, testdata, failure_counter) VALUES (?, ?, ?, ?, ?, ?)");
		
		preparedstmt.setBigDecimal (1, repository_id);
		preparedstmt.setDate (2, harvested);
		preparedstmt.setDate (3, repository_datestamp);
		preparedstmt.setString (4, repository_identifier);
		preparedstmt.setBoolean (5, testdata);
		preparedstmt.setInt (6, failureCounter);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @param repository_timestamp
	 * @param data
	 * @param metaDataFormat
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement RawRecordData (Connection connection,
			BigDecimal object_id, Date repository_timestamp, String data,
			String metaDataFormat) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.RawData (object_id, repository_timestamp, data, MetaDataFormat) VALUES (?, ?, ?, ?)");
		
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setDate (2, repository_timestamp);
		preparedstmt.setString (3, data);
		preparedstmt.setString (4, metaDataFormat);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @param time
	 * @param service_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement WorkflowDB (Connection connection,
			BigDecimal object_id, Date time, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.WorkflowDB (object_id, time, service_id) VALUES (?, ?, ?)");
		
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setDate (2, time);
		preparedstmt.setBigDecimal (3, service_id);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param qualifier
	 * @param title
	 * @param lang
	 * @return
	 */
	public static PreparedStatement Title (BigDecimal object_id,
			String qualifier, String title, String lang) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param extract_datestamp
	 * @return
	 */
	public static PreparedStatement DateValue (BigDecimal object_id,
			int number, Date extract_datestamp) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param schema_f
	 * @return
	 */
	public static PreparedStatement Format (BigDecimal object_id, int number,
			String schema_f) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param identifier
	 * @return
	 */
	public static PreparedStatement Identifier (BigDecimal object_id,
			int number, String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param description
	 * @return
	 */
	public static PreparedStatement Description (BigDecimal object_id,
			int number, String description) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param typeValue
	 * @return
	 */
	public static PreparedStatement TypeValue (BigDecimal object_id,
			String typeValue) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param name
	 * @return
	 */
	public static PreparedStatement Publisher (BigDecimal object_id,
			int number, String name) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param firstname
	 * @param lastname
	 * @param title
	 * @param institution
	 * @param email
	 * @return
	 */
	public static PreparedStatement Person (String firstname, String lastname,
			String title, String institution, String email) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param person_id
	 * @param number
	 * @return
	 */
	public static PreparedStatement Object2Author (BigDecimal object_id,
			BigDecimal person_id, int number) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param person_id
	 * @param number
	 * @return
	 */
	public static PreparedStatement Object2Editor (BigDecimal object_id,
			BigDecimal person_id, int number) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param keyword
	 * @param language
	 * @return
	 */
	public static PreparedStatement Keyword (String keyword, String language) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param keyword_id
	 * @return
	 */
	public static PreparedStatement Object2Keyword (BigDecimal object_id,
			BigDecimal keyword_id) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param language
	 * @return
	 */
	public static PreparedStatement Language (String language) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param object_id2
	 * @param language_id
	 * @param number
	 * @return
	 */
	public static PreparedStatement Object2Language (BigDecimal object_id,
			BigDecimal object_id2, BigDecimal language_id, int number) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param ddcValue
	 * @return
	 */
	public static PreparedStatement DDCClassification (BigDecimal object_id,
			String ddcValue) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param categorie
	 * @return
	 */
	public static PreparedStatement DNBClassification (BigDecimal object_id,
			String categorie) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param dini_set_id
	 * @return
	 */
	public static PreparedStatement DINISetClassification (
			BigDecimal object_id, BigDecimal dini_set_id) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param value
	 * @return
	 */
	public static PreparedStatement OtherCategories (String value) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param object_id
	 * @param other_id
	 * @return
	 */
	public static PreparedStatement OtherClassification (BigDecimal object_id,
			BigDecimal other_id) {

		// TODO Auto-generated method stub
		return null;
	}
}
