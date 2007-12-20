/**
 * 
 */

package de.dini.oanetzwerk;


/**
 * @author Michael Kühn
 *
 */

public class ObjectEntryID implements Modul2Database {

	/**
	 * @see de.dini.oanetzwerk.Modul2Database#processRequest(java.lang.String, java.lang.String[], int)
	 */
	
	public String processRequest (String data, String [ ] path, int i) {

		switch (i) {
		case 0:
			return getObjectEntryID (path);
			case 1:
			deleteObjectEntryID (path);
			break;
		case 2:
			postObjectEntryID (path, data);
			break;
		case 3:
			putObjectEntryID (path, data);
			break;
		default:
			break;
		}
		
		return "null";
	}

	/**
	 * @param path
	 * @param data
	 */
	private void putObjectEntryID (String [ ] path, String data) {
		//NOT IMPLEMENTED
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param path
	 * @param data
	 */
	private void postObjectEntryID (String [ ] path, String data) {
		//NOT IMPLEMENTED
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param path
	 */
	private void deleteObjectEntryID (String [ ] path) {
		//NOT IMPLEMENTED
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param path
	 */
	private String getObjectEntryID (String [ ] path) {

		DBAccessInterface db = DBAccess.createDBAccess ( );
		String response = db.selectObjectEntryId (path[1], path[2]);

		if (response != null) {
			
			return ObjectEntryIDResponse ("<OID>" + response + "</OID>");
			
		} else return ObjectEntryIDResponse ("<NULL />");
	}

	/**
	 * @param string
	 * @return
	 */
	private String ObjectEntryIDResponse (String string) {

		StringBuffer buffer = new StringBuffer ("<OAN-REST xsi:schemaLocation=\"http://localhost/schema.xsd\n")
			.append ("\t<ObjectEntryIDResponse>\n");
		
		buffer.append ("\t\t").append (string).append ("\n");
		buffer.append ("\t</ObjectEntryIDResponse>\n");
		buffer.append ("</OAN-REST>");
		return null;
	}

	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}

}
