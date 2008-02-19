package de.dini.oanetzwerk.utils.imf;

public class DateValue {
	String dateValue;
	int number;
	
	public DateValue() {
		
	}
	
	public String getDateValue() {
		return dateValue;
	}

	public void setDateValue(String dateValue) {
		this.dateValue = dateValue;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

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
