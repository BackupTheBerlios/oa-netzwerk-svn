import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dini.oanetzwerk.migration.DeleteFromDBPostgres;
import de.dini.oanetzwerk.migration.InsertIntoDBPostgres;
import de.dini.oanetzwerk.migration.SelectFromDBPostgres;
import de.dini.oanetzwerk.migration.UpdateInDBPostgres;
import de.dini.oanetzwerk.server.database.DeleteFromDB;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SelectFromDB;



public class UpdateInDBTests {
	static Connection postgres;
	static MultipleStatementConnection msPostgres;
	
	@BeforeClass static public void setup() {
		try {
			Class.forName("org.postgresql.Driver");
			String urlPostgres = "jdbc:postgresql://localhost:5432/oanetdb";
			
			Properties propsPostgres = new Properties();
			propsPostgres.put("user", "imrael");
			propsPostgres.put("password", "fd6zaw9c");
			propsPostgres.put("charSet", "UTF-8");
			postgres = DriverManager.getConnection(urlPostgres, propsPostgres);
			System.out.println("Established Connection to local Postgres Instance");
	
			msPostgres = new MultipleStatementConnection(postgres);
			
			
//			// update the indexes
//			PreparedStatement updateObject = postgres.prepareStatement(
//				"SELECT setval('\"Object_object_id_seq\"', max(object_id)) FROM \"Object\""
//			);
//			PreparedStatement updateDINI = postgres.prepareStatement(
//				"SELECT setval('\"DINI_Set_Categories_DINI_set_id_seq\"', max(\"DINI_set_id\")) FROM \"DINI_Set_Categories\""
//			);
//			PreparedStatement updateKeywords = postgres.prepareStatement(
//				"SELECT setval('\"Keywords_keyword_id_seq\"', max(keyword_id)) FROM \"Keywords\""
//			);
//			PreparedStatement updateLanguage = postgres.prepareStatement(
//				"SELECT setval('\"Language_language_id_seq\"', max(language_id)) FROM \"Language\""
//			);
//			PreparedStatement updatePerson = postgres.prepareStatement(
//				"SELECT setval('\"Person_person_id_seq\"', max(person_id)) FROM \"Person\""
//			);
//			PreparedStatement updateRepositories = postgres.prepareStatement(
//				"SELECT setval('\"Repositories_repository_id_seq\"', max(repository_id)) FROM \"Repositories\""
//			);
//			PreparedStatement updateServices = postgres.prepareStatement(
//				"SELECT setval('\"Services_service_id_seq\"', max(service_id)) FROM \"Services\""
//			);
//			PreparedStatement updateTypeValue = postgres.prepareStatement(
//				"SELECT setval('\"TypeValue_type_id_seq\"', max(type_id)) FROM \"TypeValue\""
//			);
//			PreparedStatement updateUsageData_Metrics = postgres.prepareStatement(
//				"SELECT setval('\"UsageData_Metrics_metrics_id_seq\"', max(metrics_id)) FROM \"UsageData_Metrics\""
//			);
//			PreparedStatement updateWorkflowDB = postgres.prepareStatement(
//				"SELECT setval('\"WorkflowDB_workflow_id_seq\"', max(workflow_id)) FROM \"WorkflowDB\""
//			);
//			PreparedStatement updateIso639Language = postgres.prepareStatement(
//				"SELECT setval('\"Iso639Language_language_id_seq\"', max(language_id)) FROM \"Iso639Language\""
//			);
//			PreparedStatement[] indexUpdate = {
//				updateObject, updateDINI, updateKeywords, updateLanguage,
//				updatePerson, updateRepositories, updateServices, updateTypeValue,
//				updateUsageData_Metrics, updateWorkflowDB, updateIso639Language
//			};
//			for (int i = 0; i < indexUpdate.length; i++) {
//				msPostgres.loadStatement(indexUpdate[i]);
//				msPostgres.execute();
//				msPostgres.commit();
//			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@Test public void LoginData() {
		try {
			PreparedStatement stmt = UpdateInDBPostgres.LoginData(postgres, "malitzro", "4dm1n", "foo");
			msPostgres.loadStatement(stmt);
			msPostgres.execute();
			msPostgres.commit();
			
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false,false);
		}
		Assert.assertEquals(true, true);
	}
	@Test public void Object() {
		try {
			PreparedStatement object = UpdateInDBPostgres.Object(
				postgres, 
				new BigDecimal(1), 
				new BigDecimal(1), 
				Date.valueOf("2010-07-15"), 
				Date.valueOf("2002-07-30"), 
				"oai:HUBerlin.de:4002", 
				true, 
				0, 
				false, 
				false, 
				1
			);
			msPostgres.loadStatement(object);
			msPostgres.execute();
			msPostgres.commit();
			
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false,false);
		}
		Assert.assertEquals(true, true);
	}
	@Test public void PrecleanedData() {
		try {
			PreparedStatement stmt = UpdateInDBPostgres.PrecleanedData(
				postgres, 
				new BigDecimal(1), 
				Date.valueOf("2002-07-30"), 
				null
			);
			msPostgres.loadStatement(stmt);
			msPostgres.execute();
			msPostgres.commit();
			
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false,false);
		}
		Assert.assertEquals(true, true);
	}
	@Test public void Repository1() {
		try {
			PreparedStatement stmt = UpdateInDBPostgres.Repository(postgres, 1L, true);
			msPostgres.loadStatement(stmt);
			msPostgres.execute();
			msPostgres.commit();
			
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false,false);
		}
		Assert.assertEquals(true, true);
	}
	@Test public void Repository2() {
		try {
			PreparedStatement stmt = UpdateInDBPostgres.Repository(
				postgres, 
				new BigDecimal(1), 
				Date.valueOf("2010-07-16"), 
				"last_full_harvest_begin"
			);
			msPostgres.loadStatement(stmt);
			msPostgres.execute();
			msPostgres.commit();
			
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false,false);
		}
		Assert.assertEquals(true, true);
	}
	
	
	
}
