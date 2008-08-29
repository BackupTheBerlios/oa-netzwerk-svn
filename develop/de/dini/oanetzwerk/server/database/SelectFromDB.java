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

public class SelectFromDB {
	
	static Logger logger = Logger.getLogger (SelectFromDB.class);
	
	/**
	 * @param connection
	 * @param object_id 
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement ObjectEntry (Connection connection, BigDecimal object_id) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.Object o WHERE o.object_id = ?");
		
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

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

	public static PreparedStatement ObjectEntry (Connection connection,
			BigDecimal repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier,
			boolean testdata, int failureCounter) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT object_id FROM dbo.Object WHERE repository_id = ? AND repository_identifier = ? AND repository_datestamp = ?");
		
		preparedstmt.setBigDecimal (1, repository_id);
		preparedstmt.setString (2, repository_identifier);
		preparedstmt.setDate (3, repository_datestamp);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param bigDecimal
	 * @param string
	 * @return
	 * @throws SQLException 
	 */

	public static PreparedStatement ObjectEntryID (Connection connection,
			BigDecimal repositoryID, String externalOID) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT o.object_id FROM dbo.Object o WHERE o.repository_id = ? and o.repository_identifier = ?");
		
		preparedstmt.setBigDecimal (1, repositoryID);
		preparedstmt.setString (2, externalOID);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement AllOIDs (Connection connection) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT o.object_id FROM dbo.Object o");
		return preparedstmt;
	}
	
	/**
	 * @param connection
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement AllOIDsMarkAsTest (Connection connection) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT o.object_id FROM dbo.Object o WHERE o.testdata = 1");
		return preparedstmt;
	}
	
	/**
	 * @param connection
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement AllOIDsMarkAsNotTest (Connection connection) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT o.object_id FROM dbo.Object o WHERE o.testdata = 0");
		return preparedstmt;
	}
	
	/**
	 * @param connection
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement AllOIDsMarkAsHasFulltextlink (Connection connection) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT DISTINCT object_id FROM dbo.FullTextLinks");
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement AllOIDsFromRepositoryID (Connection connection, BigDecimal repID) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT o.object_id FROM dbo.Object o WHERE (repository_id = ?)");
		preparedstmt.setBigDecimal (1, repID);
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement AllOIDsFromRepositoryIDMarkAsTest (Connection connection, BigDecimal repID) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT o.object_id FROM dbo.Object o WHERE (repository_id = ?) AND o.testdata = 1");
		preparedstmt.setBigDecimal (1, repID);
		return preparedstmt;
	}
	
	public static PreparedStatement Repository (Connection connection) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT repository_id, name, url FROM dbo.Repositories");
		
		return preparedstmt;
	}
	
	/**
	 * @param connection
	 * @param repositoryID
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Repository (Connection connection,
			BigDecimal repositoryID) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT name, url, oai_url, test_data, harvest_amount, harvest_pause FROM dbo.Repositories WHERE (repository_id = ?)");
		preparedstmt.setBigDecimal (1, repositoryID);
		
		return preparedstmt;
	}
	
	/**
	 * @param connection
	 * @param predecessor_id
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */
	
	public static PreparedStatement Workflow (Connection connection, BigDecimal predecessor_id, BigDecimal service_id) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT w1.object_id FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so ON w1.service_id = so.predecessor_id AND so.service_id = ? " + 
										"WHERE (w1.time > (SELECT MAX(time) FROM dbo.WorkflowDB WHERE object_id = w1.object_id AND service_id = so.service_id) " +
										"OR w1.object_id NOT IN (SELECT object_id FROM dbo.WorkflowDB WHERE object_id = w1.object_id AND service_id = so.service_id)) GROUP BY w1.object_id");
		
		preparedstmt.setBigDecimal (1, service_id);
		
		return preparedstmt;
	}
	
	public static PreparedStatement RawRecordDataHistory (Connection connection, BigDecimal internalOID) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.RawData WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, internalOID);
		
		return preparedstmt;
	}
	
	/**
	 * @param connection
	 * @param internalOID
	 * @param repository_timestamp
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement RawRecordData (Connection connection,
			BigDecimal internalOID, Date repository_timestamp) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.RawData rd WHERE rd.object_id = ? AND rd.repository_timestamp = ?");
		preparedstmt.setBigDecimal (1, internalOID);
		preparedstmt.setDate (2, repository_timestamp);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param internalOID
	 * @return
	 * @throws SQLException 
	 */

	public static PreparedStatement RawRecordData (Connection connection,
			BigDecimal internalOID) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.RawData rd WHERE rd.object_id = ? AND rd.repository_timestamp = (SELECT max(rdmax.repository_timestamp) FROM dbo.RawData rdmax WHERE rdmax.object_id = ?)");
		preparedstmt.setBigDecimal (1, internalOID);
		preparedstmt.setBigDecimal (2, internalOID);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param name
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Services (Connection connection, String name) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.Services WHERE name = ?");
		preparedstmt.setString (1, name);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param objectEntryID
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Services (Connection connection,
			BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT * FROM dbo.Services WHERE service_id = ?");
		preparedstmt.setBigDecimal (1, service_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement ServicesOrder (Connection connection,
			BigDecimal service_id) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT predecessor_id FROM dbo.ServicesOrder WHERE service_id = ?");
		preparedstmt.setBigDecimal (1, service_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement WorkflowDB (Connection connection,
			BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT w1.object_id FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so ON w1.service_id = so.predecessor_id AND so.service_id = ? " + 
				"WHERE (w1.time > (SELECT MAX(time) FROM dbo.WorkflowDB WHERE object_id = w1.object_id AND service_id = so.service_id) " +
				"OR w1.object_id NOT IN (SELECT object_id FROM dbo.WorkflowDB WHERE object_id = w1.object_id AND service_id = so.service_id)) GROUP BY w1.object_id");

		preparedstmt.setBigDecimal (1, service_id);

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

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT workflow_id FROM dbo.WorkflowDB WHERE object_id = ? AND time = ? AND service_id = ?");
		
		preparedstmt.setBigDecimal (1, object_id);
		preparedstmt.setDate (2, time);
		preparedstmt.setBigDecimal (3, service_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Title (Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT title, qualifier, lang FROM dbo.Titles WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Authors (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email FROM dbo.Person P JOIN dbo.Object2Author O ON P.person_id = O.person_id WHERE O.object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Editors (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email FROM dbo.Person P JOIN dbo.Object2Editor O ON P.person_id = O.person_id WHERE O.object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Contributors (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email FROM dbo.Person P JOIN dbo.Object2Contributor O ON P.person_id = O.person_id WHERE O.object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Format (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT schema_f, number FROM dbo.Format WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Identifier (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT identifier, number FROM dbo.Identifier WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Description (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT abstract, lang, number FROM dbo.Description WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement DateValues (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT number, value FROM dbo.DateValues WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement TypeValues (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT value FROM dbo.TypeValue WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Publisher (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT name, number FROM dbo.Publisher WHERE object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement DDCClassification (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT name, D.DDC_Categorie FROM dbo.DDC_Classification D JOIN dbo.DDC_Categories C ON D.DDC_Categorie = C.DDC_Categorie WHERE D.object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement DNBClassification (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT name, D.DNB_Categorie FROM dbo.DNB_Classification D JOIN dbo.DNB_Categories C ON D.DNB_Categorie = C.DNB_Categorie WHERE D.object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement DINISetClassification (
			Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT name, D.DINI_set_id FROM dbo.DINI_Set_Classification D JOIN dbo.DINI_Set_Categories C ON D.DINI_set_id = C.DINI_set_id WHERE D.object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement OtherClassification (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT name, D.other_id FROM dbo.Other_Classification D JOIN dbo.Other_Categories C ON D.other_id = C.other_id WHERE D.object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Keywords (Connection connection,
			BigDecimal object_id) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT keyword, lang FROM dbo.Keywords K JOIN dbo.Object2Keywords O ON K.keyword_id = O.keyword_id WHERE O.object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement Languages (Connection connection,
			BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT language, number FROM dbo.Language L JOIN dbo.Object2Language O ON L.language_id = O.language_id WHERE O.object_id = ?");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param firstname
	 * @param lastname
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement LatestPerson (Connection connection, String firstname,
			String lastname) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT MAX(person_id) FROM dbo.Person WHERE (firstname = ? AND lastname = ?)");
		preparedstmt.setString (1, firstname);
		preparedstmt.setString (2, lastname);
		
		return preparedstmt;
	}

	/**
	 * @param keyword
	 * @param language
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement LatestKeyword (Connection connection, String keyword,
			String language) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT MAX(keyword_id) FROM dbo.Keywords WHERE (keyword = ? AND lang = ?)");
		preparedstmt.setString (1, keyword);
		preparedstmt.setString (2, language);
		
		return preparedstmt;
	}

	/**
	 * @param language
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement LanguageByName (Connection connection, String language) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT language_id FROM dbo.Language WHERE (language = ?)");
		preparedstmt.setString (1, language);
		
		return preparedstmt;
	}

	/**
	 * @param categorie
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement DDCCategoriesByCategorie (Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT DDC_Categorie FROM dbo.DDC_Categories WHERE (DDC_Categorie = ?)");
		preparedstmt.setString (1, category);
		
		return preparedstmt;
	}

	/**
	 * @param value
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement DNBCategoriesByCategorie (Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT DNB_Categorie FROM dbo.DNB_Categories WHERE (DNB_Categorie = ?)");
		preparedstmt.setString (1, category);
		
		return preparedstmt;
	}

	/**
	 * @param value
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement DINISetCategoriesByName (Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT DINI_set_id FROM dbo.DINI_Set_Categories WHERE (name = ?)");
		preparedstmt.setString (1, category);
		
		return preparedstmt;
	}

	/**
	 * @param value
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement LatestOtherCategories (Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT MAX(other_id) FROM dbo.Other_Categories WHERE (name = ?)");
		preparedstmt.setString (1, category);
		
		return preparedstmt;
	}

	/**
	 * @param value
	 * @return
	 * @throws SQLException 
	 */
	
	public static PreparedStatement FullTextLinks (Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT object_id, mimeformat, link FROM dbo.FullTextLinks WHERE (object_id = ?)");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}

	/**
	 * @param value
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement ObjectServiceStatusAll (Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("select object_id, time, service_id from dbo.WorkflowDB GROUP BY object_id, service_id, time ORDER BY object_id, time DESC");
		
		return preparedstmt;
	}
	
	/**
	 * @param value
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement ObjectServiceStatusID (Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT object_id, time, service_id FROM dbo.WorkflowDB WHERE (object_id = ?) GROUP BY object_id, service_id, time ORDER BY object_id, time DESC ");
		preparedstmt.setBigDecimal (1, object_id);
		
		return preparedstmt;
	}
	
	/**
	 * @param value
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement ServiceNotify (Connection connection, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT service_id, inserttime, finishtime, urgent, complete FROM dbo.ServiceNotify WHERE (service_id = ?)");
		preparedstmt.setBigDecimal (1, service_id);
		
		return preparedstmt;
	}


	/**
	 * @param name
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement LoginData (Connection connection, String name) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT name, password, email, superuser FROM dbo.LoginData WHERE (name = ?)");
		preparedstmt.setString (1, name);
		
		return preparedstmt;
	}

	/**
	 * @param name in lower cases
	 * @return
	 * @throws SQLException 
	 */
	public static PreparedStatement LoginDataLowerCase (Connection connection, String name) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement ("SELECT LOWER(name), password, email, superuser FROM dbo.LoginData WHERE (name = ?)");
		preparedstmt.setString (1, name.toLowerCase());
		
		return preparedstmt;
	}

	
	
}
