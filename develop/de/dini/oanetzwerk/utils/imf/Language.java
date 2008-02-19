package de.dini.oanetzwerk.utils.imf;

public class Language {
	String language;
	int number;

	public Language() {
		
	}
	
	public Language(String language, int number) {
		this.language = language;
		this.number = number;
	}

	public String toString() {
		String result = "language=" +this.language;
		result = result + "\n" + "number=" + this.number;
		return result;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
}
