package de.dini.oanetzwerk.servicemodule.aggregator;

public class DDCClassification extends Classification {
	public DDCClassification(String value) {
		super(value);
		setSplitValue();
	}
	
	public String toString() {
		String result = "DDC Classification, value=" + this.value;
		return result;
	}
}
