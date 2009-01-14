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

	@Override
	public ArrayList <String> getClassifications (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDateStamp (String identifier) {

		// TODO Auto-generated method stub
		return "1646-07-01";
	}

	@Override
	public ArrayList <String> getCreators (String string) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getSubjects (String string) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getTitles (String string) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getDates (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getDescriptions (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getFormats (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getIdentifiers (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getLanguages (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getPublishers (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList <String> getTypes (String identifier) {

		// TODO Auto-generated method stub
		return null;
	}
}
