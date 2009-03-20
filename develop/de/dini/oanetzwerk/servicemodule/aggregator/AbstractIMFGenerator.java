package de.dini.oanetzwerk.servicemodule.aggregator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

import de.dini.oanetzwerk.utils.DDCMatcher_DINI;
import de.dini.oanetzwerk.utils.HelperMethods;
import de.dini.oanetzwerk.utils.ISO639LangNormalizer;
import de.dini.oanetzwerk.utils.ISO8601DateNormalizer;
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

					// ein oder mehrere Klassifikationen aus einem setSpec-Eintrag extrahieren 
					List<Classification> list = this.extractClassifications(headerEntry.getValue());
					for(Classification cl : list) {
					  if (cl != null) {	
						this.im.addClassfication(cl);
						this.classificationCounter++;
					  } else {
						// cl wurde nicht erzeugt, muss noch als DEBUG raus
					  }
					}
					
				}
			}
		}
		return result;
	}
	
	protected List<Classification> extractClassifications(String metadataEntry) {
		List<Classification> list = new ArrayList<Classification>();
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
			
			String valDDC = null;			
			// eine Zahl herausmatchen
			for ( Matcher m = Pattern.compile("\\d+").matcher(StringUtils.substringAfterLast(value, ":")); m.find(); ) { 
			  valDDC = m.toMatchResult().group();
			  break;
			}
			
			if(valDDC != null && valDDC.length() > 0) {
				// hier wird der DDC-Wert DINI-konform "abgerundet" und - falls unterschiedlich - zusätzlich als Other gemerkt
				valDDC = DDCMatcher_DINI.fillUpWithZeros(valDDC);
				String s[] = DDCMatcher_DINI.convert(valDDC);
				list.add(new DDCClassification(s[0]));			
				if(s.length > 1) list.add(new OtherClassification(value));
			} else {
				list.add(new OtherClassification(value));
			}
		} else if (Classification.isDNB(value)) {
			String[] s = value.split(":");
			String valDNB = null;
			// eine Zahl herausmatchen
			for ( Matcher m = Pattern.compile("\\d+").matcher(s[1]); m.find(); ) { 
			  valDNB = m.toMatchResult().group();
			  break;
			}

			if(valDNB != null && valDNB.length() > 0) {
				if(valDNB.length() > 0) valDNB = StringUtils.substring(valDNB, valDNB.length()-2, valDNB.length());
				if(valDNB.length() < 2) valDNB = "0" + valDNB;
				list.add(new DNBClassification(valDNB));				
			} else {
				list.add(new OtherClassification(value));
			}
		} else if (Classification.isDINISet(value)) {
			String[] s = value.split(":");
			if(s.length > 1 && s[0].equalsIgnoreCase("pub-type")) {
			  s[0] = s[0].toLowerCase();
			  value = s[0] + ":" + s[1];
			  list.add(new DINISetClassification(value));
			} else {
			  list.add(new OtherClassification(value));
			}
		} else if (Classification.isOther(value)) {
			list.add(new OtherClassification(value));
		}

		return list;
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
			Locale locale = ISO639LangNormalizer.get_ISO639_3(value);
			if(locale != null) {
				language.setIso639language(locale.getISO3Language());
			} else {
			    if(value.equalsIgnoreCase("mis") || 
			       value.equalsIgnoreCase("mul") || 
			       value.equalsIgnoreCase("und")) {
			    	// gültige Status-Kürzel aus ISO639, die keiner Locale entsprechen, werden durchgeschleift
			    	language.setIso639language(value.toLowerCase());
			    } else if(value != null && value.length() > 0) {
			    	// ein existierender Wert, der weder zu Locale gemappt werden konnte, noch iso-Status-Kürzel ist 
			    	// Spezialfälle
			    	//TODO: hier konfigurierbares Spezialmapping einbetten
			    	if(value.equalsIgnoreCase("esp")) {
			    		language.setIso639language("spa");
			    	} else if(value.equalsIgnoreCase("other")) {
			    		language.setIso639language("mis"); 
			    	} else {
			    	  	// wenn nichts hilft als "undetermined" markieren
			    	    language.setIso639language("und");
			    	}
			    } else {
			    	// wenn der value leer ist, als "missing" markieren
			    	language.setIso639language("mis");
			    }
			}
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
			Date date = ISO8601DateNormalizer.getDateFromUTCString(value);
		    if(date == null) aggrStateLog.warn("couldn't parse value '" + value + "' as datestamp");
   		    dateValue.setDateValue(date);
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
