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
	 * @throws SQLException 
	 */
	public static PreparedStatement Title (Connection connection, BigDecimal object_id,
			String qualifier, String title, String lang) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.Titles (object_id, qualifier, title, lang) VALUES (?,?,?,?)");
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setString (2, qualifier);
		preparedstmt.setString (3, title);
		preparedstmt.setString (4, lang);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param extract_datestamp
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement DateValue (Connection connection, BigDecimal object_id,
			int number, Date extract_datestamp) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.DateValues (object_id, number, value) VALUES (?,?,?)");
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setInt (2, number);
		preparedstmt.setDate (3, extract_datestamp);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param schema_f
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Format (Connection connection, BigDecimal object_id, int number,
			String schema_f) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.Format (object_id, number, schema_f) VALUES (?,?,?)");
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setInt (2, number);
		preparedstmt.setString (3, schema_f);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param identifier
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Identifier (Connection connection, BigDecimal object_id,
			int number, String identifier) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.Identifier (object_id, number, identifier) VALUES (?,?,?)");
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setInt (2, number);
		preparedstmt.setString (3, identifier);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param description
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Description (Connection connection, BigDecimal object_id,
			int number, String description) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.Description (object_id, number, abstract) VALUES (?,?,?)");
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setInt (2, number);
		preparedstmt.setString (3, description);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param typeValue
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement TypeValue (Connection connection, BigDecimal object_id,
			String typeValue) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.TypeValue (object_id, value) VALUES (?,?)");
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setString (2, typeValue);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param name
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Publisher (Connection connection, BigDecimal object_id,
			int number, String name) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.Publisher (object_id, number, name) VALUES (?,?,?)");
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setInt (2, number);
		preparedstmt.setString (3, name);
		
		return preparedstmt;
	}

	/**
	 * @param firstname
	 * @param lastname
	 * @param title
	 * @param institution
	 * @param email
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Person (Connection connection, String firstname, String lastname,
			String title, String institution, String email) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.Person (firstname, lastname, title, institution, email) VALUES (?,?,?,?,?)");
		preparedstmt.setString (1, firstname);
		preparedstmt.setString (2, lastname);
		preparedstmt.setString (3, title);
		preparedstmt.setString (4, institution);
		preparedstmt.setString (5, email);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param person_id
	 * @param number
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Object2Author (Connection connection, BigDecimal object_id,
			BigDecimal person_id, int number) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.Object2Author (object_id, person_id, number) VALUES (?,?,?)");
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setBigDecimal (2, person_id);
		preparedstmt.setInt (3, number);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param person_id
	 * @param number
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Object2Editor (Connection connection, BigDecimal object_id,
			BigDecimal person_id, int number) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.Object2Editor (object_id, person_id, number) VALUES (?,?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, person_id);
		preparedstmt.setInt(3, number);
		
		return preparedstmt;
	}

	/**
	 * @param keyword
	 * @param language
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Keyword (Connection connection, String keyword, String language) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.Keywords (keyword, lang) VALUES (?,?)");
		preparedstmt.setString (1, keyword);
		preparedstmt.setString (2, language);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param keyword_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Object2Keyword (Connection connection, BigDecimal object_id,
			BigDecimal keyword_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.Object2Keywords (object_id, keyword_id) VALUES (?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, keyword_id);

		
		return preparedstmt;
	}

	/**
	 * @param language
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Language (Connection connection, String language) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.Language (language) VALUES (?)");
		preparedstmt.setString(1, language);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param object_id2
	 * @param language_id
	 * @param number
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement Object2Language (Connection connection, BigDecimal object_id,
			BigDecimal language_id, int number) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.Object2Language (object_id, language_id, number) VALUES (?,?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, language_id);
		preparedstmt.setInt(3, number);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param ddcValue
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement DDCClassification (Connection connection, BigDecimal object_id,
			String ddcValue) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.DDC_Classification (object_id, DDC_Categorie) VALUES (?, ?)");
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setString (2, ddcValue);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param categorie
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement DNBClassification (Connection connection, BigDecimal object_id,
			String category) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.DNB_Classification (object_id, DNB_Categorie) VALUES (?, ?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setString(2, category);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param dini_set_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement DINISetClassification (Connection connection, BigDecimal object_id,
			BigDecimal dini_set_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.DINI_Set_Classification (object_id, DINI_set_id) VALUES (?, ?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, dini_set_id);
		
		return preparedstmt;
	}

	/**
	 * @param value
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement OtherCategories (Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.Other_Categories (name) VALUES (?)");
		preparedstmt.setString(1, category);
		
		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param other_id
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement OtherClassification (Connection connection, BigDecimal object_id,
			BigDecimal other_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("INSERT INTO dbo.Other_Classification (object_id, other_id) VALUES (?, ?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, other_id);
		
		return preparedstmt;
	}
}
