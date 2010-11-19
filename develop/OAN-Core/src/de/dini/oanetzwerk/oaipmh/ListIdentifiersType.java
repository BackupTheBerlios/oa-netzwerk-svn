package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="ListIdentifiersType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:headerType" name="header" maxOccurs="unbounded"/>
 *     &lt;xs:element type="ns:resumptionTokenType" name="resumptionToken" minOccurs="0"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class ListIdentifiersType
{
    private List<HeaderType> headerList = new ArrayList<HeaderType>();
    private ResumptionTokenType resumptionToken;

    /** 
     * Get the list of 'header' element items.
     * 
     * @return list
     */
    public List<HeaderType> getHeaders() {
        return headerList;
    }

    /** 
     * Set the list of 'header' element items.
     * 
     * @param list
     */
    public void setHeaders(List<HeaderType> list) {
        headerList = list;
    }

    /** 
     * Get the 'resumptionToken' element value.
     * 
     * @return value
     */
    public ResumptionTokenType getResumptionToken() {
        return resumptionToken;
    }

    /** 
     * Set the 'resumptionToken' element value.
     * 
     * @param resumptionToken
     */
    public void setResumptionToken(ResumptionTokenType resumptionToken) {
        this.resumptionToken = resumptionToken;
    }
}
