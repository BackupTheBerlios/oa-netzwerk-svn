package de.dini.oanetzwerk.servicemodule.aggregator;

public class Format {
	
	int number = 0;
	String schema_f;
	
	public Format(String schema_f, int number) {
		this.schema_f = schema_f;
		this.number = number;
	}

	public String toString() {
		String result = "schema_f=" +this.schema_f;
		result = result + "\n" + "number=" + this.number;
		return result;
	}
	
}