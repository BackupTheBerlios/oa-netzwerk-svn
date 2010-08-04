
package de.dini.oanetzwerk.oaipmh;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="GetRecordType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:recordType" name="record"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class GetRecordType
{
    private RecordType record;

    /** 
     * Get the 'record' element value.
     * 
     * @return value
     */
    public RecordType getRecord() {
        return record;
    }

    /** 
     * Set the 'record' element value.
     * 
     * @param record
     */
    public void setRecord(RecordType record) {
        this.record = record;
    }
}
