package de.dini.oanetzwerk.oaipmh;

import java.util.LinkedList;


/**
 * @author Michael K&uuml;hn
 *
 */

public class Record {

	/**
	 * 
	 */
	
	private Header header;
	
	/**
	 * 
	 */
	
	private MetaData metaData;
	
	/**
	 * @return the header
	 */
	
	public final Header getHeader ( ) {
		
		if (this.header == null)
			this.header = new Header ( );
		
		return this.header;
	}
	
	/**
	 * @return the metaData
	 */
	
	public final MetaData getMetaData ( ) {
		
		if (this.metaData == null)
			this.metaData = new MetaData ( );
		
		return this.metaData;
	}
	
}

/**
 * @author Michael K&uuml;hn
 *
 */

class Header {
	
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

class MetaData {
	
}