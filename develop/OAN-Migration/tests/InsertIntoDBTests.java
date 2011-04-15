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
import org.junit.Test;

import de.dini.oanetzwerk.migration.InsertIntoDBPostgres;
import de.dini.oanetzwerk.migration.SelectFromDBPostgres;
import de.dini.oanetzwerk.server.database.DeleteFromDB;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.QueryResult;
import de.dini.oanetzwerk.server.database.SelectFromDB;



public class InsertIntoDBTests {
	Connection postgres;
	MultipleStatementConnection msPostgres;
	
	@Before public void setup() {
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
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@Test public void Object() {
		try {
			System.out.println("Testcase Object(repository_id, harvested, repository_datestamp, repository_identifier, testdata, failureCounter)");
			PreparedStatement postgresQuery = InsertIntoDBPostgres.Object(
				postgres, 
				new BigDecimal(1), 
				Date.valueOf("2010-07-15"), 
				Date.valueOf("2010-07-16"), 
				"this_is_just_a_dummy_entry:testing:01", 
				true, 
				0
			);
			
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			QueryResult qr = msPostgres.execute();
			ResultSet rs = qr.getResultSet();
			rs.first();
			if (!rs.rowInserted()) {
				System.out.println("Keine Zeile eingefügt");
				Assert.assertEquals(false,false);
			}
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			// DELETE THE INPUT AGAIN
			PreparedStatement whichID = postgres.prepareStatement("SELECT max(object_id) FROM \"Object\"");
			msPostgres.loadStatement(whichID);
			QueryResult whichIDResult = msPostgres.execute();
			ResultSet whichIDResultSet = whichIDResult.getResultSet();

			if(!whichIDResultSet.first()) {
				Assert.assertEquals(false, false);
			}
			BigDecimal object_id = whichIDResultSet.getBigDecimal(1);
			
			PreparedStatement pgDelete = DeleteFromDB.Object(postgres, object_id);
			Assert.assertEquals(true,true);
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
}
