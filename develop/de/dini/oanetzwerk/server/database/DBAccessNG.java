package de.dini.oanetzwerk.server.database;

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
			
		} catch (NamingException ex) {
			
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
			
			this.statementConnection = new SingleStatementConnection (this.ds.getConnection ( ));
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
			
			this.statementConnection = new MultipleStatementConnection (this.ds.getConnection ( ));
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
