package de.dini.oanetzwerk.servicemodule.aggregator;

public class DNBClassification extends Classification {
	public DNBClassification(String value) {
		super(value);
		setSplitValue();
	}

	public String toString() {
		String result = "DNB Classification, value=" + this.value;
		return result;
	}

}
