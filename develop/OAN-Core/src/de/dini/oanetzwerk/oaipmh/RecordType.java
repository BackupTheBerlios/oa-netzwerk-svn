package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;
import java.util.List;

/** 
 * A record has a header, a metadata part, and
 an optional about container
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="recordType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:headerType" name="header"/>
 *     &lt;xs:element type="ns:metadataType" name="metadata" minOccurs="0"/>
 *     &lt;xs:element type="ns:aboutType" name="about" minOccurs="0" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class RecordType
{
    private HeaderType header;
    private MetadataType metadata;
    private List<AboutType> aboutList = new ArrayList<AboutType>();

    /** 
     * Get the 'header' element value.
     * 
     * @return value
     */
    public HeaderType getHeader() {
        return header;
    }

    /** 
     * Set the 'header' element value.
     * 
     * @param header
     */
    public void setHeader(HeaderType header) {
        this.header = header;
    }

    /** 
     * Get the 'metadata' element value.
     * 
     * @return value
     */
    public MetadataType getMetadata() {
        return metadata;
    }

    /** 
     * Set the 'metadata' element value.
     * 
     * @param metadata
     */
    public void setMetadata(MetadataType metadata) {
        this.metadata = metadata;
    }

    /** 
     * Get the list of 'about' element items.
     * 
     * @return list
     */
    public List<AboutType> getAbouts() {
        return aboutList;
    }

    /** 
     * Set the list of 'about' element items.
     * 
     * @param list
     */
    public void setAbouts(List<AboutType> list) {
        aboutList = list;
    }
}
