package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;

/**
 * @author Michael K&uuml;hn
 *
 */

public abstract class DataConnection {

	/**
	 * @return
	 */
	
	abstract public String getEarliestDataStamp ( );

	/**
	 * @param identifier
	 * @return
	 */
	
	abstract public boolean existsIdentifier (String identifier);

	/**
	 * @return
	 */
	
	abstract public ArrayList <String [ ]> getSets ( );
}
