package de.dini.oanetzwerk.utils.imf;

public class Format {
	
	int number = 0;
	String schema_f;
	
	public Format() {
		
	}
	
	public Format(String schema_f, int number) {
		this.schema_f = schema_f;
		this.number = number;
	}

	public String toString() {
		String result = "schema_f=" +this.schema_f;
		result = result + "\n" + "number=" + this.number;
		return result;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getSchema_f() {
		return schema_f;
	}

	public void setSchema_f(String schema_f) {
		this.schema_f = schema_f;
	}
		
}