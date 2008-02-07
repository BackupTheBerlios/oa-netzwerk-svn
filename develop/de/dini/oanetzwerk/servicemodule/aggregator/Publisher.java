package de.dini.oanetzwerk.servicemodule.aggregator;

public class Publisher {
	String name;
	int number = 0;
	
	private void init(String name, int number) {
		this.name = name;
		this.number = number;
	}
	
	public Publisher(String name, int number) {
		this.init(name, number);
	}
	
	public String toString() {
		String result = "name=" +this.name;
		result = result + "\n" + "number=" + this.number;
		return result;
	}
	
}