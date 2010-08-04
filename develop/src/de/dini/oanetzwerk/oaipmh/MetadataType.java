package de.dini.oanetzwerk.oaipmh;


/** 
 * Metadata must be expressed in XML that complies
 with another XML Schema (namespace=#other). Metadata must be 
 explicitly qualified in the response.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="metadataType">
 *   &lt;xs:sequence>
 *     &lt;xs:any processContents="strict" namespace="##other"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class MetadataType
{
    private AbstractMetadataType any;

    /** 
     * Get the 'metadataType' complexType value.
     * 
     * @return value
     */
    public AbstractMetadataType getAny() {
        return any;
    }

    /** 
     * Set the 'metadataType' complexType value.
     * 
     * @param any
     */
    public void setAny(AbstractMetadataType any) {
        this.any = any;
    }
}
