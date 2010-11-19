package de.dini.oanetzwerk.oaipmh;


/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:simpleType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="granularityType">
 *   &lt;xs:restriction base="xs:string">
 *     &lt;xs:enumeration value="YYYY-MM-DD"/>
 *     &lt;xs:enumeration value="YYYY-MM-DDThh:mm:ssZ"/>
 *   &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 */
public enum GranularityType {
    YYYYMMDD("YYYY-MM-DD"), YYYYMMDD_THHMMSS_Z("YYYY-MM-DDThh:mm:ssZ");
    private final String value;

    private GranularityType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static GranularityType convert(String value) {
        for (GranularityType inst : values()) {
            if (inst.toString().equals(value)) {
                return inst;
            }
        }
        return null;
    }
}
