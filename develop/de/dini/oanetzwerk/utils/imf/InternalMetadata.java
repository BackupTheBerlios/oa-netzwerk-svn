package de.dini.oanetzwerk.utils.imf;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.dini.oanetzwerk.utils.HelperMethods;

@XmlRootElement( namespace = "http://oanetzwerk.dini.de/" ) 
public class InternalMetadata {

	BigDecimal oid;
	
	List<Title> titleList;
	int titleCounter = 0;

	List<Author> authorList;
	int authorCounter = 0;

	List<Editor> editorList;
	int editorCounter = 0;
	
	List<Contributor> contributorList;
	int contributorCounter = 0;
	
	List<Keyword> keywordList;
	int keywordCounter = 0;
	
	List<Description> descriptionList;
	int descriptionCounter = 0;
	
	List<Publisher> publisherList;
	int publisherCounter = 0;

	List<DateValue> dateValueList;
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
	int classificationCounter = 0;
	
	public boolean isEmpty() {
		//TODO: stets überprüfen, ob neue Metadatenelemente hinzugekommen sind, die hier nicht überprüft werden
		if (!titleList.isEmpty()) return false; 
		if (!authorList.isEmpty()) return false; 
		if (!editorList.isEmpty()) return false; 
		if (!contributorList.isEmpty()) return false; 
		if (!keywordList.isEmpty()) return false; 
		if (!descriptionList.isEmpty()) return false; 
		if (!publisherList.isEmpty()) return false; 
		if (!dateValueList.isEmpty()) return false; 
		if (!formatList.isEmpty()) return false; 
		if (!identifierList.isEmpty()) return false; 
		if (!typeValueList.isEmpty()) return false; 
		if (!languageList.isEmpty()) return false; 
		if (!classificationList.isEmpty()) return false; 				
		return true;
	}
	
	public InternalMetadata() {
		oid = new BigDecimal(-1);
		
		titleList = new LinkedList<Title>();
		titleCounter = 0;
		
		authorList = new LinkedList<Author>();
		authorCounter = 0;

		keywordList = new LinkedList<Keyword>();
		keywordCounter = 0;
		
		descriptionList = new LinkedList<Description>();
		descriptionCounter = 0;
		
		publisherList = new LinkedList<Publisher>();
		publisherCounter = 0;
		
		dateValueList = new LinkedList<DateValue>();
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
		classificationCounter = 0;
		
		contributorList = new LinkedList<Contributor>();
		contributorCounter = 0;
		
		editorList = new LinkedList<Editor>();
		editorCounter = 0;
	}
	
