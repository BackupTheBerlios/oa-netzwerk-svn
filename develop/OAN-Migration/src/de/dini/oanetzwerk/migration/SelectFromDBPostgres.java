package de.dini.oanetzwerk.migration;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.sql.Connection;
import org.apache.log4j.Logger;

import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.SelectFromDB;

public class SelectFromDBPostgres {

	
	private static Logger logger = Logger.getLogger(SelectFromDB.class);
		
	public static void main(String[] args) {
		SelectFromDBPostgres t = new SelectFromDBPostgres();
		t.compareStatements();
	}

	private void compareStatements() {
		Connection connPostgres = null;
		try {
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://localhost:5432/oanetdb";
			
			Properties props = new Properties();
			props.put("user", "imrael");
			props.put("password", "fd6zaw9c");
			props.put("charSet", "UTF-8");
			connPostgres = DriverManager.getConnection(url, props);
			System.out.println("Established Connection to local Postgres Instance");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		String defaultString = "blakeks";
		BigDecimal defaultBigDecimal = new BigDecimal(1);
		Vector<BigDecimal> defaultBigDecimalList = new Vector<BigDecimal>();
		defaultBigDecimalList.add(new BigDecimal(1));
		Date defaultDate = new Date(50);
		Boolean defaultBoolean = true;
		int defaultInt = 1;
		MultipleStatementConnection mConn;
		try {
			mConn = new MultipleStatementConnection(connPostgres);
		
		
		
			Class c = this.getClass();
			Method[] methods = c.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				if (!methods[i].getName().contains("Postgres")) {
					// schleifendurchlauf überspringen wenn keine der Postgres-Funktionen angesprochen wird
					continue;
				}
				
				Class[] parameters = methods[i].getParameterTypes();
	
				int parameterCount = parameters.length;
				System.out.println("Methode "+methods[i].getName()+" hat "+parameterCount+" Parameter");
				System.out.print("Methode "+methods[i].getName()+"(Connection");
				java.lang.Object[] definierteParameter = new java.lang.Object[parameterCount];
				definierteParameter[0] = connPostgres;
				for (int j = 1; j < parameterCount; j++) {
					if (j < parameterCount ) {
						System.out.print(", ");
					}
					if (parameters[j].getName().equals("java.lang.String")) {
						System.out.print("String");
						definierteParameter[j] = defaultString;
					}
					else if(parameters[j].getName().equals("java.math.BigDecimal")) {
						System.out.print("BigDecimal");
						definierteParameter[j] = defaultBigDecimal;
					}
					else if (parameters[j].getName().equals("java.util.List")) {
						System.out.print("List");
						definierteParameter[j] = defaultBigDecimalList;
					}
					else if (parameters[j].getName().equals("java.sql.Date")) {
						System.out.print("Date");
						definierteParameter[j] = defaultDate;
					}
					else if (parameters[j].getName().equals("java.lang.Boolean")) {
						System.out.print("Boolean");
						definierteParameter[j] = defaultBoolean;
					}
					else if (parameters[j].getName().equals("java.lang.Integer")) {
						System.out.print("int");
						definierteParameter[j] = defaultInt;
					}
					else {
						System.out.println("foo");
						System.out.println("Nicht erkannter Parametertyp: "+parameters[j].getName());
						System.exit(-1);
					}
					
				}
				System.out.println(")");
		
				PreparedStatement stmt = (PreparedStatement) methods[i].invoke(this, definierteParameter);
				mConn.loadStatement(stmt);
				mConn.execute();
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
		
		

	
	public static PreparedStatement InternalIDPostgres(Connection connection, String repository_identifier) throws SQLException {
		// old sybase Statement	
		//PreparedStatement preparedstmt = connection.prepareStatement("SELECT o.object_id FROM dbo.Object o WHERE o.repository_identifier = ?");

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"Object\".object_id FROM \"Object\" WHERE \"Object\".repository_identifier = ?");
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
	public static PreparedStatement ObjectEntryPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		//PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM dbo.Object o WHERE o.object_id = ?");
		
		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM \"Object\" where \"Object\".object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

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
	public static PreparedStatement ObjectEntryPostgres(Connection connection, BigDecimal repository_id, Date repository_datestamp,
			String repository_identifier) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT object_id FROM \"Object\" WHERE repository_id = ? AND repository_identifier = ? AND repository_datestamp = ?");

		preparedstmt.setBigDecimal(1, repository_id);
		preparedstmt.setString(2, repository_identifier);
		preparedstmt.setDate(3, repository_datestamp);

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
	public static PreparedStatement ObjectEntryPostgres(Connection connection, String repository_identifier) throws SQLException {

		//PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM dbo.Object o WHERE o.repository_identifier = ?");
		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM \"Object\" WHERE \"Object\".repository_identifier = ?");

		preparedstmt.setString(1, repository_identifier);

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
	public static PreparedStatement ObjectEntryIDPostgres(Connection connection, BigDecimal repositoryID, String externalOID) throws SQLException {

		//PreparedStatement preparedstmt = connection.prepareStatement("SELECT o.object_id FROM dbo.Object o WHERE o.repository_id = ? and o.repository_identifier = ?");
		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"Object\".object_id FROM \"Object\" WHERE \"Object\".repository_id = ? and \"Object\".repository_identifier = ?");
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
	public static PreparedStatement ObjectEntryPostgres(Connection connection, BigDecimal repository_id, BigDecimal oid_offset) throws SQLException {

		// TODO: make batch size configurable
		//PreparedStatement preparedstmt = connection.prepareStatement("SELECT TOP 1000 * FROM dbo.Object o WHERE o.repository_id = ? AND o.object_id > ?");
		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM \"Object\" WHERE \"Object\".repository_id = ? AND \"Object\".object_id > ? LIMIT 1000");
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
	public static PreparedStatement AllOIDsPostgres(Connection connection) throws SQLException {

		//PreparedStatement preparedstmt = connection.prepareStatement("SELECT o.object_id FROM dbo.Object o");
		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"Object\".object_id FROM \"Object\"");
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
	public static PreparedStatement AllOIDsMarkAsTestPostgres(Connection connection) throws SQLException {

		//PreparedStatement preparedstmt = connection.prepareStatement("SELECT o.object_id FROM dbo.Object o WHERE o.testdata = 1");
		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"Object\".object_id FROM \"Object\" WHERE \"Object\".testdata = true");
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
	public static PreparedStatement AllOIDsMarkAsNotTestPostgres(Connection connection) throws SQLException {

		//PreparedStatement preparedstmt = connection.prepareStatement("SELECT o.object_id FROM dbo.Object o WHERE o.testdata = 0");
		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"Object\".object_id FROM \"Object\" WHERE \"Object\".testdata = false");
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
	public static PreparedStatement AllOIDsMarkAsHasFulltextlinkPostgres(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT DISTINCT \"FullTextLinks\".object_id FROM \"FullTextLinks\"");
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
	public static PreparedStatement AllOIDsFromRepositoryIDPostgres(Connection connection, BigDecimal repID) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"Object\".object_id FROM \"Object\" WHERE \"Object\".repository_id = ?");
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
	public static PreparedStatement AllOIDsFromRepositoryIDMarkAsTestPostgres(Connection connection, BigDecimal repID) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT \"Object\".object_id FROM \"Object\" WHERE \"Object\".repository_id = ? AND \"Object\".testdata = true");
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
	public static PreparedStatement RepositoryPostgres(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
				"SELECT " +
				"	\"Repositories\".name, " +
				"	\"Repositories\".repository_id, " +
				"	\"Repositories\".url, " +
				"	\"Repositories\".oai_url, " +
				"	\"Repositories\".test_data, " +
				"	\"Repositories\".harvest_amount, " +
				"	\"Repositories\".harvest_pause " +
				"FROM \"Repositories\"");

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
	public static PreparedStatement RepositoryPostgres(Connection connection, BigDecimal repositoryID) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement(
					"SELECT " +
					"	\"Repositories\".name, " +
					"	\"Repositories\".url, " +
					"	\"Repositories\".oai_url, " +
					"	\"Repositories\".test_data, " +
					"	\"Repositories\".harvest_amount, " +
					"	\"Repositories\".harvest_pause, " +
					"	\"Repositories\".last_full_harvest_begin, " +
					"	\"Repositories\".last_markereraser_begin " +
					"FROM \"Repositories\" WHERE \"Repositories\".repository_id = ?");
		preparedstmt.setBigDecimal(1, repositoryID);

