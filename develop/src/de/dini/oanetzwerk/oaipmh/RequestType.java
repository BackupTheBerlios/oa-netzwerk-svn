package de.dini.oanetzwerk.oaipmh;

/** 
 * Define requestType, indicating the protocol request that 
 led to the response. Element content is BASE-URL, attributes are arguments 
 of protocol request, attribute-values are values of arguments of protocol 
 request
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="requestType">
 *   &lt;xs:simpleContent>
 *     &lt;xs:extension base="xs:string">
 *       &lt;xs:attribute type="ns:verbType" use="optional" name="verb"/>
 *       &lt;xs:attribute type="xs:string" use="optional" name="identifier"/>
 *       &lt;xs:attribute type="xs:string" use="optional" name="metadataPrefix"/>
 *       &lt;xs:attribute type="xs:string" use="optional" name="from"/>
 *       &lt;xs:attribute type="xs:string" use="optional" name="until"/>
 *       &lt;xs:attribute type="xs:string" use="optional" name="set"/>
 *       &lt;xs:attribute type="xs:string" use="optional" name="resumptionToken"/>
 *     &lt;/xs:extension>
 *   &lt;/xs:simpleContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class RequestType
{
	
	private static final String REQUEST_VALUE = "http://oanet.cms.hu-berlin.de/oaipmh/oaipmh";
	
    private String value = REQUEST_VALUE;
    private VerbType verb;
    private String identifier;
    private String metadataPrefix;
    private String from;
    private String until;
    private String set;
    private String resumptionToken;

    /** 
     * Get the extension value.
     * 
     * @return value
     */
    public String getValue() {
        return value;
    }

    /** 
     * Set the extension value.
     * 
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /** 
     * Get the 'verb' attribute value.
     * 
     * @return value
     */
    public VerbType getVerb() {
        return verb;
    }

    /** 
     * Set the 'verb' attribute value.
     * 
     * @param verb
     */
    public void setVerb(VerbType verb) {
        this.verb = verb;
    }

    /** 
     * Get the 'identifier' attribute value.
     * 
     * @return value
     */
    public String getIdentifier() {
        return identifier;
    }

    /** 
     * Set the 'identifier' attribute value.
     * 
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /** 
     * Get the 'metadataPrefix' attribute value.
     * 
     * @return value
     */
    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    /** 
     * Set the 'metadataPrefix' attribute value.
     * 
     * @param metadataPrefix
     */
    public void setMetadataPrefix(String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
    }

    /** 
     * Get the 'from' attribute value.
     * 
     * @return value
     */
    public String getFrom() {
        return from;
    }

    /** 
     * Set the 'from' attribute value.
     * 
     * @param from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /** 
     * Get the 'until' attribute value.
     * 
     * @return value
     */
    public String getUntil() {
        return until;
    }

    /** 
     * Set the 'until' attribute value.
     * 
     * @param until
     */
    public void setUntil(String until) {
        this.until = until;
    }

    /** 
     * Get the 'set' attribute value.
     * 
     * @return value
     */
    public String getSet() {
        return set;
    }

    /** 
     * Set the 'set' attribute value.
     * 
     * @param set
     */
    public void setSet(String set) {
        this.set = set;
    }

    /** 
     * Get the 'resumptionToken' attribute value.
     * 
     * @return value
     */
    public String getResumptionToken() {
        return resumptionToken;
    }

    /** 
     * Set the 'resumptionToken' attribute value.
     * 
     * @param resumptionToken
     */
    public void setResumptionToken(String resumptionToken) {
        this.resumptionToken = resumptionToken;
    }
}
