package de.dini.oanetzwerk.servicemodule.aggregator;

public class Identifier {
	
	int number = 0;
	String identifier;
	
	public Identifier(String identifier, int number) {
		this.identifier = identifier;
		this.number = number;
	}
	
	public String toString() {
		String result = "identifier=" +this.identifier;
		result = result + "\n" + "number=" + this.number;
		return result;
	}

}
