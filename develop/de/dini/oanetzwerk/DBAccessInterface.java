/**
 * 
 */

package de.dini.oanetzwerk;

import java.sql.Connection;


/**
 * @author Michael Kühn
 *
 */

public interface DBAccessInterface {

	/**
	 * 
	 */
	
	public abstract void createConnection ( );

	/**
	 * 
	 */
	
	public abstract void closeConnection ( );

	/**
	 * @return
	 */
	
	public abstract Connection getConnetion ( );
	
	/**
	 * @param ac
	 */
	
	public abstract void setAutoCom (boolean ac);
	
	/**
	 * @param moduls
	 * @param i
	 * @param request
	 * @param string
	 */
	
	public abstract void putData (DBAccess.moduls modul, String repositoryName, String repositoryIdentifier, String repositoryDate, String data);

}
