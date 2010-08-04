package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="setType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="xs:string" name="setSpec"/>
 *     &lt;xs:element type="xs:string" name="setName"/>
 *     &lt;xs:element type="ns:descriptionType" name="setDescription" minOccurs="0" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class SetType
{
    private String setSpec;
    private String setName;
    private List<DescriptionType> setDescriptionList = new ArrayList<DescriptionType>();

    /** 
     * Get the 'setSpec' element value.
     * 
     * @return value
     */
    public String getSetSpec() {
        return setSpec;
    }

    /** 
     * Set the 'setSpec' element value.
     * 
     * @param setSpec
     */
    public void setSetSpec(String setSpec) {
        this.setSpec = setSpec;
    }

    /** 
     * Get the 'setName' element value.
     * 
     * @return value
     */
    public String getSetName() {
        return setName;
    }

    /** 
     * Set the 'setName' element value.
     * 
     * @param setName
     */
    public void setSetName(String setName) {
        this.setName = setName;
    }

    /** 
     * Get the list of 'setDescription' element items.
     * 
     * @return list
     */
    public List<DescriptionType> getSetDescriptions() {
        return setDescriptionList;
    }

    /** 
     * Set the list of 'setDescription' element items.
     * 
     * @param list
     */
    public void setSetDescriptions(List<DescriptionType> list) {
        setDescriptionList = list;
    }
}
