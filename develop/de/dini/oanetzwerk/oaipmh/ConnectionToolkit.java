package de.dini.oanetzwerk.oaipmh;

/**
 * @author Michael K&uuml;hn
 *
 */

public abstract class ConnectionToolkit {
	
	/**
	 * 
	 */
	
	private static final DBDataConnectionFactory dbDataConnectionFactory = new DBDataConnectionFactory ( );
	
	/**
	 * 
	 */
	
	private static final RestDataConnectionFactory restDataConnectionFactory = new RestDataConnectionFactory ( );
	
	/**
	 * @param connectionType
	 * @return
	*/
	
	static final ConnectionToolkit getFactory (DataConnectionType connectionType) {
		
		switch (connectionType) {
			case DB:
				return dbDataConnectionFactory;
			
			case REST:
				return restDataConnectionFactory;
		}
		
		throw new IllegalArgumentException (connectionType + " does not exist");
	}
	
	/**
	 * @return
	 */
	
	public abstract DataConnection createDataConnection ( );
}
