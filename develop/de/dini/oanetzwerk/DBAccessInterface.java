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

	/**
	 * @param string
	 * @param string2
	 * @return
	 */
	public abstract String selectObjectEntryId (String repositoryID, String externalOID);

	/**
	 * @param string
	 * @param string2
	 */
	public abstract String selectRawRecordData (String internalOID, String datestamp);

	/**
	 * @param string
	 */
	public abstract String selectRawRecordData (String internalOID);

	/**
	 * @param string
	 * @param string2
	 * @param data
	 */
	public abstract int insertRawRecordData (int internalOID, String datestamp,
			String blobbb);

}
