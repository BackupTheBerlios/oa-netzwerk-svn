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







