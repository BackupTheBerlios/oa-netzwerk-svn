package de.dini.oanetzwerk.oaipmh;

/**
 * @author Michael K&uuml;hn
 *
 */

class DBDataConnectionFactory extends ConnectionToolkit {

	/**
	 * @see de.dini.oanetzwerk.oaipmh.ConnectionToolkit#createDataConnection()
	 */
	
	private DBDataConnection dataConnection = null;
	
	@Override
	public DataConnection createDataConnection ( ) {
		
		if (this.dataConnection == null) {
			
			dataConnection = new DBDataConnection();
		}
		
		return this.dataConnection;
	}
}
