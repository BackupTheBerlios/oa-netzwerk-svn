package de.dini.oanetzwerk.servicemodule.aggregator;

public class DateValue {
	String dateValue;
	int number;
	
	public DateValue(String dateValue, int number) {
		this.dateValue = dateValue;
		this.number = number;
	}

	public String toString() {
		String result = "dateValue=" +this.dateValue;
		result = result + "\n" + "number=" + this.number;
		return result;
	}
}