		return preparedstmt;
	}

	

	/**
	 * Fetch all "RawRecord"-data for the specified object_id ("internalOID")
	 * 
	 * @param connection
	 * @param internalOID
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement RawRecordDataHistoryPostgres(Connection connection, BigDecimal internalOID) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM \"RawData\" WHERE \"RawData\".object_id = ?");
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
	public static PreparedStatement RawRecordDataPostgres(Connection connection, BigDecimal internalOID, Date repository_timestamp)
			throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM \"RawData\" AS rd WHERE rd.object_id = ? AND rd.repository_timestamp = ?");
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
	public static PreparedStatement RawRecordDataPostgres(Connection connection, BigDecimal internalOID) throws SQLException {

		// TODO: Klären ob so ok
		PreparedStatement preparedstmt = connection.prepareStatement(
				"SELECT * FROM \"RawData\" AS rd " +
				"WHERE rd.object_id = ? " +
				"ORDER BY rd.repository_timestamp DESC " +
				"LIMIT 1");
				
				
				/*
				"	AND rd.repository_timestamp = (" +
				"		SELECT max(rdmax.repository_timestamp) FROM dbo.RawData rdmax WHERE rdmax.object_id = ?)");
				*/
		preparedstmt.setBigDecimal(1, internalOID);
		//preparedstmt.setBigDecimal(2, internalOID);

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

	public static PreparedStatement ServicesPostgres(Connection connection, String name) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM \"Services\" WHERE \"Services\".name = ?");
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

	public static PreparedStatement ServicesPostgres(Connection connection, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT * FROM \"Services\" WHERE \"Services\".service_id = ?");
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

	public static PreparedStatement ServicesOrderPostgres(Connection connection, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"ServicesOrder\".predecessor_id FROM \"ServicesOrder\" WHERE \"ServicesOrder\".service_id = ?");
		preparedstmt.setBigDecimal(1, service_id);

		return preparedstmt;
	}

	

	/**
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement WorkflowDBPostgres(Connection connection, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT w1.object_id, max(w1.time) " +
			"FROM \"WorkflowDB\" AS w1 " +
			"JOIN \"ServicesOrder\" AS so " +
			"	ON w1.service_id = so.predecessor_id  " +
			"WHERE so.service_id = ? " +
			"GROUP BY w1.object_id");

		preparedstmt.setBigDecimal(1, service_id);

		return preparedstmt;
	}

	/**
	 * @param connection
	 * @param service_id
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement WorkflowDBCompletePostgres(Connection connection, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT * " +
			"FROM (" +
			"	SELECT w1.object_id, max(w1.time) AS time " +
			"		FROM \"WorkflowDB\" AS w1 " +
			"		JOIN \"ServicesOrder\" AS so ON w1.service_id = so.predecessor_id  " +
			"	WHERE so.service_id = ? " +
			"	GROUP BY w1.object_id "+
			"UNION " +
			"	SELECT w1.object_id, max(w1.time) AS time " +
			"	FROM \"Worklist\" AS w1 " +
			"	WHERE w1.service_id = ? " +
			"	GROUP BY w1.object_id " +
			") AS list "+
			"ORDER BY time");

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
	public static PreparedStatement WorkflowDBPostgres(Connection connection, BigDecimal service_id, BigDecimal repository_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT w1.object_id, w1.time " +
			"FROM \"WorkflowDB\" AS w1 " +
			"JOIN \"ServicesOrder\" AS so " +
			"	ON w1.service_id = so.predecessor_id  " +
			"JOIN \"Object\" AS o" +
			"	ON w1.object_id = o.object_id " +
			"WHERE so.service_id = ? and repository_id = ? ");

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

	public static PreparedStatement WorkflowDBInsertedPostgres(Connection connection, BigDecimal object_id, BigDecimal service_id)
			throws SQLException {

		// TODO: geändert von Sammy, zu testen ob der die gleichen Ergebnisse bringt wie der Sybase Query
		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT \"WorkflowDB\".workflow_id\", \"WorkflowDB\".time" +
			"FROM \"WorkflowDB\" " +
			"WHERE \"WorkflowDB\".object_id = ? AND \"WorkflowDB\".service_id = ?" +
			"ORDER BY time");

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

	public static PreparedStatement WorkflowDBPostgres(Connection connection, BigDecimal object_id, Timestamp time, BigDecimal service_id)
			throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT \"WorkflowDB\".workflow_id " +
			"FROM \"WorkflowDB\" " +
			"WHERE \"WorkflowDB\".object_id = ? AND \"WorkflowDB\".time = ? AND \"WorkflowDB\".service_id = ?");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setTimestamp(2, time);
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

	public static PreparedStatement TitlePostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT \"Titles\".title, \"Titles\".qualifier, \"Titles\".lang FROM \"Titles\" WHERE \"Titles\".object_id = ?");
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

	public static PreparedStatement AuthorsPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email " +
			"FROM \"Person\" AS P " +
			"JOIN \"Object2Author\" AS O " +
			"	ON P.person_id = O.person_id " +
			"WHERE O.object_id = ?");
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

	public static PreparedStatement EditorsPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email " +
			"FROM \"Person\" AS P " +
			"JOIN \"Object2Editor\" AS O " +
			"	ON P.person_id = O.person_id " +
			"WHERE O.object_id = ?");
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

	public static PreparedStatement ContributorsPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT O.number, P.firstname, P.lastname, P.title, P.institution, P.email " +
			"FROM \"Person\" AS P " +
			"JOIN \"Object2Contributor\" AS O " +
			"	ON P.person_id = O.person_id " +
			"WHERE O.object_id = ?");
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

	public static PreparedStatement FormatPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT schema_f, number FROM \"Format\" WHERE \"Format\".object_id = ?");
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

	public static PreparedStatement IdentifierPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"Identifier\".identifier, \"Identifier\".number FROM \"Identifier\" WHERE \"Identifier\".object_id = ?");
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

	public static PreparedStatement DescriptionPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"Description\".abstract, \"Description\".lang, \"Description\".number FROM \"Description\" WHERE \"Description\".object_id = ?");
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
	public static PreparedStatement DateValuesPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"DateValues\".number, \"DateValues\".value, \"DateValues\".\"originalValue\" FROM \"DateValues\" WHERE \"DateValues\".object_id = ?");
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
	public static PreparedStatement TypeValuesPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"TypeValue\".value FROM \"TypeValue\" WHERE \"TypeValue\".object_id = ?");
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
	public static PreparedStatement PublisherPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"Publisher\".name, \"Publisher\".number FROM \"Publisher\" WHERE \"Publisher\".object_id = ?");
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
	public static PreparedStatement DDCClassificationPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT \"C\".name, \"D\".\"DDC_Categorie\" " +
			"FROM \"DDC_Classification\" AS \"D\" " +
			"JOIN \"DDC_Categories\" AS \"C\" " +
			"	ON \"D\".\"DDC_Categorie\" = \"C\".\"DDC_Categorie\" WHERE \"D\".object_id = ?");
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
	public static PreparedStatement DNBClassificationPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT C.name, D.\"DNB_Categorie\" " +
			"FROM \"DNB_Classification\" AS D " +
			"JOIN \"DNB_Categories\" AS C " +
			"	ON D.\"DNB_Categorie\" = C.\"DNB_Categorie\" " +
			"WHERE D.object_id = ?");
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
	public static PreparedStatement DINISetClassificationPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT C.name, D.\"DINI_set_id\" " +
			"FROM \"DINI_Set_Classification\" AS D " +
			"JOIN \"DINI_Set_Categories\" AS C " +
			"	ON D.\"DINI_set_id\" = C.\"DINI_set_id\" " +
			"WHERE D.object_id = ?");
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
	public static PreparedStatement OtherClassificationPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT C.name, D.other_id " +
			"FROM \"Other_Classification\" AS D " +
			"JOIN \"Other_Categories\" AS C " +
			"	ON D.other_id = C.other_id " +
			"WHERE D.object_id = ?");
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
	public static PreparedStatement AllClassificationsPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT name, 'ddc:' || \"D\".\"DDC_Categorie\" AS \"set\" " +
			"FROM \"DDC_Classification\" AS \"D\" " +
			"JOIN \"DDC_Categories\" AS \"C\" " +
			"	ON \"D\".\"DDC_Categorie\" = \"C\".\"DDC_Categorie\" " +
			"WHERE \"D\".object_id = ?" +
			"UNION " +
			"SELECT name, 'dnb:' || \"D2\".\"DNB_Categorie\" AS \"set\" " +
			"FROM \"DNB_Classification\" AS \"D2\" " +
			"JOIN \"DNB_Categories\" AS \"C2\" " +
			"	ON \"D2\".\"DNB_Categorie\" = \"C2\".\"DNB_Categorie\" " +
			"WHERE \"D2\".object_id = ?" +
			"UNION " +
			"SELECT name, 'dini:' || CAST(\"D3\".\"DINI_set_id\" AS varchar(10)) AS \"set\" " +
			"FROM \"DINI_Set_Classification\" AS \"D3\" " +
			"JOIN \"DINI_Set_Categories\" AS \"C3\" " +
			"	ON \"D3\".\"DINI_set_id\" = \"C3\".\"DINI_set_id\" " +
			"WHERE \"D3\".object_id = ?" +
			"UNION " +
			"SELECT rs.name , rs.name AS \"set\" " +
			"FROM \"Repository_Sets\" AS rs " +
			"JOIN \"Object\" AS o " +
			"	ON rs.repository_id = o.repository_id " +
			"WHERE o.object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setBigDecimal(2, object_id);
		preparedstmt.setBigDecimal(3, object_id);
		preparedstmt.setBigDecimal(4, object_id);

		return preparedstmt;
	}

	public static PreparedStatement AllClassificationsPostgres(Connection connection, List<BigDecimal> ids) throws SQLException {

		if (ids == null || ids.isEmpty())
		{
			logger.warn("Couldn't prepare query to fetch all classifications, as ID-List was empty!");
			return null;
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append(
			"SELECT D.object_id, C.name, 'ddc:' || D.\"DDC_Categorie\" AS \"set\" " +
			"FROM \"DDC_Classification\" AS D " +
			"JOIN \"DDC_Categories\" AS C " +
			"	ON D.\"DDC_Categorie\" = C.\"DDC_Categorie\" " +
			"WHERE ");

		for (int i = 0; i< ids.size(); i++) {
			sql.append("D.object_id = ? OR ");
		}
		sql.delete(sql.length()- 3, sql.length()); // cut off last 'OR '
		
		sql.append(
			" UNION " +
			"SELECT D2.object_id, C2.name, 'dnb:' || D2.\"DNB_Categorie\" AS \"set\" " +
			"FROM \"DNB_Classification\" AS D2 " +
			"JOIN \"DNB_Categories\" AS C2 " +
			"	ON D2.\"DNB_Categorie\" = C2.\"DNB_Categorie\" WHERE ");
		
		for (int i = 0; i< ids.size(); i++) {
			sql.append("D2.object_id = ? OR ");
		}
		sql.delete(sql.length()- 3, sql.length());
		
		sql.append(
			" UNION " +
			"SELECT D3.object_id, CAST(D3.\"DINI_set_id\" AS varchar(10)), 'dini:' || name AS \"set\" " +
			"FROM \"DINI_Set_Classification\" AS D3 " +
			"JOIN \"DINI_Set_Categories\" AS C3 " +
			"	ON D3.\"DINI_set_id\" = C3.\"DINI_set_id\" WHERE ");
		
		for (int i = 0; i< ids.size(); i++) {
			sql.append("D3.object_id = ? OR ");
		}
		sql.delete(sql.length()- 3, sql.length());
		
		sql.append(
			" UNION " +
			"SELECT o.object_id, rs.name , rs.name AS \"set\" " +
			"FROM \"Repository_Sets\" AS rs " +
			"JOIN \"Object\" AS o " +
			"	ON rs.repository_id = o.repository_id " +
			"WHERE ");
		
		for (int i = 0; i< ids.size(); i++) {
			sql.append("o.object_id = ? OR ");
		}
		sql.delete(sql.length()- 3, sql.length());
		//System.out.println(sql.toString());
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
	public static PreparedStatement KeywordsPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT \"K\".keyword, \"K\".lang " +
			"FROM \"Keywords\" AS \"K\" " +
			"JOIN \"Object2Keywords\" AS \"O\" " +
			"	ON \"K\".keyword_id = \"O\".keyword_id " +
			"WHERE \"O\".object_id = ?");
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
	public static PreparedStatement LanguagesPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT L.language AS language, I.iso639language AS iso639language, O2L.number AS number "+
			"FROM \"Object2Language\" AS O2L " + 
			"LEFT JOIN \"Language\" AS L " +
			"	ON L.language_id = O2L.language_id "+
			"LEFT JOIN \"Object2Iso639Language\" AS I2L " +
			"	ON O2L.object_id = I2L.object_id "+
			"LEFT JOIN \"Iso639Language\" AS I " +
			"	ON I.language_id = I2L.language_id " + 
			"WHERE O2L.object_id = ?");

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
	public static PreparedStatement LatestPersonPostgres(Connection connection, String firstname, String lastname) throws SQLException {
		// TODO: Sinnfrage dieses Queries??
		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT person_id FROM \"Person\" WHERE (firstname = ? AND lastname = ?) ORDER BY person_id DESC LIMIT 1");
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
	public static PreparedStatement LatestKeywordPostgres(Connection connection, String keyword, String language) throws SQLException {
		// TODO: Sinnfrage?
		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT MAX(keyword_id) FROM \"Keywords\" WHERE (keyword = ? AND lang = ?)");
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
	public static PreparedStatement LanguageByNamePostgres(Connection connection, String language) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT language_id FROM \"Language\" WHERE (language = ?)");
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
	public static PreparedStatement Iso639LanguageByNamePostgres(Connection connection, String iso639language) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT language_id FROM \"Iso639Language\" WHERE (iso639language = ?)");
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
	public static PreparedStatement DDCCategoriesByCategoriePostgres(Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT \"DDC_Categorie\" FROM \"DDC_Categories\" WHERE (\"DDC_Categorie\" = ?)");
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
	public static PreparedStatement DNBCategoriesByCategoriePostgres(Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT \"DNB_Categorie\" FROM \"DNB_Categories\" WHERE (\"DNB_Categorie\" = ?)");
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
	public static PreparedStatement DINISetCategoriesByNamePostgres(Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT \"DINI_set_id\" FROM \"DINI_Set_Categories\" WHERE (name = ?)");
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
	public static PreparedStatement LatestOtherCategoriesPostgres(Connection connection, String category) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT MAX(other_id) FROM \"Other_Categories\" WHERE (name = ?)");
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
	public static PreparedStatement FullTextLinksPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT object_id, mimeformat, link FROM \"FullTextLinks\" WHERE (object_id = ?)");
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
	public static PreparedStatement ObjectServiceStatusAllPostgres(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("select object_id, time, service_id from \"WorkflowDB\" GROUP BY object_id, service_id, time ORDER BY object_id, time DESC");

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
	public static PreparedStatement ObjectServiceStatusIDPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT object_id, time, service_id FROM \"WorkflowDB\" WHERE (object_id = ?) GROUP BY object_id, service_id, time ORDER BY object_id, time DESC ");
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
	public static PreparedStatement ServiceNotifyPostgres(Connection connection, BigDecimal service_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT service_id, inserttime, finishtime, urgent, complete FROM \"ServiceNotify\" WHERE (service_id = ?)");
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
	public static PreparedStatement LoginDataPostgres(Connection connection, String name) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT name, password, email, superuser FROM \"LoginData\" WHERE (name = ?)");
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
	public static PreparedStatement LoginDataLowerCasePostgres(Connection connection, String name) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT LOWER(name), password, email, superuser FROM \"LoginData\" WHERE (name = ?)");
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
	public static PreparedStatement AllDDCCategoriesPostgres(Connection connection) throws SQLException {

		// PreparedStatement preparedstmt = connection.prepareStatement
		// ("select dc.DDC_Categorie, count(o.object_id) FROM dbo.Object o JOIN DDC_Classification d ON o.object_id = d.object_id JOIN DDC_Categories dc ON d.DDC_Categorie = dc.DDC_Categorie GROUP BY dc.DDC_Categorie ORDER BY dc.DDC_Categorie");
		PreparedStatement preparedstmt = connection.prepareStatement("select * from \"DDC_Browsing_Help\"");

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
	public static PreparedStatement DDCCategoryWildcardPostgres(Connection connection, String wildcardCategory) throws SQLException {

		// PreparedStatement preparedstmt = connection.prepareStatement
		// ("select count(*) from DDC_Classification where DDC_Categorie like ?");
		// //geht nich :(
		// preparedstmt.setString(1, wildcardCategory);
		// PreparedStatement preparedstmt = connection.prepareStatement
		// ("select count(*) from DDC_Classification where DDC_Categorie like '5%'");
		// // geht

		PreparedStatement preparedstmt = connection.prepareStatement("select count(*) from \"DDC_Classification\" where \"DDC_Categorie\" like '"
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
	public static PreparedStatement DuplicateProbabilitiesPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT object_id, duplicate_id, percentage, reverse_percentage FROM \"DuplicatePossibilities\" WHERE object_id = ?");

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
	public static PreparedStatement OAIListSetsPostgres(Connection connection) throws SQLException {
		
		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT d.name AS name, d.\"setNameEng\" as \"setName\" FROM \"DINI_Set_Categories\" AS d " +
			"JOIN \"DINI_Set_Classification\" AS dsc " +
			"	ON d.\"DINI_set_id\" = dsc.\"DINI_set_id\" " +
		//	"GROUP BY d.name" +
			" UNION "+
			"SELECT 'ddc:' || d.\"DDC_Categorie\" as \"Name\", d.name as \"setName\" " +
			"FROM \"DDC_Categories\" AS d " +
			"JOIN \"DDC_Classification\" AS dc " +
			"	ON d.\"DDC_Categorie\" = dc.\"DDC_Categorie\" " +
		//	"GROUP BY d.name"+
			" UNION "+
			"SELECT 'dnb:' || d.\"DNB_Categorie\" as \"Name\",  d.name as \"setName\"  " +
			"FROM \"DNB_Categories\" AS d JOIN \"DNB_Classification\" AS dc " +
			"	ON d.\"DNB_Categorie\" = dc.\"DNB_Categorie\" " +
		//	"GROUP BY d.name"+
			" UNION " +
			"SELECT rs.name,  r.name as \"setName\"  " +
			"FROM \"Repository_Sets\" AS rs " +
			"JOIN \"Repositories\" AS r " +
			"	ON rs.repository_id = r.repository_id");

		return preparedstmt;
	}

	/**
	 * OAI-Export: Fetch the oldest datestamp of any object
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement OAIGetOldestDatePostgres(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT MIN(repository_datestamp) FROM \"Object\" ");

		return preparedstmt;
	}


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
	public static PreparedStatement OAIListSetsbyIDPostgres(Connection connection, String set, Date from, Date until, List<BigDecimal> ids) throws SQLException {

		boolean hasSet  	= false;
		boolean hasFrom 	= false;
		boolean hasUntil	= false;
		
		//bla
		StringBuffer sql = new StringBuffer("SELECT o.object_id, o.repository_identifier, o.repository_datestamp, ddc.\"DDC_Categorie\" AS ddc, dnb.\"DNB_Categorie\" AS dnb, d.name AS dini , rs.name as repo_set FROM \"Object\" AS o ");
		sql.append("LEFT OUTER JOIN \"DDC_Classification\" AS ddc ON o.object_id = ddc.object_id ");
		sql.append("LEFT OUTER JOIN \"DNB_Classification\" AS dnb ON o.object_id = dnb.object_id ");
		sql.append("LEFT OUTER JOIN \"DINI_Set_Classification\" AS dsc ON o.object_id = dsc.object_id ");
		sql.append("LEFT OUTER JOIN \"DINI_Set_Categories\" AS d ON dsc.\"DINI_set_id\" = d.\"DINI_set_id\" ");
		sql.append("LEFT OUTER JOIN \"Repository_Sets\" AS rs ON rs.repository_id = o.repository_id ");
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

	public static PreparedStatement OAIListAllPostgres(Connection connection, String set, Date fromDate, Date untilDate, 
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
				.append("FROM \"Object\" AS o ").append(
						"LEFT OUTER JOIN \"Titles\" AS t ON o.object_id = t.object_id ").append(
						"LEFT OUTER JOIN \"Object2Author\" AS o2a ON o.object_id = o2a.object_id ").append(
						"LEFT OUTER JOIN \"Person\" AS p1 ON o2a.person_id = p1.person_id ").append(
						"LEFT OUTER JOIN \"Object2Editor\" AS o2e ON o.object_id = o2e.object_id ").append(
						"LEFT OUTER JOIN \"Person\" AS p2 ON o2e.person_id = p2.person_id ").append(
						"LEFT OUTER JOIN \"Object2Contributor\" AS o2c ON o.object_id = o2c.object_id ").append(
						"LEFT OUTER JOIN \"Person\" AS p3 ON o2c.person_id = p2.person_id ").append(
						"LEFT OUTER JOIN \"Description\" AS d ON o.object_id = d.object_id ").append(
						"LEFT OUTER JOIN \"DateValues\" AS dv ON o.object_id = dv.object_id ").append(
						"LEFT OUTER JOIN \"Object2Language\" AS o2l ON o.object_id = o2l.object_id ").append(
						"LEFT OUTER JOIN \"Language\" AS l ON o2l.language_id = l.language_id ").append(
						"LEFT OUTER JOIN \"Object2Keywords\" AS o2k ON o.object_id = o2k.object_id ").append(
						"LEFT OUTER JOIN \"Keywords\" AS k ON o2k.keyword_id = k.keyword_id ").append(
						"LEFT OUTER JOIN \"TypeValue\" AS tv ON o.object_id = tv.object_id ").append(
						"LEFT OUTER JOIN \"FullTextLinks\" AS ftl ON o.object_id = ftl.object_id ").append(
						"LEFT OUTER JOIN \"Publisher\" AS pu ON o.object_id = pu.object_id ").append(
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

	/**
	 * @param value
	 * @return
	 * @throws SQLException
	 */

	public static PreparedStatement RepositoryDataPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT * FROM \"Object\" AS o, \"Repositories\" AS r WHERE o.repository_id = r.repository_id AND object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	public static PreparedStatement InterpolatedDDCClassification_withCategoriePostgres(Connection connection, BigDecimal object_id)
			throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement(
			"SELECT name, \"D\".\"Interpolated_DDC_Categorie\", \"D\".percentage FROM \"Interpolated_DDC_Classification\" AS \"D\" " +
			"JOIN \"DDC_Categories\" AS \"C\" ON \"D\".\"Interpolated_DDC_Categorie\" = \"C\".\"DDC_Categorie\" WHERE \"D\".object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	public static PreparedStatement InterpolatedDDCClassificationPostgres(Connection connection, BigDecimal object_id) throws SQLException {

		PreparedStatement preparedstmt = connection
				.prepareStatement("SELECT object_id, \"D\".\"Interpolated_DDC_Categorie\", \"D\".percentage FROM \"Interpolated_DDC_Classification\" AS \"D\" WHERE \"D\".object_id = ?");
		preparedstmt.setBigDecimal(1, object_id);

		return preparedstmt;
	}

	public static PreparedStatement UsageData_MetricsPostgres(Connection connection, String metrics_name) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT metrics_id FROM \"UsageData_Metrics\" WHERE name=?");

		preparedstmt.setString(1, metrics_name);

		return preparedstmt;
	}

	public static PreparedStatement UsageData_Metrics_AllNamesPostgres(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT name FROM \"UsageData_Metrics\"");

		return preparedstmt;
	}

	public static PreparedStatement UsageData_Months_ListForMetricsNamePostgres(Connection connection, BigDecimal object_id, String metrics_name)
			throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT object_id, name, counter, counted_for_date "
				+ "FROM \"UsageData_Months\" AS mo JOIN \"UsageData_Metrics\" AS me ON mo.metrics_id = me.metrics_id "
				+ "WHERE object_id=? AND name=? " + "ORDER BY counted_for_date DESC");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setString(2, metrics_name);

		return preparedstmt;
	}

	public static PreparedStatement UsageData_Overall_ForMetricsNamePostgres(Connection connection, BigDecimal object_id, String metrics_name)
			throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT object_id, name, counter, last_update "
				+ "FROM \"UsageData_Overall\" AS mo JOIN \"UsageData_Metrics\" AS me ON mo.metrics_id = me.metrics_id "
				+ "WHERE object_id=? AND name=?");

		preparedstmt.setBigDecimal(1, object_id);
		preparedstmt.setString(2, metrics_name);

		return preparedstmt;
	}
	
	
	public static PreparedStatement UsageDataOIDsPostgres(Connection connection) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT DISTINCT udm.object_id FROM \"UsageData_Months\" AS udm ORDER BY udm.object_id");

		return preparedstmt;
	}
	
	public static PreparedStatement UsageDataOIDsForRepositoryPostgres(Connection connection, Integer repository_id) throws SQLException {

		PreparedStatement preparedstmt = connection.prepareStatement("SELECT DISTINCT udm.object_id FROM \"UsageData_Months\" AS udm, \"Object\" AS o WHERE o.repository_id = ? AND o.object_id = udm.object_id ORDER BY udm.object_id");
		preparedstmt.setInt(1, repository_id);
		
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
	 * @deprecated
	 */

	public static PreparedStatement AllOIDsByDatePostgres(Connection connection, Date from, Date until, String set) throws SQLException {

		PreparedStatement preparedstmt;

		if (from == null && until == null)
			preparedstmt = connection.prepareStatement("SELECT object_id, repository_datestamp from \"Object\"");

		else {

			if (from != null && until == null) {

				preparedstmt = connection
						.prepareStatement("SELECT object_id, repository_datestamp from \"Object\" WHERE repository_datestamp > ?");
				preparedstmt.setDate(1, from);

			} else if (from == null && until != null) {

				preparedstmt = connection
						.prepareStatement("SELECT object_id, repository_datestamp from \"Object\" WHERE repository_datestamp < ?");
				preparedstmt.setDate(1, until);

			} else {

				preparedstmt = connection
						.prepareStatement("SELECT object_id, repository_datestamp from \"Object\" WHERE repository_datestamp > ? AND repository_datestamp < ?");
				preparedstmt.setDate(1, from);
				preparedstmt.setDate(2, until);
			}
		}

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
	 * 
	 * @deprecated
	 */
	
	public static PreparedStatement AllOIDsByDatePostgres(Connection connection, Date from, Date until, String set, BigInteger idOffset,
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
		}
		sql.append("from \"Object\" AS o ");

		HashMap<Integer, Object> params = new HashMap<Integer, Object>();
		int paramCount = 1;

		if (set != null) {
			if (set.startsWith("ddc:")) {
				sql.append("LEFT OUTER JOIN \"DDC_Classification\" AS ddc ON o.object_id = ddc.object_id ");
			} else if (set.startsWith("dnb:")) {
				sql.append("LEFT OUTER JOIN \"DNB_Classification\" AS dnb ON o.object_id = dnb.object_id ");
			} else if (set.startsWith("pub-type:")) {
				sql.append("LEFT OUTER JOIN \"DINI_Set_Classification\" AS dsc ON o.object_id = dsc.object_id ");
				sql.append("LEFT OUTER JOIN \"DINI_Set_Categories\" AS d ON d.DINI_set_id = dsc.DINI_set_id ");
			} else {
				// 'X_OAN'-Sets
				sql.append("LEFT OUTER JOIN \"Repository_Sets\" AS rs ON o.repository_id = rs.repository_id ");
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
				sql.append("AND ddc.\"DDC_Categorie\" = ?");
			} else if (set.startsWith("dnb:")) {
				sql.append("AND dnb.\"DNB_Categorie\" = ?");
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
		if (!rowCountOnly) {
			sql.append(" LIMIT " + resultCount);
		}

		return preparedstmt;
	}
}
