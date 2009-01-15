package de.dini.oanetzwerk.oaipmh.oaidc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Michael K&uuml;hn
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = "http://www.openarchives.org/OAI/2.0/oai_dc/", name = "dc")
@XmlType(name = "OAIDCType", propOrder = {
		"title",
		"namespace",
		"date",
		"language",
		"relation",
		"rights",
		"coverage",
		"contributor",
		"subject",
		"creator",
		"format",
		"type",
		"publisher",
		"source",
		"description",
		"identifier"
})
public class OAIDCType {
	
	@XmlElement(required=false, name="title", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> title;
	
	@XmlElement(required=false, name="namespace", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> namespace;
	
	@XmlElement(required=false, name="date", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> date;
	
	@XmlElement(required=false, name="language", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> language;
	
	@XmlElement(required=false, name="relation", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> relation;
	
	@XmlElement(required=false, name="rights", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> rights;
	
	@XmlElement(required=false, name="coverage", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> coverage;
	
	@XmlElement(required=false, name="contributor", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> contributor;
	
	@XmlElement(required=false, name="subject", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> subject;
	
	@XmlElement(required=false, name="creator", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> creator;
	
	@XmlElement(required=false, name="type", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> type;
	
	@XmlElement(required=false, name="format", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> format;
	
	@XmlElement(required=false, name="publisher", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> publisher;
	
	@XmlElement(required=false, name="source", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> source;
	
	@XmlElement(required=false, name="description", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> description;
	
	@XmlElement(required=false, name="identifier", namespace="http://purl.org/dc/elements/1.1/")
	protected List<String> identifier;
	
	/**
	 * @return the title
	 */
	
	public final List<String> getTitle ( ) {
		
		if (this.title == null)
			this.title = new ArrayList <String> ( );
		
		return this.title;
	}
	
	/**
	 * @return the namespace
	 */
	
	public final List<String> getNamespace ( ) {
		
		if (this.namespace == null)
			this.namespace = new ArrayList <String> ( );
		
		return this.namespace;
	}

	/**
	 * @return the date
	 */
	
	public final List<String> getDate ( ) {
		
		if (this.date == null)
			this.date = new ArrayList <String> ( );
		
		return this.date;
	}

	/**
	 * @return the language
	 */
	
	public final List<String> getLanguage ( ) {
		
		if (this.language == null)
			this.language = new ArrayList <String> ( );
		
		return this.language;
	}

	/**
	 * @return the relation
	 */
	
	public final List<String> getRelation ( ) {
		
		if (this.relation == null)
			this.relation = new ArrayList <String> ( );
		
		return this.relation;
	}
	
	/**
	 * @return the rights
	 */
	
	public final List<String> getRights ( ) {
		
		if (this.rights == null)
			this.rights = new ArrayList <String> ( );
		
		return this.rights;
	}
	
	/**
	 * @return the coverage
	 */
	
	public final List<String> getCoverage ( ) {
		
		if (this.coverage == null)
			this.coverage = new ArrayList <String> ( );
		
		return this.coverage;
	}

	/**
	 * @return the contributor
	 */
	
	public final List<String> getContributor ( ) {
		
		if (this.contributor == null)
			this.contributor = new ArrayList <String> ( );
		
		return this.contributor;
	}
	
	/**
	 * @return the subject
	 */
	
	public final List<String> getSubject ( ) {
		
		if (this.subject == null)
			this.subject = new ArrayList <String> ( );
		
		return this.subject;
	}

	/**
	 * @return the creator
	 */
	
	public final List<String> getCreator ( ) {
		
		if (this.creator == null)
			this.creator = new ArrayList <String> ( );
		
		return this.creator;
	}
	
	/**
	 * @return the creator
	 */
	
	public final List<String> getType ( ) {
		
		if (this.type == null)
			this.type = new ArrayList <String> ( );
		
		return this.type;
	}
	
	/**
	 * @return the format
	 */
	
	public final List<String> getFormat ( ) {
		
		if (this.format == null)
			this.format = new ArrayList <String> ( );
		
		return this.format;
	}
	
	/**
	 * @return the publisher
	 */
	
	public final List<String> getPublisher ( ) {
		
		if (this.publisher == null)
			this.publisher = new ArrayList <String> ( );
		
		return this.publisher;
	}
	
	/**
	 * @return the source
	 */
	
	public final List<String> getSource ( ) {
		
		if (this.source == null)
			this.source = new ArrayList <String> ( );
		
		return this.source;
	}

	/**
	 * @return the description
	 */
	
	public final List<String> getDescription ( ) {
		
		if (this.description == null)
			this.description = new ArrayList <String> ( );
		
		return this.description;
	}
	
	/**
	 * @return the identifier
	 */
	
	public final List<String> getIdentifier ( ) {
		
		if (this.identifier == null)
			this.identifier = new ArrayList <String> ( );
		
		return this.identifier;
	}
}
