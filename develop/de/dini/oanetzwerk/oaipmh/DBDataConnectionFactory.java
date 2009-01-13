package de.dini.oanetzwerk.oaipmh;

/**
 * @author Michael K&uuml;hn
 *
 */

class DBDataConnectionFactory extends ConnectionToolkit {

	/**
	 * @see de.dini.oanetzwerk.oaipmh.ConnectionToolkit#createDataConnection()
	 */
	
	@Override
	public DataConnection createDataConnection ( ) {
		
		DBDataConnection dataConnection = new DBDataConnection ( ); 
		
		return dataConnection;
	}
}
