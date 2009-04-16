package de.dini.oanetzwerk.server.database;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 *
 */
public class DBAccessNGTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass ( ) throws Exception {

	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass ( ) throws Exception {

	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp ( ) throws Exception {

	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown ( ) throws Exception {

	}
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.server.database.DBAccessNG#DBAccessNG()}.
	 */
	@Test
	public final void testDBAccessNG ( ) {
		
		DBAccessNG dbaccess = new DBAccessNG ( );
		
		assertNotNull (dbaccess);
		dbaccess = null;
	}
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.server.database.DBAccessNG#DBAccessNG(java.lang.String)}.
	 */
	@Test
	public final void testDBAccessNGString ( ) {

		DBAccessNG dbaccess = new DBAccessNG ("jdbc/oanetztest");
		
		assertNotNull (dbaccess);
		
		dbaccess = null;
	}
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.server.database.DBAccessNG#getSingleStatementConnection()}.
	 * @throws SQLException 
	 * @throws WrongStatementException 
	 */
	@Test
	public final void testGetSingleStatementConnection ( ) throws WrongStatementException {

		DBAccessNG dbaccess = new DBAccessNG ( );
		
		try {
			
			dbaccess.getSingleStatementConnection ( );
			fail ("SQLException expected due to invalid dataSource");
			
		} catch (SQLException ex) {
			
		}
		
		dbaccess = null;
	}
	
	/**
	 * Test method for {@link de.dini.oanetzwerk.server.database.DBAccessNG#getMultipleStatementConnection()}.
	 */
	@Test
	public final void testGetMultipleStatementConnection ( ) throws WrongStatementException {

		DBAccessNG dbaccess = new DBAccessNG ( );
		
		try {
			
			dbaccess.getMultipleStatementConnection ( );
			fail ("SQLException expected due to invalid dataSource");
			
		} catch (SQLException ex) {
			
		}
		
		dbaccess = null;
	}
	
}
