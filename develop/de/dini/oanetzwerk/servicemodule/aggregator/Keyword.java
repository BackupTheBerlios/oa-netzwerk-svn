package de.dini.oanetzwerk.servicemodule.aggregator;

import javax.xml.bind.annotation.XmlAttribute;

public class Keyword {
	
	String keyword;
	String language;
	
	public Keyword() {
		
	}
	
	public Keyword(String keyword, String lang) {
		this.keyword = keyword;
		this.language = lang;
	}
	
	public String toString() {
		String result = "keyword=" +this.keyword;
		result = result + "\n" + "lang=" + this.language;
		return result;
	}

	@XmlAttribute (name = "name")
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	
}
