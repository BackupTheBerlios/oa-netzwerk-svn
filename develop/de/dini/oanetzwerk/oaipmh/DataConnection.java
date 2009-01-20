package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;
import java.util.LinkedList;

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
	
	/**
	 * @param identifier
	 * @return
	 */
	
	abstract public ArrayList <String> getDescriptions (String identifier);
	
	/**
	 * @param identifier
	 * @return
	 */
	
	abstract public ArrayList <String> getPublishers (String identifier);
	
	/**
	 * @param identifier
	 * @return
	 */
	
	abstract public ArrayList <String> getDates (String identifier);
	
	/**
	 * @param identifier
	 * @return
	 */
	
	abstract public ArrayList <String> getTypes (String identifier);
	
	/**
	 * @param identifier
	 * @return
	 */
	
	abstract public ArrayList <String> getFormats (String identifier);
	
	/**
	 * @param identifier
	 * @return
	 */
	
	abstract public ArrayList <String> getIdentifiers (String identifier);
	
	/**
	 * @param identifier
	 * @return
	 */
	
	abstract public ArrayList <String> getLanguages (String identifier);

	/**
	 * @param from
	 * @param until
	 * @param set
	 * @return
	 */
	
	abstract public LinkedList <Record> getIdentifier (String from, String until, String set);
}
