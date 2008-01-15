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

public class ObjectEntryID extends 
AbstractKeyWordHandler implements Modul2Database {
	
	static Logger logger = Logger.getLogger (ObjectEntryID.class);
	
	public ObjectEntryID ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug (ObjectEntryID.class.getName ( ) + " is called");
	}
	
	/**
	 * @param string
	 * @return
	 */
	
	private String ObjectEntryIDResponse (String string) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("getObjectEntryID");
		
		// to include: xmlns=\"http://localhost/\"\n" +
		//"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
		//"xsi:schemaLocation=\"http://localhost/schema.xsd\"
		
		StringBuffer buffer = new StringBuffer ("<OAN-REST xmlns=\"http://localhost/\"\n")
			.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n")
			.append("xsi:schemaLocation=\"http://localhost/schema.xsd\">\n")
			.append ("\t<ObjectEntryIDResponse>\n");
		
		buffer.append ("\t\t").append (string).append ("\n");
		buffer.append ("\t</ObjectEntryIDResponse>\n");
		buffer.append ("</OAN-REST>");
		
		return buffer.toString ( );
	}

	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	@Override
	protected String deleteKeyWord (String [ ] path) {

		//NOT IMPLEMENTED
		logger.warn ("deleteObjectEntryID is not implemented");
		throw new NotImplementedException ( );
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	@Override
	protected String getKeyWord (String [ ] path) {

		DBAccessInterface db = DBAccess.createDBAccess ( );
		String response = db.selectObjectEntryId (path[2], path[3]);
		
		if (response != null) {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Database response: " + response);
			
			return ObjectEntryIDResponse ("<OID>" + response + "</OID>");
			
		} else {
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Database response: NULL");
			
			return ObjectEntryIDResponse ("<NULL />");
		}
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
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

		//NOT IMPLEMENTED
		logger.warn ("putObjectEntryID is not implemented");
		throw new NotImplementedException ( );
	}
}
