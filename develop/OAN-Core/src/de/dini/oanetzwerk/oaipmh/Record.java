package de.dini.oanetzwerk.oaipmh;

import java.io.Serializable;

/**
 * @author Sammy David
 * @author Michael Kühn
 *
 */

public class Record implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2411596695965731565L;

	/**
	 * 
	 */
	
	private DCHeader header;
	
	/**
	 * 
	 */
	
	private DCMetaData metaData;
	
	/**
	 * @return the header
	 */
	
	public final DCHeader getHeader ( ) {
		
		if (this.header == null)
			this.header = new DCHeader ( );
		
		return this.header;
	}
	
	/**
	 * @return the metaData
	 */
	
	public final DCMetaData getMetaData ( ) {
		
		if (this.metaData == null)
			this.metaData = new DCMetaData ( );
		
		return this.metaData;
	}
}