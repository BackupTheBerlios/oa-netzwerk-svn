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

import de.dini.oanetzwerk.utils.imf.InternalMetadata;


public class IMFGeneratorDCSimple extends AbstractIMFGenerator {
	
	//private Namespace namespace = Namespace.getNamespace("oai_dc","http://www.openarchives.org/OAI/2.0/oai_dc/");
	
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
