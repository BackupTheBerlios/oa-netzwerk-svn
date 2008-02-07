package de.dini.oanetzwerk.servicemodule.aggregator;

public class Language {
	String language;
	int number;

	public Language(String language, int number) {
		this.language = language;
		this.number = number;
	}

	public String toString() {
		String result = "language=" +this.language;
		result = result + "\n" + "number=" + this.number;
		return result;
	}
}
