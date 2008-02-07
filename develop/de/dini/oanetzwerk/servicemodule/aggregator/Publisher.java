package de.dini.oanetzwerk.servicemodule.aggregator;

public class Publisher {
	String name;
	int number = 0;
	
	public Publisher() {
		
	}
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
		
}