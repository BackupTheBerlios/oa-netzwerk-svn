package de.dini.oanetzwerk.oaipmh.oai_identifier;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for oai-identifierType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="oai-identifierType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="scheme" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="repositoryIdentifier" type="{http://www.openarchives.org/OAI/2.0/oai-identifier}repositoryIdentifierType"/>
 *         &lt;element name="delimiter" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sampleIdentifier" type="{http://www.openarchives.org/OAI/2.0/oai-identifier}sampleIdentifierType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = "http://www.openarchives.org/OAI/2.0/oai-identifier/", name = "oai-identifier")
@XmlType(name = "oai-identifierType", propOrder = {
	"oaiidentifier",
    "scheme",
    "repositoryIdentifier",
    "delimiter",
    "sampleIdentifier"
})
public class OaiIdentifierType {
	//TODO: still problem with the xml, see http://re.cs.uct.ac.za/
	@XmlElement(required = true, name="oai-identifier", namespace = "http://www.openarchives.org/OAI/2.0/oai-identifier/")
	protected OaiIdentifierType oaiidentifier;
	
    @XmlElement(required = true, namespace = "http://www.w3.org/2001/XMLSchema")
    protected String scheme;
    @XmlElement(required = true, namespace = "http://www.openarchives.org/OAI/2.0/oai-identifier/")
    protected String repositoryIdentifier;
    @XmlElement(required = true, namespace = "http://www.w3.org/2001/XMLSchema")
    protected String delimiter;
    @XmlElement(required = true, namespace = "http://www.openarchives.org/OAI/2.0/oai-identifier/")
    protected String sampleIdentifier;
    
    /**
     * @return
     */
    
    public OaiIdentifierType getOaiIdentifier ( ) {
    	
    	if (this.oaiidentifier == null)
    		this.oaiidentifier = new OaiIdentifierType ( );
    	
    	return this.oaiidentifier;
    }
    
    /**
     * Gets the value of the scheme property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Sets the value of the scheme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScheme(String value) {
        this.scheme = value;
    }

    /**
     * Gets the value of the repositoryIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepositoryIdentifier() {
        return repositoryIdentifier;
    }

    /**
     * Sets the value of the repositoryIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepositoryIdentifier(String value) {
        this.repositoryIdentifier = value;
    }

    /**
     * Gets the value of the delimiter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the value of the delimiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelimiter(String value) {
        this.delimiter = value;
    }

    /**
     * Gets the value of the sampleIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSampleIdentifier() {
        return sampleIdentifier;
    }

    /**
     * Sets the value of the sampleIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSampleIdentifier(String value) {
        this.sampleIdentifier = value;
    }

}
