package de.dini.oanetzwerk.oaipmh;

/**
 * @author Michael K&uuml;hn
 *
 */

public class DataConnectionClient {
	
	/**
	 * 
	 */
	
	private DataConnection dataConnection;
	
	/**
	 * 
	 */
	
	public DataConnectionClient ( ) {
		
		ConnectionToolkit ct;
		
		if (true) { //here will be properties
			
			ct = ConnectionToolkit.getFactory (DataConnectionType.DB);
			
		} else {
			
			ct = ConnectionToolkit.getFactory (DataConnectionType.REST);
		}
		
		this.dataConnection = ct.createDataConnection ( );
	}
	
	/**
	 * @return
	 */
	
	protected String getEarliestDateStamp ( ) {
		
		return "1970-01-01";
	}
	
	/**
	 * @return
	 */
	
	protected String getSetList ( ) {
		
		return "";
	}
	
	
}
