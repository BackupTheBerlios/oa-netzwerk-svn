package de.dini.oanetzwerk.oaipmh;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:simpleType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="verbType">
 *   &lt;xs:restriction base="xs:string">
 *     &lt;xs:enumeration value="Identify"/>
 *     &lt;xs:enumeration value="ListMetadataFormats"/>
 *     &lt;xs:enumeration value="ListSets"/>
 *     &lt;xs:enumeration value="GetRecord"/>
 *     &lt;xs:enumeration value="ListIdentifiers"/>
 *     &lt;xs:enumeration value="ListRecords"/>
 *   &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 */
public enum VerbType {
    IDENTIFY("Identify"), 
    LIST_METADATA_FORMATS("ListMetadataFormats"), 
    LIST_SETS("ListSets"), 
    GET_RECORD("GetRecord"),
    LIST_IDENTIFIERS("ListIdentifiers"), 
    LIST_RECORDS("ListRecords");
    
    private final String value;

    private VerbType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static VerbType convert(String value) {
        for (VerbType inst : values()) {
            if (inst.toString().equals(value)) {
                return inst;
            }
        }
        return null;
    }
}
