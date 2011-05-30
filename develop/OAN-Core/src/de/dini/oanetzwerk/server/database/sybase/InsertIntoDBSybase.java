/**
 * 
 */

package de.dini.oanetzwerk.server.database.sybase;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.InsertIntoDB;

/**
 * @author Michael K&uuml;hn
 * 
 */

public class InsertIntoDBSybase implements InsertIntoDB {

	static Logger logger = Logger.getLogger(InsertIntoDBSybase.class);

	/**
	 * This creates the Prepared Statement for inserting a new Object into the
	 * Database.
	 * 
	 * @param connection
	 *            the connection instance
	 * @param repository_id
	 *            the Identifier of the repository, which have to exist in Table
	 *            Repositories
	 * @param harvested
	 *            the Date when the Object was harvested
	 * @param repository_datestamp
	 *            the timestamp when the record has been changed or created in
	 *            the repository
	 * @param repository_identifier
	 *            the record irdentifier within the reposititory
	 * @param testdata
	 *            whether this is just a test record or not
	 * @param failureCounter
	 *            how many failures already occured while harvesting this record
	 * @return the Prepared Statment to insert a new Object into the Database
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Object(Connection connection, BigDecimal repository_id, Date harvested, Date repository_datestamp,
	                String repository_identifier, boolean testdata, int failureCounter) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Object: INSERT INTO dbo.Object (repository_id, harvested, repository_datestamp, repository_identifier, testdata, failure_counter)"
			                + " VALUES ("
			                + repository_id
			                + ", "
			                + harvested
			                + ", "
			                + repository_datestamp
			                + ", "
			                + repository_identifier
			                + ", " + testdata + ", " + failureCounter + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Object (repository_id, harvested, repository_datestamp, repository_identifier, testdata, failure_counter) VALUES (?, ?, ?, ?, ?, ?)");

		preparedstmt.setBigDecimal(1, repository_id);
		preparedstmt.setDate(2, harvested);
		preparedstmt.setDate(3, repository_datestamp);
		preparedstmt.setString(4, repository_identifier);
		preparedstmt.setBoolean(5, testdata);
		preparedstmt.setInt(6, failureCounter);

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

	@Override
	public PreparedStatement RawRecordData(Connection connection, BigDecimal object_id, Date repository_timestamp, String data,
	                String metaDataFormat) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert RawData: INSERT INTO dbo.RawData (object_id, repository_timestamp, data, MetaDataFormat)" + " VALUES ("
			                + object_id + ", " + repository_timestamp + ", <data>, " + metaDataFormat + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.RawData (object_id, repository_timestamp, data, MetaDataFormat) VALUES (?, ?, ?, ?)");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setDate(2, repository_timestamp);
		preparedstmt.setString(3, data);
		preparedstmt.setString(4, metaDataFormat);

		return preparedstmt;
	}

	// /**
	// * @param connection
	// * @param object_id
	// * @param time
	// * @param service_id
	// * @return
	// * @throws SQLException
	// */
	// public PreparedStatement WorkflowDB (Connection connection,
	// BigDecimal object_id, Date time, BigDecimal service_id) throws
	// SQLException {
	//
	// if (logger.isDebugEnabled ( )) {
	//
	// logger.debug
	// ("Insert WorkflowDB: INSERT INTO dbo.WorkflowDB (object_id, time, service_id) "
	// +
	// "VALUES (" + object_id + ", " + time + ", " + service_id + ")");
	// }
	//
	// PreparedStatement preparedstmt = connection.prepareStatement
	// ("INSERT INTO dbo.WorkflowDB (object_id, time, service_id) VALUES (?, ?, ?)");
	//
	// preparedstmt.setBigDecimal (1, object_id);
	// preparedstmt.setDate (2, time);
	// preparedstmt.setBigDecimal (3, service_id);
	//
	// return preparedstmt;
	// }

	/**
	 * @param connection
	 * @param object_id
	 * @param time
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */
	@Override
	public PreparedStatement WorkflowDB(Connection connection, BigDecimal object_id, BigDecimal service_id) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert WorkflowDB: INSERT INTO dbo.WorkflowDB (object_id, time, service_id) " + "VALUES (" + object_id
			                + ",  time  set by db " + service_id + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.WorkflowDB (object_id, time, service_id) VALUES (?, GetDate(), ?)");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, service_id);

		return preparedstmt;
	}

