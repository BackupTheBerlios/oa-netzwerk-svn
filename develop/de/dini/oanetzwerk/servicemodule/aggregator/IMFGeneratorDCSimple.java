package de.dini.oanetzwerk.servicemodule.aggregator;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import de.dini.oanetzwerk.utils.imf.InternalMetadata;
import de.dini.oanetzwerk.utils.imf.Keyword;
import de.dini.oanetzwerk.utils.imf.Language;


public class IMFGeneratorDCSimple extends AbstractIMFGenerator {
	
	//private Namespace namespace = Namespace.getNamespace("oai_dc","http://www.openarchives.org/OAI/2.0/oai_dc/");
	
	static Logger logger = Logger.getLogger (IMFGeneratorDCSimple.class);
	static Logger aggrStateLog = Logger.getLogger("AggregationState");
	
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
		classificationCounter = 0;
	}
	
	
	public IMFGeneratorDCSimple(BigDecimal object_id) {
		this();
		if (object_id != null)
			im.setOid(object_id);
		
		
	}

	
	@SuppressWarnings("unchecked")
	public InternalMetadata generateIMF(String data) {

//		this.im = new InternalMetadata();
		//		InternalMetadata im = new InternalMetadata();
		this.xmlData = data;

		Set<Keyword> setKeywords = new HashSet<Keyword>();
		
		org.jdom.Document doc;
		SAXBuilder builder = new SAXBuilder();
		
		Namespace namespace = Namespace.getNamespace("oai_dc","http://www.openarchives.org/OAI/2.0/oai_dc/");
		try {		
//			InputSource is = new InputSource("testdata.xml");
//			doc = builder.build(is);
			doc = builder.build(new InputSource (new StringReader((String) data)));
		
			// Set-Informationen laden --> Klassifikationen extrahieren
			this.addSetInformation(doc);
			
			logger.debug("** doc generated");

			// nach <oai_dc:dc> suchen, da darunter der gesamte Metadaten-Eintrag steht
			ElementFilter filter = new ElementFilter("dc",namespace);
			Iterator iterator = doc.getDescendants(filter);
			while (iterator.hasNext()) {
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
							// nicht direkt einspeichern, um durch Einlesen in ein Set zuerst Dopplungen auszuschlie�en!
							//im.addKeyword(this.extractKeyword(metadataEntry.getText()));
                            setKeywords.add(this.extractKeyword(metadataEntry.getText()));
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
						if (metadataEntry.getName().equals("contributor")) {
							im.addContributor(this.extractContributor(metadataEntry.getText()));
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
						if (metadataEntry.getName().equals("source")) {
							aggrStateLog.info("found \"source\" element, but do not parse it yet: " + metadataEntry.getText());
						}
						if (metadataEntry.getName().equals("language")) {
							im.addLanguage(this.extractLanguage(metadataEntry.getText()));
						}
						if (metadataEntry.getName().equals("relation")) {
							aggrStateLog.info("found \"relation\" element, but do not parse it yet: " + metadataEntry.getText());
						}
						if (metadataEntry.getName().equals("coverage")) {
							aggrStateLog.info("found \"coverage\" element, but do not parse it yet: " + metadataEntry.getText());
						}
						if (metadataEntry.getName().equals("rights")) {
							aggrStateLog.info("found \"rights\" element, but do not parse it yet: " + metadataEntry.getText());
						}
				}
				
				if(im.getLanguageList().isEmpty()) {
					Language lang = new Language();
					lang.setNumber(1);
					lang.setIso639language("mis");
					im.addLanguage(lang);
				}
				
				for(Keyword keyword : setKeywords) {
					im.addKeyword(keyword);
				}
				
				// counter setzen
				im.setAuthorCounter(this.authorCounter);
				im.setClassificationCounter(this.classificationCounter);
				im.setContributorCounter(this.contributorCounter);
				im.setDateValueCounter(this.dateValueCounter);
				im.setDescriptionCounter(this.descriptionCounter);
				//TODO: editor wird in DC nicht benutzt
				im.setEditorCounter(this.editorCounter);  
				im.setFormatCounter(this.formatCounter);
				im.setIdentifierCounter(this.identifierCounter);
				im.setKeywordCounter(this.keywordCounter);
				im.setLanguageCounter(this.languageCounter);
				im.setPublisherCounter(this.publisherCounter);
				im.setTitleCounter(this.titleCounter);
				im.setTypeValueCounter(this.typeValueCounter);
												
			}
		} catch(Exception e) {
			logger.error("error while parsing XML String: " + e);
		}

		return im;
	}
	
}
