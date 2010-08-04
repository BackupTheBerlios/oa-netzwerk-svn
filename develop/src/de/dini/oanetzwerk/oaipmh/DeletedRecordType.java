
package de.dini.oanetzwerk.oaipmh;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:simpleType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="deletedRecordType">
 *   &lt;xs:restriction base="xs:string">
 *     &lt;xs:enumeration value="no"/>
 *     &lt;xs:enumeration value="persistent"/>
 *     &lt;xs:enumeration value="transient"/>
 *   &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 */
public enum DeletedRecordType {
    NO("no"), PERSISTENT("persistent"), TRANSIENT("transient");
    private final String value;

    private DeletedRecordType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static DeletedRecordType convert(String value) {
        for (DeletedRecordType inst : values()) {
            if (inst.toString().equals(value)) {
                return inst;
            }
        }
        return null;
    }
}
