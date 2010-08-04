package de.dini.oanetzwerk.oaipmh;

import java.math.BigInteger;
import java.util.Date;

/** 
 * A resumptionToken may have 3 optional attributes
 and can be used in ListSets, ListIdentifiers, ListRecords
 responses.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="resumptionTokenType">
 *   &lt;xs:simpleContent>
 *     &lt;xs:extension base="xs:string">
 *       &lt;xs:attribute type="xs:dateTime" use="optional" name="expirationDate"/>
 *       &lt;xs:attribute type="xs:string" use="optional" name="completeListSize"/>
 *       &lt;xs:attribute type="xs:integer" use="optional" name="cursor"/>
 *     &lt;/xs:extension>
 *   &lt;/xs:simpleContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class ResumptionTokenType
{
    private String value; 
    private Date expirationDate;
    private BigInteger completeListSize;
    private BigInteger cursor;

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
     * Get the 'expirationDate' attribute value.
     * 
     * @return value
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /** 
     * Set the 'expirationDate' attribute value.
     * 
     * @param expirationDate
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /** 
     * Get the 'completeListSize' attribute value.
     * 
     * @return value
     */
    public BigInteger getCompleteListSize() {
        return completeListSize;
    }

    /** 
     * Set the 'completeListSize' attribute value.
     * 
     * @param completeListSize
     */
    public void setCompleteListSize(BigInteger completeListSize) {
        this.completeListSize = completeListSize;
    }

    /** 
     * Get the 'cursor' attribute value.
     * 
     * @return value
     */
    public BigInteger getCursor() {
        return cursor;
    }

    /** 
     * Set the 'cursor' attribute value.
     * 
     * @param cursor
     */
    public void setCursor(BigInteger cursor) {
        this.cursor = cursor;
    }
}
