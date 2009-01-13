/**
 * 
 */
package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;

/**
 * @author Michael K&uuml;hn
 *
 */

public class RestDataConnection extends DataConnection {

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getEarliestDataStamp()
	 */
	
	@Override
	public String getEarliestDataStamp ( ) {
		
		return "1970-01-01";
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#existsIdentifier(java.lang.String)
	 */
	
	@Override
	public boolean existsIdentifier (String identifier) {

		return true;
	}

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getSets()
	 */
	
	@Override
	public ArrayList <String [ ]> getSets ( ) {
		
		return null;
	}
}
