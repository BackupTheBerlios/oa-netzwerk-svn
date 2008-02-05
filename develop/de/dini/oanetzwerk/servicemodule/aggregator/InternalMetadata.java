package de.dini.oanetzwerk.servicemodule.aggregator;

import java.util.LinkedList;
import java.util.List;

public class InternalMetadata {


	List<Title> titles;
	int titleCounter = 0;

	List<Author> authors;
	int authorCounter = 0;

	List<Keyword> keywords;
	
	List<Description> descriptions;
	int descriptionCounter = 0;
	
	List<Publisher> publishers;
	int publisherCounter = 0;

	List<DateValue> dateValues;
	int dateValueCounter = 0;
	
	List<Format> formatList;
	int formatCounter = 0;

	List<Identifier> identifierList;
	int identifierCounter = 0;

	List<TypeValue> typeValueList;
	int typeValueCounter = 0;
	
	List<Language> languageList;
	int languageCounter = 0;
	
	List<Classification> classificationList;
	
	
	
	public InternalMetadata() {
		titles = new LinkedList<Title>();
		titleCounter = 0;
		
		authors = new LinkedList<Author>();
		authorCounter = 0;

		keywords = new LinkedList<Keyword>();
		
		descriptions = new LinkedList<Description>();
		descriptionCounter = 0;
		
		publishers = new LinkedList<Publisher>();
		publisherCounter = 0;
		
		dateValues = new LinkedList<DateValue>();
		dateValueCounter = 0;
		
		formatList = new LinkedList<Format>();
		formatCounter = 0;

		identifierList = new LinkedList<Identifier>();
		identifierCounter = 0;
		
		typeValueList = new LinkedList<TypeValue>();
		typeValueCounter = 0;
		
		languageList = new LinkedList<Language>();
		languageCounter = 0;

		classificationList = new LinkedList<Classification>();
		
	}
	
