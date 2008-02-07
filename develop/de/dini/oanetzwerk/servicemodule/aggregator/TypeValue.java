package de.dini.oanetzwerk.servicemodule.aggregator;

public class TypeValue {
	String typeValue;
	int number;

	public TypeValue(String typeValue, int number) {
		this.typeValue = typeValue;
		this.number = number;
	}

	public String toString() {
		String result = "typeValue=" +this.typeValue;
		result = result + "\n" + "number=" + this.number;
		return result;
	}
}