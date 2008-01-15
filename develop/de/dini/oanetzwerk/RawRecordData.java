/**
 * 
 */

package de.dini.oanetzwerk;

import org.apache.log4j.Logger;

/**
 * @author Michael Kühn
 *
 */

public class RawRecordData extends
AbstractKeyWordHandler implements Modul2Database {
	
	static Logger logger = Logger.getLogger (RawRecordData.class);
	
	/**
	 * @param args
	 */
	
	public RawRecordData ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug (RawRecordData.class.getName ( ) + " is called");
	}
	
	public static void main (String [ ] args) {
		
		
	}
	
	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	@Override
	protected String deleteKeyWord (String [ ] path) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	@Override
	protected String getKeyWord (String [ ] path) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("getRawRecordData");
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		
		if (path.length > 2)
			db.selectRawRecordData (path [1], path [2]);
		
		else
			db.selectRawRecordData (path [1]);
		
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String postKeyWord (String [ ] path, String data) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String putKeyWord (String [ ] path, String data) {

		if (logger.isDebugEnabled ( ))
			logger.debug ("putRawRecordData");
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.insertRawRecordData (new Integer (path [2]), path [3], data);
		
		return path [2];
	}
}