	public int addClassfication(String value) {
		int result = 0;
		Classification cl = null;
		if (Classification.isDDC(value)) {
			cl = new DDCClassification(value);
		} else if (Classification.isDNB(value)) {
			cl = new DNBClassification(value);
		} else if (Classification.isDINISet(value)) {
			cl = new DINISetClassification(value);
		} else if (Classification.isOther(value)) {
			cl = new OtherClassification(value);
		}
		
		if (cl != null) {
			this.classificationList.add(cl);
			System.out.println(cl);
		}
		
		return result;
		
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

	public int addPublisher(String name) {
		int result = 0;
		publisherCounter ++;
		result = this.addPublisher(name, this.publisherCounter);
		return result;
	}
	
	public int addPublisher(String name, int number) {
		int result = 0;
		Publisher temp = new Publisher(name, number);
		this.publishers.add(temp);
		System.out.println(temp);
		return result;
	}

	public int addDateValue(String dateValue) {
		int result = 0;
		dateValueCounter ++;
		result = this.addDateValue(dateValue, this.publisherCounter);
		return result;
	}
	
	public int addDateValue(String dateValue, int number) {
		int result = 0;
		DateValue temp = new DateValue(dateValue, number);
		this.dateValues.add(temp);
		System.out.println(temp);
		return result;
	}

	public int addIdentifier(String identifier) {
		int result = 0;
		identifierCounter ++;
		result = this.addIdentifier(identifier, this.identifierCounter);
		return result;
	}
	
	public int addIdentifier(String identifier, int number) {
		int result = 0;
		Identifier temp = new Identifier(identifier, number);
		this.identifierList.add(temp);
		System.out.println(temp);
		return result;
	}

	public int addTypeValue(String value) {
		int result = 0;
		typeValueCounter ++;
		result = this.addTypeValue(value, this.typeValueCounter);
		return result;
	}
	
	public int addTypeValue(String value, int number) {
		int result = 0;
		TypeValue temp = new TypeValue(value, number);
		this.typeValueList.add(temp);
		System.out.println(temp);
		return result;
	}

	public int addLanguage(String value) {
		int result = 0;
		languageCounter ++;
		result = this.addLanguage(value, this.languageCounter);
		return result;
	}
	
	public int addLanguage(String value, int number) {
		int result = 0;
		Language temp = new Language(value, number);
		this.languageList.add(temp);
		System.out.println(temp);
		return result;
	}
	
	
	public int addFormat(String format) {
		int result = 0;
		this.formatCounter ++;
		result = this.addFormat(format, this.formatCounter);
		return result;
	}
	
	public int addFormat(String format, int number) {
		int result = 0;
		Format temp = new Format(format, number);
		this.formatList.add(temp);
		System.out.println(temp);
		return result;
	}	
	
	
	
	public int addDescription(String description) {
		int result = 0;
		descriptionCounter ++;
		result = this.addDescription(description, null, this.descriptionCounter );
		return result;
	}
	
	public int addDescription(String description, String language, int number) {
		int result = 0;
		Description temp = new Description(description,language, number);
		this.descriptions.add(temp);
		
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
	
	private void init(String description, String language, int number) {
		this.description = description;
		this.language = language;
		this.number = number;
	}
	
	public Description(String description, String language, int number) {
		this.init(description, language, number);
	}
	
	public Description(String description, int number) {
		this.init(description, null, number);
	}
	
	public String toString() {
		String result = "description=" +this.description;
		result = result + "\n" + "language=" + this.language;
		result = result + "\n" + "number=" + this.number;
		return result;
	}
	
}

class Publisher {
	String name;
	int number = 0;
	
	private void init(String name, int number) {
		this.name = name;
		this.number = number;
	}
	
	public Publisher(String name, int number) {
		this.init(name, number);
	}
	
	public String toString() {
		String result = "name=" +this.name;
		result = result + "\n" + "number=" + this.number;
		return result;
	}
	
}

class DateValue {
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


class TypeValue {
	String typeValue;
	int number;

	public TypeValue(String typeValue, int number) {
		this.typeValue = typeValue;
		this.number = number;
	}

	public String toString() {
		String result = "typeValue=" +this.typeValue;
		result = result + "\n" + "number=" + this.number;
		return result;
	}
}


class Language {
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



class Format {
	
	int number = 0;
	String schema_f;
	
	public Format(String schema_f, int number) {
		this.schema_f = schema_f;
		this.number = number;
	}

	public String toString() {
		String result = "schema_f=" +this.schema_f;
		result = result + "\n" + "number=" + this.number;
		return result;
	}
	
}

class Identifier {
	
	int number = 0;
	String identifier;
	
	public Identifier(String identifier, int number) {
		this.identifier = identifier;
		this.number = number;
	}
	
	public String toString() {
		String result = "identifier=" +this.identifier;
		result = result + "\n" + "number=" + this.number;
		return result;
	}

}


abstract class  Classification {
	String value;
	
	public static boolean isDDC(String testvalue) {
		if (testvalue.toLowerCase().startsWith("ddc:")) return true;
		else 
			return false;
	}

	public static boolean isDNB(String testvalue) {
		if (testvalue.toLowerCase().startsWith("dnb:")) return true;
		else 
			return false;
	}
	
	public static boolean isDINISet(String testvalue) {
		if (testvalue.toLowerCase().startsWith("pub-type:")) return true;
		else 
			return false;
	}
	
	public static boolean isOther(String testvalue) {
		if (!(isDNB(testvalue)) && !(isDDC(testvalue)) && !(isDINISet(testvalue))) {
			return true;
		} else
			return false;
	}
	
	public void setSplitValue() {
		String[] temp = this.value.split(":");
		if (temp.length < 2) {
			// Fehler, keine korrekte DNB-Codierung bzw. kein Wert
			// ÜBergangsweise wird der gesamte Eintrag eingestellt
//			this.value = value;
		} else {
			this.value = value.split(":")[1];
		}
	}

	
	public Classification(String value) {
		this.value = value;
	}
	public Classification() {}
}

class DDCClassification extends Classification {
	public DDCClassification(String value) {
		super(value);
		setSplitValue();
	}
	
	public String toString() {
		String result = "DDC Classification, value=" + this.value;
		return result;
	}
}

class DNBClassification extends Classification {
	public DNBClassification(String value) {
		super(value);
		setSplitValue();
	}

	public String toString() {
		String result = "DNB Classification, value=" + this.value;
		return result;
	}

}

class DINISetClassification extends Classification {
	public DINISetClassification(String value) {
		super(value);
		setSplitValue();
	}
	public String toString() {
		String result = "DINISet Classification, value=" + this.value;
		return result;
	}
}

class OtherClassification extends Classification {
	public OtherClassification(String value) {
		super(value);
	}
	public String toString() {
		String result = "Other Classification, value=" + this.value;
		return result;
	}
}

