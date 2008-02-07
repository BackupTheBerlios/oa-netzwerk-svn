package de.dini.oanetzwerk.servicemodule.aggregator;

public class OtherClassification extends Classification {
	public OtherClassification(String value) {
		super(value);
	}
	public String toString() {
		String result = "Other Classification, value=" + this.value;
		return result;
	}
}
