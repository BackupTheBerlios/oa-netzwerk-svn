package de.dini.oanetzwerk.servicemodule.aggregator;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.Classification;
import de.dini.oanetzwerk.utils.imf.DDCClassification;
import de.dini.oanetzwerk.utils.imf.DINISetClassification;
import de.dini.oanetzwerk.utils.imf.DNBClassification;
import de.dini.oanetzwerk.utils.imf.DateValue;
import de.dini.oanetzwerk.utils.imf.Description;
import de.dini.oanetzwerk.utils.imf.Format;
import de.dini.oanetzwerk.utils.imf.Identifier;
import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.Language;
import de.dini.oanetzwerk.utils.imf.OtherClassification;
import de.dini.oanetzwerk.utils.imf.Publisher;
import de.dini.oanetzwerk.utils.imf.Title;
import de.dini.oanetzwerk.utils.imf.TypeValue;

public class IMFGeneratorDCSimple extends AbstractIMFGenerator {

	
	private int titleCounter = 0;
	private int authorCounter = 0;
	private int descriptionCounter = 0;
	private int formatCounter = 0;
	private int identifierCounter = 0;
	private int languageCounter = 0;
	private int keywordCounter = 0;
	private int typeValueCounter = 0;
	private int dateValueCounter = 0;
	private int publisherCounter = 0;
	
	private Namespace namespace = Namespace.getNamespace("oai_dc","http://www.openarchives.org/OAI/2.0/oai_dc/");
	
	static Logger logger = Logger.getLogger (IMFGeneratorDCSimple.class);
	
	public IMFGeneratorDCSimple() {
		super();

		titleCounter = 0;
		authorCounter = 0;
		descriptionCounter = 0;
		formatCounter = 0;
		identifierCounter = 0;
		languageCounter = 0;
		keywordCounter = 0;
		typeValueCounter = 0;
		dateValueCounter = 0;
		publisherCounter = 0;
	}
	
	
	@SuppressWarnings("unchecked")
	private int addSetInformation(org.jdom.Document doc) {
		int result = 0;
		if (logger.isDebugEnabled()) {
			logger.debug("** Auswertung SET-Struktur im <header>");
		}
		// Auswertung der SET-Struktur aus dem Header
		ElementFilter filter = new ElementFilter("header");
		Iterator iterator = doc.getDescendants(filter);
		while (iterator.hasNext()) {
			if (logger.isDebugEnabled()) {
				logger.debug("** <header> found");
			}
			Element headerSet = (Element) iterator.next();
			List headerList = headerSet.getChildren();
			Iterator iteratorHeader = headerList.iterator();
			while (iteratorHeader.hasNext()) {
				Element headerEntry = (Element) iteratorHeader.next();
				if (headerEntry.getName().equals("setSpec")) {
					Classification cl = this.extractClassification(headerEntry.getValue());
					if (cl != null) {
						this.im.addClassfication(cl);
					} else {
						// cl wurde nicht erzeugt, muss noch als DEBUG raus
					}
				}
			}
		}
		return result;
	}
	
	private Classification extractClassification(String metadataEntry) {
		Classification result = null;
		String value = null;
		
		value = metadataEntry;
//		String[] temp = metadataEntry.split(":");
//		if (temp.length < 2) {
//			// Fehler, keine korrekte DNB-Codierung bzw. kein Wert
//			// ÜBergangsweise wird der gesamte Eintrag eingestellt
//			value = metadataEntry;
//		} else {
////			value = temp[1];
//			value = metadataEntry;
//			//			value = value.split(":")[1];
//		}
		
		if (Classification.isDDC(value)) {
			result = new DDCClassification(value);
		} else if (Classification.isDNB(value)) {
			result = new DNBClassification(value);
		} else if (Classification.isDINISet(value)) {
			result = new DINISetClassification(value);
		} else if (Classification.isOther(value)) {
			result = new OtherClassification(value);
		}

		return result;
	}
	
	private Title extractTitleInformation(String value) {
		Title title = null;
		this.titleCounter ++;
		if (value != null) {
			title = new Title();
			title.setTitle(value);
			title.setNumber(this.titleCounter);
			title.setQualifier(this.titleQualifier(this.titleCounter));
		}
		return title;
	}
	
	private String titleQualifier(int titlenumber) {
		String result = null;
		
		switch (titlenumber) {
			case 1 : 
				result = "main";
				break;
			case 2 :
				result = "subtitle";
				break;
			default :
				result = ""+titlenumber;
		}	
		return result;
	}
	
	private Description extractDescription(String value) {
		Description description = null;
		this.descriptionCounter ++;
		if (value != null) {
			description = new Description();
			description.setDescription(value);
			description.setNumber(this.descriptionCounter);
		}
		return description;
	}

	private Format extractFormat(String value) {
		Format format = null;
		this.formatCounter ++;
		if (value != null) {
			format = new Format();
			format.setSchema_f(value);
			format.setNumber(this.formatCounter);
		}
		return format;
	}

	private Identifier extractIdentifier(String value) {
		Identifier identifier = null;
		this.identifierCounter ++;
		if (value != null) {
			identifier = new Identifier();
			identifier.setIdentifier(value);
			identifier.setNumber(this.identifierCounter);
		}
		return identifier;
	}
	
