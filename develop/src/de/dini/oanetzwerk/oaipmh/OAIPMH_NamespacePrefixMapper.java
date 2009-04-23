package de.dini.oanetzwerk.oaipmh;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;

/**
 * Diese Klasse kann eine Schnittstelle des JAXBMarshallers überschreiben, die festlegt, welche
 * Namespace-Prefixes vor die generierten XML-Elemente gesetzt wird.
 * 
 * Beispiel des Aufrufs:
 *   myJAXBMarshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", 
 *                                new OAIPMH_NamespacePrefixMapper()); 
 * 
 * Zum Komplieren aus ant heraus erfordert NamespacePrefixMapper das rt.jar.
 * Im Ant-Task muss daher stehen:  <javac bootclasspathref="jre.libs" ...
 * Wobei das wie folgt vorher gesetzt ist:
 * <path id="jre.libs" description="Java runtime libraries">
 *	<pathelement location="${java.home}/lib" />
 * </path>	
 * 
 * @author malitzro
 *
 */
public class OAIPMH_NamespacePrefixMapper extends NamespacePrefixMapper {

	  @Override
	  public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix)
	  {
		 //if("uri:meinNamespace/Foo".equals(namespaceUri)) return "foo";

		if("http://www.openarchives.org/OAI/2.0/".equals(namespaceUri)) return "";
		if("http://www.openarchives.org/OAI/2.0/oai_dc/".equals(namespaceUri)) return "oai_dc";
		if("http://purl.org/dc/elements/1.1/".equals(namespaceUri)) return "dc";

	    return suggestion;
	  } 

}
