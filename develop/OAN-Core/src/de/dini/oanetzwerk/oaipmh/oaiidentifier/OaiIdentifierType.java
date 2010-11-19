package de.dini.oanetzwerk.oaipmh.oaiidentifier;

import de.dini.oanetzwerk.oaipmh.IAnyObject;


/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/oai-identifier" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="oai-identifierType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="xs:string" fixed="oai" name="scheme" minOccurs="1" maxOccurs="1"/>
 *     &lt;xs:element type="xs:string" name="repositoryIdentifier" minOccurs="1" maxOccurs="1"/>
 *     &lt;xs:element type="xs:string" fixed=":" name="delimiter" minOccurs="1" maxOccurs="1"/>
 *     &lt;xs:element type="xs:string" name="sampleIdentifier" minOccurs="1" maxOccurs="1"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class OaiIdentifierType implements IAnyObject
{
	protected String schemaLocation = "http://www.openarchives.org/OAI/2.0/oai-identifier http://www.openarchives.org/OAI/2.0/oai-identifier.xsd";
	
    private String scheme;
    private String repositoryIdentifier;
    private String delimiter;
    private String sampleIdentifier;

    /** 
     * Get the 'scheme' element value.
     * 
     * @return value
     */
    public String getScheme() {
        return scheme;
    }

    /** 
     * Set the 'scheme' element value.
     * 
     * @param scheme
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /** 
     * Get the 'repositoryIdentifier' element value.
     * 
     * @return value
     */
    public String getRepositoryIdentifier() {
        return repositoryIdentifier;
    }

    /** 
     * Set the 'repositoryIdentifier' element value.
     * 
     * @param repositoryIdentifier
     */
    public void setRepositoryIdentifier(String repositoryIdentifier) {
        this.repositoryIdentifier = repositoryIdentifier;
    }

    /** 
     * Get the 'delimiter' element value.
     * 
     * @return value
     */
    public String getDelimiter() {
        return delimiter;
    }

    /** 
     * Set the 'delimiter' element value.
     * 
     * @param delimiter
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /** 
     * Get the 'sampleIdentifier' element value.
     * 
     * @return value
     */
    public String getSampleIdentifier() {
        return sampleIdentifier;
    }

    /** 
     * Set the 'sampleIdentifier' element value.
     * 
     * @param sampleIdentifier
     */
    public void setSampleIdentifier(String sampleIdentifier) {
        this.sampleIdentifier = sampleIdentifier;
    }
}
