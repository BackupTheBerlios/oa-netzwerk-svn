/**
 * 
 */

package de.dini.oanetzwerk;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;


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
	 * @param string
	 * @param string2
	 * @return
	 */
	public abstract ResultSet selectObjectEntryId (String repositoryID, String externalOID);

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

	/**
	 * @param repository_id
	 * @param harvested
	 * @param repository_datestamp
	 * @param repository_identifier
	 * @return
	 */
	public abstract String insertObject (int repository_id, Date harvested,
			Date repository_datestamp, String repository_identifier);

	/**
	 * @param string
	 * @return
	 */
	public abstract ResultSet getObject (int oid);

}
