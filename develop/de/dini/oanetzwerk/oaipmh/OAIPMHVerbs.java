package de.dini.oanetzwerk.oaipmh;

import java.util.Map;

/**
 * @author MIchael K&uuml;hn
 *
 */

public interface OAIPMHVerbs {

	/**
	 * @param parameterMap 
	 * @return
	 */
	
	String processRequest (Map <String, String [ ]> parameterMap );
}
