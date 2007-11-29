/**
 * 
 */

package de.dini.oanetzwerk;

import org.apache.log4j.Logger;
//import org.apache.log4j.xml.DOMConfigurator;

/**
 * @author Michael Kühn
 *
 */

public class Harvester2Database implements Modul2Database {
	
	static Logger logger = Logger.getLogger (Harvester2Database.class);
	
	/**
	 * @param args
	 */
	
	public Harvester2Database ( ) {
		
		//DOMConfigurator.configure ("log4j.xml");
	}
	
	public static void main (String [ ] args) {
		
		
	}

	/**
	 * @see de.dini.oanetzwerk.Modul2Database#processRequest()
	 */
	
	public void processRequest (String request) {

		System.out.println (request);
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.putData (DBAccess.moduls.Harvester, 13, request, "date");
		
	}
}
