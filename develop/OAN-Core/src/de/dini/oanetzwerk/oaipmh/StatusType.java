package de.dini.oanetzwerk.oaipmh;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:simpleType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="statusType">
 *   &lt;xs:restriction base="xs:string">
 *     &lt;xs:enumeration value="deleted"/>
 *   &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 */
public enum StatusType {
    DELETED("deleted");
    private final String value;

    private StatusType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static StatusType convert(String value) {
        for (StatusType inst : values()) {
            if (inst.toString().equals(value)) {
                return inst;
            }
        }
        return null;
    }
}