	public static InternalMetadata createDummy() {
		InternalMetadata myIM = new InternalMetadata();
		
		Author author = new Author();
		author.setNumber(1);
		author.setFirstname("Max");
		author.setLastname("Mustermann");	
		myIM.addAuthor(author);
		author = new Author();
		author.setNumber(2);
		author.setFirstname("Maxine");
		author.setLastname("Musterfrau");
		myIM.addAuthor(author);
		
		Contributor contri = new Contributor();
		contri.setNumber(1);
		contri.setFirstname("Hägar");
		contri.setLastname("Löwenmaul");	
		myIM.addContributor(contri);
		contri = new Contributor();
		contri.setNumber(2);
		contri.setFirstname("Páris");
		contri.setLastname("Mühlenstrauß");
		myIM.addContributor(contri);
		
		Title title = new Title();
		title.setNumber(1);
		title.setLang("de");
		title.setQualifier("main");
		title.setTitle("Titel des Musterdatensatzes");
		myIM.addTitle(title);
		title = new Title();
		title.setNumber(2);
		title.setLang("en");
		title.setQualifier("subtitle");
		title.setTitle("subtitle of sample metadata entry");
		myIM.addTitle(title);	
		
		Classification classi = new DNBClassification();
		classi.setValue("01");
		myIM.addClassfication(classi);
		classi = new DDCClassification();
		classi.setValue("000");
		myIM.addClassfication(classi);
		classi = new DINISetClassification();
		classi.setValue("pub-type:dissertation");
		myIM.addClassfication(classi);
		classi = new OtherClassification();
		classi.setValue("unbekannter Klassifikationswert");
		myIM.addClassfication(classi);
		
		DateValue dv = new DateValue();
		dv.setNumber(1);
		try {
		  dv.setDateValue(HelperMethods.extract_java_datestamp("2008-02-28"));
		} catch(Exception ex) {}
		myIM.addDateValue(dv);
		
		Description desc = new Description();
		desc.setNumber(1);
		desc.setLanguage("de");
		desc.setDescription("Dies ist eine deutschsprachige Musterzusammenfassung.");
		myIM.addDescription(desc);
		desc = new Description();
		desc.setNumber(2);
		desc.setLanguage("en");
		desc.setDescription("This is a sample abstract in English language.");
		myIM.addDescription(desc);
		desc = new Description();
		desc.setNumber(3);
		desc.setLanguage(null);
		desc.setDescription("Dies ist eine Musterzusammenfassung, für die keine Sprache ermittelt werden konnte.");
		myIM.addDescription(desc);
		
		Format format = new Format();
		format.setNumber(1);
		format.setSchema_f("text/pdf");
		format = new Format();
		format.setNumber(2);
		format.setSchema_f("text/ps");
		myIM.addFormat(format);
		
		Identifier ident = new Identifier();
		ident.setNumber(1);
		ident.setIdentifier("www.noSuchURL.foo/downloads/Musterdoc.pdf");
        myIM.addIdentifier(ident);

        Keyword keyword = new Keyword();
        keyword.setLanguage("en");
        keyword.setKeyword("keyword A");
        myIM.addKeyword(keyword);
        keyword = new Keyword();
        keyword.setLanguage("de");
        keyword.setKeyword("Schlagwort A");
        myIM.addKeyword(keyword);
        keyword = new Keyword();
        keyword.setLanguage("en");
        keyword.setKeyword("keyword B");
        myIM.addKeyword(keyword);
        keyword = new Keyword();
        keyword.setLanguage("de");
        keyword.setKeyword("Schlagwort B");
        myIM.addKeyword(keyword);
        
        Language lang = new Language();
        lang.setNumber(1);
        lang.setLanguage("de");
        myIM.addLanguage(lang);
        
        Publisher pub = new Publisher();
        pub.setNumber(1);
        pub.setName("Musterherausgeber, Musterstadt");
		myIM.addPublisher(pub);
		
		TypeValue tv = new TypeValue();
		tv.setNumber(1);
		tv.setTypeValue("article");
		myIM.addTypeValue(tv);
		
		return myIM;
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
//			System.out.println(cl);
		}
		return result;
	}
	
	public int addKeyword(Keyword value) {
		int result = 0;
		if (value != null) {
			this.keywordList.add(value);
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
		this.keywordList.add(temp);
		System.out.println(temp);
		return result;
	}

	public int addPublisher(Publisher value) {
		int result = 0;
		if (value != null) {
			this.publisherList.add(value);
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
		this.publisherList.add(temp);
		System.out.println(temp);
		return result;
	}


	public int addDateValue(DateValue value) {
		int result = 0;
		if (value != null) {
			this.dateValueList.add(value);
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
		DateValue temp = null;
		try {
			temp = new DateValue();
			temp.setNumber(number);
			temp.setDateValue(HelperMethods.extract_java_datestamp(dateValue));
		} catch(Exception ex) {}


		this.dateValueList.add(temp);
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
			this.descriptionList.add(value);
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
		this.descriptionList.add(temp);
		
		System.out.println(temp);
		
		return result;

	}
	
	public int addTitle(Title title) {
		int result = 0;
		if (title != null) {
			this.titleList.add(title);
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
		this.titleList.add(temp);
		
		System.out.println(temp.toString());
		
		return result;
	}
	
	public int addAuthor(Author author) {
		int result = 0;
		if (author != null) {
			this.authorList.add(author);
		}
		return result;
	}

	public int addEditor(Editor editor) {
		int result = 0;
		if (editor != null) {
			this.editorList.add(editor);
		}
		return result;
	}
	
	public int addContributor(Contributor contributor) {
		int result = 0;
		if (contributor != null) {
			this.contributorList.add(contributor);
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
		this.authorList.add(temp);
		System.out.println(temp.toString());
		return result;
	}
	
	public int getAuthorCounter() {
		return authorCounter;
	}

	public void setAuthorCounter(int authorCounter) {
		this.authorCounter = authorCounter;
	}

	public List<Author> getAuthorList() {
		return authorList;
	}

	public void setAuthorList(List<Author> authors) {
		this.authorList = authors;
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

	public List<DateValue> getDateValueList() {
		return dateValueList;
	}

	public void setDateValueList(List<DateValue> dateValues) {
		this.dateValueList = dateValues;
	}

	public int getDescriptionCounter() {
		return descriptionCounter;
	}

	public void setDescriptionCounter(int descriptionCounter) {
		this.descriptionCounter = descriptionCounter;
	}

	public List<Description> getDescriptionList() {
		return descriptionList;
	}

	public void setDescriptionList(List<Description> descriptions) {
		this.descriptionList = descriptions;
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
	public List<Keyword> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List<Keyword> keywords) {
		this.keywordList = keywords;
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

	public List<Publisher> getPublisherList() {
		return publisherList;
	}

	public void setPublisherList(List<Publisher> publishers) {
		this.publisherList = publishers;
	}

	public int getTitleCounter() {
		return titleCounter;
	}

	public void setTitleCounter(int titleCounter) {
		this.titleCounter = titleCounter;
	}

	public List<Title> getTitleList() {
		return titleList;
	}

	public void setTitleList(List<Title> titles) {
		this.titleList = titles;
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
		
		sb.append("\nOID: " + oid);
				
		sb.append("\n-- titles -- counter " +  titleCounter + " :\n");
		for(Title title : titleList) {
			sb.append(title+"\n");
		}
		sb.append("\n-- authors -- counter " +  authorCounter + " :\n");
		for(Author author : authorList) {
			sb.append(author+"\n");
		}
		sb.append("\n-- editors -- counter " +  editorCounter + " :\n");
		for(Editor editor : editorList) {
			sb.append(editor+"\n");
		}
		sb.append("\n-- contributor -- counter " +  contributorCounter + " :\n");
		for(Contributor contributor : contributorList) {
			sb.append(contributor+"\n");
		}		
		sb.append("\n-- keywords:\n");
		for(Keyword keyword : keywordList) {
			sb.append(keyword+"\n");
		}
		sb.append("\n-- descriptions -- counter " +  descriptionCounter + " :\n");
		for(Description desc : descriptionList) {
			sb.append(desc+"\n");
		}
		sb.append("\n-- publishers -- counter " +  publisherCounter + " :\n");
		for(Publisher publisher : publisherList) {
			sb.append(publisher+"\n");
		}
		sb.append("\n-- dateValues -- counter " +  dateValueCounter + " :\n");
		for(DateValue dv : dateValueList) {
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
	
	public List<Editor> getEditorList() {
		return editorList;
	}

	public void setEditorList(List<Editor> editorList) {
		this.editorList = editorList;
	}

	public int getEditorCounter() {
		return editorCounter;
	}

	public void setEditorCounter(int editorCounter) {
		this.editorCounter = editorCounter;
	}

	public List<Contributor> getContributorList() {
		return contributorList;
	}

	public void setContributorList(List<Contributor> contributorList) {
		this.contributorList = contributorList;
	}

	public int getContributorCounter() {
		return contributorCounter;
	}

	public void setContributorCounter(int contributorCounter) {
		this.contributorCounter = contributorCounter;
	}
	
	public int getKeywordCounter() {
		return keywordCounter;
	}

	public void setKeywordCounter(int keywordCounter) {
		this.keywordCounter = keywordCounter;
	}

	public int getClassificationCounter() {
		return classificationCounter;
	}

	public void setClassificationCounter(int classificationCounter) {
		this.classificationCounter = classificationCounter;
	}

	public BigDecimal getOid() {
		return oid;
	}

	public void setOid(BigDecimal oid) {
		this.oid = oid;
	}

	
	
}







