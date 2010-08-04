package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="ListRecordsType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:recordType" name="record" maxOccurs="unbounded"/>
 *     &lt;xs:element type="ns:resumptionTokenType" name="resumptionToken" minOccurs="0"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class ListRecordsType
{
    private List<RecordType> recordList = new ArrayList<RecordType>();
    private ResumptionTokenType resumptionToken;

    /** 
     * Get the list of 'record' element items.
     * 
     * @return list
     */
    public List<RecordType> getRecords() {
        return recordList;
    }

    /** 
     * Set the list of 'record' element items.
     * 
     * @param list
     */
    public void setRecords(List<RecordType> list) {
        recordList = list;
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