	private Language extractLanguage(String value) {
		Language language = null;
		this.languageCounter ++;
		if (value != null) {
			language = new Language();
			language.setLanguage(value);
			language.setNumber(this.languageCounter);
		}
		return language;
	}

	private TypeValue extractTypeValue(String value) {
		TypeValue typeValue = null;
		this.typeValueCounter ++;
		if (value != null) {
			typeValue = new TypeValue();
			typeValue.setTypeValue(value);
			typeValue.setNumber(this.typeValueCounter);
		}
		return typeValue;
	}

	private DateValue extractDateValue(String value) {
		DateValue dateValue = null;
		this.dateValueCounter ++;
		if (value != null) {
			dateValue = new DateValue();
			dateValue.setDateValue(value);
			dateValue.setNumber(this.dateValueCounter);
		}
		return dateValue;
	}

	private Publisher extractPublisher(String value) {
		Publisher publisher = null;
		this.publisherCounter ++;
		if (value != null) {
			publisher = new Publisher();
			publisher.setName(value);
			publisher.setNumber(this.publisherCounter);
		}
		return publisher;
	}
	
	private Keyword extractKeyword(String value) {
		Keyword keyword = null;
		this.keywordCounter ++;
		if (value != null) {
			keyword = new Keyword();
			keyword.setKeyword(value);
//			keyword.(this.keywordCounter);
		}
		return keyword;
	}
	
	
	
	
	private Author extractAuthor(String value) {
		Author author = null;
		this.authorCounter ++;
		if (value != null) {
			author = new Author();
			String[] temp = value.split(",");
			if (temp.length < 2) {
				temp = value.split(" ");
				if (temp.length < 2) {
					// Split nicht erfolgreich, es gibt wohl nur einen Nachnamen
					author.setLastname(value.trim());
				} else {
					author.setFirstname(temp[0].trim());
					author.setLastname(temp[1].trim());
				}
			
			} else {
				// Split nach , war erfolgreich, Format wird als Nachname, Vorname angenommen
				author.setFirstname(temp[1].trim());
				author.setLastname(temp[0].trim());
		
			}
			author.setNumber(this.authorCounter);
		}
		return author;
	}
	
	@SuppressWarnings("unchecked")
	public InternalMetadata generateIMF(String data) {

//		this.im = new InternalMetadata();
		//		InternalMetadata im = new InternalMetadata();
		this.xmlData = data;

		org.jdom.Document doc;
		SAXBuilder builder = new SAXBuilder();
		
		Namespace namespace = Namespace.getNamespace("oai_dc","http://www.openarchives.org/OAI/2.0/oai_dc/");
		try {		
//			InputSource is = new InputSource("testdata.xml");
//			doc = builder.build(is);
			doc = builder.build(new InputSource (new StringReader((String) data)));
		
			// Set-Informationen laden
			this.addSetInformation(doc);
			
			logger.debug("** doc generated");

			// nach <oai_dc:dc> suchen, da darunter der gesamte Metadaten-Eintrag steht
			ElementFilter filter = new ElementFilter("dc",namespace);
			Iterator iterator = doc.getDescendants(filter);
			while (iterator.hasNext()) {
				System.out.println("** <oai_dc:dc> found");
				logger.debug("** <oai_dc:dc> found");
				
				Element metadataSet = (Element) iterator.next();
				List metadataList = metadataSet.getChildren();
				Iterator iteratorMetadata = metadataList.iterator(); 
				while (iteratorMetadata.hasNext()) {
						Element metadataEntry = (Element) iteratorMetadata.next();
//						System.out.println("Element: " + metadataEntry.getName());
						if (logger.isDebugEnabled()) {
							logger.debug("Element: " + metadataEntry.getName());
						}

						
						if (metadataEntry.getName().equals("title")) {
							im.addTitle(this.extractTitleInformation(metadataEntry.getText()));
						}
						if (metadataEntry.getName().equals("creator")) {
							im.addAuthor(this.extractAuthor(metadataEntry.getText()));
						}
						if (metadataEntry.getName().equals("subject")) {
							im.addKeyword(this.extractKeyword(metadataEntry.getText()));
						}
						if (metadataEntry.getName().equals("description")) {
							im.addDescription(this.extractDescription(metadataEntry.getText()));
						}
						if (metadataEntry.getName().equals("publisher")) {
							im.addPublisher(this.extractPublisher(metadataEntry.getText()));
						}
						if (metadataEntry.getName().equals("date")) {
							im.addDateValue(this.extractDateValue(metadataEntry.getText()));
						}
						if (metadataEntry.getName().equals("type")) {
							im.addTypeValue(this.extractTypeValue(metadataEntry.getText()));
						}
						if (metadataEntry.getName().equals("format")) {
							im.addFormat(this.extractFormat(metadataEntry.getText()));
						}
						if (metadataEntry.getName().equals("identifier")) {
							im.addIdentifier(this.extractIdentifier(metadataEntry.getText()));
						}
						if (metadataEntry.getName().equals("language")) {
							im.addLanguage(this.extractLanguage(metadataEntry.getText()));
						}
				}
				
			}
		} catch(Exception e) {
			System.err.println("Fehler beim Parsen");
			System.err.println("error while decoding XML String: " + e);
			logger.error("error while decoding XML String: " + e);
		}

		return im;
	}
	
}
