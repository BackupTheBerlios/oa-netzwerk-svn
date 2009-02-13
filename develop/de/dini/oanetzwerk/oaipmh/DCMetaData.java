package de.dini.oanetzwerk.oaipmh;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author Michael K&uuml;hn
 *
 */

public class DCMetaData implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	
	private LinkedList <String> title;
	
	/**
	 * 
	 */
	
	private LinkedList <String> creator;
	
	/**
	 * 
	 */
	
	private LinkedList <String> subject;
	
	/**
	 * 
	 */
	
	private LinkedList <String> description;
	
	/**
	 * 
	 */
	
	private LinkedList <String> date;
	
	/**
	 * 
	 */
	
	private LinkedList <String> type;
	
	/**
	 * 
	 */
	
	private LinkedList <String> format;
	
	/**
	 * 
	 */
	
	private LinkedList <String> identifier;
	
	/**
	 * 
	 */
	
	private LinkedList <String> language;
	
	/**
	 * @return the title
	 */
	
	public final LinkedList <String> getTitle ( ) {
		
		if (this.title == null)
			this.title = new LinkedList <String> ( );
		
		return this.title;
	}
	
	/**
	 * @return the creator
	 */
	
	public final LinkedList <String> getCreator ( ) {
		
		if (this.creator == null)
			this.creator = new LinkedList <String> ( );
		
		return this.creator;
	}
	
	/**
	 * @return the subject
	 */
	
	public final LinkedList <String> getSubject ( ) {
		
		if (this.subject == null)
			this.subject = new LinkedList <String> ( );
		
		return this.subject;
	}
	
	/**
	 * @return the description
	 */
	
	public final LinkedList <String> getDescription ( ) {
		
		if (this.description == null)
			this.description = new LinkedList <String> ( );
		
		return this.description;
	}
	
	/**
	 * @return the date
	 */
	
	public final LinkedList <String> getDate ( ) {
		
		if (this.date == null)
			this.date  = new LinkedList <String> ( );
		
		return this.date;
	}
	
	/**
	 * @return the type
	 */
	
	public final LinkedList <String> getType ( ) {
		
		if (this.type == null)
			this.type = new LinkedList <String> ( );
		
		return this.type;
	}
	
	/**
	 * @return the format
	 */
	
	public final LinkedList <String> getFormat ( ) {
		
		if (this.format == null)
			this.format = new LinkedList <String> ( );
		
		return this.format;
	}
	
	/**
	 * @return the identifier
	 */
	
	public final LinkedList <String> getIdentifier ( ) {
		
		if (this.identifier == null)
			this.identifier = new LinkedList <String> ( );
		
		return this.identifier;
	}
	
	/**
	 * @return the language
	 */
	
	public final LinkedList <String> getLanguage ( ) {
		
		if (this.language == null)
			this.language = new LinkedList <String> ( );
		
		return this.language;
	}
}
