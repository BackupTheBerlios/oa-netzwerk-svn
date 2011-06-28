package de.dini.oanetzwerk.migration;

import java.lang.reflect.Method;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Properties;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.StatementConnection;

public class SybaseToPostgreSQL {

	public String[] tables = {
					"Repositories"
					};
	public String[] tables1 = {
			"DDC_Categories",
			"DINI_Set_Categories",
			"DNB_Categories",
			"DuplicatePossibilities",
			"Interpolated_DDC_Categories",
			"Interpolated_DDC_Classification",
			"Iso639Language",
			"Keywords",
			"Language",
			"LoginData",
			"Person",
			"Repositories",
			"Services",
			"UsageData_Metrics",
			"Object",
			"AggregatorMetadata",
			"DateValues",
			"DDC_Browsing_Help",
			"DDC_Classification",
			"Description",
			"DINI_Set_Classification",
			"DNB_Classification",
			"Format",
			"FullTextLinks",
			"Identifier",
			"OAIExportCache",
			"Object2Author",
			"Object2Contributor",
			"Object2Editor",
			"Object2Language",
			"Object2Iso639Language",
			"Object2Keywords",
			"Other_Categories",
			"Other_Classification",
			"Publisher",
			"RawData",
			"Repository_Sets",
			"ServiceNotify",
			"ServicesOrder",
			"TypeValue",
			"Titles",
			"UsageData_Months",
			"UsageData_Overall",
			"WorkflowDB",
//			"Worklist"
	};
	private int batchSize = 10000;
	private int currentTableRowCount = 0;
		
	public static void main(String[] args) {
		
		new SybaseToPostgreSQL().copyTableContent();
	}
	
