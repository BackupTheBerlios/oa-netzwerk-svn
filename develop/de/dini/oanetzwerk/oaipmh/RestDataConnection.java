package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;
import java.util.LinkedList;

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
		
		return "1646-07-01";
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

	/**
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getClassifications(java.lang.String)
	 */
	
	@Override
	public ArrayList <String> getClassifications (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getDateStamp(java.lang.String)
	 */
	@Override
	public String getDateStamp (String identifier) {

		// TODO Auto-generated method stub
		return "1646-07-01";
	}
	
	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getCreators(java.lang.String)
	 */
	@Override
	public ArrayList <String> getCreators (String string) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getSubjects(java.lang.String)
	 */
	@Override
	public ArrayList <String> getSubjects (String string) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getTitles(java.lang.String)
	 */
	@Override
	public ArrayList <String> getTitles (String string) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getDates(java.lang.String)
	 */
	@Override
	public ArrayList <String> getDates (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getDescriptions(java.lang.String)
	 */
	@Override
	public ArrayList <String> getDescriptions (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getFormats(java.lang.String)
	 */
	@Override
	public ArrayList <String> getFormats (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getIdentifiers(java.lang.String)
	 */
	@Override
	public ArrayList <String> getIdentifiers (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getLanguages(java.lang.String)
	 */
	@Override
	public ArrayList <String> getLanguages (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getPublishers(java.lang.String)
	 */
	@Override
	public ArrayList <String> getPublishers (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getTypes(java.lang.String)
	 */
	@Override
	public ArrayList <String> getTypes (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getIdentifierList(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public LinkedList <Record> getIdentifierList (String from, String until, String set) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.dini.oanetzwerk.oaipmh.DataConnection#getRecordList(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public LinkedList <Record> getRecordList (String from, String until, String set) {

		// TODO Auto-generated method stub
		return null;
	}
}
