/**
 * 
 */

package de.dini.oanetzwerk;

import java.sql.SQLException;
import java.sql.Statement;


/**
 * @author Michael Kühn
 *
 */

public class Harvester2Database implements Modul2Database {

	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.Modul2Database#processRequest()
	 */
	
	public void processRequest (String request) {

		System.out.println (request);
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		String query = "Insert...";
		
		try {
			
			Statement stmt = db.getConnetion ( ).createStatement ( );
			
			
			
		} catch (SQLException ex) {
			
			ex.printStackTrace ( );
		} 
		
	}

}
