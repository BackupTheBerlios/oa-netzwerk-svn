package de.dini.oanetzwerk.servicemodule.dcgenerator

import groovy.util.XmlSlurper
import de.dini.oanetzwerk.oaipmh.Recordimport groovy.xml.MarkupBuilder/**
 * @author Michael K&uuml;hn
 *
 */
 
public class DCGroovyXMLParser {
	 
	public Record xmlTestParser (String cmf) {
		
		Record record = new Record ( );
		
		def result = new XmlSlurper ( ).parseText (cmf);
		def classification = result.classificationList
		
		result.classificationList.each {elem -> record.getMetaData ( ).getType ( ).add ("$elem".toString ( ))}
		result.dateValueList.each {elem -> record.getMetaData ( ).getDate ( ).add ("$elem.dateValue".toString ( ))}
		result.formatList.each {elem -> record.getMetaData ( ).getFormat ( ).add ("$elem.schema_f".toString ( ))}
		result.identifierList.each {elem -> record.getMetaData ( ).getIdentifier ( ).add ("$elem.identifier".toString ( ))}
		result.languageList.each {elem -> record.getMetaData ( ).getLanguage ( ).add ("$elem.language".toString ( ))}
		result.publisherList.each {elem -> record.getMetaData ( ).getCreator ( ).add ("$elem.name".toString ( ))}
		result.titleList.each {elem -> record.getMetaData ( ).getTitle ( ).add ("$elem.title".toString ( ))}
		
		return record;
	}
	
	public String xmlGeneratior (Record record) {
		
		def dcxml = new MarkupBuilder ( );
		
		dcxml.record ( ) {
			
			header ( ) {
				
				identifier ('oai:oanet:1234');
			}
			
			metadata {
				
				for (String titleString : record.getMetaData ( ).getTitle ( )) {
					
					title (titleString.toString());
				}
			}
		}
		
		return dcxml;
	}
}
