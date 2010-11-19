package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;
import java.util.List;

/** 
 * A header has a unique identifier, a datestamp,
 and setSpec(s) in case the item from which
 the record is disseminated belongs to set(s).
 the header can carry a deleted status indicating
 that the record is deleted.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="headerType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="xs:string" name="identifier"/>
 *     &lt;xs:element type="xs:string" name="datestamp"/>
 *     &lt;xs:element type="xs:string" name="setSpec" minOccurs="0" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 *   &lt;xs:attribute type="ns:statusType" use="optional" name="status"/>
 * &lt;/xs:complexType>
 * </pre>
 */
public class HeaderType
{
    private String identifier;
    private String datestamp;
    private List<String> setSpecList = new ArrayList<String>();
    private StatusType status;

    /** 
     * Get the 'identifier' element value.
     * 
     * @return value
     */
    public String getIdentifier() {
        return identifier;
    }

    /** 
     * Set the 'identifier' element value.
     * 
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /** 
     * Get the 'datestamp' element value.
     * 
     * @return value
     */
    public String getDatestamp() {
        return datestamp;
    }

    /** 
     * Set the 'datestamp' element value.
     * 
     * @param datestamp
     */
    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }

    /** 
     * Get the list of 'setSpec' element items.
     * 
     * @return list
     */
    public List<String> getSetSpecs() {
        return setSpecList;
    }

    /** 
     * Set the list of 'setSpec' element items.
     * 
     * @param list
     */
    public void setSetSpecs(List<String> list) {
        setSpecList = list;
    }

    /** 
     * Get the 'status' attribute value.
     * 
     * @return value
     */
    public StatusType getStatus() {
        return status;
    }

    /** 
     * Set the 'status' attribute value.
     * 
     * @param status
     */
    public void setStatus(StatusType status) {
        this.status = status;
    }
}
