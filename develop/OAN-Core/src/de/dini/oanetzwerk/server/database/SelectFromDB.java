package de.dini.oanetzwerk.server.database;

import java.awt.image.DataBufferDouble;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Michael K&uuml;hn
 * 
 */

public class SelectFromDB {

	/**
	 * 
	 */

	private static Logger logger = Logger.getLogger(SelectFromDB.class);

	/**
	 * Fetch the internal object id for an original repository_identifier.
	 * 
	 * 
	 * @param connection
	 * @param repository_identifier
	 * @return
	 * @throws SQLException
	 */	
	public static PreparedStatement InternalID(Connection connection, String repository_identifier) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT o.object_id FROM dbo.Object o WHERE o.repository_identifier = ?");

		preparedstmt.setString(1, repository_identifier);

		return preparedstmt;
	}
	
	
	/**
	 * Fetch all information for the object specified by "object_id"
	 * 
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement ObjectEntry(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM dbo.Object o WHERE o.object_id = ?");

		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}
	
	/**
	 * Fetch all information for the object specified by "repository_identifier"
	 * 
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement ObjectEntry(Connection connection, String repository_identifier) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM dbo.Object o WHERE o.repository_identifier = ?");

		preparedstmt.setString(1, repository_identifier);

		return preparedstmt;
	}


	/**
	 * 
	 * Find the object_id of an object with the given information
	 * "repository_id", "repository_identifier" and "repository_datestamp"
	 * 
	 * @param connection
	 * @param repository_id
	 * @param repository_datestamp
	 * @param repository_identifier
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement ObjectEntry(Connection connection, BigDecimal repository_id, Date repository_datestamp,
			String repository_identifier) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT object_id FROM dbo.Object WHERE repository_id = ? AND repository_identifier = ? AND repository_datestamp = ?");

		preparedstmt.setBigDecimal(1, repository_id);
		preparedstmt.setString(2, repository_identifier);
		preparedstmt.setDate(3, repository_datestamp);

		return preparedstmt;
	}

	/**
	 * Find the object_id of an object with the given information
	 * "repository_id", "externalOID" which in fact is the
	 * "repository_identifier"
	 * 
	 * @param connection
	 * @param bigDecimal
	 * @param string
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement ObjectEntryID(Connection connection, BigDecimal repositoryID, String externalOID) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT o.object_id FROM dbo.Object o WHERE o.repository_id = ? and o.repository_identifier = ?");

		preparedstmt.setBigDecimal(1, repositoryID);
		preparedstmt.setString(2, externalOID);

		return preparedstmt;
	}

	/**
	 * 
	 * Find the objects for a specified repository and object ids which are
	 * higher than the specified offset
	 * 
	 * @param connection
	 * @param repository_id
	 * @param repository_datestamp
	 * @param repository_identifier
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement ObjectEntry(Connection connection, BigDecimal repository_id, BigDecimal oid_offset) throws SQLException {

		// TODO: make batch size configurable
		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT TOP 1000 * FROM dbo.Object o WHERE o.repository_id = ? AND o.object_id > ?");
		preparedstmt.setBigDecimal(1, repository_id);
		preparedstmt.setBigDecimal(2, oid_offset);

		return preparedstmt;
	}

	/**
	 * 
	 * Fetch all "object_id"-values of all objects
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement AllOIDs(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT o.object_id FROM dbo.Object o");
		return preparedstmt;
	}

	/**
	 * Fetch all "object_id"-values of all objects which are marked as
	 * "testdata"
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement AllOIDsMarkAsTest(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT o.object_id FROM dbo.Object o WHERE o.testdata = 1");
		return preparedstmt;
	}

	/**
	 * Fetch all "object_id"-values of all objects which are NOT marked as
	 * "testdata"
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement AllOIDsMarkAsNotTest(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT o.object_id FROM dbo.Object o WHERE o.testdata = 0");
		return preparedstmt;
	}

	/**
	 * Fetch all "object_id"-values of all objects with an entry in the
	 * "FullTextLinks"-table, that are all objects with a link to the full text
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement AllOIDsMarkAsHasFulltextlink(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT DISTINCT object_id FROM dbo.FullTextLinks");
		return preparedstmt;
	}

	/**
	 * Fetch all "object_id"-values for the given repository "repID"
	 * 
	 * @param connection
	 * @param repID
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement AllOIDsFromRepositoryID(Connection connection, BigDecimal repID) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT o.object_id FROM dbo.Object o WHERE (repository_id = ?)");
		preparedstmt.setBigDecimal(1, repID);
		return preparedstmt;
	}

	/**
	 * Fetch all "object_id"-values for the given repository "repID" that are
	 * marked as testdata
	 * 
	 * @param connection
	 * @param repID
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement AllOIDsFromRepositoryIDMarkAsTest(Connection connection, BigDecimal repID) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT o.object_id FROM dbo.Object o WHERE (repository_id = ?) AND o.testdata = 1");
		preparedstmt.setBigDecimal(1, repID);
		return preparedstmt;
	}

	/**
	 * Fetch the repository_id, the name and the url of all repositories in the
	 * database
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement Repository(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT name, repository_id, url, oai_url, test_data, harvest_amount, harvest_pause FROM dbo.Repositories");

		return preparedstmt;
	}

	/**
	 * Fetch the name, url, oai_url, test_data, harvest_amount and harvest_pause
	 * data for the specified repository
	 * 
	 * @param connection
	 * @param repositoryID
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement Repository(Connection connection, BigDecimal repositoryID) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT name, url, oai_url, test_data, harvest_amount, harvest_pause, last_full_harvest_begin, last_markereraser_begin FROM dbo.Repositories WHERE (repository_id = ?)");
		preparedstmt.setBigDecimal(1, repositoryID);

		return preparedstmt;
	}

//	/**
//	 * @param connection
//	 * @param predecessor_id
//	 * @param service_id
//	 * @return
//	 * @throws SQLException
//	 */
//	public static PreparedStatement Workflow(Connection connection, BigDecimal predecessor_id, BigDecimal service_id) throws SQLException {
//
//		PreparedStatement preparedstmt = connection
//				.prepareStatement("SELECT w1.object_id FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so ON w1.service_id = so.predecessor_id AND so.service_id = ? "
//						+ "WHERE (w1.time > (SELECT MAX(time) FROM dbo.WorkflowDB WHERE object_id = w1.object_id AND service_id = so.service_id) "
//						+ "OR w1.object_id NOT IN (SELECT object_id FROM dbo.WorkflowDB WHERE object_id = w1.object_id AND service_id = so.service_id)) GROUP BY w1.object_id");
//
//		preparedstmt.setBigDecimal(1, service_id);
//
//		return preparedstmt;
//	}

	/**
	 * Fetch all "RawRecord"-data for the specified object_id ("internalOID")
	 * 
	 * @param connection
	 * @param internalOID
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement RawRecordDataHistory(Connection connection, BigDecimal internalOID) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM dbo.RawData WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, internalOID);

		return preparedstmt;
	}

	/**
	 * Fetch the RawRecord-data for the specified object_id and the given date,
	 * should return only one record set
	 * 
	 * @param connection
	 * @param internalOID
	 * @param repository_timestamp
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement RawRecordData(Connection connection, BigDecimal internalOID, Date repository_timestamp)
			throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT * FROM dbo.RawData rd WHERE rd.object_id = ? AND rd.repository_timestamp = ?");
		preparedstmt.setBigDecimal(1, internalOID);
		preparedstmt.setDate(2, repository_timestamp);

		return preparedstmt;
	}

	/**
	 * 
	 * Fetch the RawRecord-data for the specified object_id, it delivers the
	 * newest entry for the given object
	 * 
	 * @param connection
	 * @param internalOID
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement RawRecordData(Connection connection, BigDecimal internalOID) throws SQLException {

		logger.debug("SELECT * FROM dbo.RawData");

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT * FROM dbo.RawData rd WHERE rd.object_id = ? AND rd.repository_timestamp = (SELECT max(rdmax.repository_timestamp) FROM dbo.RawData rdmax WHERE rdmax.object_id = ?)");
		preparedstmt.setBigDecimal(1, internalOID);
		preparedstmt.setBigDecimal(2, internalOID);

		return preparedstmt;
	}

	/**
	 * Fetch all information, including the service-id, for the service
	 * specified by name
	 * 
	 * @param connection
	 * @param name
	 *            String representing the name of the service
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement Services(Connection connection, String name) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM dbo.Services WHERE name = ?");
		preparedstmt.setString(1, name);

		return preparedstmt;
	}

	/**
	 * Fetch all information, including the service-id, for the service
	 * specified by the service_id
	 * 
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement Services(Connection connection, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM dbo.Services WHERE service_id = ?");
		preparedstmt.setBigDecimal(1, service_id);

		return preparedstmt;
	}

	/**
	 * Fetch the id of the service that is predecessor to the service specified
	 * by service_id. Used in conjunction with workflow queries.
	 * 
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement ServicesOrder(Connection connection, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT predecessor_id FROM dbo.ServicesOrder WHERE service_id = ?");
		preparedstmt.setBigDecimal(1, service_id);

		return preparedstmt;
	}

//	/**
//	 * @param connection
//	 * @param service_id
//	 * @return
//	 * @throws SQLException
//	 */
//	public static PreparedStatement WorkflowDBTimeAsString(Connection connection, BigDecimal service_id) throws SQLException {
//
//		PreparedStatement preparedstmt = connection
//				.prepareStatement("SELECT w1.object_id, CAST(max(w1.time) FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so ON w1.service_id = so.predecessor_id  WHERE so.service_id = ? GROUP BY w1.object_id");
//
//		preparedstmt.setBigDecimal(1, service_id);
//
//		return preparedstmt;
//	}

	/**
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement WorkflowDB(Connection connection, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT w1.object_id, max(w1.time) FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so ON w1.service_id = so.predecessor_id  WHERE so.service_id = ? GROUP BY w1.object_id");

		preparedstmt.setBigDecimal(1, service_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement WorkflowDBComplete(Connection connection, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT * FROM "
						+ "(SELECT w1.object_id, max(w1.time) as time FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so ON w1.service_id = so.predecessor_id  WHERE so.service_id = ? GROUP BY w1.object_id "
						+ "UNION "
						+ "SELECT w1.object_id, max(w1.time) as time FROM dbo.Worklist w1 WHERE w1.service_id = ? GROUP BY w1.object_id ) AS list "
						+ "ORDER BY time");

		preparedstmt.setBigDecimal(1, service_id);
		preparedstmt.setBigDecimal(2, service_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement WorkflowDB(Connection connection, BigDecimal service_id, BigDecimal repository_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT w1.object_id, w1.time FROM dbo.WorkflowDB w1 JOIN dbo.ServicesOrder so ON w1.service_id = so.predecessor_id  JOIN dbo.Object o ON w1.object_id = o.object_id WHERE so.service_id = ? and repository_id = ? ");

		preparedstmt.setBigDecimal(1, service_id);
		preparedstmt.setBigDecimal(2, repository_id);

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

	public static PreparedStatement WorkflowDBInserted(Connection connection, BigDecimal object_id, BigDecimal service_id)
			throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT workflow_id  FROM dbo.WorkflowDB WHERE (object_id = ? AND service_id = ?) GROUP BY object_id, time, service_id HAVING time=max(time)");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, service_id);

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

	public static PreparedStatement WorkflowDB(Connection connection, BigDecimal object_id, Date time, BigDecimal service_id)
			throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT workflow_id FROM dbo.WorkflowDB WHERE object_id = ? AND time = ? AND service_id = ?");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setDate(2, time);
		preparedstmt.setBigDecimal(3, service_id);

		return preparedstmt;
	}

	/**
	 * Fetch the title-information of the object specified by the object_id;
	 * returns title, qualifier and language
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement Title(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT title, qualifier, lang FROM dbo.Titles WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the author-information of the object specified by the object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement Authors(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email FROM dbo.Person P JOIN dbo.Object2Author O ON P.person_id = O.person_id WHERE O.object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the editor information of the object specified by the object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement Editors(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email FROM dbo.Person P JOIN dbo.Object2Editor O ON P.person_id = O.person_id WHERE O.object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the contributor information of the object specified by the
	 * object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement Contributors(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email FROM dbo.Person P JOIN dbo.Object2Contributor O ON P.person_id = O.person_id WHERE O.object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the format information of the object specified by the object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement Format(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT schema_f, number FROM dbo.Format WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the Identifier information of the object specified by the
	 * object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement Identifier(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT identifier, number FROM dbo.Identifier WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the Description information of the object specified by the
	 * object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement Description(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT abstract, lang, number FROM dbo.Description WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the DateValues information of the object specified by the
	 * object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement DateValues(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT number, value, originalValue FROM dbo.DateValues WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the TypeValues information of the object specified by the
	 * object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement TypeValues(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT value FROM dbo.TypeValue WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the Publisher information of the object specified by the
	 * object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement Publisher(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT name, number FROM dbo.Publisher WHERE object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the DDC classification information of the object specified by the
	 * object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement DDCClassification(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT name, D.DDC_Categorie FROM dbo.DDC_Classification D JOIN dbo.DDC_Categories C ON D.DDC_Categorie = C.DDC_Categorie WHERE D.object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the DNB classification information of the object specified by the
	 * object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement DNBClassification(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT name, D.DNB_Categorie FROM dbo.DNB_Classification D JOIN dbo.DNB_Categories C ON D.DNB_Categorie = C.DNB_Categorie WHERE D.object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the DINI-Set-Classification information of the object specified
	 * by the object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement DINISetClassification(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT name, D.DINI_set_id FROM dbo.DINI_Set_Classification D JOIN dbo.DINI_Set_Categories C ON D.DINI_set_id = C.DINI_set_id WHERE D.object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the Other Classifcation information of the object specified by
	 * the object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement OtherClassification(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT name, D.other_id FROM dbo.Other_Classification D JOIN dbo.Other_Categories C ON D.other_id = C.other_id WHERE D.object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}
	
	/**
	 * Combined search for multiple Set-Classifications (DINI,DNB,DDC and Repository-Association) of the object specified
	 * by the object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement AllClassifications(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT name, 'ddc:' + D.DDC_Categorie AS \"set\" FROM dbo.DDC_Classification D JOIN dbo.DDC_Categories C ON D.DDC_Categorie = C.DDC_Categorie WHERE D.object_id = ?" +
									" UNION " +
								"SELECT name, 'dnb:' + D2.DNB_Categorie AS \"set\" FROM dbo.DNB_Classification D2 JOIN dbo.DNB_Categories C2 ON D2.DNB_Categorie = C2.DNB_Categorie WHERE D2.object_id = ?" +
									" UNION " +
								"SELECT name, 'dini:' + convert(univarchar, D3.DINI_set_id) AS \"set\" FROM dbo.DINI_Set_Classification D3 JOIN dbo.DINI_Set_Categories C3 ON D3.DINI_set_id = C3.DINI_set_id WHERE D3.object_id = ?" +
									" UNION " +
								"SELECT rs.name , rs.name AS \"set\" FROM dbo.Repository_Sets rs JOIN dbo.Object o ON rs.repository_id = o.repository_id WHERE o.object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, object_id);
		preparedstmt.setBigDecimal(3, object_id);
		preparedstmt.setBigDecimal(4, object_id);

		return preparedstmt;
	}

	public static PreparedStatement AllClassifications(Connection connection, List<BigDecimal> ids) throws SQLException {

		if (ids == null || ids.isEmpty())
		{
			logger.warn("Couldn't prepare query to fetch all classifications, as ID-List was empty!");
			return null;
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT D.object_id, name, 'ddc:' + D.DDC_Categorie AS \"set\" FROM dbo.DDC_Classification D JOIN dbo.DDC_Categories C ON D.DDC_Categorie = C.DDC_Categorie WHERE ");

		for (int i = 0; i< ids.size(); i++) {
			sql.append("D.object_id = ? OR ");
		}
		sql.delete(sql.length()- 3, sql.length()); // cut off last 'OR '
		
		sql.append(" UNION " +
					"SELECT D2.object_id, name, 'dnb:' + D2.DNB_Categorie AS \"set\" FROM dbo.DNB_Classification D2 JOIN dbo.DNB_Categories C2 ON D2.DNB_Categorie = C2.DNB_Categorie WHERE ");
		
		for (int i = 0; i< ids.size(); i++) {
			sql.append("D2.object_id = ? OR ");
		}
		sql.delete(sql.length()- 3, sql.length());
		
		sql.append(" UNION " +
					"SELECT D3.object_id, convert(univarchar, D3.DINI_set_id), 'dini:' + name AS \"set\" FROM dbo.DINI_Set_Classification D3 JOIN dbo.DINI_Set_Categories C3 ON D3.DINI_set_id = C3.DINI_set_id WHERE ");
		
		for (int i = 0; i< ids.size(); i++) {
			sql.append("D3.object_id = ? OR ");
		}
		sql.delete(sql.length()- 3, sql.length());
		
		sql.append(" UNION " +
					"SELECT o.object_id, rs.name , rs.name AS \"set\" FROM dbo.Repository_Sets rs JOIN dbo.Object o ON rs.repository_id = o.repository_id WHERE ");
		
		for (int i = 0; i< ids.size(); i++) {
			sql.append("o.object_id = ? OR ");
		}
		sql.delete(sql.length()- 3, sql.length());
		System.out.println(sql.toString());
		PreparedStatement preparedstmt = connection.prepareStatement(sql.toString());
				
		for (int x = 0; x < 4; x++) {
			
			for (int y = 1; y <= ids.size(); y++) {
//				System.out.println("Generated Statement ID: " + Integer.toString(ids.size() * x + y));
				preparedstmt.setBigDecimal(ids.size() * x + y, ids.get(y-1));
				
			}
		}
	
		return preparedstmt;
	}

	/**
	 * Fetches the Keyword information of the object specified by the object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement Keywords(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT keyword, lang FROM dbo.Keywords K JOIN dbo.Object2Keywords O ON K.keyword_id = O.keyword_id WHERE O.object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the Languages information of the object specified by the
	 * object_id Includes the iso-639-mapping.
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement Languages(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT L.language AS language, I.iso639language AS iso639language, O2L.number AS number "
						+ "FROM dbo.Object2Language O2L " + "LEFT JOIN dbo.Language L ON L.language_id = O2L.language_id "
						+ "LEFT JOIN dbo.Object2Iso639Language I2L ON O2L.object_id = I2L.object_id "
						+ "LEFT JOIN dbo.Iso639Language I ON I.language_id = I2L.language_id " + "WHERE O2L.object_id = ?");

		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the ID of the last person that was inserted (uses MAX(id))
	 * 
	 * @param firstname
	 *            of the person that was inserted
	 * @param lastname
	 *            of the person that was inserted
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement LatestPerson(Connection connection, String firstname, String lastname) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT MAX(person_id) FROM dbo.Person WHERE (firstname = ? AND lastname = ?)");
		preparedstmt.setString(1, firstname);
		preparedstmt.setString(2, lastname);

		return preparedstmt;
	}

	/**
	 * Fetches the ID of the last keyword that was inserted (uses MAX(id))
	 * 
	 * @param keyword
	 * @param language
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement LatestKeyword(Connection connection, String keyword, String language) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT MAX(keyword_id) FROM dbo.Keywords WHERE (keyword = ? AND lang = ?)");
		preparedstmt.setString(1, keyword);
		preparedstmt.setString(2, language);

		return preparedstmt;
	}

	/**
	 * Fetches the ID of the language specified by "language"
	 * 
	 * @param language
	 *            String value
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement LanguageByName(Connection connection, String language) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT language_id FROM dbo.Language WHERE (language = ?)");
		preparedstmt.setString(1, language);

		return preparedstmt;
	}

	/**
	 * Fetches the ID of the ISO639-language specified by "language"
	 * 
	 * @param iso639language
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement Iso639LanguageByName(Connection connection, String iso639language) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT language_id FROM dbo.Iso639Language WHERE (iso639language = ?)");
		preparedstmt.setString(1, iso639language);

		return preparedstmt;
	}

	/**
	 * Fetches the ID of the DDC-category specified by "category", example: 610
	 * 
	 * @param category
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement DDCCategoriesByCategorie(Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT DDC_Categorie FROM dbo.DDC_Categories WHERE (DDC_Categorie = ?)");
		preparedstmt.setString(1, category);

		return preparedstmt;
	}

	/**
	 * Fetches the ID of the DNB-category specified by "category"
	 * 
	 * @param category
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement DNBCategoriesByCategorie(Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT DNB_Categorie FROM dbo.DNB_Categories WHERE (DNB_Categorie = ?)");
		preparedstmt.setString(1, category);

		return preparedstmt;
	}

	/**
	 * Fetches the ID of the DINI-Set-category specified by "category"
	 * 
	 * @param category
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement DINISetCategoriesByName(Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT DINI_set_id FROM dbo.DINI_Set_Categories WHERE (name = ?)");
		preparedstmt.setString(1, category);

		return preparedstmt;
	}

	/**
	 * Fetches the ID of the last "OtherClassification" that was inserted (uses
	 * MAX(id))
	 * 
	 * @param category
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement LatestOtherCategories(Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT MAX(other_id) FROM dbo.Other_Categories WHERE (name = ?)");
		preparedstmt.setString(1, category);

		return preparedstmt;
	}

	/**
	 * Fetches the FullTextLink information of the object specified by object_id
	 * 
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement FullTextLinks(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT object_id, mimeformat, link FROM dbo.FullTextLinks WHERE (object_id = ?)");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the current services status for all objects from the workflow
	 * control
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement ObjectServiceStatusAll(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("select object_id, time, service_id from dbo.WorkflowDB GROUP BY object_id, service_id, time ORDER BY object_id, time DESC");

		return preparedstmt;
	}

	/**
	 * Fetches the current services status from the workflow control for the
	 * object specified by object_id
	 * 
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement ObjectServiceStatusID(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT object_id, time, service_id FROM dbo.WorkflowDB WHERE (object_id = ?) GROUP BY object_id, service_id, time ORDER BY object_id, time DESC ");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * Fetches the information necessary for service_notifier. Data retrieved
	 * for the service specified by service_id
	 * 
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement ServiceNotify(Connection connection, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT service_id, inserttime, finishtime, urgent, complete FROM dbo.ServiceNotify WHERE (service_id = ?)");
		preparedstmt.setBigDecimal(1, service_id);

		return preparedstmt;
	}

	/**
	 * Fetches the user data for the login control, specified by the user name
	 * "name"
	 * 
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement LoginData(Connection connection, String name) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT name, password, email, superuser FROM dbo.LoginData WHERE (name = ?)");
		preparedstmt.setString(1, name);

		return preparedstmt;
	}

	/**
	 * Fetches the user data for the login control, specified by the user name
	 * "name". the returned "name" is in lower case and the parameter "name" is
	 * automatically converted to lower case
	 * 
	 * @param name
	 *            in lower cases
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement LoginDataLowerCase(Connection connection, String name) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT LOWER(name), password, email, superuser FROM dbo.LoginData WHERE (name = ?)");
		preparedstmt.setString(1, name.toLowerCase());

		return preparedstmt;
	}

	/**
	 * Necessary for browsing. Fetches the DDC_Categorie and the amount of
	 * documents categorized in this DDC Categorie
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement AllDDCCategories(Connection connection) throws SQLException {

		// PreparedStatement preparedstmt = connection.prepareStatement
		// ("select dc.DDC_Categorie, count(o.object_id) FROM dbo.Object o JOIN DDC_Classification d ON o.object_id = d.object_id JOIN DDC_Categories dc ON d.DDC_Categorie = dc.DDC_Categorie GROUP BY dc.DDC_Categorie ORDER BY dc.DDC_Categorie");
		PreparedStatement preparedstmt = connection.prepareStatement("select * from DDC_Browsing_Help");

		return preparedstmt;
	}

	/**
	 * Necessary for browsing. Fetches the amount of documents categorized by
	 * "wildcardCategory"
	 * 
	 * @param connection
	 * @param wildcardCategory
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement DDCCategoryWildcard(Connection connection, String wildcardCategory) throws SQLException {

		// PreparedStatement preparedstmt = connection.prepareStatement
		// ("select count(*) from DDC_Classification where DDC_Categorie like ?");
		// //geht nich :(
		// preparedstmt.setString(1, wildcardCategory);
		// PreparedStatement preparedstmt = connection.prepareStatement
		// ("select count(*) from DDC_Classification where DDC_Categorie like '5%'");
		// // geht

		PreparedStatement preparedstmt = connection.prepareStatement("select count(*) from DDC_Classification where DDC_Categorie like '"
				+ wildcardCategory + "'");

		// alternativ kann man auch WHERE LEFT(DDC_Categorie,2) = '44'

		return preparedstmt;
	}

	/**
	 * Fetches the DuplicateProbabilities information for the object specified
	 * by object_id
	 * 
	 * @param connection
	 * @param object_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement DuplicateProbabilities(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT object_id, duplicate_id, percentage, reverse_percentage FROM dbo.DuplicatePossibilities WHERE object_id = ?");

		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	/**
	 * OAI-Export: fetch all set information
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement OAIListSets(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT d.name, d.setNameEng as \"setName\" FROM dbo.DINI_Set_Categories d JOIN dbo.DINI_Set_Classification dsc ON d.DINI_set_id = dsc.DINI_set_id GROUP BY d.name"
						+ " UNION "
						+ " SELECT 'ddc:' + d.DDC_Categorie as \"Name\", d.name as \"setName\" FROM dbo.DDC_Categories d JOIN dbo.DDC_Classification dc ON d.DDC_Categorie = dc.DDC_Categorie GROUP BY d.name"
						+ " UNION "
						+ " SELECT 'dnb:' + d.DNB_Categorie as \"Name\",  d.name as \"setName\"  FROM dbo.DNB_Categories d JOIN dbo.DNB_Classification dc ON d.DNB_Categorie = dc.DNB_Categorie GROUP BY d.name"
						+ " UNION " 
						+ " SELECT rs.name,  r.name as \"setName\"  FROM dbo.Repository_Sets rs JOIN dbo.Repositories r ON rs.repository_id = r.repository_id"						
						+ " ORDER BY d.name");

		return preparedstmt;
	}

	/**
	 * OAI-Export: Fetch the oldest datestamp of any object
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement OAIGetOldestDate(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("select MIN(repository_datestamp) from dbo.Object ");

		return preparedstmt;
	}

	/**
	 * OAI-Export: fetch all object_ids specified by the set and from/until
	 * restrictions
	 * 
	 * @param connection
	 * @param from
	 * @param until
	 * @param set
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement AllOIDsByDate(Connection connection, Date from, Date until, String set) throws SQLException {

		PreparedStatement preparedstmt;

		if (from == null && until == null)
			preparedstmt = connection.prepareStatement("SELECT object_id, repository_datestamp from dbo.Object");

		else {

			if (from != null && until == null) {

				preparedstmt = connection
						.prepareStatement("SELECT object_id, repository_datestamp from dbo.Object WHERE repository_datestamp > ?");
				preparedstmt.setDate(1, from);

			} else if (from == null && until != null) {

				preparedstmt = connection
						.prepareStatement("SELECT object_id, repository_datestamp from dbo.Object WHERE repository_datestamp < ?");
				preparedstmt.setDate(1, until);

			} else {

				preparedstmt = connection
						.prepareStatement("SELECT object_id, repository_datestamp from dbo.Object WHERE repository_datestamp > ? AND repository_datestamp < ?");
				preparedstmt.setDate(1, from);
				preparedstmt.setDate(2, until);
			}
		}

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
//	public static PreparedStatement OAIListSetsbyID(Connection connection, String set, Date from, Date until) throws SQLException {
//
//		boolean aND = false;
//
//		StringBuffer sql = new StringBuffer("SELECT o.object_id, o.repository_datestamp, 'dnb:'+ d.DNB_Categorie FROM dbo.Object o ");
//		sql.append("JOIN dbo.DNB_Classification dsc ON o.object_id = dsc.object_id ");
//		sql.append("JOIN dbo.DNB_Categories d ON d.DNB_Categorie = dsc.DNB_Categorie ");
//
//		StringBuffer setFromUntil = new StringBuffer("");
//
//		if (set != null && !set.equals("")) {
//
//			sql.append("WHERE dsc.DNB_Categorie = ? ");
//			aND = true;
//
//		} else
//			set = null;
//
//		if (from != null) {
//
//			if (aND)
//				setFromUntil.append("AND o.repository_datestamp > ? ");
//
//			else {
//
//				aND = true;
//				setFromUntil.append("WHERE o.repository_datestamp > ? ");
//			}
//		}
//
//		if (until != null) {
//
//			if (aND)
//				setFromUntil.append("AND o.repository_datestamp < ? ");
//
//			else
//				setFromUntil.append("WHERE o.repository_datestamp < ? ");
//		}
//
//		sql.append(setFromUntil);
//		sql.append("UNION ");
//		sql.append("SELECT o.object_id, o.repository_datestamp, d.name FROM dbo.Object o ");
//		sql.append("JOIN dbo.DINI_Set_Classification dsc ON o.object_id = dsc.object_id ");
//		sql.append("JOIN dbo.DINI_Set_Categories d ON d.DINI_set_id = dsc.DINI_set_id ");
//
//		if (set != null && !set.equals(""))
//			sql.append("WHERE d.name = ? ");
//
//		sql.append(setFromUntil);
//		sql.append("UNION ");
//		sql.append("SELECT o.object_id, o.repository_datestamp, 'ddc:'+ d.DDC_Categorie FROM dbo.Object o ");
//		sql.append("JOIN dbo.DDC_Classification dsc ON o.object_id = dsc.object_id ");
//		sql.append("JOIN dbo.DDC_Categories d ON d.DDC_Categorie = dsc.DDC_Categorie ");
//
//		if (set != null && !set.equals(""))
//			sql.append("WHERE dsc.DDC_Categorie = ? ");
//
//		sql.append(setFromUntil);
//		sql.append("ORDER BY o.object_id");
//
//		logger.debug(sql.toString());
//
//		PreparedStatement preparedstmt = connection.prepareStatement(sql.toString());
//
//		if (set != null) {
//
//			preparedstmt.setString(1, set);
//
//			if (from != null && until != null) {
//
//				preparedstmt.setDate(2, from);
//				preparedstmt.setDate(3, until);
//				preparedstmt.setString(4, set);
//				preparedstmt.setDate(5, from);
//				preparedstmt.setDate(6, until);
//				preparedstmt.setString(7, set);
//				preparedstmt.setDate(8, from);
//				preparedstmt.setDate(9, until);
//
//			} else if (from == null && until == null) {
//
//				preparedstmt.setString(2, set);
//				preparedstmt.setString(3, set);
//
//			} else {
//
//				preparedstmt.setString(3, set);
//				preparedstmt.setString(5, set);
//
//				if (until == null) {
//
//					preparedstmt.setDate(2, from);
//					preparedstmt.setDate(4, from);
//					preparedstmt.setDate(6, from);
//
//				} else {
//
//					preparedstmt.setDate(2, until);
//					preparedstmt.setDate(4, until);
//					preparedstmt.setDate(6, until);
//				}
//			}
//
//		} else if (until != null || from != null) {
//
//			if (until == null) {
//
//				preparedstmt.setDate(1, from);
//				preparedstmt.setDate(2, from);
//				preparedstmt.setDate(3, from);
//
//			} else if (from == null) {
//
//				preparedstmt.setDate(1, until);
//				preparedstmt.setDate(2, until);
//				preparedstmt.setDate(3, until);
//
//			} else {
//
//				preparedstmt.setDate(1, from);
//				preparedstmt.setDate(2, until);
//				preparedstmt.setDate(3, from);
//				preparedstmt.setDate(4, until);
//				preparedstmt.setDate(5, from);
//				preparedstmt.setDate(6, until);
//			}
//
//		} else
//			;
//
//		return preparedstmt;
//
//		/*
//		 * SELECT o.object_id, o.repository_datestamp, 'dnb:'+ d.DNB_Categorie
//		 * FROM dbo.Object o JOIN dbo.DNB_Classification dsc ON o.object_id =
//		 * dsc.object_id JOIN dbo.DNB_Categories d ON d.DNB_Categorie =
//		 * dsc.DNB_Categorie -- WHERE o.repository_datestamp > '2008-01-01'
//		 * 
//		 * 
//		 * UNION
//		 * 
//		 * SELECT o.object_id, o.repository_datestamp, d.name FROM dbo.Object o
//		 * JOIN dbo.DINI_Set_Classification dsc ON o.object_id = dsc.object_id
//		 * JOIN dbo.DINI_Set_Categories d ON d.DINI_set_id = dsc.DINI_set_id --
//		 * WHERE o.repository_datestamp > '2008-01-01'
//		 * 
//		 * UNION
//		 * 
//		 * SELECT o.object_id, o.repository_datestamp, 'ddc:'+ d.DDC_Categorie
//		 * FROM dbo.Object o JOIN dbo.DDC_Classification dsc ON o.object_id =
//		 * dsc.object_id JOIN dbo.DDC_Categories d ON d.DDC_Categorie =
//		 * dsc.DDC_Categorie -- WHERE o.repository_datestamp > '2008-01-01'
//		 * 
//		 * ORDER BY o.object_id
//		 * 
//		 * 
//		 * -- Abfrage 2
//		 * 
//		 * SELECT o.object_id, o.repository_datestamp, 'dnb:'+ d.DNB_Categorie
//		 * FROM dbo.Object o JOIN dbo.DNB_Classification dsc ON o.object_id =
//		 * dsc.object_id JOIN dbo.DNB_Categories d ON d.DNB_Categorie =
//		 * dsc.DNB_Categorie WHERE dsc.DNB_Categorie = '000'
//		 * 
//		 * 
//		 * UNION
//		 * 
//		 * SELECT o.object_id, o.repository_datestamp, d.name FROM dbo.Object o
//		 * JOIN dbo.DINI_Set_Classification dsc ON o.object_id = dsc.object_id
//		 * JOIN dbo.DINI_Set_Categories d ON d.DINI_set_id = dsc.DINI_set_id
//		 * WHERE d.name = '000' -- WHERE o.repository_datestamp > '2008-01-01'
//		 * 
//		 * UNION
//		 * 
//		 * SELECT o.object_id, o.repository_datestamp, 'ddc:'+ d.DDC_Categorie
//		 * FROM dbo.Object o JOIN dbo.DDC_Classification dsc ON o.object_id =
//		 * dsc.object_id JOIN dbo.DDC_Categories d ON d.DDC_Categorie =
//		 * dsc.DDC_Categorie WHERE dsc.DDC_Categorie = '000' -- WHERE
//		 * o.repository_datestamp > '2008-01-01'
//		 * 
//		 * ORDER BY o.object_id
//		 */
//	}

	/**
	 * OAIPMH-Export ListIdentifiers
	 * 
	 * @param connection
	 * @param set
	 * @param from
	 * @param until
	 * @param ids
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement OAIListSetsbyID(Connection connection, String set, Date from, Date until, List<BigDecimal> ids) throws SQLException {

		boolean hasSet  	= false;
		boolean hasFrom 	= false;
		boolean hasUntil	= false;
		
		//bla
		StringBuffer sql = new StringBuffer("SELECT o.object_id, o.repository_identifier, o.repository_datestamp, ddc.DDC_Categorie AS ddc, dnb.DNB_Categorie AS dnb, d.name AS dini , rs.name as repo_set FROM dbo.Object o ");
		sql.append("LEFT OUTER JOIN dbo.DDC_Classification ddc ON o.object_id = ddc.object_id ");
		sql.append("LEFT OUTER JOIN dbo.DNB_Classification dnb ON o.object_id = dnb.object_id ");
		sql.append("LEFT OUTER JOIN dbo.DINI_Set_Classification dsc ON o.object_id = dsc.object_id ");
		sql.append("LEFT OUTER JOIN dbo.DINI_Set_Categories d ON dsc.DINI_set_id = d.DINI_set_id ");
		sql.append("LEFT OUTER JOIN dbo.Repository_Sets rs ON rs.repository_id = o.repository_id ");
		StringBuffer setFromUntil = new StringBuffer("");

//		if (set != null && !set.equals("")) {
//
//			hasSet = true;
//			if (set.startsWith("pubtype:"))
//				sql.append("WHERE d.name = ? ");
//
//			else if (set.startsWith("ddc:"))
//				sql.append("WHERE ddc.DDC_Categorie = ? ");
//			
//			else if (set.startsWith("dnb:"))
//				sql.append("WHERE dnb.DNB_Categorie = ? ");
//			
//			else
//				hasSet = false;// wrong set type
//
//		} else
//			set = null;

		if (from != null) {

			hasFrom = true;
			
//			if (hasSet)
//				setFromUntil.append("AND o.repository_datestamp > ? ");
//
//			else {

				setFromUntil.append("WHERE o.repository_datestamp > ? ");
//			}
		}

		if (until != null) {

			hasUntil = true;
			if (hasSet || hasFrom)
				setFromUntil.append("AND o.repository_datestamp < ? ");

			else
				setFromUntil.append("WHERE o.repository_datestamp < ? ");
		}


		sql.append(setFromUntil);
		
		// add ids to fetch elements for
		
		if (ids != null && ids.size() > 0)
		{
			if (!hasSet && !hasFrom && !hasUntil)
				sql.append("WHERE ");
			else 
				sql.append("AND (");
		
			StringBuffer buf = new StringBuffer();
			for (BigDecimal id : ids) {
				buf.append(" o.object_id = ").append(id).append(" OR");
			}
			String idClause = buf.toString();
			sql.append(idClause.substring(0, idClause.length() - 2)); // remove last 'OR'
													
			if (hasSet || hasFrom || hasUntil)
				sql.append(") ");
		}
		
		sql.append("ORDER BY o.object_id");

		logger.info(sql.toString());

		PreparedStatement preparedstmt = connection.prepareStatement(sql.toString());
			//dbng.safelyCreatePreparedStatement(connection, sql.toString());

		int parameterIndex = 1;
//		if (hasSet)
//		{
//			String setNumber = set.startsWith("ddc:") || set.startsWith("dnb:") ? set.split(":")[1] : set;
//			preparedstmt.setString(parameterIndex++, setNumber);
//		}
		if (hasFrom)
		{
			preparedstmt.setDate(parameterIndex++, from);
		}
		if (hasUntil)
		{
			preparedstmt.setDate(parameterIndex, until);
		}

		return preparedstmt;
	}
	
	


	/**
	 * 
	 * OAIPMH-Export ListRecords
	 * 
	 * @param connection
	 * @param set
	 * @param fromDate
	 * @param untilDate
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement OAIListAll(Connection connection, String set, Date fromDate, Date untilDate, 
			List<BigDecimal> ids) throws SQLException {

		StringBuffer sql = new StringBuffer("SELECT o.object_id, o.repository_identifier, t.title, o.repository_datestamp, ") // Title
				.append("p1.firstname AS author_firstname, p1.lastname AS author_lastname, ") // Author
				.append("p2.firstname AS editor_firstname, p2.lastname AS editor_lastname, ") // Editor
				.append("p3.firstname AS contributor_firstname, p3.lastname AS contributor_lastname, ") // Contributor
				.append("d.abstract, d.lang, ") // Description
				.append("dv.value AS date, ") // DateValue
				.append("l.language, ") // Language
				.append("k.keyword, k.lang, ") // Keywords
				.append("tv.value AS type, ") // TypeValue
				.append("ftl.mimeformat, ftl.link, ") // FullTextLinks
				.append("pu.name AS publisher ") // Publisher
				//.append(", ddc.DDC_Categorie AS ddc ")
				.append("FROM dbo.Object o ").append(
						"LEFT OUTER JOIN dbo.Titles t ON o.object_id = t.object_id ").append(
						"LEFT OUTER JOIN dbo.Object2Author o2a ON o.object_id = o2a.object_id ").append(
						"LEFT OUTER JOIN dbo.Person p1 ON o2a.person_id = p1.person_id ").append(
						"LEFT OUTER JOIN dbo.Object2Editor o2e ON o.object_id = o2e.object_id ").append(
						"LEFT OUTER JOIN dbo.Person p2 ON o2e.person_id = p2.person_id ").append(
						"LEFT OUTER JOIN dbo.Object2Contributor o2c ON o.object_id = o2c.object_id ").append(
						"LEFT OUTER JOIN dbo.Person p3 ON o2c.person_id = p2.person_id ").append(
						"LEFT OUTER JOIN dbo.Description d ON o.object_id = d.object_id ").append(
						"LEFT OUTER JOIN dbo.DateValues dv ON o.object_id = dv.object_id ").append(
						"LEFT OUTER JOIN dbo.Object2Language o2l ON o.object_id = o2l.object_id ").append(
						"LEFT OUTER JOIN dbo.Language l ON o2l.language_id = l.language_id ").append(
						"LEFT OUTER JOIN dbo.Object2Keywords o2k ON o.object_id = o2k.object_id ").append(
						"LEFT OUTER JOIN dbo.Keywords k ON o2k.keyword_id = k.keyword_id ").append(
						"LEFT OUTER JOIN dbo.TypeValue tv ON o.object_id = tv.object_id ").append(
						"LEFT OUTER JOIN dbo.FullTextLinks ftl ON o.object_id = ftl.object_id ").append(
						"LEFT OUTER JOIN dbo.Publisher pu ON o.object_id = pu.object_id ").append(
						//"LEFT OUTER JOIN dbo.DDC_Classification ddc ON o.object_id = ddc.object_id ").append(
						"WHERE");

		StringBuffer buf = new StringBuffer();

		if (ids != null && ids.size() > 0) {

			for (BigDecimal id : ids) {

				buf.append(" o.object_id = ").append(id).append(" OR");
			}
			String whereClause = buf.toString();
			sql.append(whereClause.substring(0, whereClause.length() - 2)); // remove
																			// last
																			// 'OR'

		}

		sql.append(" ORDER BY o.object_id, t.qualifier, o2a.number, o2e.number, o2c.number, d.number, dv.number, o2l.number, k.keyword_id, tv.type_id, pu.number");

		logger.info(sql.toString());

		PreparedStatement preparedstmt = connection.prepareStatement(sql.toString());

		return preparedstmt;
	}

	/**
	 * OAIPMH-Export ListRecords & ListIdentifiers
	 * Gets the ids to actually join on and fetch data for in another request.
	 * 
	 * @param connection
	 * @param from
	 * @param until
	 * @param set
	 * @param idOffset
	 * @param resultCount
	 * @return
	 * @throws SQLException
	 */
	
	public static PreparedStatement AllOIDsByDate(Connection connection, Date from, Date until, String set, BigInteger idOffset,
			int resultCount, boolean rowCountOnly) throws SQLException {

		// example query: select TOP 50 o.object_id FROM dbo.Object o LEFT OUTER
		// JOIN dbo.DDC_Classification ddc ON
		// o.object_id = ddc.object_id where DDC_Categorie = '510' AND
		// o.object_id > 50000 and
		// o.repository_datestamp > '2008-01-01' and o.repository_datestamp <
		// '2008-12-31')

		PreparedStatement preparedstmt;

		StringBuffer sql = new StringBuffer("SELECT ");
		if (rowCountOnly) {
			sql.append("COUNT(o.object_id) AS size ");
		}else {
			sql.append("TOP " + resultCount + " o.object_id ");
		}
		sql.append("from dbo.Object o ");

		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		int paramCount = 1;

		if (set != null) {
			if (set.startsWith("ddc:")) {
				sql.append("LEFT OUTER JOIN dbo.DDC_Classification ddc ON o.object_id = ddc.object_id ");
			} else if (set.startsWith("dnb:")) {
				sql.append("LEFT OUTER JOIN dbo.DNB_Classification dnb ON o.object_id = dnb.object_id ");
			} else if (set.startsWith("pub-type:")) {
				sql.append("LEFT OUTER JOIN dbo.DINI_Set_Classification dsc ON o.object_id = dsc.object_id ");
				sql.append("LEFT OUTER JOIN dbo.DINI_Set_Categories d ON d.DINI_set_id = dsc.DINI_set_id ");
			} else {
				// 'X_OAN'-Sets
				sql.append("LEFT OUTER JOIN dbo.Repository_Sets rs ON o.repository_id = rs.repository_id ");
//				sql.append("LEFT OUTER JOIN dbo.Repository_Set rs ON o.repository_id = rs.repository_id ");
			}
		}

		if (idOffset != null) { // required

			sql.append("WHERE o.object_id > ? ");
			params.put(paramCount++, idOffset);
		}

		if (from != null) {

			sql.append("AND o.repository_datestamp > ? ");
			params.put(paramCount++, from);
		}

		if (until != null) {

			sql.append("AND o.repository_datestamp < ? ");
			params.put(paramCount++, until);
		}

		if (set != null) {
			if (set.startsWith("ddc:")) {
				sql.append("AND ddc.DDC_Categorie = ?");
			} else if (set.startsWith("dnb:")) {
				sql.append("AND dnb.DNB_Categorie = ?");
			} else if (set.startsWith("pub-type:")) {
				sql.append("AND d.name = ?");
			} else {
				sql.append("AND rs.name = ?");
			}
			params.put(paramCount++, set);
		}
		if(!rowCountOnly) {
			
			sql.append(" ORDER BY o.object_id");
		}

		logger.info("sql: " + sql.toString() + "   using offset value " + idOffset);

		preparedstmt = connection.prepareStatement(sql.toString());

		Object param;
		for (int i = 1; i <= params.size(); i++) {
			param = params.get(i);
			if (param instanceof Date)
				preparedstmt.setDate(i, (Date) param);
			else if (param instanceof String)
				preparedstmt.setString(i, set.startsWith("pub-type:") ? set : set.endsWith("_OAN") ? set : set.split(":")[1]);
			else
				preparedstmt.setInt(i, ((BigInteger) param).intValue());
		}

		return preparedstmt;
	}

	/**
	 * @param value
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement RepositoryData(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT * FROM Object o, Repositories r WHERE o.repository_id = r.repository_id AND object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	public static PreparedStatement InterpolatedDDCClassification_withCategorie(Connection connection, BigDecimal object_id)
			throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT name, D.Interpolated_DDC_Categorie, D.percentage FROM dbo.Interpolated_DDC_Classification D JOIN dbo.DDC_Categories C ON D.Interpolated_DDC_Categorie = C.DDC_Categorie WHERE D.object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	public static PreparedStatement InterpolatedDDCClassification(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT object_id, D.Interpolated_DDC_Categorie, D.percentage FROM dbo.Interpolated_DDC_Classification D WHERE D.object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	public static PreparedStatement UsageData_Metrics(Connection connection, String metrics_name) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT metrics_id FROM dbo.UsageData_Metrics WHERE name=?");

		preparedstmt.setString(1, metrics_name);

		return preparedstmt;
	}

	public static PreparedStatement UsageData_Metrics_AllNames(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT name FROM dbo.UsageData_Metrics");

		return preparedstmt;
	}

	public static PreparedStatement UsageData_Months_ListForMetricsName(Connection connection, BigDecimal object_id, String metrics_name)
			throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT object_id, name, counter, counted_for_date "
				+ "FROM dbo.UsageData_Months mo JOIN dbo.UsageData_Metrics me ON mo.metrics_id = me.metrics_id "
				+ "WHERE object_id=? AND name=? " + "ORDER BY counted_for_date DESC");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setString(2, metrics_name);

		return preparedstmt;
	}

	public static PreparedStatement UsageData_Overall_ForMetricsName(Connection connection, BigDecimal object_id, String metrics_name)
			throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT object_id, name, counter, last_update "
				+ "FROM dbo.UsageData_Overall mo JOIN dbo.UsageData_Metrics me ON mo.metrics_id = me.metrics_id "
				+ "WHERE object_id=? AND name=?");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setString(2, metrics_name);

		return preparedstmt;
	}
	
	
	public static PreparedStatement UsageDataOIDs(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT DISTINCT udm.object_id FROM UsageData_Months udm ORDER BY udm.object_id");

		return preparedstmt;
	}
	
	public static PreparedStatement UsageDataOIDsForRepository(Connection connection, Integer repository_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT DISTINCT udm.object_id FROM UsageData_Months udm, Object o WHERE o.repository_id = ? AND o.object_id = udm.object_id ORDER BY udm.object_id");
		preparedstmt.setInt(1, repository_id);
		
		return preparedstmt;
	}
	
	public static PreparedStatement UsageData_Ranking(Connection connection) throws SQLException {
		// TODO Anpassen der Datumsanfrage wenn die Statistik DB es erlaubt.
		PreparedStatement preparedstmt = connection.prepareStatement("Select distinct top 10 object_id, counter, counted_for_date FROM UsageData_Months where counted_for_date in (SELECT max(counted_for_date) from UsageData_Months where counted_for_date <(SELECT max(counted_for_date) from UsageData_Months)) Order by counter desc");
		return preparedstmt;
	}
	
	public static PreparedStatement UsageData_Counter_ForOID(Connection connection, BigDecimal object_id) throws SQLException {
		// TODO Anpassen der Datumsanfrage wenn die Statistik DB es erlaubt.
		PreparedStatement preparedstmt = connection.prepareStatement("Select distinct object_id, counter, counted_for_date FROM UsageData_Months where object_id=? and counted_for_date in (SELECT max(counted_for_date) from UsageData_Months where counted_for_date <(SELECT max(counted_for_date) from UsageData_Months))");
		preparedstmt.setBigDecimal(1, object_id);
		return preparedstmt;
	}
	
	public static PreparedStatement ServicesScheduling(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM ServicesScheduling");

		return preparedstmt;
	}
	
	public static PreparedStatement ServicesScheduling(Connection connection, int jobId) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM ServicesScheduling WHERE job_id = ?");

		preparedstmt.setInt(1, jobId);
		return preparedstmt;
	}
}
