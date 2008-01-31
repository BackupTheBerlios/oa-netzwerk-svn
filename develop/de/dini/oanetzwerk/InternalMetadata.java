package de.dini.oanetzwerk;

import java.util.LinkedList;
import java.util.List;

public class InternalMetadata {


	List<Title> titles;
	int titleCounter = 0;

	List<Author> authors;
	int authorCounter = 0;

	List<Keyword> keywords;
	
	public InternalMetadata() {
		titles = new LinkedList<Title>();
		titleCounter = 0;
		
		authors = new LinkedList<Author>();
		authorCounter = 0;

		keywords = new LinkedList<Keyword>();
	}
	
	public int addKeyword(String keyword) {
		int result = 0;
		result = this.addKeyword(keyword, null);
		return result;
	}
	
	public int addKeyword(String keyword, String language) {
		int result = 0;
		Keyword temp = new Keyword(keyword, language);
		this.keywords.add(temp);
		System.out.println(temp);
		return result;
	}
	
	public int addTitle(String title, String qualifier, String lang) {
		// es wurde der Titel-Zähler nicht angegeben, deshalb wird der interne Zähler erhöht und übergeben
		int result = 0;
		this.titleCounter ++;
		result = this.addTitle(title, qualifier, lang, this.titleCounter);
		return result;
	}
	
	public int addTitle(String title, String qualifier, String lang, int number) {
		int result = 0;

		Title temp = new Title();
		temp.setLang(lang);
		temp.setTitle(title);
		temp.setQualifier(qualifier);
		temp.setNumber(number);
		this.titles.add(temp);
		
		System.out.println(temp.toString());
		
		return result;
	}
	
	public int addAuthor(String original) {
		int result = 0;
		authorCounter ++;
		result = addAuthor(original, this.authorCounter);
		return result;
	}
	
	public int addAuthor(String original, int number) {
		int result = 0;
		Author temp = new Author();
		temp.init(original, number);
		this.authors.add(temp);
		System.out.println(temp.toString());
		return result;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

class Title {
	String title;
	int number;
	String qualifier;
	String lang;
	
	public Title() {
		title = new String();
		lang = new String();
		qualifier = new String();
		number = 0;
	}

	public String toString() {
		String result = "title=" +this.title;
		result = result + "\n" + "qualifier=" + this.qualifier;
		result = result + "\n" + "number=" + this.number;
		result = result + "\n" + "language=" + this.lang;
		return result;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title == null) this.title = "";
		else
			this.title = title;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		if (qualifier == null) this.qualifier = "";
		else
			this.qualifier = qualifier;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		if (lang == null) this.lang = "";
		else
			this.lang = lang;
	}
}


class Keyword {
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

class Author {
	String firstname;
	String lastname;
	int number;
	
	public int init(String original, int number) {
		int result = 0;
		
		String[] temp =	null;
		// Splitten zwischen Nach- und Vornamen (separiert durch ein Komma
		temp = original.split(",");
		if (temp.length >= 2) {
			this.lastname = temp[0];
			this.firstname = temp[1];
		} else {
		// wenn es nicht genug Elemente gibt, konnte nicht gesplittet werden, neuer Versuch am Leerzeichen
			temp = original.split(" ");
		}
		if (temp.length < 2) {
			temp[0] = original;
		}

		this.number = number;
		
		
		return result;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	public String toString() {
		String result = "lastname=" +this.lastname;
		result = result + "\n" + "firstname=" + this.firstname;
		result = result + "\n" + "number=" + this.number;
		return result;
	}
	
}	

class Description {
	String description;
	String language;
	int number = 0;
	
	public Description(String description, String language, int number) {
		this.description = description;
		this.language = language;
		this.number = number;
	}
	
	public Description(String description, int number) {
		Description(description, null, number);
	}
	
	public String toString() {
		String result = "description=" +this.description;
		result = result + "\n" + "language=" + this.language;
		result = result + "\n" + "number=" + this.number;
		return result;
	}
	
}
