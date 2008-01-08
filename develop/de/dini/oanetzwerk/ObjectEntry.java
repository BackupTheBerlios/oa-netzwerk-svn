/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.dini.oanetzwerk.utils.HelperMethods;


/**
 * @author Michael Kühn
 *
 */

public class ObjectEntry implements Modul2Database {
	
	static Logger logger = Logger.getLogger (ObjectEntry.class);
	
	/**
	 * @see de.dini.oanetzwerk.Modul2Database#processRequest(java.lang.String, java.lang.String[], int)
	 */
	public String processRequest (String data, String [ ] path, int i) {

		if (logger.isDebugEnabled ( ))
			logger.debug (ObjectEntry.class.getName ( ) + " called");
		
		switch (i) {
		case 0:
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("get case chosen");
			
			return getObjectEntry (path);
			
		case 1:
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("delete case chosen");
						
			return deleteObjectEntry (path);
			
		case 2:
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("post case chosen");
			
			return postObjectEntry (path, data);
			
		case 3:
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("put case chosen");

			return putObjectEntry (path, data);
			
		default:
			break;
		}
		
		return "null";
	}

	/**
	 * @param path
	 * @param data
	 * @return
	 */
	
	private String putObjectEntry (String [ ] path, String data) {
		
		int repository_id = extract_repository_id (data);
		String repository_identifier = extract_repository_identifier (data);
		Date repository_datestamp = extract_repository_datestamp (data);
		Date harvested = HelperMethods.today ( );
		DBAccessInterface db = DBAccess.createDBAccess ( );
		String response = db.insertObject (repository_id, harvested, repository_datestamp, repository_identifier);
		
		return ObjectEntryResponse ("<OID>" + response + "</OID>");
	}

	/**
	 * @param response
	 * @return
	 */
	
	private String ObjectEntryResponse (String response) {

		StringBuffer buffer = new StringBuffer ("<OAN-REST xmlns=\"http://localhost/\"\n")
		.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n")
		.append("xsi:schemaLocation=\"http://localhost/schema.xsd\">\n")
		.append ("\t<ObjectEntryResponse>\n");
		
		buffer.append ("\t\t").append (response).append ("\n");
		buffer.append ("\t</ObjectEntryResponse>\n");
		buffer.append ("</OAN-REST>");
		
		return buffer.toString ( );
	}

	/**
	 * @param data
	 * @return
	 */
	
	private Date extract_repository_datestamp (String data) {
		
		Date date = null;
		
		try {
			
			date = new Date (new SimpleDateFormat ("yyyy-MM-dd").parse (parseXML (data, "repository_datestamp")).getTime ( ));
			
		} catch (ParseException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		return date;
	}

	/**
	 * @param data
	 * @param string
	 */
	
	private String parseXML (String data, String node) {
		
		String result = null;
		
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
			DocumentBuilder builder = factory.newDocumentBuilder ( );
			Document document = builder.parse (new InputSource (new StringReader (data)));
			
			NodeList nodelist = document.getElementsByTagName (node);
			
			result = nodelist.item (1).getTextContent ( );
			
		} catch (SAXException saex) {
			
			logger.error (saex.getLocalizedMessage ( ));
			saex.printStackTrace ( );
			
		} catch (ParserConfigurationException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
			
		} catch (IOException ex) {
			
			logger.error (ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		return result;
	}

	/**
	 * @param data
	 * @return
	 */
	
	private String extract_repository_identifier (String data) {
		
		return parseXML (data, "repository_identifier");
	}

	/**
	 * @param data
	 * @return
	 */
	private int extract_repository_id (String data) {
		
		int result;
		result = new Integer (parseXML (data, "repository_id"));
		
		return result;
	}

	/**
	 * @param path
	 * @param data
	 * @return
	 */
	
	private String postObjectEntry (String [ ] path, String data) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param path
	 * @return
	 */
	
	private String deleteObjectEntry (String [ ] path) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param path
	 * @return
	 */
	
	private String getObjectEntry (String [ ] path) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}
}
