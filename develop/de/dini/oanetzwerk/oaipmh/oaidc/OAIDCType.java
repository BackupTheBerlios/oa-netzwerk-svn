package de.dini.oanetzwerk.oaipmh.oaidc;

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
@XmlRootElement(namespace = "http://purl.org/dc/elements/1.1/", name = "dc")
@XmlType(name = "oai-dc", propOrder = {
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
		"publisher",
		"source",
		"description",
		"identifier"
})
public class OAIDCType {
	
	@XmlElement(required=false)
	protected String title;
	
	@XmlElement(required=false)
	protected String namespace;
	
	@XmlElement(required=false)
	protected String date;
	
	@XmlElement(required=false)
	protected String language;
	
	@XmlElement(required=false)
	protected String relation;
	
	@XmlElement(required=false)
	protected String rights;
	
	@XmlElement(required=false)
	protected String coverage;
	
	@XmlElement(required=false)
	protected String contributor;
	
	@XmlElement(required=false)
	protected String subject;
	
	@XmlElement(required=false)
	protected String creator;
	
	@XmlElement(required=false)
	protected String format;
	
	@XmlElement(required=false)
	protected String publisher;
	
	@XmlElement(required=false)
	protected String source;
	
	@XmlElement(required=false)
	protected String description;
	
	@XmlElement(required=false)
	protected String identifier;
	
	/**
	 * @return the title
	 */
	public final String getTitle ( ) {
	
		return this.title;
	}
	
	/**
	 * @param title the title to set
	 */
	public final void setTitle (String title) {
	
		this.title = title;
	}

	
	/**
	 * @return the namespace
	 */
	public final String getNamespace ( ) {
	
		return this.namespace;
	}

	
	/**
	 * @param namespace the namespace to set
	 */
	public final void setNamespace (String namespace) {
	
		this.namespace = namespace;
	}

	
	/**
	 * @return the date
	 */
	public final String getDate ( ) {
	
		return this.date;
	}

	
	/**
	 * @param date the date to set
	 */
	public final void setDate (String date) {
	
		this.date = date;
	}

	
	/**
	 * @return the language
	 */
	public final String getLanguage ( ) {
	
		return this.language;
	}

	
	/**
	 * @param language the language to set
	 */
	public final void setLanguage (String language) {
	
		this.language = language;
	}

	
	/**
	 * @return the relation
	 */
	public final String getRelation ( ) {
	
		return this.relation;
	}

	
	/**
	 * @param relation the relation to set
	 */
	public final void setRelation (String relation) {
	
		this.relation = relation;
	}

	
	/**
	 * @return the rights
	 */
	public final String getRights ( ) {
	
		return this.rights;
	}

	
	/**
	 * @param rights the rights to set
	 */
	public final void setRights (String rights) {
	
		this.rights = rights;
	}

	
	/**
	 * @return the coverage
	 */
	public final String getCoverage ( ) {
	
		return this.coverage;
	}

	
	/**
	 * @param coverage the coverage to set
	 */
	public final void setCoverage (String coverage) {
	
		this.coverage = coverage;
	}

	
	/**
	 * @return the contributor
	 */
	public final String getContributor ( ) {
	
		return this.contributor;
	}

	
	/**
	 * @param contributor the contributor to set
	 */
	public final void setContributor (String contributor) {
	
		this.contributor = contributor;
	}

	
	/**
	 * @return the subject
	 */
	public final String getSubject ( ) {
	
		return this.subject;
	}

	
	/**
	 * @param subject the subject to set
	 */
	public final void setSubject (String subject) {
	
		this.subject = subject;
	}

	
	/**
	 * @return the creator
	 */
	public final String getCreator ( ) {
	
		return this.creator;
	}

	
	/**
	 * @param creator the creator to set
	 */
	public final void setCreator (String creator) {
	
		this.creator = creator;
	}

	
	/**
	 * @return the format
	 */
	public final String getFormat ( ) {
	
		return this.format;
	}

	
	/**
	 * @param format the format to set
	 */
	public final void setFormat (String format) {
	
		this.format = format;
	}

	
	/**
	 * @return the publisher
	 */
	public final String getPublisher ( ) {
	
		return this.publisher;
	}

	
	/**
	 * @param publisher the publisher to set
	 */
	public final void setPublisher (String publisher) {
	
		this.publisher = publisher;
	}

	
	/**
	 * @return the source
	 */
	public final String getSource ( ) {
	
		return this.source;
	}

	
	/**
	 * @param source the source to set
	 */
	public final void setSource (String source) {
	
		this.source = source;
	}

	
	/**
	 * @return the description
	 */
	public final String getDescription ( ) {
	
		return this.description;
	}

	
	/**
	 * @param description the description to set
	 */
	public final void setDescription (String description) {
	
		this.description = description;
	}

	
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
	
	
}
