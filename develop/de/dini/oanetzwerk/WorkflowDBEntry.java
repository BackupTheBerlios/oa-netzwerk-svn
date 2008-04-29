/**
 * 
 */

package de.dini.oanetzwerk;

import org.apache.log4j.Logger;

import de.dini.oanetzwerk.codec.RestKeyword;
import de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler;
import de.dini.oanetzwerk.server.handler.KeyWord2DatabaseInterface;

/**
 * @author Michael Kühn
 *
 */

public class WorkflowDBEntry extends
AbstractKeyWordHandler implements KeyWord2DatabaseInterface {
	
	static Logger logger = Logger.getLogger (WorkflowDBEntry.class);
	
	/**
	 * @param args
	 */
	
	public WorkflowDBEntry ( ) {
		
		super ("", RestKeyword.UNKNOWN);
	}
	
	public static void main (String [ ] args) {
		
		
	}
	
	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#deleteKeyWord(java.lang.String[])
	 */
	@Override
	protected String deleteKeyWord (String [ ] path) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	@Override
	protected String getKeyWord (String [ ] path) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("getRawRecordData");
		
		//DBAccessInterface db = DBAccess.createDBAccess ( );
		//String result = "";
		
//		if (path.length > 2)
//			result = db.selectRawRecordData (path [1], path [2]);
//		
//		else
//			result = db.selectRawRecordData (path [1]);

		
		// muss noch implementiert werden.
		return "kein Resultat.";
//		return "<OAN-REST xmlns=\"http://localhost/\"\n" +
//				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
//				"xsi:schemaLocation=\"http://localhost/schema.xsd\">\n" +
//				"\t<RawRecordData>\n\t\t" + result + "\n\t</RawRecordData>\n" +
//				"</OAN-REST>";
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#postKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String postKeyWord (String [ ] path, String data) {

		// ist nicht implementiert
		// TODO Auto-generated method stub
		return "kein Resultat.";
	}

	/**
	 * @see de.dini.oanetzwerk.server.handler.AbstractKeyWordHandler#putKeyWord(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String putKeyWord (String [ ] path, String data) {

		if (logger.isDebugEnabled ( ))
			logger.debug ("putRawRecordData");
		
		//DBAccessInterface db = DBAccess.createDBAccess ( );
//		db.insertRawRecordData (new Integer (path [2]), path [3], data);
		
		// muss noch implementiert werden
		
		return "kein Resultat.";
	}
}
