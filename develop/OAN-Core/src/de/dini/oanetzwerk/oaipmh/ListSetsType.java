package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="ListSetsType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:setType" name="set" maxOccurs="unbounded"/>
 *     &lt;xs:element type="ns:resumptionTokenType" name="resumptionToken" minOccurs="0"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class ListSetsType
{
    private List<SetType> setList = new ArrayList<SetType>();
    private ResumptionTokenType resumptionToken;

    /** 
     * Get the list of 'set' element items.
     * 
     * @return list
     */
    public List<SetType> getSets() {
        return setList;
    }

    /** 
     * Set the list of 'set' element items.
     * 
     * @param list
     */
    public void setSets(List<SetType> list) {
        setList = list;
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
