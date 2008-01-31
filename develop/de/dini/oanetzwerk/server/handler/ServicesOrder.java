/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.ResultSet;

import de.dini.oanetzwerk.server.database.DBAccess;
import de.dini.oanetzwerk.server.database.DBAccessInterface;


/**
 * @author Michael Kühn
 *
 */

public class ServicesOrder extends AbstractKeyWordHandler implements
		KeyWord2DatabaseInterface {
	
	private ResultSet resultset;
	
	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	@Override
	protected String deleteKeyWord (String [ ] path) {

		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		db.closeConnection ( );
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	@Override
	protected String getKeyWord (String [ ] path) {

		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		this.resultset = db.selectServicesOrder (new BigDecimal (path [2]));
		
		db.closeConnection ( );
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String postKeyWord (String [ ] path, String data) {

		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		db.closeConnection ( );
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String putKeyWord (String [ ] path, String data) {

		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.createConnection ( );
		
		db.closeConnection ( );
		return null;
	}

	/**
	 * @param args
	 */
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}

}
