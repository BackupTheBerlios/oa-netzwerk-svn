
package de.dini.oanetzwerk.oaipmh;


/** 
 * The descriptionType is used for the description
 element in Identify and for setDescription element in ListSets.
 Content must be compliant with an XML Schema defined by a 
 community.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="descriptionType">
 *   &lt;xs:sequence>
 *     &lt;xs:any processContents="strict" namespace="##other"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class DescriptionType
{
    private Object object;

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

}
