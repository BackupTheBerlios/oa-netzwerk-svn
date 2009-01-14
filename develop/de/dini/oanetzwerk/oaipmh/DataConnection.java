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

	/**
	 * @return
	 */
	
	abstract public String getDateStamp (String identifier);

	/**
	 * @return
	 */
	
	abstract public ArrayList <String> getClassifications (String identifier);
	
	/**
	 * @param string
	 * @return
	 */
	
	abstract public ArrayList <String> getTitles (String identifier);
	
	/**
	 * @param string
	 * @return
	 */
	
	abstract public ArrayList <String> getCreators (String identifier);
	
	/**
	 * @param string
	 * @return
	 */
	
	abstract public ArrayList <String> getSubjects (String identifier);

	abstract public ArrayList <String> getDescriptions (String identifier);

	abstract public ArrayList <String> getPublishers (String identifier);

	abstract public ArrayList <String> getDates (String identifier);

	abstract public ArrayList <String> getTypes (String identifier);

	abstract public ArrayList <String> getFormats (String identifier);

	abstract public ArrayList <String> getIdentifiers (String identifier);

	abstract public ArrayList <String> getLanguages (String identifier);
}
