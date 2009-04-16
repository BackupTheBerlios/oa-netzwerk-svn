package de.dini.oanetzwerk.server.database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.utils.exceptions.WrongStatementException;

/**
 * @author Michael K&uuml;hn
 *
 */

public class DBAccessNG {
	
	/**
	 * 
	 */
	
	private static Logger logger = Logger.getLogger (DBAccessNG.class);
	
	/**
	 * 
	 */
	
	private DataSource ds;
	
	/**
	 * 
	 */
	
	private Connection dataSourceConnection;
	
	/**
	 * 
	 */
	
	private StatementConnection statementConnection;
	
	/**
	 * 
	 */
	
	private boolean isSingeleStatementConnection;
	
	/**
	 * 
	 */
	
	public DBAccessNG ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("DBAccessNG Instance will be prepared");
		
		try {
			
			this.statementConnection = null;
			this.ds = (DataSource) ((Context) new InitialContext ( ).lookup ("java:comp/env")).lookup ("jdbc/oanetztest");
			this.dataSourceConnection = this.ds.getConnection ( );
			
		} catch (NamingException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
			
		} catch (SQLException ex) {
			
			logger.error (ex.getLocalizedMessage ( ), ex);
		}
	}
	
	public DBAccessNG (String dataSource) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("DBAccessNG Instance will be prepared");
		
		try {
			
			this.statementConnection = null;
			this.ds = (DataSource) ((Context) new InitialContext ( ).lookup ("java:comp/env")).lookup (dataSource);
			this.dataSourceConnection = this.ds.getConnection ( );
			
		} catch (Exception ex) {
			
			logger.error ("DataSource could not be loaded. We can't connect to the database. Please enter the correct datasource!");
			logger.error (ex.getLocalizedMessage ( ), ex);
		}
	}
	
	/**
	 * @return
	 * @throws WrongStatementException
	 * @throws SQLException
	 */
	
	public StatementConnection getSingleStatementConnection ( ) throws WrongStatementException, SQLException {
		
		if (this.statementConnection == null) {

			if (logger.isDebugEnabled ( ))
				logger.debug ("Creating new SingleStatementConnection");

			if (this.dataSourceConnection == null)
				throw new SQLException ("Connection based on the given data source not available!");
			
			this.statementConnection = new SingleStatementConnection (this.dataSourceConnection);
			this.isSingeleStatementConnection = true;
			
			return this.statementConnection;
		}
		
		if (this.isSingeleStatementConnection) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Connection already exists, getting the existing one");
			
			this.statementConnection.close ( );
			this.statementConnection = new SingleStatementConnection (this.ds.getConnection ( ));
			
			return this.statementConnection;
			
		} else {
			
			logger.error ("You can not combine SingleStatementConnections and MultipeStatementConnections!");
			throw new WrongStatementException ("Please use a MultipleStatementConnection");
		}
	}
	
	/**
	 * @return
	 * @throws WrongStatementException
	 * @throws SQLException
	 */
	
	public StatementConnection getMultipleStatementConnection ( ) throws WrongStatementException, SQLException {
		
		if (this.statementConnection == null) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Creating new MultipleStatementConnection");
			
			if (this.dataSourceConnection == null)
				throw new SQLException ("Connection based on the given data source not available!");
			
			this.statementConnection = new MultipleStatementConnection (this.dataSourceConnection);
			this.isSingeleStatementConnection = false;
			
			return this.statementConnection;
		}
		
		if (isSingeleStatementConnection) {
			
			logger.error ("You can not combine SingleStatementConnections and MultipeStatementConnections!");
			throw new WrongStatementException ("Please use a SingleStatementConnection");
			
		} else {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Connection already exists, getting the existing one");
			
			this.statementConnection.close ( );
			this.statementConnection = new MultipleStatementConnection (this.ds.getConnection ( ));
			
			return this.statementConnection;
		}
	}
}
