package de.dini.oanetzwerk.oaipmh;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="metadataFormatType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="xs:string" name="metadataPrefix"/>
 *     &lt;xs:element type="xs:string" name="schema"/>
 *     &lt;xs:element type="xs:string" name="metadataNamespace"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class MetadataFormatType
{
    private String metadataPrefix;
    private String schema;
    private String metadataNamespace;

    /** 
     * Get the 'metadataPrefix' element value.
     * 
     * @return value
     */
    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    /** 
     * Set the 'metadataPrefix' element value.
     * 
     * @param metadataPrefix
     */
    public void setMetadataPrefix(String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
    }

    /** 
     * Get the 'schema' element value.
     * 
     * @return value
     */
    public String getSchema() {
        return schema;
    }

    /** 
     * Set the 'schema' element value.
     * 
     * @param schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /** 
     * Get the 'metadataNamespace' element value.
     * 
     * @return value
     */
    public String getMetadataNamespace() {
        return metadataNamespace;
    }

    /** 
     * Set the 'metadataNamespace' element value.
     * 
     * @param metadataNamespace
     */
    public void setMetadataNamespace(String metadataNamespace) {
        this.metadataNamespace = metadataNamespace;
    }
}
