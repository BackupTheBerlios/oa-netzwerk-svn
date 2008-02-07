package de.dini.oanetzwerk.servicemodule.aggregator;

public class Keyword {
	String keyword;
	String language;
	
	public Keyword(String keyword, String lang) {
		this.keyword = keyword;
		this.language = lang;
	}
	
	public String toString() {
		String result = "keyword=" +this.keyword;
		result = result + "\n" + "lang=" + this.language;
		return result;
	}
}
