package de.dini.oanetzwerk.servicemodule.aggregator;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.imf.Author;
import de.dini.oanetzwerk.utils.imf.Classification;
import de.dini.oanetzwerk.utils.imf.Contributor;
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

abstract class AbstractIMFGenerator {

	static Logger logger = Logger.getLogger (AbstractIMFGenerator.class);
	static Logger aggrStateLog = Logger.getLogger("AggregationState");
	
	protected String xmlData;
	protected InternalMetadata im;
	
	protected int titleCounter = 0;
	protected int authorCounter = 0;
	protected int descriptionCounter = 0;
	protected int formatCounter = 0;
	protected int identifierCounter = 0;
	protected int languageCounter = 0;
	protected int keywordCounter = 0;
	protected int typeValueCounter = 0;
	protected int dateValueCounter = 0;
	protected int publisherCounter = 0;
	protected int contributorCounter = 0;
	protected int classificationCounter = 0;
	
	protected int editorCounter = 0;
	
	public AbstractIMFGenerator() {
		this.im = new InternalMetadata();
		this.xmlData = null;
	}
	
	public abstract InternalMetadata generateIMF(String data);
	
	@SuppressWarnings("unchecked")
	protected int addSetInformation(org.jdom.Document doc) {
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
	
	protected Classification extractClassification(String metadataEntry) {
		Classification result = null;
		this.classificationCounter++;
		String value = null;
		
		value = removeNoisyWhitespace(metadataEntry);
		
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
	
	protected Title extractTitleInformation(String value) {
		Title title = null;
		this.titleCounter ++;
		
		value = removeNoisyWhitespace(value);
		
		if (value != null) {
			title = new Title();
			title.setTitle(value);
			title.setNumber(this.titleCounter);
			title.setQualifier(this.titleQualifier(this.titleCounter));
		}
		return title;
	}
	
	protected String titleQualifier(int titlenumber) {
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
	
	protected Description extractDescription(String value) {
		Description description = null;
		this.descriptionCounter ++;
				
		if (value != null) {
			description = new Description();
			description.setDescription(value);
			description.setNumber(this.descriptionCounter);
		}
		return description;
	}

	protected Format extractFormat(String value) {
		Format format = null;
		this.formatCounter ++;
		
		value = removeNoisyWhitespace(value);
		
		if (value != null) {
			format = new Format();
			format.setSchema_f(value);
			format.setNumber(this.formatCounter);
		}
		return format;
	}

	protected Identifier extractIdentifier(String value) {
		Identifier identifier = null;
		this.identifierCounter ++;
		
		value = removeNoisyWhitespace(value);
		
		if (value != null) {
			identifier = new Identifier();
			identifier.setIdentifier(value);
			identifier.setNumber(this.identifierCounter);
		}
		return identifier;
	}
	
	protected Language extractLanguage(String value) {
		Language language = null;
		this.languageCounter ++;
		
		value = removeNoisyWhitespace(value);
		
		if (value != null) {
			language = new Language();
			language.setLanguage(value);
			language.setNumber(this.languageCounter);
		}
		return language;
	}

	protected TypeValue extractTypeValue(String value) {
		TypeValue typeValue = null;
		this.typeValueCounter ++;
		
		value = removeNoisyWhitespace(value);
		
		if (value != null) {
			typeValue = new TypeValue();
			typeValue.setTypeValue(value);
			typeValue.setNumber(this.typeValueCounter);
		}
		return typeValue;
	}

	protected DateValue extractDateValue(String value) {
		DateValue dateValue = null;
		this.dateValueCounter ++;
		
		value = removeNoisyWhitespace(value);
		
		if (value != null) {
			dateValue = new DateValue();
			dateValue.setStringValue(value);
			try {
			  dateValue.setDateValue(HelperMethods.extract_datestamp(value));
			} catch(ParseException pex) {
				aggrStateLog.error("couldn't parse value '" + value + "' as datestamp:", pex);
			}
			dateValue.setNumber(this.dateValueCounter);
		}
		return dateValue;
	}

	protected Publisher extractPublisher(String value) {
		Publisher publisher = null;
		this.publisherCounter ++;
		
		value = removeNoisyWhitespace(value);
		
		if (value != null) {
			publisher = new Publisher();
			publisher.setName(value);
			publisher.setNumber(this.publisherCounter);
		}
		return publisher;
	}
	
	protected Keyword extractKeyword(String value) {
		Keyword keyword = null;
		this.keywordCounter ++;
		
		value = removeNoisyWhitespace(value);
		
		if (value != null) {
			keyword = new Keyword();
			keyword.setKeyword(value);
//			keyword.(this.keywordCounter);
		}
		return keyword;
	}
		
	protected Author extractAuthor(String value) {
		Author author = null;
		this.authorCounter ++;
		if (value != null) {
			author = new Author();
			String[] names = extractSinglePersonName(value);
			author.setFirstname(names[0]);
			author.setLastname(names[1]);
			author.setNumber(this.authorCounter);
		}
		return author;
	}

	protected Contributor extractContributor(String value) {
		Contributor contributor = null;
		this.contributorCounter ++;
		if (value != null) {
			contributor = new Contributor();
			String[] names = extractSinglePersonName(value);
			contributor.setFirstname(names[0]);
			contributor.setLastname(names[1]);
			contributor.setNumber(this.contributorCounter);
		}
		return contributor;
	}	
	
	protected String[] extractSinglePersonName(String value) {
		String[] name = new String[2];
		name[0] = null;
		name[1] = null;
		
		value = removeNoisyWhitespace(value);
		
		String[] temp = value.split(",");
		if (temp.length < 2) {
			temp = value.split(" ");
			if (temp.length < 2) {
				// Split nicht erfolgreich, es gibt wohl nur einen Nachnamen
				name[1] = value.trim();
			} else {
				String firstname = temp[0];
				if(temp.length>2) {
					firstname = StringUtils.join(Arrays.copyOfRange(temp, 0, temp.length-1)," ");
				}
				name[0] = firstname.trim();					
				name[1] = temp[temp.length-1].trim();
			}
		
		} else {
			// Split nach , war erfolgreich, Format wird als Nachname, Vorname angenommen
			String firstname = temp[1];
			name[0] = firstname.trim();
			name[1] = temp[0].trim();
	
		}
		
		return name;
	}
	
	private String removeNoisyWhitespace(String value) {
		value = StringUtils.trim(value);
		value = StringUtils.strip(value);
		value = value.replaceAll("\t", "");
		value = value.replaceAll("\n", "");
		value = value.replaceAll("[ ]{2,}+", " ");
		return value;
	}
	
}
