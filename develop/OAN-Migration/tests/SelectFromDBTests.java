import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import de.dini.oanetzwerk.migration.SelectFromDBPostgres;
import de.dini.oanetzwerk.server.database.MultipleStatementConnection;
import de.dini.oanetzwerk.server.database.SelectFromDB;
import junit.framework.Assert;

public class SelectFromDBTests {
	Connection sybase;
	Connection postgres;
	MultipleStatementConnection msSybase;
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
			
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setUrl("jdbc:jtds:sybase://themis.cms.hu-berlin.de:2025;DatabaseName=oanetzwerktest");
			dataSource.setUsername("oanetdbo");
			dataSource.setPassword("U7%wD8e4");
			dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
			
			sybase = dataSource.getConnection();
			msSybase = new MultipleStatementConnection(sybase);
			msPostgres = new MultipleStatementConnection(postgres);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	@Test public void InternalIDTest() {
		try{ 
			System.out.println("Testcase InternalID(String repository_identifier)");
			String repository_identifier = "";
			
			PreparedStatement sybaseQuery = SelectFromDB.InternalID(sybase, repository_identifier);
			PreparedStatement postgresQuery = SelectFromDBPostgres.InternalIDPostgres(postgres, repository_identifier);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
		
	}
	
	@Test public void ObjectEntryTest() {
		try {
			System.out.println("Testcase ObjectEntry(BigDecimal object_id)");
			BigDecimal object_id = new BigDecimal(98123);
			
			PreparedStatement sybaseQuery = SelectFromDB.ObjectEntry(sybase, object_id);
			PreparedStatement postgresQuery = SelectFromDBPostgres.ObjectEntryPostgres(postgres, object_id);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
		
	}
	
	@Test public void ObjectEntry2Test() {
		try{
			System.out.println("Testcase ObjectEntry(String repository_identifier)");
			String repository_identifier = "oai:bth.rwth-aachen.de-opus:2655";
			
			PreparedStatement sybaseQuery = SelectFromDB.ObjectEntry(sybase, repository_identifier);
			PreparedStatement postgresQuery = SelectFromDBPostgres.ObjectEntryPostgres(postgres, repository_identifier);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
		
	}
	
	@Test public void ObjectEntryIDTest() {
		try{
			System.out.println("Testcase ObjectEntryID(BigDecimal repositoryID, String externalOID)");
			BigDecimal repositoryID = new BigDecimal(1);
			String externalOID = "";
			
			PreparedStatement sybaseQuery = SelectFromDB.ObjectEntryID(sybase, repositoryID, externalOID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.ObjectEntryIDPostgres(postgres, repositoryID, externalOID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void ObjectEntry3Test() {
		try{
			System.out.println("Testcase ObjectEntry(BigDecimal repositoryID, BigDecimal oid_offset)");
			BigDecimal repositoryID = new BigDecimal(1);
			BigDecimal oid_offset = new BigDecimal(1);
			
			PreparedStatement sybaseQuery = SelectFromDB.ObjectEntry(sybase, repositoryID, oid_offset);
			PreparedStatement postgresQuery = SelectFromDBPostgres.ObjectEntryPostgres(postgres, repositoryID, oid_offset);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void ObjectEntry4Test() {
		try{
			System.out.println("Testcase ObjectEntry(BigDecimal repositoryID, Date repository_date_stamp, String repository_identifier)");
			BigDecimal repositoryID = new BigDecimal(1);
			Date repository_date_stamp = Date.valueOf("2010-07-01");
			String repository_identifier = "oai:HUBerlin.de:35790";
			
			PreparedStatement sybaseQuery = SelectFromDB.ObjectEntry(sybase, repositoryID, repository_date_stamp, repository_identifier);
			PreparedStatement postgresQuery = SelectFromDBPostgres.ObjectEntryPostgres(postgres, repositoryID, repository_date_stamp, repository_identifier);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void AllOIDsTest() {
		try{
			System.out.println("Testcase AllOIDs()");
			PreparedStatement sybaseQuery = SelectFromDB.AllOIDs(sybase);
			PreparedStatement postgresQuery = SelectFromDBPostgres.AllOIDsPostgres(postgres);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void AllOIDsMarkAsTestTest() {
		try {
			System.out.println("Testcase AllOIDsMarkAsTest");
			PreparedStatement sybaseQuery = SelectFromDB.AllOIDsMarkAsTest(sybase);
			PreparedStatement postgresQuery = SelectFromDBPostgres.AllOIDsMarkAsTestPostgres(postgres);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}

	@Test public void AllOIDsMarkAsNotTestTest() {
		try{ 
			System.out.println("Testcase AllOIDsMarkAsNotTest");
			PreparedStatement sybaseQuery = SelectFromDB.AllOIDsMarkAsNotTest(sybase);
			PreparedStatement postgresQuery = SelectFromDBPostgres.AllOIDsMarkAsNotTestPostgres(postgres);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void AllOIDsMarkAsHasFulltextlinkTest() {
		try {
			System.out.println("Testcase AllOIDsMarkAsHasFulltextlink()");
			PreparedStatement sybaseQuery = SelectFromDB.AllOIDsMarkAsHasFulltextlink(sybase);
			PreparedStatement postgresQuery = SelectFromDBPostgres.AllOIDsMarkAsHasFulltextlinkPostgres(postgres);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void AllOIDsFromRepositoryIDTest() {
		try {
			BigDecimal repID = new BigDecimal(25);
			
			System.out.println("Testcase AllOIDsFromRepositoryID(BigDecimal repID)");
			PreparedStatement sybaseQuery = SelectFromDB.AllOIDsFromRepositoryID(sybase, repID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.AllOIDsFromRepositoryIDPostgres(postgres, repID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void AllOIDsFromRepositoryIDMarkAsTestTest() {
		try {
			BigDecimal repID = new BigDecimal(4);
			
			System.out.println("Testcase AllOIDsFromRepositoryIDMarkAsTest(BigDecimal repID)");
			PreparedStatement sybaseQuery = SelectFromDB.AllOIDsFromRepositoryIDMarkAsTest(sybase, repID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.AllOIDsFromRepositoryIDMarkAsTestPostgres(postgres, repID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void RepositoryTest() {
		try {
			System.out.println("Testcase Repository()");
			PreparedStatement sybaseQuery = SelectFromDB.Repository(sybase);
			PreparedStatement postgresQuery = SelectFromDBPostgres.RepositoryPostgres(postgres);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void Repository2Test() {
		try {
			System.out.println("Testcase Repository(BigDecimal repository_id)");
			
			BigDecimal repository_id = new BigDecimal(33);
			
			PreparedStatement sybaseQuery = SelectFromDB.Repository(sybase, repository_id);
			PreparedStatement postgresQuery = SelectFromDBPostgres.RepositoryPostgres(postgres, repository_id);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void RawRecordDataHistoryTest() {
		try {
			System.out.println("Testcase RawRecordDataHistory(BigDecimal internalOID)");
			BigDecimal internalOID = new BigDecimal(88888);
			PreparedStatement sybaseQuery = SelectFromDB.RawRecordDataHistory(sybase, internalOID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.RawRecordDataHistoryPostgres(postgres, internalOID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void RawRecordDataTest() {
		try {
			System.out.println("Testcase RawRecordData(BigDecimal internalOID, Date repository_timestamp)");
			BigDecimal internalOID = new BigDecimal(101231);
			Date repository_timestamp = Date.valueOf("2009-09-08");
			PreparedStatement sybaseQuery = SelectFromDB.RawRecordData(sybase, internalOID, repository_timestamp);
			PreparedStatement postgresQuery = SelectFromDBPostgres.RawRecordDataPostgres(postgres, internalOID, repository_timestamp);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void RawRecordData2Test() {
		try {
			System.out.println("Testcase RawRecordData(BigDecimal internalOID)");
			BigDecimal internalOID = new BigDecimal(88888);
			PreparedStatement sybaseQuery = SelectFromDB.RawRecordData(sybase, internalOID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.RawRecordDataPostgres(postgres, internalOID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void ServicesTest() {
		try {
			System.out.println("Testcase Services(String name)");
			String name = "Aggregator";
			PreparedStatement sybaseQuery = SelectFromDB.Services(sybase, name);
			PreparedStatement postgresQuery = SelectFromDBPostgres.ServicesPostgres(postgres, name);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void Services2Test() {
		try {
			System.out.println("Testcase Services(BigDecimal serviceID)");
			BigDecimal serviceID = new BigDecimal(5);
			PreparedStatement sybaseQuery = SelectFromDB.Services(sybase, serviceID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.ServicesPostgres(postgres, serviceID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void ServicesOrderTest() {
		try {
			System.out.println("Testcase ServicesOrder(BigDecimal serviceID)");
			BigDecimal serviceID = new BigDecimal(4);
			PreparedStatement sybaseQuery = SelectFromDB.ServicesOrder(sybase, serviceID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.ServicesOrderPostgres(postgres, serviceID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void WorkflowDBTest() {
		try {
			System.out.println("Testcase WorkflowDB(BigDecimal serviceID)");
			BigDecimal serviceID = new BigDecimal(2);
			PreparedStatement sybaseQuery = SelectFromDB.WorkflowDB(sybase, serviceID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.WorkflowDBPostgres(postgres, serviceID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void WorkflowDBCompleteTest() {
		try {
			System.out.println("Testcase WorkflowDBComplete(BigDecimal serviceID)");
			BigDecimal serviceID = new BigDecimal(2);
			PreparedStatement sybaseQuery = SelectFromDB.WorkflowDBComplete(sybase, serviceID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.WorkflowDBCompletePostgres(postgres, serviceID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void WorkflowDB2Test() {
		try {
			System.out.println("Testcase WorkflowDBComplete(BigDecimal serviceID, BigDecimal repositoryID)");
			BigDecimal serviceID = new BigDecimal(2);
			BigDecimal repositoryID = new BigDecimal(2);
			PreparedStatement sybaseQuery = SelectFromDB.WorkflowDB(sybase, serviceID, repositoryID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.WorkflowDBPostgres(postgres, serviceID, repositoryID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void WorkflowDBInsertedTest() {
		try {
			System.out.println("Testcase WorkflowDBInserted(BigDecimal objectID, BigDecimal serviceID)");
			BigDecimal serviceID = new BigDecimal(1);
			BigDecimal objectID = new BigDecimal(52003);
			PreparedStatement sybaseQuery = SelectFromDB.WorkflowDB(sybase, objectID, serviceID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.WorkflowDBPostgres(postgres, objectID, serviceID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void WorkflowDB3Test() {
		try {
			System.out.println("Testcase WorkflowDB(BigDecimal objectID, Timestamp time, BigDecimal serviceID)");
			BigDecimal serviceID = new BigDecimal(2);
			BigDecimal objectID = new BigDecimal(2);
			Timestamp time = Timestamp.valueOf("2010-07-21 11:21:57");
			Date sybaseTime = Date.valueOf("2010-07-21 11:21:57");
			PreparedStatement sybaseQuery = SelectFromDB.WorkflowDB(sybase, objectID, sybaseTime, serviceID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.WorkflowDBPostgres(postgres, objectID, time, serviceID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void TitleTest() {
		try {
			System.out.println("Testcase Title(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(98765);

			PreparedStatement sybaseQuery = SelectFromDB.Title(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.TitlePostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void AuthorsTest() {
		try {
			System.out.println("Testcase Authors(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.Authors(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.AuthorsPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void EditorsTest() {
		try {
			System.out.println("Testcase Editors(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(102345);

			PreparedStatement sybaseQuery = SelectFromDB.Editors(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.EditorsPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void ContributorsTest() {
		try {
			System.out.println("Testcase Contributors(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(1045);

			PreparedStatement sybaseQuery = SelectFromDB.Contributors(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.ContributorsPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void FormatTest() {
		try {
			System.out.println("Testcase Format(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.Format(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.FormatPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void IdentifierTest() {
		try {
			System.out.println("Testcase Identifier(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.Identifier(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.IdentifierPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void DescriptionTest() {
		try {
			System.out.println("Testcase Description(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.Description(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.DescriptionPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void DateValuesTest() {
		try {
			System.out.println("Testcase DateValues(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.DateValues(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.DateValuesPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void TypeValuesTest() {
		try {
			System.out.println("Testcase TypeValues(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.TypeValues(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.TypeValuesPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void PublisherTest() {
		try {
			System.out.println("Testcase Publisher(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.Publisher(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.PublisherPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void DDCClassificationTest() {
		try {
			System.out.println("Testcase DDCClassification(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(102345);

			PreparedStatement sybaseQuery = SelectFromDB.DDCClassification(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.DDCClassificationPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void DNBClassificationTest() {
		try {
			System.out.println("Testcase DNBClassification(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.DNBClassification(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.DNBClassificationPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void DINISetClassificationTest() {
		try {
			System.out.println("Testcase DINISetClassification(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.DINISetClassification(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.DINISetClassificationPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void OherClassificationTest() {
		try {
			System.out.println("Testcase OtherClassification(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.OtherClassification(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.OtherClassificationPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void AllClassificationsTest() {
		try {
			System.out.println("Testcase AllClassifications(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.AllClassifications(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.AllClassificationsPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void AllClassifications2Test() {
		try {
			System.out.println("Testcase AllClassifications(List<BigDecimal> ids)");
			List<BigDecimal> ids = new Vector<BigDecimal>();
			ids.add(new BigDecimal(88888));
			ids.add(new BigDecimal(102345));
			ids.add(new BigDecimal(102748));

			PreparedStatement sybaseQuery = SelectFromDB.AllClassifications(sybase, ids);
			PreparedStatement postgresQuery = SelectFromDBPostgres.AllClassificationsPostgres(postgres, ids);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void KeywordsTest() {
		try {
			System.out.println("Testcase Keywords(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.Keywords(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.KeywordsPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void LanguagesTest() {
		try {
			System.out.println("Testcase Languages(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.Languages(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.LanguagesPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void LatestPersonTest() {
		try {
			System.out.println("Testcase LatestPerson(String firstname, String lastname)");
			String firstname = "Fiorella";
			String lastname = "Monti";

			PreparedStatement sybaseQuery = SelectFromDB.LatestPerson(sybase, firstname, lastname);
			PreparedStatement postgresQuery = SelectFromDBPostgres.LatestPersonPostgres(postgres, firstname, lastname);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void LatestKeywordTest() {
		try {
			System.out.println("Testcase LatestKeyword(String keyword, String language)");
			String keyword = "Philosophie";
			

			PreparedStatement sybaseQuery = SelectFromDB.LatestKeyword(sybase, keyword, null);
			PreparedStatement postgresQuery = SelectFromDBPostgres.LatestPersonPostgres(postgres, keyword, null);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void LanguageByNameTest() {
		try {
			System.out.println("Testcase LanguageByName(String language)");
			String language = "fre";

			PreparedStatement sybaseQuery = SelectFromDB.LanguageByName(sybase, language);
			PreparedStatement postgresQuery = SelectFromDBPostgres.LanguageByNamePostgres(postgres, language);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void Iso639LanguageByNameTest() {
		try {
			System.out.println("Testcase Iso639LanguageByName(String iso639language)");
			String iso639language = "deu";

			PreparedStatement sybaseQuery = SelectFromDB.Iso639LanguageByName(sybase, iso639language);
			PreparedStatement postgresQuery = SelectFromDBPostgres.Iso639LanguageByNamePostgres(postgres, iso639language);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void DDCCategoriesByCategorieTest() {
		try {
			System.out.println("Testcase DDCCategoriesByCategorie(String category)");
			String category = "004";

			PreparedStatement sybaseQuery = SelectFromDB.DDCCategoriesByCategorie(sybase, category);
			PreparedStatement postgresQuery = SelectFromDBPostgres.DDCCategoriesByCategoriePostgres(postgres, category);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void DNBCategoriesByCategorieTest() {
		try {
			System.out.println("Testcase DNBCategoriesByCategorie(String category)");
			String category = "27";

			PreparedStatement sybaseQuery = SelectFromDB.DNBCategoriesByCategorie(sybase, category);
			PreparedStatement postgresQuery = SelectFromDBPostgres.DNBCategoriesByCategoriePostgres(postgres, category);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void DINISetCategoriesByNameTest() {
		try {
			System.out.println("Testcase DINISetCategoriesByName(String name)");
			String name = "pub-type:dissertation";

			PreparedStatement sybaseQuery = SelectFromDB.DINISetCategoriesByName(sybase, name);
			PreparedStatement postgresQuery = SelectFromDBPostgres.DINISetCategoriesByNamePostgres(postgres, name);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void LatestOtherCategoriesTest() {
		try {
			System.out.println("Testcase LatestOtherCategories(String category)");
			String category = "EP:Buch, Monographie";

			PreparedStatement sybaseQuery = SelectFromDB.LatestOtherCategories(sybase, category);
			PreparedStatement postgresQuery = SelectFromDBPostgres.LatestOtherCategoriesPostgres(postgres, category);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void FullTextLinksTest() {
		try {
			System.out.println("Testcase FullTextLinks(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(45623);

			PreparedStatement sybaseQuery = SelectFromDB.FullTextLinks(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.FullTextLinksPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void ObjectServiceStatusAllTest() {
		try {
			System.out.println("Testcase ObjectServiceStatusAll()");

			PreparedStatement sybaseQuery = SelectFromDB.ObjectServiceStatusAll(sybase);
			PreparedStatement postgresQuery = SelectFromDBPostgres.ObjectServiceStatusAllPostgres(postgres);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void ObjectServiceStatusIDTest() {
		try {
			System.out.println("Testcase ObjectServiceStatusID(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.ObjectServiceStatusID(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.ObjectServiceStatusIDPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void ServiceNotifyTest() {
		try {
			System.out.println("Testcase ServiceNotify(BigDecimal serviceID)");
			BigDecimal serviceID = new BigDecimal(4);

			PreparedStatement sybaseQuery = SelectFromDB.ServiceNotify(sybase, serviceID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.ServiceNotifyPostgres(postgres, serviceID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void LoginDataTest() {
		try {
			System.out.println("Testcase LoginData(String name)");
			String name = "malitzro";

			PreparedStatement sybaseQuery = SelectFromDB.LoginData(sybase, name);
			PreparedStatement postgresQuery = SelectFromDBPostgres.LoginDataPostgres(postgres, name);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void LoginDataLowerCaseTest() {
		try {
			System.out.println("Testcase LoginDataLowerCase(String name)");
			String name = "malitzro";

			PreparedStatement sybaseQuery = SelectFromDB.LoginDataLowerCase(sybase, name);
			PreparedStatement postgresQuery = SelectFromDBPostgres.LoginDataLowerCasePostgres(postgres, name);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void AllDDCCategoriesTest() {
		try {
			System.out.println("Testcase AllDDCCategories()");
			

			PreparedStatement sybaseQuery = SelectFromDB.AllDDCCategories(sybase);
			PreparedStatement postgresQuery = SelectFromDBPostgres.AllDDCCategoriesPostgres(postgres);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void DDCCategoryWildcardTest() {
		try {
			System.out.println("Testcase DDCCategoryWildcard(String wildcardCategory)");
			String wildcardCategory = "%5";

			PreparedStatement sybaseQuery = SelectFromDB.DDCCategoryWildcard(sybase, wildcardCategory);
			PreparedStatement postgresQuery = SelectFromDBPostgres.LoginDataPostgres(postgres,wildcardCategory);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void DuplicateProbabilitiesTest() {
		try {
			System.out.println("Testcase DuplicateProbabilities(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(4966);

			PreparedStatement sybaseQuery = SelectFromDB.DuplicateProbabilities(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.DuplicateProbabilitiesPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void OAIListSetsTest() {
		try {
			System.out.println("Testcase OAIListSets()");

			PreparedStatement sybaseQuery = SelectFromDB.OAIListSets(sybase);
			PreparedStatement postgresQuery = SelectFromDBPostgres.OAIListSetsPostgres(postgres);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void OAIGetOldestDateTest() {
		try {
			System.out.println("Testcase OAIGetOldestDate()");

			PreparedStatement sybaseQuery = SelectFromDB.OAIGetOldestDate(sybase);
			PreparedStatement postgresQuery = SelectFromDBPostgres.OAIGetOldestDatePostgres(postgres);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void OAIListSetsbyIDTest() {
		try {
			System.out.println("Testcase OAIListSetsbyID(String set, Date from, Date until, List<BigDecimal> ids)");
			String set = "004";
			Date from = Date.valueOf("2000-09-08");
			Date until = Date.valueOf("2005-09-08");
			List<BigDecimal> ids = new Vector<BigDecimal>();
			ids.add(new BigDecimal(88888));
			ids.add(new BigDecimal(102345));
			ids.add(new BigDecimal(102748));
			
			
			
			PreparedStatement sybaseQuery = SelectFromDB.OAIListSetsbyID(sybase, set, from, until, ids);
			PreparedStatement postgresQuery = SelectFromDBPostgres.OAIListSetsbyIDPostgres(postgres, set, from, until, ids);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void OAIListAllTest() {
		try {
			System.out.println("Testcase OAIListAll(String set, Date from, Date until, List<BigDecimal> ids)");
			String set = "004";
			Date from = Date.valueOf("2000-09-08");
			Date until = Date.valueOf("2005-09-08");
			List<BigDecimal> ids = new Vector<BigDecimal>();
			ids.add(new BigDecimal(88888));
			ids.add(new BigDecimal(102345));
			ids.add(new BigDecimal(102748));
			
			
			
			PreparedStatement sybaseQuery = SelectFromDB.OAIListAll(sybase, set, from, until, ids);
			PreparedStatement postgresQuery = SelectFromDBPostgres.OAIListAllPostgres(postgres, set, from, until, ids);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void RepositoryDataTest() {
		try {
			System.out.println("Testcase RepositoryData(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.RepositoryData(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.RepositoryDataPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void InterpolatedDDCClassification_withCategorieTest() {
		try {
			System.out.println("Testcase InterpolatedDDCClassification_withCategorie(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.InterpolatedDDCClassification_withCategorie(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.InterpolatedDDCClassification_withCategoriePostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void InterpolatedDDCClassificationTest() {
		try {
			System.out.println("Testcase InterpolatedDDCClassification(BigDecimal objectID)");
			BigDecimal objectID = new BigDecimal(88888);

			PreparedStatement sybaseQuery = SelectFromDB.InterpolatedDDCClassification(sybase, objectID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.InterpolatedDDCClassificationPostgres(postgres, objectID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void UsageData_MetricsTest() {
		try {
			System.out.println("Testcase UsageData_Metrics(String metrics_name)");
			String metrics_name = "Counter";

			PreparedStatement sybaseQuery = SelectFromDB.UsageData_Metrics(sybase, metrics_name);
			PreparedStatement postgresQuery = SelectFromDBPostgres.UsageData_MetricsPostgres(postgres, metrics_name);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void UsageData_Metrics_AllNamesTest() {
		try {
			System.out.println("Testcase UsageData_Metrics_AllNames(String metrics_name)");

			PreparedStatement sybaseQuery = SelectFromDB.UsageData_Metrics_AllNames(sybase);
			PreparedStatement postgresQuery = SelectFromDBPostgres.UsageData_Metrics_AllNamesPostgres(postgres);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void UsageData_Months_ListForMetricsNameTest() {
		try {
			System.out.println("Testcase UsageData_Months_ListForMetricsName(BigDecimal objectID, String metrics_name)");
			String metrics_name = "Counter";
			BigDecimal objectID = new BigDecimal(47141);

			PreparedStatement sybaseQuery = SelectFromDB.UsageData_Months_ListForMetricsName(sybase, objectID, metrics_name);
			PreparedStatement postgresQuery = SelectFromDBPostgres.UsageData_Months_ListForMetricsNamePostgres(postgres, objectID, metrics_name);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void UsageData_Overall_ForMetricsNameTest() {
		try {
			System.out.println("Testcase UsageData_Overall_ForMetricsName(BigDecimal objectID, String metrics_name)");
			String metrics_name = "Counter";
			BigDecimal objectID = new BigDecimal(1);
			
			PreparedStatement sybaseQuery = SelectFromDB.UsageData_Overall_ForMetricsName(sybase, objectID, metrics_name);
			PreparedStatement postgresQuery = SelectFromDBPostgres.UsageData_Overall_ForMetricsNamePostgres(postgres, objectID, metrics_name);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void UsageDataOIDsTest() {
		try {
			System.out.println("Testcase UsageDataOIDs()");

			PreparedStatement sybaseQuery = SelectFromDB.UsageDataOIDs(sybase);
			PreparedStatement postgresQuery = SelectFromDBPostgres.UsageDataOIDsPostgres(postgres);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void UsageDataOIDsForRepositoryTest() {
		try {
			System.out.println("Testcase UsageDataOIDsForRepository(BigDecimal repositoryID)");
			int repositoryID = 1;	
			
			PreparedStatement sybaseQuery = SelectFromDB.UsageDataOIDsForRepository(sybase, repositoryID);
			PreparedStatement postgresQuery = SelectFromDBPostgres.UsageDataOIDsForRepositoryPostgres(postgres, repositoryID);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	
	@Test public void AllOIDsByDateTest() {
		try {
			System.out.println("Testcase AllOIDsByDate(Date from, Date until, String set, BigInteger idOffset, int resultCount, Boolean rowCountOnly = false)");
			Date from = Date.valueOf("2004-09-08");
			Date until = Date.valueOf("2009-09-08");
			String set = "510";
			BigInteger idOffset = new BigInteger("50000");
			int resultCount = 100;
			Boolean rowCountOnly = false;
			
			PreparedStatement sybaseQuery = SelectFromDB.AllOIDsByDate(sybase, from, until, set, idOffset, resultCount, rowCountOnly);
			PreparedStatement postgresQuery = SelectFromDBPostgres.AllOIDsByDatePostgres(sybase, from, until, set, idOffset, resultCount, rowCountOnly);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
	@Test public void AllOIDsByDate2Test() {
		try {
			System.out.println("Testcase AllOIDsByDate(Date from, Date until, String set, BigInteger idOffset, int resultCount, Boolean rowCountOnly = true)");
			Date from = Date.valueOf("2004-09-08");
			Date until = Date.valueOf("2009-09-08");
			String set = "510";
			BigInteger idOffset = new BigInteger("50000");
			int resultCount = 100;
			Boolean rowCountOnly = true;
			
			PreparedStatement sybaseQuery = SelectFromDB.AllOIDsByDate(sybase, from, until, set, idOffset, resultCount, rowCountOnly);
			PreparedStatement postgresQuery = SelectFromDBPostgres.AllOIDsByDatePostgres(sybase, from, until, set, idOffset, resultCount, rowCountOnly);
			
			msSybase.loadStatement(sybaseQuery);
			msPostgres.loadStatement(postgresQuery);
			
			long executionTime = System.currentTimeMillis();
			msSybase.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution time Sybase: "+executionTime+" ms");
			
			executionTime = System.currentTimeMillis();
			msPostgres.execute();
			msSybase.commit();
			executionTime = System.currentTimeMillis() - executionTime;
			System.out.println("Execution Time Postgres: "+executionTime+" ms");
			
			Assert.assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(false, false);
		}
	}
}
