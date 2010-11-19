package de.dini.oanetzwerk.oaipmh;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:simpleType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="protocolVersionType">
 *   &lt;xs:restriction base="xs:string">
 *     &lt;xs:enumeration value="2.0"/>
 *   &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 */
public enum ProtocolVersionType {
    _20("2.0");
    private final String value;

    private ProtocolVersionType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static ProtocolVersionType convert(String value) {
        for (ProtocolVersionType inst : values()) {
            if (inst.toString().equals(value)) {
                return inst;
            }
        }
        return null;
    }
}
