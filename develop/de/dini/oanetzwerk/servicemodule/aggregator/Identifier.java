package de.dini.oanetzwerk.servicemodule.aggregator;

public class Identifier {
	
	int number = 0;
	String identifier;
	
	public Identifier() {
		
	}
	
	public Identifier(String identifier, int number) {
		this.identifier = identifier;
		this.number = number;
	}
	
	public String toString() {
		String result = "identifier=" +this.identifier;
		result = result + "\n" + "number=" + this.number;
		return result;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
