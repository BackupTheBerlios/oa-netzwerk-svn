/**
 * This class represents an Object in which all necessary data for the
 * Object-Manager will be stored. Every field can be accessed via setter and
 * getter methods.
 * 
 * @author Michael K&uuml;hn
 */

package de.dini.oanetzwerk.servicemodule.harvester;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * @author kuehnmic
 *
 */

public class ObjectIdentifier {
	
	/**
	 * The external Object ID which is received from the repository
	 */
	
	private String externalOID;
	
	/**
	 * The datestamp which is received from the repository
	 */
	
	private Date datestamp;
	
	/**
	 * The matching internal Object ID which is received from our database
	 */
	
	private Element rawdata;

	/**
	 * 
	 */
	
	private int internalOID;
	
	/**
	 * The log4j logger for all logging purposes.
	 */
	
	static Logger logger = Logger.getLogger (ObjectIdentifier.class);
	
	/**
	 * @param rawdata
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	
	static String encodeRawData (String rawdata) throws UnsupportedEncodingException {

		return new String (Base64.encodeBase64 (rawdata.getBytes ("UTF-8")));
	}
	
	/**
	 * @param encodedrawdata
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	
	static String decodeRawData (String encodedrawdata) throws UnsupportedEncodingException {

		return new String (Base64.decodeBase64 ((encodedrawdata).getBytes ("UTF-8")));
	}
	/**
	 * This is the standard constructor for ObjectIdentifier. It stores the
	 * external Object Identifier (from the repository) the datestamp of the
	 * last change and the internal Object identifier (used within our system)
	 * These informations are necessary for the identification of a single
	 * object. It stores the relation between the internal and the external
	 * Object Identifier. The Datestamp is used to distinguish the different
	 * versions of one object harvested at different dates.
	 * 
	 * @param externalOID
	 *            the Object Identifier which is used in the harvested
	 *            repository
	 * @param datestamp
	 *            the date of the object's last change
	 * @param internalOID
	 *            the Object Identifier which is used in our internal system and
	 *            database
	 */
	
	/**
	 * @param externalOID
	 * @param datestamp
	 * @param internalOID
	 * @param record
	 */
	
	public ObjectIdentifier (String externalOID, String datestamp, int internalOID, Element record) {

		try {
			
			this.externalOID = externalOID;
			this.datestamp = new SimpleDateFormat ("yyyy-MM-dd").parse (datestamp);
			this.internalOID = internalOID;
			this.rawdata = record;
			
		} catch (java.text.ParseException ex) {
			
			logger.error (externalOID + "has no valid Datestamp!\n" + ex.getLocalizedMessage ( ), ex);
		}
	}
	
	/**
	 * Getter method for the externalOID
	 * 
	 * @return the external object ID (comes from the harvested repository)
	 */
	/**
	 * @return
	 */
	
	final String getExternalOID ( ) {

		return this.externalOID;
	}
	
	/**
	 * Setter method for the externalOID
	 * 
	 * @param id
	 *            the external object ID to set
	 */
	
	final void setExternalOID (String id) {

		this.externalOID = id;
	}
	
	/**
	 * Getter method for the datestamp
	 * 
	 * @return the datestamp
	 */
	
	final Date getDatestamp ( ) {

		return this.datestamp;
	}
	/**
	 * Setter method for the datestamp
	 * 
	 * @param datestamp
	 *            the datestamp to set
	 */
	
	/**
	 * @param datestamp
	 */
	
	final void setDatestamp (Date datestamp) {

		this.datestamp = datestamp;
	}
	
	/**
	 * Getter method for the internalOID
	 * 
	 * @return the internalOID
	 */
	/**
	 * @return
	 */
	
	final int getInternalOID ( ) {

		return this.internalOID;
	}
	/**
	 * Setter method for the internalOID
	 * 
	 * @param internalOID
	 *            the internalOID to set
	 */
	
	/**
	 * @param internalOID
	 */
	
	final void setInternalOID (int internalOID) {

		this.internalOID = internalOID;
	}
	
	/**
	 * @return
	 */
	
	final Element getRawData ( ) {

		return this.rawdata;
	}
	
	/**
	 * @return
	 */
	
	final String getRawDataAsString ( ) {

		return rawData2String ( );
	}
	
	/**
	 * @param rawdata
	 */
	
	final void setRawData (Element rawdata) {

		this.rawdata = rawdata;
	}
	
	/**
	 * @return
	 */
	
	private String rawData2String ( ) {

		XMLOutputter outputter = new XMLOutputter ( );
		
		return outputter.outputString (this.rawdata);
	}
}
