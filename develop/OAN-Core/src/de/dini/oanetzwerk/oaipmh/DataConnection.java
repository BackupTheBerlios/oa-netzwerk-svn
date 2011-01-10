package de.dini.oanetzwerk.oaipmh;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Michael K&uuml;hn
 *
 */

public abstract class DataConnection {

	abstract public BigDecimal getInternalIdentifier (String repository_identifier);
	
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
	 * @param identifier
	 * @return
	 */
	
	abstract public boolean existsRepositoryIdentifier (String identifier);

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
	
	abstract public LinkedList <Record> getIdentifierList (String from, String until, String set, BigInteger idOffset, int resultCount);

	/**
	 * @param from
	 * @param until
	 * @param set
	 * @return
	 */
	
	abstract public Integer getRecordListSize (String from, String until, String set);
	
	
	abstract public LinkedList<Record> getRecordList (String from, String until, String set, BigInteger idOffset, int resultCount);
	
	
}