	// /**
	// * @param connection
	// * @param object_id
	// * @param time
	// * @param service_id
	// * @return
	// * @throws SQLException
	// */
	// public PreparedStatement Worklist (Connection connection,
	// BigDecimal object_id, Date time, BigDecimal service_id) throws
	// SQLException {
	//
	// if (logger.isDebugEnabled ( )) {
	//
	// logger.debug
	// ("Insert WorkflowDB: INSERT INTO dbo.Worklist (object_id, time, service_id) "
	// +
	// "VALUES (" + object_id + ", " + time + ", " + service_id + ")");
	// }
	//
	// PreparedStatement preparedstmt = connection.prepareStatement
	// ("INSERT INTO dbo.Worklist (object_id, time, service_id) VALUES (?, ?, ?)");
	//
	// preparedstmt.setBigDecimal (1, object_id);
	// preparedstmt.setDate (2, time);
	// preparedstmt.setBigDecimal (3, service_id);
	//
	// return preparedstmt;
	// }

	/**
	 * @param object_id
	 * @param qualifier
	 * @param title
	 * @param lang
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Title(Connection connection, BigDecimal object_id, String qualifier, String title, String lang)
	                throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Title: INSERT INTO dbo.Titles (object_id, qualifier, title, lang) " + "VALUES (" + object_id + ", "
			                + qualifier + "," + title + ", " + lang + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Titles (object_id, qualifier, title, lang) VALUES (?,?,?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setString(2, qualifier);
		preparedstmt.setString(3, title);
		preparedstmt.setString(4, lang);

		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param extract_datestamp
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement DateValue(Connection connection, BigDecimal object_id, int number, Date extract_datestamp,
	                String origValue) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert DateValues: INSERT INTO dbo.DateValues (object_id, number, value, originalValue) " + "VALUES ("
			                + object_id + ", " + number + ", " + extract_datestamp + ", " + origValue + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.DateValues (object_id, number, value, originalValue) VALUES (?,?,?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setInt(2, number);
		preparedstmt.setDate(3, extract_datestamp);
		preparedstmt.setString(4, origValue);

		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param schema_f
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Format(Connection connection, BigDecimal object_id, int number, String schema_f) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Format: INSERT INTO dbo.Format (object_id, number, schema_f) " + "VALUES (" + object_id + ", " + number
			                + ", " + schema_f + ")");
		}

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.Format (object_id, number, schema_f) VALUES (?,?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setInt(2, number);
		preparedstmt.setString(3, schema_f);

		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param identifier
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Identifier(Connection connection, BigDecimal object_id, int number, String identifier)
	                throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Identifier: INSERT INTO dbo.Identifier (object_id, number, identifier) " + "VALUES (" + object_id + ", "
			                + number + ", " + identifier + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Identifier (object_id, number, identifier) VALUES (?,?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setInt(2, number);
		preparedstmt.setString(3, identifier);

		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param description
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Description(Connection connection, BigDecimal object_id, int number, String description)
	                throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Description: INSERT INTO dbo.Description (object_id, number, abstract) " + "VALUES (" + object_id + ", "
			                + number + ", " + description + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Description (object_id, number, abstract) VALUES (?,?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setInt(2, number);
		preparedstmt.setString(3, description);

		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param typeValue
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement TypeValue(Connection connection, BigDecimal object_id, String typeValue) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert TypeValue: INSERT INTO dbo.TypeValue (object_id, value) " + "VALUES (" + object_id + ", " + typeValue
			                + ")");
		}

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.TypeValue (object_id, value) VALUES (?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setString(2, typeValue);

		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param number
	 * @param name
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Publisher(Connection connection, BigDecimal object_id, int number, String name) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Publisher: INSERT INTO dbo.Publisher (object_id, number, name) " + "VALUES (" + object_id + ", " + number
			                + ", " + name + ")");
		}

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.Publisher (object_id, number, name) VALUES (?,?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setInt(2, number);
		preparedstmt.setString(3, name);

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

	@Override
	public PreparedStatement Person(Connection connection, String firstname, String lastname, String title, String institution,
	                String email) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Person: INSERT INTO dbo.Person (firstname, lastname, title, institution, email) " + "VALUES (" + firstname
			                + ", " + lastname + ", " + title + ", " + institution + ", " + email + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Person (firstname, lastname, title, institution, email) VALUES (?,?,?,?,?)");
		preparedstmt.setString(1, firstname);
		preparedstmt.setString(2, lastname);
		preparedstmt.setString(3, title);
		preparedstmt.setString(4, institution);
		preparedstmt.setString(5, email);

		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param person_id
	 * @param number
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Object2Author(Connection connection, BigDecimal object_id, BigDecimal person_id, int number)
	                throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Object2Author: INSERT INTO dbo.Object2Author (object_id, person_id, number) " + "VALUES (" + object_id
			                + ", " + person_id + ", " + number + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Object2Author (object_id, person_id, number) VALUES (?,?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, person_id);
		preparedstmt.setInt(3, number);

		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param person_id
	 * @param number
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Object2Editor(Connection connection, BigDecimal object_id, BigDecimal person_id, int number)
	                throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Object2Editor: INSERT INTO dbo.Object2Editor (object_id, person_id, number) " + "VALUES (" + object_id
			                + ", " + person_id + ", " + number + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Object2Editor (object_id, person_id, number) VALUES (?,?,?)");
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

	@Override
	public PreparedStatement Keyword(Connection connection, String keyword, String language) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Keyword: INSERT INTO dbo.Keywords (keyword, lang) " + "VALUES (" + keyword + ", " + language + ")");
		}

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.Keywords (keyword, lang) VALUES (?,?)");
		preparedstmt.setString(1, keyword);
		preparedstmt.setString(2, language);

		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param keyword_id
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Object2Keyword(Connection connection, BigDecimal object_id, BigDecimal keyword_id) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Object2Keyword: INSERT INTO dbo.Object2Keywords (object_id, keyword_id) " + "VALUES (" + object_id + ", "
			                + keyword_id + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Object2Keywords (object_id, keyword_id) VALUES (?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, keyword_id);

		return preparedstmt;
	}

	/**
	 * @param language
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Language(Connection connection, String language) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Language: INSERT INTO dbo.Language (language) VALUES (" + language + ")");
		}

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.Language (language) VALUES (?)");
		preparedstmt.setString(1, language);

		return preparedstmt;
	}

	/**
	 * @param language
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Iso639Language(Connection connection, String iso639language) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Language: INSERT INTO dbo.Iso639Language (iso639language) VALUES (" + iso639language + ")");
		}

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.Iso639Language (iso639language) VALUES (?)");
		preparedstmt.setString(1, iso639language);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @param language_id
	 * @param number
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Object2Language(Connection connection, BigDecimal object_id, BigDecimal language_id, int number)
	                throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Object2Language: INSERT INTO dbo.Object2Language (object_id, language_id, number) " + "VALUES ("
			                + object_id + ", " + language_id + ", " + number + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Object2Language (object_id, language_id, number) VALUES (?,?,?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, language_id);
		preparedstmt.setInt(3, number);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @param language_id
	 * @param number
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement Object2Iso639Language(Connection connection, BigDecimal object_id, BigDecimal language_id, int number)
	                throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert Object2Language: INSERT INTO dbo.Object2Iso639Language (object_id, language_id, number) " + "VALUES ("
			                + object_id + ", " + language_id + ", " + number + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Object2Iso639Language (object_id, language_id, number) VALUES (?,?,?)");
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

	@Override
	public PreparedStatement DDCClassification(Connection connection, BigDecimal object_id, String ddcValue) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert DDCClassification: INSERT INTO dbo.DDC_Classification (object_id, DDC_Categorie) " + "VALUES ("
			                + object_id + ", " + ddcValue + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.DDC_Classification (object_id, DDC_Categorie) VALUES (?, ?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setString(2, ddcValue);

		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param categorie
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement DNBClassification(Connection connection, BigDecimal object_id, String category) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert DNBClassification: INSERT INTO dbo.DNB_Classification (object_id, DNB_Categorie) " + "VALUES ("
			                + object_id + ", " + category + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.DNB_Classification (object_id, DNB_Categorie) VALUES (?, ?)");
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

	@Override
	public PreparedStatement DINISetClassification(Connection connection, BigDecimal object_id, BigDecimal dini_set_id)
	                throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert DINISetClassification: INSERT INTO dbo.DINI_Set_Classification (object_id, DINI_set_id) " + "VALUES ("
			                + object_id + ", " + dini_set_id + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.DINI_Set_Classification (object_id, DINI_set_id) VALUES (?, ?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, dini_set_id);

		return preparedstmt;
	}

	/**
	 * @param value
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement OtherCategories(Connection connection, String category) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert OtherCategories: INSERT INTO dbo.Other_Categories (name) " + "VALUES (" + category + ")");
		}

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.Other_Categories (name) VALUES (?)");
		preparedstmt.setString(1, category);

		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param other_id
	 * @return
	 * @throws SQLException
	 */

