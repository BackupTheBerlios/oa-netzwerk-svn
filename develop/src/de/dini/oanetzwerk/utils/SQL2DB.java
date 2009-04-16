/**
 * 
 */

package de.dini.oanetzwerk.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
		
		
		String username = args[0];
		String pw = args[1];
		
		DriverManager.setLogWriter( new PrintWriter (System.out) );
		
		
		
		
		DriverManager.registerDriver (new com.sybase.jdbc3.jdbc.SybDriver ( ));
		
		Connection con = DriverManager.getConnection ("jdbc:sybase:Tds:127.0.0.1:2025?ServiceName=oanetzwerktest", username, pw); 
		
		
		
		con.setAutoCommit (false);
		
		Statement stmt = con.createStatement ( );
		
//		for (String filename : args) {
		String filename = args[2];

		// read an UTF-8 encoded file
		FileInputStream fis = new FileInputStream(filename);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");	
		
		BufferedReader br = new BufferedReader (isr);
			
			
			String sql = br.readLine ( );
			
			try {
				
				while (sql != null) {
					System.out.println (sql);					
					stmt.executeUpdate (sql);
					if (1 == 0)
						throw new SQLException ( );
					

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
//		}
		
		if (con != null)
			con.close ( );
	}
}
