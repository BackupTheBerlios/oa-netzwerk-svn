package de.dini.oanetzwerk.utils.imf;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( namespace = "http://oanetzwerk.dini.de/" ) 
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
	
	@Deprecated
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
	
	public int addClassfication(Classification cl) {
		int result = 0;
		if (cl != null) {
			this.classificationList.add(cl);
			System.out.println(cl);
		}
		return result;
	}
	
	public int addKeyword(Keyword value) {
		int result = 0;
		if (value != null) {
			this.keywords.add(value);
		}
		return result;
	}
	
	@Deprecated
	public int addKeyword(String keyword) {
		int result = 0;
		result = this.addKeyword(keyword, null);
		return result;
	}

	@Deprecated
	public int addKeyword(String keyword, String language) {
		int result = 0;
		Keyword temp = new Keyword(keyword, language);
		this.keywords.add(temp);
		System.out.println(temp);
		return result;
	}

	public int addPublisher(Publisher value) {
		int result = 0;
		if (value != null) {
			this.publishers.add(value);
		}
		return result;
	}
	
	@Deprecated
	public int addPublisher(String name) {
		int result = 0;
		publisherCounter ++;
		result = this.addPublisher(name, this.publisherCounter);
		return result;
	}

	@Deprecated
	public int addPublisher(String name, int number) {
		int result = 0;
		Publisher temp = new Publisher(name, number);
		this.publishers.add(temp);
		System.out.println(temp);
		return result;
	}


	public int addDateValue(DateValue value) {
		int result = 0;
		if (value != null) {
			this.dateValues.add(value);
		}
		return result;
	}

	
	
	
	@Deprecated
	public int addDateValue(String dateValue) {
		int result = 0;
		dateValueCounter ++;
		result = this.addDateValue(dateValue, this.dateValueCounter);
		return result;
	}

	@Deprecated
	public int addDateValue(String dateValue, int number) {
		int result = 0;
		DateValue temp = new DateValue(dateValue, number);
		this.dateValues.add(temp);
		System.out.println(temp);
		return result;
	}
	
	
	public int addIdentifier(Identifier value) {
		int result = 0;
		if (value != null) {
			this.identifierList.add(value);
		}
		return result;
	}

	
	@Deprecated
	public int addIdentifier(String identifier) {
		int result = 0;
		identifierCounter ++;
		result = this.addIdentifier(identifier, this.identifierCounter);
		return result;
	}

	@Deprecated
	public int addIdentifier(String identifier, int number) {
		int result = 0;
		Identifier temp = new Identifier(identifier, number);
		this.identifierList.add(temp);
		System.out.println(temp);
		return result;
	}

	public int addTypeValue(TypeValue value) {
		int result = 0;
		if (value != null) {
			this.typeValueList.add(value);
		}
		return result;
	}

	
	@Deprecated
	public int addTypeValue(String value) {
		int result = 0;
		typeValueCounter ++;
		result = this.addTypeValue(value, this.typeValueCounter);
		return result;
	}

	@Deprecated
	public int addTypeValue(String value, int number) {
		int result = 0;
		TypeValue temp = new TypeValue(value, number);
		this.typeValueList.add(temp);
		System.out.println(temp);
		return result;
	}

	public int addLanguage(Language value) {
		int result = 0;
		if (value != null) {
			this.languageList.add(value);
		}
		return result;
	}

	
	@Deprecated
	public int addLanguage(String value) {
		int result = 0;
		languageCounter ++;
		result = this.addLanguage(value, this.languageCounter);
		return result;
	}
	
	@Deprecated
	public int addLanguage(String value, int number) {
		int result = 0;
		Language temp = new Language(value, number);
		this.languageList.add(temp);
		System.out.println(temp);
		return result;
	}
	
	
	public int addFormat(Format value) {
		int result = 0;
		if (value != null) {
			this.formatList.add(value);
		}
		return result;
	}

	
	@Deprecated
	public int addFormat(String format) {
		int result = 0;
		this.formatCounter ++;
		result = this.addFormat(format, this.formatCounter);
		return result;
	}
	@Deprecated
	public int addFormat(String format, int number) {
		int result = 0;
		Format temp = new Format(format, number);
		this.formatList.add(temp);
		System.out.println(temp);
		return result;
	}	
	
	
	public int addDescription(Description value) {
		int result = 0;
		if (value != null) {
			this.descriptions.add(value);
		}
		return result;
	}
	
	@Deprecated
	public int addDescription(String description) {
		int result = 0;
		descriptionCounter ++;
		result = this.addDescription(description, null, this.descriptionCounter );
		return result;
	}
	
	@Deprecated
	public int addDescription(String description, String language, int number) {
		int result = 0;
		Description temp = new Description(description,language, number);
		this.descriptions.add(temp);
		
		System.out.println(temp);
		
		return result;

	}
	
	public int addTitle(Title title) {
		int result = 0;
		if (title != null) {
			this.titles.add(title);
		}
		return result;
	}
	
	@Deprecated
	public int addTitle(String title, String qualifier, String lang) {
		// es wurde der Titel-Zähler nicht angegeben, deshalb wird der interne Zähler erhöht und übergeben
		int result = 0;
		this.titleCounter ++;
		result = this.addTitle(title, qualifier, lang, this.titleCounter);
		return result;
	}
	
	@Deprecated
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
	
	public int addAuthor(Author author) {
		int result = 0;
		if (author != null) {
			this.authors.add(author);
		}
		return result;
	}
	
	@Deprecated
	public int addAuthor(String original) {
		int result = 0;
		authorCounter ++;
		result = addAuthor(original, this.authorCounter);
		return result;
	}
	
	@Deprecated
	public int addAuthor(String original, int number) {
		int result = 0;
		Author temp = new Author();
		temp.init(original, number);
		this.authors.add(temp);
		System.out.println(temp.toString());
		return result;
	}
	
	public int getAuthorCounter() {
		return authorCounter;
	}

	public void setAuthorCounter(int authorCounter) {
		this.authorCounter = authorCounter;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	//@XmlElementWrapper
    //@XmlAnyElement
	public List<Classification> getClassificationList() {
		return classificationList;
	}

	public void setClassificationList(List<Classification> classificationList) {
		this.classificationList = classificationList;
	}

	public int getDateValueCounter() {
		return dateValueCounter;
	}

	public void setDateValueCounter(int dateValueCounter) {
		this.dateValueCounter = dateValueCounter;
	}

	public List<DateValue> getDateValues() {
		return dateValues;
	}

	public void setDateValues(List<DateValue> dateValues) {
		this.dateValues = dateValues;
	}

	public int getDescriptionCounter() {
		return descriptionCounter;
	}

	public void setDescriptionCounter(int descriptionCounter) {
		this.descriptionCounter = descriptionCounter;
	}

	public List<Description> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<Description> descriptions) {
		this.descriptions = descriptions;
	}

	public int getFormatCounter() {
		return formatCounter;
	}

	public void setFormatCounter(int formatCounter) {
		this.formatCounter = formatCounter;
	}

	public List<Format> getFormatList() {
		return formatList;
	}

	public void setFormatList(List<Format> formatList) {
		this.formatList = formatList;
	}

	public int getIdentifierCounter() {
		return identifierCounter;
	}

	public void setIdentifierCounter(int identifierCounter) {
		this.identifierCounter = identifierCounter;
	}

	public List<Identifier> getIdentifierList() {
		return identifierList;
	}

	public void setIdentifierList(List<Identifier> identifierList) {
		this.identifierList = identifierList;
	}

	@XmlElementWrapper( name = "keywords" )
	@XmlElement ( name = "keyword" )
	public List<Keyword> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<Keyword> keywords) {
		this.keywords = keywords;
	}

	public int getLanguageCounter() {
		return languageCounter;
	}

	public void setLanguageCounter(int languageCounter) {
		this.languageCounter = languageCounter;
	}

	public List<Language> getLanguageList() {
		return languageList;
	}

	public void setLanguageList(List<Language> languageList) {
		this.languageList = languageList;
	}

	public int getPublisherCounter() {
		return publisherCounter;
	}

	public void setPublisherCounter(int publisherCounter) {
		this.publisherCounter = publisherCounter;
	}

	public List<Publisher> getPublishers() {
		return publishers;
	}

	public void setPublishers(List<Publisher> publishers) {
		this.publishers = publishers;
	}

	public int getTitleCounter() {
		return titleCounter;
	}

	public void setTitleCounter(int titleCounter) {
		this.titleCounter = titleCounter;
	}

	public List<Title> getTitles() {
		return titles;
	}

	public void setTitles(List<Title> titles) {
		this.titles = titles;
	}

	public int getTypeValueCounter() {
		return typeValueCounter;
	}

	public void setTypeValueCounter(int typeValueCounter) {
		this.typeValueCounter = typeValueCounter;
	}

	public List<TypeValue> getTypeValueList() {
		return typeValueList;
	}

	public void setTypeValueList(List<TypeValue> typeValueList) {
		this.typeValueList = typeValueList;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n-- titles -- counter " +  titleCounter + " :\n");
		for(Title title : titles) {
			sb.append(title+"\n");
		}
		sb.append("\n-- authors -- counter " +  authorCounter + " :\n");
		for(Author author : authors) {
			sb.append(author+"\n");
		}
		sb.append("\n-- keywords:\n");
		for(Keyword keyword : keywords) {
			sb.append(keyword+"\n");
		}
		sb.append("\n-- descriptions -- counter " +  descriptionCounter + " :\n");
		for(Description desc : descriptions) {
			sb.append(desc+"\n");
		}
		sb.append("\n-- publishers -- counter " +  publisherCounter + " :\n");
		for(Publisher publisher : publishers) {
			sb.append(publisher+"\n");
		}
		sb.append("\n-- dateValues -- counter " +  dateValueCounter + " :\n");
		for(DateValue dv : dateValues) {
			sb.append(dv+"\n");
		}
		sb.append("\n-- formatList -- counter " +  formatCounter + " :\n");
		for(Format format : formatList) {
			sb.append(format+"\n");
		}
		sb.append("\n-- identifierList -- counter " +  identifierCounter + " :\n");
		for(Identifier ident : identifierList) {
			sb.append(ident+"\n");
		}
		sb.append("\n-- typeValueList -- counter " +  typeValueCounter + " :\n");
		for(TypeValue tv : typeValueList) {
			sb.append(tv+"\n");
		}
		sb.append("\n-- languageList -- counter " +  languageCounter + " :\n");
		for(Language lang : languageList) {
			sb.append(lang+"\n");
		}
		sb.append("\n-- classificationList:\n");
		for(ClassificationInterface classification : classificationList) {
			sb.append(classification+"\n");
		}
		return sb.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}







