package de.dini.oanetzwerk.oaipmh;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="OAI-PMHerrorType">
 *   &lt;xs:simpleContent>
 *     &lt;xs:extension base="xs:string">
 *       &lt;xs:attribute type="ns:OAI-PMHerrorcodeType" use="required" name="code"/>
 *     &lt;/xs:extension>
 *   &lt;/xs:simpleContent>
 * &lt;/xs:complexType>
 * </pre>
 */

public class OAIPMHErrorType
{
    private String value;
    private OAIPMHErrorcodeType code;

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
     * Get the 'code' attribute value.
     * 
     * @return value
     */
    public OAIPMHErrorcodeType getCode() {
        return code;
    }

    /** 
     * Set the 'code' attribute value.
     * 
     * @param code
     */
    public void setCode(OAIPMHErrorcodeType code) {
        this.code = code;
    }
}
