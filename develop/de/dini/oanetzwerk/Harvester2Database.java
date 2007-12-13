/**
 * 
 */

package de.dini.oanetzwerk;

import org.apache.log4j.Logger;

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
	
	public void processRequest (String data, String path) {
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		String repositoryName = getRepositoryName ( );
		String repositoryIdentifier = getRepositoryIdentifier ( );
		String repositoryDate = getRepositoryDate ( );
		db.putData (DBAccess.moduls.Harvester, repositoryName, repositoryIdentifier, repositoryDate, data);
	}

	/**
	 * @return
	 */
	private String getRepositoryDate ( ) {

		return null;
	}

	/**
	 * @return
	 */
	private String getRepositoryIdentifier ( ) {

		return null;
	}

	/**
	 * @return
	 */
	private String getRepositoryName ( ) {

		return null;
	}
}
