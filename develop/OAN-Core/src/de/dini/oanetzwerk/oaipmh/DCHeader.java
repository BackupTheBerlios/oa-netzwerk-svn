/**
 * 
 */
package de.dini.oanetzwerk.oaipmh;

import java.io.Serializable;
import java.util.LinkedList;


/**
 * @author Michael K&uuml;hn
 *
 */

public class DCHeader implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	
	private String identifier;
	
	/**
	 * 
	 */
	
	private String datestamp;
	
	/**
	 * 
	 */
	
	private LinkedList <String> set;
	
	/**
	 * @return the identifier
	 */
	
	public final String getIdentifier ( ) {
		
		if (this.identifier == null)
			this.identifier = "";
		
		return this.identifier;
	}
	
	/**
	 * @param identifier the identifier to set
	 */
	
	public final void setIdentifier (String identifier) {
	
		this.identifier = identifier;
	}
	
	/**
	 * @return the datestamp
	 */
	
	public final String getDatestamp ( ) {
		
		if (this.datestamp == null)
			this.datestamp = "";
		
		return this.datestamp;
	}
	
	/**
	 * @param datestamp the datestamp to set
	 */
	
	public final void setDatestamp (String datestamp) {
		
		this.datestamp = datestamp;
	}
	
	/**
	 * @return the set
	 */
	
	public final LinkedList <String> getSet ( ) {
		
		if (this.set == null)
			this.set = new LinkedList <String> ( );
		
		return this.set;
	}
}
