/**
 * 
 */

package de.dini.oanetzwerk;

import org.apache.log4j.Logger;

/**
 * @author Michael Kühn
 *
 */

public class RawRecordData implements Modul2Database {
	
	static Logger logger = Logger.getLogger (RawRecordData.class);
	
	/**
	 * @param args
	 */
	
	public RawRecordData ( ) {
		
	}
	
	public static void main (String [ ] args) {
		
		
	}

	/**
	 * @see de.dini.oanetzwerk.Modul2Database#processRequest()
	 */
	
	public String processRequest (String data, String [ ] path, int i) {
		
		switch (i) {
		case 0:
			return getRawRecordData (path);
			case 1:
			deleteORawRecordData (path);
			break;
		case 2:
			postRawRecordData (path, data);
			break;
		case 3:
			putORawRecordData (path, data);
			break;
		default:
			break;
		}
		
		return "null";
	}

	/**
	 * @param path
	 * @return
	 */
	private String getRawRecordData (String [ ] path) {
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		
		if (path.length > 2)
			db.selectRawRecordData (path [1], path [2]);
		
		else
			db.selectRawRecordData (path [1]);
		
		return null;
	}

	/**
	 * @param path
	 */
	private void deleteORawRecordData (String [ ] path) {
		//NOT IMPLEMENTED
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param path
	 * @param data
	 */
	private void postRawRecordData (String [ ] path, String data) {
		//NOT IMPLEMENTED
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param path
	 * @param data
	 */
	private void putORawRecordData (String [ ] path, String data) {
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		db.insertRawRecordData (new Integer (path [1]), path [2], data);
	}
}
