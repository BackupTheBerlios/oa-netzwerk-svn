package de.dini.oanetzwerk.utils.imf;

import java.util.Date;

public class DateValue {
	String stringValue;
	Date dateValue;
	
	int number;
	
	public DateValue() {
		
	}
	
	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public DateValue(Date dateValue, String stringValue, int number) {
		this.dateValue = dateValue;
		this.number = number;
	}

	public String toString() {
		String result = "dateValue=" +this.dateValue;
		result = result + "\n  stringValue=" +this.stringValue;
		result = result + "\n  number=" + this.number;
		return result;
	}
}