	public void clearLocalPostgresDB() {
		Connection conn;
		try {
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://localhost:5432/oanetdb";
			
			Properties props = new Properties();
			props.put("user", "oan_user");
			props.put("password", "oan123");
			props.put("charSet", "UTF-8");
			conn = DriverManager.getConnection(url, props);
			
			MultipleStatementConnection stmtconn = new MultipleStatementConnection(conn);
			for (int i = this.tables.length -1; i >= 0; i-- ) {

//				System.out.println("Leere Tabelle "+methodName+" vor dem neu befüllen");
				System.out.println("Leere Tabelle "+this.tables[i]);
				String sql = "DELETE FROM \""+this.tables[i]+"\"";
				PreparedStatement deleteStatement = conn.prepareStatement(sql);
				stmtconn.loadStatement(deleteStatement);
				stmtconn.execute();
				stmtconn.commit();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void copyTableContent() {



		MultipleStatementConnection stmtconn = null;
		QueryResult queryresult = null;

		// DBAccessNG dbng = new DBAccessNG();

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:jtds:sybase://themis.cms.hu-berlin.de:2025;DatabaseName=oanetzwerktest");
		dataSource.setUsername("oanetdbo");
		dataSource.setPassword("U7%wD8e4");
		dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
		

		// dataSource.s

		System.out.println("Database connection will be prepared!");

		if (dataSource == null) {
			System.out.println("Datasource could not be resolved!");
		}

		Connection connection = null;
		try {

			connection = dataSource.getConnection();

		} catch (SQLException ex) {

			System.out.println(ex.getLocalizedMessage() + " " + ex);
			ex.printStackTrace();
			return;
		}

		PreparedStatement singleStatement;

		try {

			stmtconn = new MultipleStatementConnection(connection);
			singleStatement = null;

			SQLWarning warning = connection.getWarnings();

			while (warning != null) {

				System.out.println("A SQL-Connection-Warning occured: " + warning.getMessage() + " " + warning.getSQLState() + " "
				                + warning.getErrorCode());
				warning = warning.getNextWarning();
			}

			for (int j = 0; j < tables.length; j++) {
				// teste ob die Methoden für die Tabelle vorhanden sind
				try {
					this.getClass().getMethod(tables[j], new Class[]{Connection.class});
					
				}
				catch (NoSuchMethodException e) {
					System.out.println("Konnte Methode \""+tables[j]+"(Connection)\" nicht finden.");
					System.exit(-1);
				}
				try {
					this.getClass().getMethod(tables[j], new Class[]{QueryResult.class, Connection.class});
				}
				catch (NoSuchMethodException e) {
					System.out.println("Konnte Methode \""+tables[j]+"(QueryResult, Connection)\" nicht finden.");
					System.exit(-1);
				}
			}
			
			
			// leere alle Tabellen
			clearLocalPostgresDB();
//			System.out.println("Nur leeren, kein neu befüllen");
//			System.exit(-1);			
			try{ 
				for (int i = 0; i < tables.length; i++) {
				
					System.out.println("Reading Table \""+tables[i]+"\"");
					
					// get number of rows in current table
					PreparedStatement getRowCount = connection.prepareStatement("SELECT COUNT(*) FROM \""+tables[i]+"\"");
					stmtconn.loadStatement(getRowCount);
					try{
						ResultSet rowCountResult = stmtconn.execute().getResultSet();
						rowCountResult.next();
						this.currentTableRowCount = rowCountResult.getInt(1);
						System.out.println("Tabelle "+tables[i]+" hat "+this.currentTableRowCount+ " Einträge");
					} catch (SQLException e) {
						e.printStackTrace();
						System.exit(-1);
					}
					
//					stmtconn = new MultipleStatementConnection(connection);
					PreparedStatement pStatement = (PreparedStatement) this.getClass().getMethod(tables[i], new Class[]{Connection.class}).invoke(this, stmtconn.connection );
					stmtconn.loadStatement(pStatement);
					queryresult = stmtconn.execute();

					//System.out.println("Number of results:" + ((DelegatingResultSet) queryresult.getResultSet()).getFetchSize());

					if (queryresult.getWarning() != null) {
						for (Throwable warning2 : queryresult.getWarning()) {
							System.out.println(warning2.getLocalizedMessage());
						}
					}
		
					migrate(queryresult, tables[i]);
//					stmtconn.close();
				}
				// update Indexes
				updateIndexes();
			}
			catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}

			//closeStatementConnection(stmtconn);

		} catch (SQLException ex) {

			System.out.println(ex.getLocalizedMessage() + " " + ex);

		} finally {

			closeStatementConnection(stmtconn);
		}
	}
	
	
	public void migrate(QueryResult qs, String methodName) {
		System.out.println("Running Prepared Statement Batch Update for Table \""+methodName+"\"");
		Connection conn = null;

		try {
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://localhost:5432/oanetdb";

			
			Properties props = new Properties();
			props.put("user", "postgres");
			props.put("password", "12345");
			props.put("charSet", "UTF-8");
			conn = DriverManager.getConnection(url, props);
			conn.setAutoCommit(false);
//			conn.setClientInfo(properties)
			try {
				
//				// Tabelle leeren bevor sie neu gefüllt wird
//				System.out.println("Leere Tabelle "+methodName+" vor dem neu befüllen");
//				String sql = "DELETE FROM \""+methodName+"\"";
//				conn.prepareStatement(sql).execute();
//				//conn.commit();
				System.out.println("Fülle Tabelle \""+methodName+"\" neu");
				
				Method m = this.getClass().getMethod(methodName, new Class[]{QueryResult.class, Connection.class});
				int rowCount = 0;
				PreparedStatement prest;
				while (rowCount <= this.currentTableRowCount) {
					prest = (PreparedStatement) m.invoke(this, qs, conn);
					int count[] = prest.executeBatch();
					conn.commit();
					rowCount += this.batchSize;
				}
				conn.close();
				System.out.println("Added Successfully!");
			} catch (BatchUpdateException s) {
				System.out.println("SQL statement is not executed!");
				s.printStackTrace();
				s.getNextException().printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateIndexes() {
		MultipleStatementConnection msPostgres;
		try {
			System.out.println("Updating Indexes");
			Connection postgres = null;

			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://localhost:5432/oanetdb";
			
			Properties props = new Properties();
			props.put("user", "postgres");
			props.put("password", "12345");
			props.put("charSet", "UTF-8");
			postgres = DriverManager.getConnection(url, props);
			postgres.setAutoCommit(false);
			msPostgres = new MultipleStatementConnection(postgres);
			PreparedStatement updateObject = postgres.prepareStatement(
					"SELECT setval('\"Object_object_id_seq\"', max(object_id)) FROM \"Object\""
			);
			PreparedStatement updateDINI = postgres.prepareStatement(
				"SELECT setval('\"DINI_Set_Categories_DINI_set_id_seq\"', max(\"DINI_set_id\")) FROM \"DINI_Set_Categories\""
			);
			PreparedStatement updateKeywords = postgres.prepareStatement(
				"SELECT setval('\"Keywords_keyword_id_seq\"', max(keyword_id)) FROM \"Keywords\""
			);
			PreparedStatement updateLanguage = postgres.prepareStatement(
				"SELECT setval('\"Language_language_id_seq\"', max(language_id)) FROM \"Language\""
			);
			PreparedStatement updatePerson = postgres.prepareStatement(
				"SELECT setval('\"Person_person_id_seq\"', max(person_id)) FROM \"Person\""
			);
			PreparedStatement updateRepositories = postgres.prepareStatement(
				"SELECT setval('\"Repositories_repository_id_seq\"', max(repository_id)) FROM \"Repositories\""
			);
			PreparedStatement updateServices = postgres.prepareStatement(
				"SELECT setval('\"Services_service_id_seq\"', max(service_id)) FROM \"Services\""
			);
			PreparedStatement updateTypeValue = postgres.prepareStatement(
				"SELECT setval('\"TypeValue_type_id_seq\"', max(type_id)) FROM \"TypeValue\""
			);
			PreparedStatement updateUsageData_Metrics = postgres.prepareStatement(
				"SELECT setval('\"UsageData_Metrics_metrics_id_seq\"', max(metrics_id)) FROM \"UsageData_Metrics\""
			);
			PreparedStatement updateWorkflowDB = postgres.prepareStatement(
				"SELECT setval('\"WorkflowDB_workflow_id_seq\"', max(workflow_id)) FROM \"WorkflowDB\""
			);
			PreparedStatement updateIso639Language = postgres.prepareStatement(
				"SELECT setval('\"Iso639Language_language_id_seq\"', max(language_id)) FROM \"Iso639Language\""
			);
			PreparedStatement[] indexUpdate = {
				updateObject, updateDINI, updateKeywords, updateLanguage,
				updatePerson, updateRepositories, updateServices, updateTypeValue,
				updateUsageData_Metrics, updateWorkflowDB, updateIso639Language
			};
			for (int i = 0; i < indexUpdate.length; i++) {
				msPostgres.loadStatement(indexUpdate[i]);
				msPostgres.execute();
				msPostgres.commit();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}


	

	/****************** Statements ********************/
	
	
	
	
	public PreparedStatement DDC_Categories(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT \"DDC_Categorie\", \"name\", \"name_en\" FROM DDC_Categories");
	}
	public PreparedStatement DDC_Categories(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"DDC_Categories\" (\"DDC_Categorie\", \"name\", \"name_en\") VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setString(1, rs.getString(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle DDC_Categories: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement DINI_Set_Categories(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT \"DINI_set_id\", \"name\", \"setNameEng\", \"setNameDeu\" FROM DINI_Set_Categories");
	}
	public PreparedStatement DINI_Set_Categories(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"DINI_Set_Categories\" (\"DINI_set_id\", \"name\", \"setNameEng\", \"setNameDeu\") VALUES(?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.setString(4, rs.getString(4));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle DINI_Set_Categories: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement DNB_Categories(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT \"DNB_Categorie\", \"name\" FROM DNB_Categories");
	}
	public PreparedStatement DNB_Categories(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"DNB_Categories\" (\"DNB_Categorie\", \"name\") VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setString(1, rs.getString(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle DNB_Categories: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement DuplicatePossibilities(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, duplicate_id, percentage, reverse_percentage FROM \"DuplicatePossibilities\"");
	}
	public PreparedStatement DuplicatePossibilities(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"DuplicatePossibilities\" (object_id, duplicate_id, percentage, reverse_percentage) VALUES(?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setFloat(3, rs.getFloat(3));
			pStatement.setFloat(4, rs.getFloat(4));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle DuplicatePossibilities: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Interpolated_DDC_Categories(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT \"Interpolated_DDC_Categorie\", name FROM \"Interpolated_DDC_Categories\"");
	}
	public PreparedStatement Interpolated_DDC_Categories(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Interpolated_DDC_Categories\" (\"Interpolated_DDC_Categorie\", name) VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setString(1, rs.getString(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Interpolated_DDC_Categories: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Interpolated_DDC_Classification(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, \"Interpolated_DDC_Categorie\", percentage FROM Interpolated_DDC_Classification");
	}
	public PreparedStatement Interpolated_DDC_Classification(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Interpolated_DDC_Classification\" (object_id, \"Interpolated_DDC_Categorie\", percentage) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.setFloat(3, rs.getFloat(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Interpolated_DDC_Classification: "+i+" Zeilen");

		return pStatement;
	}
	
	public PreparedStatement Iso639Language(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT language_id, iso639language FROM Iso639Language");
	}
	public PreparedStatement Iso639Language(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Iso639Language\" (language_id, iso639language) VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Iso639Language: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Keywords(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT keyword_id, keyword, lang FROM Keywords");
	}
	public PreparedStatement Keywords(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Keywords\" (keyword_id, keyword, lang) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Keywords: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Language(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT language_id, language FROM Language");
	}
	public PreparedStatement Language(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Language\" (language_id, language) VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Language: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement LoginData(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT name, email, password, superuser FROM LoginData");
	}
	public PreparedStatement LoginData(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"LoginData\" (name, email, password, superuser) VALUES(?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setString(1, rs.getString(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.setBoolean(4, rs.getBoolean(4));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle LoginData: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Person(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT person_id, firstname, lastname, title, institution, email FROM Person");
	}
	public PreparedStatement Person(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Person\" (person_id, firstname, lastname, title, institution, email) VALUES(?,?,?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.setString(4, rs.getString(4));
			pStatement.setString(5, rs.getString(5));
			pStatement.setString(6, rs.getString(6));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Person: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Repositories(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT repository_id, name, url, oai_url, test_data, harvest_amount, harvest_pause, last_full_harvest_begin, listrecords, last_markereraser_begin, active FROM Repositories");
	}
	public PreparedStatement Repositories(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Repositories\" (repository_id, name, url, oai_url, test_data, harvest_amount, harvest_pause, last_full_harvest_begin, listrecords, last_markereraser_begin, active) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.setString(4, rs.getString(4));
			pStatement.setBoolean(5, rs.getBoolean(5));
			pStatement.setInt(6, rs.getInt(6));
			pStatement.setInt(7, rs.getInt(7));
			pStatement.setTimestamp(8, rs.getTimestamp(8));
			pStatement.setBoolean(9, rs.getBoolean(9));
			pStatement.setTimestamp(10, rs.getTimestamp(10));
			pStatement.setBoolean(11, rs.getBoolean(11));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Repositories: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Services(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT service_id, name FROM Services");
	}
	public PreparedStatement Services(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Services\" (service_id, name) VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Services: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement UsageData_Metrics(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT metrics_id, name FROM UsageData_Metrics");
	}
	public PreparedStatement UsageData_Metrics(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"UsageData_Metrics\" (metrics_id, name) VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle UsageData_Metrics: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Object(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, repository_id, harvested, repository_datestamp, repository_identifier, testdata, failure_counter, peculiar, outdated, peculiar_counter FROM Object");
	}
	public PreparedStatement Object(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Object\" (object_id, repository_id, harvested, repository_datestamp, repository_identifier, testdata, failure_counter, peculiar, outdated, peculiar_counter) VALUES(?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			// TODO
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setTimestamp(3, rs.getTimestamp(3));
			pStatement.setTimestamp(4, rs.getTimestamp(4));
			pStatement.setString(5, rs.getString(5));
			pStatement.setBoolean(6, rs.getBoolean(6));
			pStatement.setInt(7, rs.getInt(7));
			pStatement.setBoolean(8, rs.getBoolean(8));
			pStatement.setBoolean(9, rs.getBoolean(9));
			pStatement.setInt(10, rs.getInt(10));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Objects: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement AggregatorMetadata(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, harvested FROM AggregatorMetadata");
	}
	public PreparedStatement AggregatorMetadata(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"AggregatorMetadata\" (object_id, harvested) VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setTimestamp(2, rs.getTimestamp(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle AggregatorMetadata: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement DateValues(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, number, value, originalValue FROM DateValues");
	}
	public PreparedStatement DateValues(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"DateValues\" (object_id, number, value, \"originalValue\") VALUES(?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setTimestamp(3, rs.getTimestamp(3));
			pStatement.setString(4, rs.getString(4));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle DateValues: "+i+" Zeilen");
		return pStatement;
	}
	
	public static PreparedStatement DDC_Browsing_Help(Connection connection) throws SQLException {
		PreparedStatement preparedstmt = connection.prepareStatement("select \"DDC_Categorie\", name_deu, name_eng, direct_count, sub_count, \"parent_DDC\" from DDC_Browsing_Help");
		return preparedstmt;
	}
	public PreparedStatement DDC_Browsing_Help(QueryResult qs, Connection conn) throws SQLException {
	    String sql = "INSERT INTO \"DDC_Browsing_Help\" (\"DDC_Categorie\", name_deu, name_eng, direct_count, sub_count, \"parent_DDC\") VALUES(?,?,?,?,?,?)";
	    PreparedStatement prest = conn.prepareStatement(sql);
	    int i = 0;
	    while (i < batchSize && qs.getResultSet().next()) {
	    	ResultSet rs = qs.getResultSet();
	    	prest.setString(1, rs.getString(1));
	        prest.setString(2, rs.getString(2));
	    	prest.setString(3, rs.getString(3));
	    	prest.setInt(4, rs.getInt(4));
	        prest.setInt(5, rs.getInt(5));
	    	prest.setString(6, rs.getString(6));
	    	prest.addBatch();
	    	i++;
	    }
	    System.out.println("Copied " + i + " rows.");
	    return prest;
    }
	
	public PreparedStatement DDC_Classification(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, \"DDC_Categorie\" FROM DDC_Classification");
	}
	public PreparedStatement DDC_Classification(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"DDC_Classification\" (object_id, \"DDC_Categorie\") VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle DDC_Classification: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Description(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, number, abstract, lang FROM Description");
	}
	public PreparedStatement Description(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Description\" (object_id, number, abstract, lang) VALUES(?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			//pStatement.setAsciiStream(3, rs.getAsciiStream(3));
			pStatement.setString(3, rs.getString(3));
			pStatement.setString(4, rs.getString(4));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Description: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement DINI_Set_Classification(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, \"DINI_set_id\" FROM DINI_Set_Classification");
	}
	public PreparedStatement DINI_Set_Classification(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"DINI_Set_Classification\" (object_id, \"DINI_set_id\") VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle DINI_Set_Classification: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement DNB_Classification(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, \"DNB_Categorie\" FROM DNB_Classification");
	}
	public PreparedStatement DNB_Classification(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"DNB_Classification\" (object_id, \"DNB_Categorie\") VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle DNB_Classification: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Format(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, number, schema_f FROM Format");
	}
	public PreparedStatement Format(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Format\" (object_id, number, schema_f) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Format: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement FullTextLinks(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, mimeformat, link FROM FullTextLinks");
	}
	public PreparedStatement FullTextLinks(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"FullTextLinks\" (object_id, mimeformat, link) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle FullTextLinks: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Identifier(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, number, identifier FROM Identifier");
	}
	public PreparedStatement Identifier(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Identifier\" (object_id, number, identifier) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Identifier: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement OAIExportCache(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, create_date, xml FROM OAIExportCache");
	}
	public PreparedStatement OAIExportCache(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"OAIExportCache\" (object_id, create_date, xml) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setTimestamp(2, rs.getTimestamp(2));
			pStatement.setAsciiStream(3, rs.getAsciiStream(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle OAIExportCache: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Object2Author(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, person_id, number FROM Object2Author");
	}
	public PreparedStatement Object2Author(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Object2Author\" (object_id, person_id, number) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setInt(3, rs.getInt(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Object2Author: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Object2Contributor(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, person_id, number FROM Object2Contributor");
	}
	public PreparedStatement Object2Contributor(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Object2Contributor\" (object_id, person_id, number) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setInt(3, rs.getInt(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Object2Contributor: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Object2Editor(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, person_id, number FROM Object2Editor");
	}
	public PreparedStatement Object2Editor(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Object2Editor\" (object_id, person_id, number) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setInt(3, rs.getInt(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Object2Editor: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Object2Iso639Language(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, language_id, number FROM Object2Iso639Language");
	}
	public PreparedStatement Object2Iso639Language(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Object2Iso639Language\" (object_id, language_id, number) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setInt(3, rs.getInt(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Object2Iso639Language: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Object2Keywords(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, keyword_id FROM \"Object2Keywords\"");
	}
	public PreparedStatement Object2Keywords(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Object2Keywords\" (object_id, keyword_id) VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Object2Keywords: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Object2Language(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, language_id, number FROM Object2Language");
	}
	public PreparedStatement Object2Language(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Object2Language\" (object_id, language_id, number) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setInt(3, rs.getInt(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Object2Language: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Other_Categories(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT other_id, name FROM Other_Categories");
	}
	public PreparedStatement Other_Categories(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Other_Categories\" (other_id, name) VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Other_Categories: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Other_Classification(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, other_id FROM Other_Classification");
	}
	public PreparedStatement Other_Classification(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Other_Classification\" (object_id, other_id) VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Other_Classification: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Publisher(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, number, name FROM Publisher");
	}
	public PreparedStatement Publisher(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Publisher\" (object_id, number, name) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Publisher: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement RawData(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, repository_timestamp, \"MetaDataFormat\", data, precleaned_data FROM RawData");
	}
	public PreparedStatement RawData(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"RawData\" (object_id, repository_timestamp, \"MetaDataFormat\", data, precleaned_data) VALUES(?,?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			//System.out.println("Tabelle RawData Iteration "+(i+1));
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setTimestamp(2, rs.getTimestamp(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.setString(4, rs.getString(4));
			pStatement.setString(5, rs.getString(5));
			pStatement.addBatch();
			i++;
			if (i == this.batchSize) {
				System.out.println("Batch voll");
				return pStatement;
			}
		}
		
		return pStatement;
	}
	
	public PreparedStatement Repository_Sets(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT name, repository_id FROM Repository_Sets");
	}
	public PreparedStatement Repository_Sets(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Repository_Sets\" (name, repository_id) VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setString(1, rs.getString(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Repository_Sets: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement ServiceNotify(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT service_id, inserttime, finishtime, urgent, complete FROM ServiceNotify");
	}
	public PreparedStatement ServiceNotify(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"ServiceNotify\" (service_id, inserttime, finishtime, urgent, complete) VALUES(?,?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setTimestamp(2, rs.getTimestamp(2));
			pStatement.setTimestamp(3, rs.getTimestamp(3));
			pStatement.setBoolean(4, rs.getBoolean(4));
			pStatement.setBoolean(5, rs.getBoolean(5));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle ServiceNotify: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement ServicesOrder(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT service_id, predecessor_id FROM ServicesOrder");
	}
	public PreparedStatement ServicesOrder(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"ServicesOrder\" (service_id, predecessor_id) VALUES(?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setObject(2, new Integer(0).equals(rs.getInt(2)) ? null : rs.getInt(2));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle ServicesOrder: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Titles(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, qualifier, title, lang FROM Titles");
	}
	public PreparedStatement Titles(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Titles\" (object_id, qualifier, title, lang) VALUES(?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setString(2, rs.getString(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.setString(4, rs.getString(4));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Titles: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement TypeValue(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT type_id, object_id, value FROM TypeValue");
	}
	public PreparedStatement TypeValue(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"TypeValue\" (type_id, object_id, value) VALUES(?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setString(3, rs.getString(3));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle TypeValue: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement UsageData_Months(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, metrics_id, counter, counted_for_date FROM UsageData_Months");
	}
	public PreparedStatement UsageData_Months(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"UsageData_Months\" (object_id, metrics_id, counter, counted_for_date) VALUES(?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setInt(3, rs.getInt(3));
			pStatement.setTimestamp(4, rs.getTimestamp(4));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle UsageData_Months: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement UsageData_Overall(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT object_id, metrics_id, counter, last_update FROM UsageData_Overall");
	}
	public PreparedStatement UsageData_Overall(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"UsageData_Overall\" (object_id, metrics_id, counter, last_update) VALUES(?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setInt(3, rs.getInt(3));
			pStatement.setTimestamp(4, rs.getTimestamp(4));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle UsageData_Overall: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement WorkflowDB(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT workflow_id, object_id, service_id, time FROM WorkflowDB");
	}
	public PreparedStatement WorkflowDB(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"WorkflowDB\" (workflow_id,object_id,service_id,time) VALUES(?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			pStatement.setInt(3, rs.getInt(3));
			pStatement.setTimestamp(4, rs.getTimestamp(4));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle WorkflowDB: "+i+" Zeilen");
		return pStatement;
	}
	
	public PreparedStatement Worklist(Connection connection) throws SQLException {
		return connection.prepareStatement("SELECT worklist_id, object_id, service_id, time FROM Worklist");
	}
	public PreparedStatement Worklist(QueryResult qr, Connection conn) throws SQLException {
		String sql = "INSERT INTO \"Worklist\" (worklist_id, object_id, service_id, time) VALUES(?,?,?,?)";
		PreparedStatement pStatement = conn.prepareStatement(sql);
		
		int i = 0;
		while (i < batchSize && qr.getResultSet().next()) {
			ResultSet rs = qr.getResultSet();
			pStatement.setInt(1, rs.getInt(1));
			System.out.println("Parameter1: "+rs.getInt(1));
			pStatement.setInt(2, rs.getInt(2));
			System.out.println("Parameter2: "+rs.getInt(2));
			pStatement.setInt(3, rs.getInt(3));
			System.out.println("Parameter3: "+rs.getInt(3));
			pStatement.setTimestamp(4, rs.getTimestamp(4));
			System.out.println("Parameter4: "+rs.getTimestamp(4));
			pStatement.addBatch();
			i++;
		}
		System.out.println("Tabelle Worklist: "+i+" Zeilen");
		return pStatement;
	}
	

	public void closeStatementConnection(StatementConnection stmtconn) {

		try {

			if (stmtconn != null)
				stmtconn.close();

		} catch (SQLException ex) {

			ex.printStackTrace();
		}
	}

}