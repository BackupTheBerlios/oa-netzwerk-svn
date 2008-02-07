package de.dini.oanetzwerk.servicemodule.aggregator;

public class Description {
	String description;
	String language;
	int number = 0;
	
	private void init(String description, String language, int number) {
		this.description = description;
		this.language = language;
		this.number = number;
	}
	
	public Description(String description, String language, int number) {
		this.init(description, language, number);
	}
	
	public Description(String description, int number) {
		this.init(description, null, number);
	}
	
	public String toString() {
		String result = "description=" +this.description;
		result = result + "\n" + "language=" + this.language;
		result = result + "\n" + "number=" + this.number;
		return result;
	}
	
}
