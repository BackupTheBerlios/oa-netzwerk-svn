package de.dini.oanetzwerk.servicemodule.aggregator;

public class DINISetClassification extends Classification {
	public DINISetClassification(String value) {
		super(value);
		setSplitValue();
	}
	public String toString() {
		String result = "DINISet Classification, value=" + this.value;
		return result;
	}
}