	@Override
	public PreparedStatement OtherClassification(Connection connection, BigDecimal object_id, BigDecimal other_id)
	                throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert OtherClassification: INSERT INTO dbo.Other_Classification (object_id, other_id) " + "VALUES (" + object_id
			                + ", " + other_id + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Other_Classification (object_id, other_id) VALUES (?, ?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, other_id);

		return preparedstmt;
	}

	/**
	 * @param object_id
	 * @param mimeformat
	 * @param link
	 * @return
	 * @throws SQLException
	 */
	@Override
	public PreparedStatement FullTextLinks(Connection connection, BigDecimal object_id, String mimeformat, String link)
	                throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert FullTextLinks: INSERT INTO dbo.FullTextLinks (object_id, mimeformat, link) VALUES " + "(" + object_id
			                + ", " + mimeformat + ", " + link + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.FullTextLinks (object_id, mimeformat, link) VALUES (?, ?, ?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setString(2, mimeformat);
		preparedstmt.setString(3, link);

		return preparedstmt;
	}

	/**
	 * @param service_id
	 * @param inserttime
	 * @param urgent
	 * @return
	 * @throws SQLException
	 */
	@Override
	public PreparedStatement ServiceNotify(Connection connection, BigDecimal service_id, String inserttime, boolean urgent,
	                boolean complete) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert ServiceNotify: INSERT INTO dbo.ServiceNotify (service_id, inserttime, urgent, complete) VALUES " + "("
			                + service_id + ", " + inserttime + ", " + urgent + ", " + complete + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.ServiceNotify (service_id, inserttime, urgent, complete) VALUES (?, ?, ?, ?)");
		preparedstmt.setBigDecimal(1, service_id);
		preparedstmt.setString(2, inserttime);
		preparedstmt.setBoolean(3, urgent);

		return preparedstmt;
	}

	/**
	 * @param name
	 * @param password
	 * @param email
	 * @param superuser
	 * @return
	 * @throws SQLException
	 */
	@Override
	public PreparedStatement LoginData(Connection connection, String name, String password, String email) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert LoginData: INSERT INTO dbo.LoginData (name, password, email) VALUES  " + "(" + name + ", " + password
			                + ", " + email + ")");
		}

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.LoginData (name, password, email) VALUES (?, ?, ?)");
		preparedstmt.setString(1, name);
		preparedstmt.setString(2, password);
		preparedstmt.setString(3, email);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @param duplicate_id
	 * @param percentage
	 * @return
	 * @throws SQLException
	 */
	@Override
	public PreparedStatement DuplicatePossibilities(Connection connection, BigDecimal object_id, BigDecimal duplicate_id,
	                BigDecimal percentage, BigDecimal reverse_percentage) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("INSERT INTO dbo.DuplicatePossibilities (object_id, duplicate_id, percentage, reverse_percentage) " + "VALUES ("
			                + object_id + ", " + duplicate_id + ", " + percentage + ", " + reverse_percentage + ")");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.DuplicatePossibilities (object_id, duplicate_id, percentage, reverse_percentage) VALUES (?, ?, ?, ?)");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, duplicate_id);
		preparedstmt.setBigDecimal(3, percentage);
		preparedstmt.setBigDecimal(4, reverse_percentage);

