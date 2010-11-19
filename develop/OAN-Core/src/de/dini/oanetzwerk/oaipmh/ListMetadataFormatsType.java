package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="ListMetadataFormatsType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:metadataFormatType" name="metadataFormat" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class ListMetadataFormatsType
{
    private List<MetadataFormatType> metadataFormatList = new ArrayList<MetadataFormatType>();

    /** 
     * Get the list of 'metadataFormat' element items.
     * 
     * @return list
     */
    public List<MetadataFormatType> getMetadataFormats() {
        return metadataFormatList;
    }

    /** 
     * Set the list of 'metadataFormat' element items.
     * 
     * @param list
     */
    public void setMetadataFormats(List<MetadataFormatType> list) {
        metadataFormatList = list;
    }
}
