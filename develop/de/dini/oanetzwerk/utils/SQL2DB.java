/**
 * 
 */

package de.dini.oanetzwerk.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Michael K&uuml;hn
 *
 */

public class SQL2DB {
	
	//static Logger logger = Logger.getLogger (SQL2DB.class);
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	
	public static void main (String [ ] args) throws Exception {
		
		DriverManager.setLogWriter( new PrintWriter (System.out) );
		
		DriverManager.registerDriver (new com.sybase.jdbc3.jdbc.SybDriver ( ));
		
		Connection con = DriverManager.getConnection ("jdbc:sybase:Tds:themis.rz.hu-berlin.de:2025?ServiceName=oanetztest", "", ""); //TODO: username und passowrd einfügen!!!
		
		con.setAutoCommit (false);
		
		Statement stmt = con.createStatement ( );
		
		for (String filename : args) {
			
			BufferedReader br = new BufferedReader (new FileReader (filename));
			
			String sql = br.readLine ( );
			
			try {
				
				while (sql != null) {
					
					stmt.executeUpdate (sql);
					if (1 == 0)
						throw new SQLException ( );
					
					//System.out.println (sql);
					sql = br.readLine ( );
				}
				con.commit ( );
				con.setAutoCommit (true);
				
			} catch (SQLException ex) {
				
				con.rollback ( );
				if (con != null)
					con.close ( );
				throw ex;
			}
		}
		
		if (con != null)
			con.close ( );
	}
}
