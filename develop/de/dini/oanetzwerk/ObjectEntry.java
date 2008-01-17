/**
 * 
 */

package de.dini.oanetzwerk;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class ObjectEntry extends 
AbstractKeyWordHandler implements Modul2Database {
	
	static Logger logger = Logger.getLogger (ObjectEntry.class);
	
	public ObjectEntry ( ) {
		
		if (logger.isDebugEnabled ( ))
			logger.debug (ObjectEntry.class.getName ( ) + " is called");
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
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("extract_repository_datestamp");
		
		try {
			
			//date = new Date (new SimpleDateFormat ("yyyy-MM-dd").parse (parseXML (data, "repository_datestamp")).getTime ( ));			
			
			String dateString = parseXML (data, "repository_datestamp");
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("datestring: " + dateString);
						
			java.util.Date sdf = new SimpleDateFormat ("yyyy-MM-dd").parse (dateString);
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("sdf: " + sdf.toString ( ));
			
			date = new Date (sdf.getTime ( ));
			//date = new Date ((sdf.parse (parseXML (data, "repository_datestamp"))).getTime ( ));
			
			
			
			//date = new Date (new SimpleDateFormat ("EEE MMM d HH:mm:ss z yyyy").parse (parseXML (data, "repository_datestamp")).getTime ( ));
			
		} catch (ParseException ex) {
			
			logger.error ("ParseError: " + ex.getLocalizedMessage ( ));
			ex.printStackTrace ( );
		}
		
		return date;
	}

	/**
	 * @param data
	 * @param string
	 */
	
	private String parseXML (String data, String node) {
		
		String result = "";
		
		if (logger.isDebugEnabled ( ))
			logger.debug ("data: " + data);
		
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
			DocumentBuilder builder = factory.newDocumentBuilder ( );
			Document document = builder.parse (new InputSource (new StringReader (data)));
			
			NodeList nodelist = document.getElementsByTagName ("entry");
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("nodlistlength: " + nodelist.getLength ( ));
			
			for (int i = 0; i < nodelist.getLength ( ); i++) {
				
				if (logger.isDebugEnabled ( )) {
					
					logger.debug (nodelist.item (i).getAttributes ( ).item (0).getNodeName ( ));
					logger.debug (nodelist.item (i).getAttributes ( ).item (0).getNodeValue ( ));
				}
				
				if (nodelist.item (i).getAttributes ( ).item (0).getNodeValue ( ).equals (node)) {
					
					result = nodelist.item (i).getTextContent ( );
					break;
				}
			}
			
			//result = nodelist.item (1).getTextContent ( );
			
			if (logger.isDebugEnabled ( ))
				logger.debug ("Node " + node + " = " + result);
			
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

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dini.oanetzwerk.AbstractKeyWordHandler#getKeyWord(java.lang.String[])
	 */
	@Override
	protected String getKeyWord (String [ ] path) {
		
		DBAccessInterface db = DBAccess.createDBAccess ( );
		String response = db.getObject (Integer.parseInt (path [2]));
		//SELECT o.object_id FROM dbo.Object o WHERE o.object_id = ?
		return ObjectEntryResponse ("<repository_datestamp>\n" + response + "</repository_datestamp>\n");
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
		
		int repository_id = extract_repository_id (data);
		String repository_identifier = extract_repository_identifier (data);
		Date repository_datestamp = extract_repository_datestamp (data);
		Date harvested = HelperMethods.today ( );
		DBAccessInterface db = DBAccess.createDBAccess ( );
		String response = db.insertObject (repository_id, harvested, repository_datestamp, repository_identifier);
		
		List <HashMap <String, String>> listentries = new ArrayList <HashMap <String, String>> ( );
		HashMap <String, String> mapEntry = new HashMap <String ,String> ( );
		mapEntry.put ("oid", response);
		listentries.add (mapEntry);
		
		return RestXmlCodec.encodeEntrySetResponseBody (listentries, "ObjectEntry");
	}
}
