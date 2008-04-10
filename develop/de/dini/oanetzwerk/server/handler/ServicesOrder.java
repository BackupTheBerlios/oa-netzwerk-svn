/**
 * 
 */

package de.dini.oanetzwerk.server.handler;

import java.math.BigDecimal;
import java.sql.SQLException;

import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.server.database.DBAccess;
import de.dini.oanetzwerk.server.database.DBAccessInterface;

/**
 * @author Michael K&uuml;hn
 *
 */

public class ServicesOrder extends AbstractKeyWordHandler implements
		KeyWord2DatabaseInterface {
	
	public ServicesOrder ( ) {

		super (ServicesOrder.class.getName ( ), RestKeyword.ServiceOrder);
	}
	
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
		
		try {
			
			db.createConnection ( );
			
			this.resultset = db.selectServicesOrder (new BigDecimal (path [0]));
			
		} catch (SQLException ex) {
			
			logger.error ("An error occured while processing Get ObjectEntryID: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} finally {
			
			db.closeConnection ( );
		}
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
}
