/**
 * 
 */

package de.dini.oanetzwerk;

import org.apache.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Michael Kühn
 *
 */

public class RawRecordData extends
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (RawRecordData.class);
	
	/**
	 * @param args
	 */
	
	public RawRecordData ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug (RawRecordData.class.getName ( ) + " is called");
	}
	
	public static void main (String [ ] args) {
		
		//TODO: Testing stuff
	}
	
	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String deleteKeyWord (String [ ] path) {
		
		//NOT IMPLEMENTED
		logger.warn ("postObjectEntryID is not implemented");
		throw new NotImplementedException ( );
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	@Override
	protected String getKeyWord (String [ ] path) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("getRawRecordData");
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		String result = "";
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("length = " + path.length);
		
		if (path.length > 3)
			result = db.selectRawRecordData (path [2], path [3]);
		
		else
			result = db.selectRawRecordData (path [2]);
		
		return "<OAN-REST xmlns=\"http://localhost/\"\n" +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
				"xsi:schemaLocation=\"http://localhost/schema.xsd\">\n" +
				"\t<RawRecordData>\n\t\t" + result + "\n\t</RawRecordData>\n" +
				"</OAN-REST>";
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 * This method is not implemented because this would be useless request for now.
	 */
	
	@Override
	protected String postKeyWord (String [ ] path, String data) {
		
		//NOT IMPLEMENTED
		logger.warn ("postObjectEntryID is not implemented");
		throw new NotImplementedException ( );
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
