package de.dini.oanetzwerk.utils.imf;

public class TypeValue {
	String typeValue;
	int number;

	public TypeValue() {
		
	}
	
	public TypeValue(String typeValue, int number) {
		this.typeValue = typeValue;
		this.number = number;
	}

	public String toString() {
		String result = "typeValue=" +this.typeValue;
		result = result + "\n" + "number=" + this.number;
		return result;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}
	
}