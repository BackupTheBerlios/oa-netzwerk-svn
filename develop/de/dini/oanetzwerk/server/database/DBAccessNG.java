/**
 * 
 */

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
	
	static Logger logger = Logger.getLogger (DBAccessNG.class);
	private DataSource ds;
	private StatementConnection statementConnection;
	private boolean isSingeleStatementConnection;
	
	public DBAccessNG ( ) {
		
		try {
			
			this.statementConnection = null;
			this.ds = (DataSource) ((Context) new InitialContext ( ).lookup ("java:comp/env")).lookup ("jdbc/oanetztest");
			
		} catch (NamingException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
	}
	
	public StatementConnection getSingleStatementConnection ( ) throws WrongStatementException, SQLException {
		
		if (this.statementConnection == null) {
			
			this.statementConnection = new SingleStatementConnection (this.ds.getConnection ( ));
			this.isSingeleStatementConnection = true;
		}
		
		if (isSingeleStatementConnection) {
			
			return this.statementConnection;
			
		} else {
			
			throw new WrongStatementException ("");
		}
	}
	
	public StatementConnection getMultipleStatementConnection ( ) throws WrongStatementException, SQLException {
		
		if (this.statementConnection == null) {
			
			this.statementConnection = new MultipleStatementConnection (this.ds.getConnection ( ));
			this.isSingeleStatementConnection = false;
			
			return this.statementConnection;
		}
		
		if (isSingeleStatementConnection) {
			
			throw new WrongStatementException ("");
			
		} else {
		
			return this.statementConnection;
		}
	}
}
