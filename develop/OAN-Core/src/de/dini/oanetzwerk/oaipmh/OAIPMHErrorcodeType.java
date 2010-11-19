package de.dini.oanetzwerk.oaipmh;



public enum OAIPMHErrorcodeType {


    CANNOT_DISSEMINATE_FORMAT("cannotDisseminateFormat"),
    ID_DOES_NOT_EXIST("idDoesNotExist"),
    BAD_ARGUMENT("badArgument"),
    BAD_VERB("badVerb"),
    NO_METADATA_FORMATS("noMetadataFormats"),
    NO_RECORDS_MATCH("noRecordsMatch"),
    BAD_RESUMPTION_TOKEN("badResumptionToken"),
    NO_SET_HIERARCHY("noSetHierarchy");
    
    private final String value;

	OAIPMHErrorcodeType(String v) {
		value = v;
	}

	public String toString() {
		return value;
	}

	public static OAIPMHErrorcodeType fromValue(String v) {
		for (OAIPMHErrorcodeType c : OAIPMHErrorcodeType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