		return preparedstmt;
	}

	@Override
	public PreparedStatement InterpolatedDDCClassification(Connection connection, BigDecimal object_id, String ddcValue,
	                BigDecimal percentage) throws SQLException {

		if (logger.isDebugEnabled()) {
			logger.debug("Insert InterpolatedDDCClassification:");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Interpolated_DDC_Classification (object_id, Interpolated_DDC_Categorie, percentage) VALUES (?, ?, ?)");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setString(2, ddcValue);
		preparedstmt.setBigDecimal(3, percentage);

		return preparedstmt;
	}

	@Override
	public PreparedStatement UsageData_Months(Connection connection, BigDecimal object_id, BigDecimal metrics_id, long counter,
	                Date counted_for_date) throws SQLException {

		if (logger.isDebugEnabled()) {
			logger.debug("Insert UsageData_Months:");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.UsageData_Months (object_id, metrics_id, counter, counted_for_date) "
		                                + "VALUES (?, ?, ?, ?)");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, metrics_id);
		preparedstmt.setLong(3, counter);
		preparedstmt.setDate(4, counted_for_date);

		return preparedstmt;
	}

	@Override
	public PreparedStatement UsageData_Overall(Connection connection, BigDecimal object_id, BigDecimal metrics_id, long counter,
	                Date last_update) throws SQLException {

		if (logger.isDebugEnabled()) {
			logger.debug("Insert UsageData_Overall:");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.UsageData_Overall (object_id, metrics_id, counter, last_update) "
		                                + "VALUES (?, ?, ?, ?)");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, metrics_id);
		preparedstmt.setLong(3, counter);
		preparedstmt.setDate(4, last_update);

		return preparedstmt;
	}

	@Override
	public PreparedStatement Repository(Connection connection, String name, String url, String oaiUrl, Integer harvestAmount,
	                Integer harvestPause, boolean listRecords, boolean testData, boolean active) throws SQLException {

		if (logger.isDebugEnabled()) {
			logger.debug("Insert Repository:");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Repositories (name, url, oai_url, test_data, harvest_amount, harvest_pause, listrecords, active) "
		                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

		preparedstmt.setString(1, name);
		preparedstmt.setString(2, url);
		preparedstmt.setString(3, oaiUrl);
		preparedstmt.setBoolean(4, testData);
		preparedstmt.setInt(5, harvestAmount);
		preparedstmt.setInt(6, harvestPause);
		preparedstmt.setBoolean(7, listRecords);
		preparedstmt.setBoolean(8, active);

		return preparedstmt;
	}

	@Override
	public PreparedStatement Repository(Connection connection, String name, String url, String oaiUrl, String owner,
	                String ownerEmail, Integer harvestAmount, Integer harvestPause, boolean listRecords, boolean testData, boolean active)
	                throws SQLException {

		if (logger.isDebugEnabled()) {
			logger.debug("Insert Repository:");
		}

		PreparedStatement preparedstmt = connection
		                .prepareStatement("INSERT INTO dbo.Repositories (name, url, oai_url, test_data, harvest_amount, harvest_pause, listrecords, active, owner_technical, email_technical) "
		                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		preparedstmt.setString(1, name);
		preparedstmt.setString(2, url);
		preparedstmt.setString(3, oaiUrl);
		preparedstmt.setBoolean(4, testData);
		preparedstmt.setInt(5, harvestAmount);
		preparedstmt.setInt(6, harvestPause);
		preparedstmt.setBoolean(7, listRecords);
		preparedstmt.setBoolean(8, active);
		preparedstmt.setString(9, owner);
		preparedstmt.setString(10, ownerEmail);

		return preparedstmt;
	}

	
	@Override
	public PreparedStatement ServicesScheduling(Connection connection, String name, BigDecimal service_id, String status, String info, boolean periodic, Date nonperiodicDate, String periodicInterval, int periodicDays) throws SQLException {

		if (logger.isDebugEnabled()) {

			logger.debug("Insert ServicesScheduling: INSERT INTO dbo.ServicesScheduling (name, service_id, status, info, periodic, nonperiodic_date, periodic_interval_type, periodic_interval_days) VALUES " + "(" + name + ", " + service_id
			                + ", " + status + info + ", " + periodic + ", " + nonperiodicDate + ", " + periodicInterval + ", " + periodicDays + ")");
		}

		PreparedStatement preparedstmt = connection.prepareStatement("INSERT INTO dbo.ServicesScheduling (name, service_id, status, info, periodic, nonperiodic_date, periodic_interval_type, periodic_interval_days) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		preparedstmt.setString(1, name);
		preparedstmt.setBigDecimal(2, service_id);
		preparedstmt.setString(3, status);
		preparedstmt.setString(4, info);
		preparedstmt.setBoolean(5, periodic);
		preparedstmt.setDate(6, nonperiodicDate);
		preparedstmt.setString(7, periodicInterval);
		preparedstmt.setInt(8, periodicDays);

		return preparedstmt;
	}
}
