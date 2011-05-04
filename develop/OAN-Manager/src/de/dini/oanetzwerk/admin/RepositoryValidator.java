package de.dini.oanetzwerk.admin;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.http.HTTPException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.dini.oanetzwerk.utils.PropertyManager;

@ManagedBean
@RequestScoped
public class RepositoryValidator {
	
	@ManagedProperty(value = "#{propertyManager}")
	private PropertyManager propertyManager;
	
	private static String schemaFileFallback = "http://oanet.cms.hu-berlin.de/xsd/OANRESTMessage.xsd";
	private static String serverFileFallback = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+"<serverList>"
		+"<server>"
		+"<url>http://oanet.cms.hu-berlin.de/restserver/server/</url>"
		+"<alias>HU Berlin Restschnittstelle</alias>"
		+"<username>Harvester</username>"
		+"<password>retsevrah</password>"
		+"<parameters>"
		+"<parameter>CompleteMetadataEntry/18</parameter>"
		+"</parameters>"
		+"</server>"
		+"</serverList>";
//	private static String validationResults = "/home/imrael/workspace/OAN-Manager/web/WEB-INF/validationResults.xml";
	private String serverFile = propertyManager.getWebApplicationRootDirectory() + "/WEB-INF/server.xml";
	private String validationResults = propertyManager.getWebApplicationRootDirectory() + "/WEB-INF/validationResults.xml";
	private String serverXML;
	private NodeList serverList;
	private XPath xpath;
	
	public RepositoryValidator() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			File f = new File(serverFile);
			if (f.exists() && f.canRead()) {
				System.out.println("Wähle Datei als serverFile");
				serverXML = this.read(serverFile, "UTF-8");
			}
			else {
				serverXML = serverFileFallback;
				System.out.println("Wähle Fallback als serverFile");
			}
			
			StringReader s = new StringReader(serverXML);
			
			Document document = builder.parse(new InputSource(s));
			xpath = XPathFactory.newInstance().newXPath();
			serverList = (NodeList) xpath.evaluate("serverList/server", document, XPathConstants.NODESET);
		}
		catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private String read(String fFileName, String fEncoding) throws IOException {
	    StringBuilder text = new StringBuilder();
	    String NL = System.getProperty("line.separator");
	    Scanner scanner = new Scanner(new FileInputStream(fFileName), fEncoding);
	    try {
	    	while (scanner.hasNextLine()){
	    		text.append(scanner.nextLine() + NL);
	    	}
	    }
	    finally{
	    	scanner.close();
	    }
	    return text.toString();
	}
	
	public ValidationResultBean getValidationDataFromXML(String serverAlias) {
		// TODO: Server aus XML suchen können
		Node server = serverList.item(0);
		
		ValidationResultBean valiData = new ValidationResultBean();
		
		if (server != null) {
			try {
				NodeList parameters = (NodeList) xpath.evaluate("parameters/parameter",server, XPathConstants.NODESET);
				
				valiData.setServerUrl( ((Node) xpath.evaluate("url", server, XPathConstants.NODE)).getTextContent() );
				valiData.setServerAlias( ((Node) xpath.evaluate("alias", server, XPathConstants.NODE)).getTextContent() );
				valiData.setServerUsername( ((Node) xpath.evaluate("username", server, XPathConstants.NODE)).getTextContent() );
				valiData.setServerPassword( ((Node) xpath.evaluate("password", server, XPathConstants.NODE)).getTextContent() );
				
				String[] parameterList = new String[parameters.getLength()];
				for (int i = 0; i < parameters.getLength(); i++) {
					parameterList[i] = parameters.item(i).getTextContent();
				}
				valiData.setParameters(parameterList);
				valiData.setValidationResults(createValidationResultList(valiData));
			}
			catch (XPathExpressionException e) {
				System.out.println(e.getMessage());
			}
		}
		return valiData;
	}
	
	public String[] createValidationResultList(ValidationResultBean valiData) {
		String[] resultList = new String[valiData.getParameters().length];
		for (int i = 0; i < valiData.getParameters().length; i++) {
			resultList[i] = validateLink(
					valiData.getServerUrl(),
					valiData.getServerUsername(),
					valiData.getServerPassword(),
					valiData.getParameters()[i]
			);
		}
		return resultList;
	}
	
	public String validateLink(String url, String username, String password, String parameter) {
		Credentials creds = null;
		HttpClient htc = new HttpClient();
		htc.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		
		if (!(username.isEmpty() || password.isEmpty())) {
			creds = new UsernamePasswordCredentials(username, password);
		}
		if (creds != null) {
            htc.getState().setCredentials(AuthScope.ANY, creds);
        }
		if (!parameter.isEmpty()) {
			// testen ob die Url auch mit einem / endet.
			// damit wird abgefangen wenn User die Schnittstellenadresse ohne / eingeben
			if (!url.endsWith("/")) {
				url = url.concat("/");
			}
			url = url.concat(parameter);
			System.out.println("Url mit Parametern: "+url);
		}
		HttpMethod method = new GetMethod(url);
        method.setFollowRedirects(true);
        String response = null;
		try {
			
			htc.executeMethod(method);
            response = method.getResponseBodyAsString();
            System.out.println(response);
            response = response.replace((String) "xsi:noNamespaceSchemaLocation=\"http://oanet.cms.hu-berlin.de/xsd/OANRESTMessage.xsd\"", (String)"");
			
            
            SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sFactory.newSchema(new URL(schemaFileFallback));
            
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);
			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
			factory.setSchema(schema);
					
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();	
			//parser.setErrorHandler(new SimpleErrorHandler());
			Document document = parser.parse(new InputSource(new StringReader(response)));

			
			Validator validator = schema.newValidator();
			
			validator.validate(new DOMSource(document));
		} 
		catch (HTTPException e) {
			System.err.println("Fehler beim Versuch auf "+url+" zuzugreifen.");
			e.printStackTrace();
			return "HTTPException: "+e.getMessage();
		}
		catch (ParserConfigurationException e) {	
			e.printStackTrace();
			return "ParserConfigurationException: "+e.getMessage();
		}
		catch (IOException e) {
			System.out.println("IOException beim Zugriff auf "+url);
			System.err.println(e.getMessage());
			e.printStackTrace();
			return "IOException: "+e.getMessage();
		}
		catch (SAXException e) {
			System.out.println("SAXException: "+e.getMessage());
			return "SAXException: "+e.getMessage();
		}
		catch (Exception e) {
			e.printStackTrace();
			return "Fehler ("+e.getClass()+"): "+e.getMessage();
		}
		
		return "passed";
	}
	
	public void formatResultsAsXML(ValidationResultBean valiData) {
		
		// XML Datei einlesen um sie später erweitern zu können (neuester Test wird hinten angehängt.
		// Da die XML Dateien zum sauberen Parsen immer nur 1 Root-Element haben dürfen, reicht es nicht einfach hinten nen String ranzukleben. Daher muss sauber ein neuer XML-Knoten erzeugt werden.
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			File f = new File(validationResults);
			String source;
			if (f.exists() && f.canRead()) {
				source = this.read(validationResults, "UTF-8");
				System.out.println("Wähle Datei als Quelle");
			}
			else {
				source = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><validationResults></validationResults>";
				System.out.println("Erstelle neue Datei als Quelle");
			}
			
			StringReader s = new StringReader(source);
			Document doc = docBuilder.parse(new InputSource(s));
		
			Node docRoot = doc.getFirstChild();
			System.out.println("Doc: " + docRoot.getNodeName());

			Node newValidationResult = doc.createElement("validationResult");
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
			String timestamp = dateFormat.format( new Date() ).toString();
			
			Node timestampNode = doc.createElement("timestamp");
			timestampNode.setTextContent(timestamp);
			newValidationResult.appendChild(timestampNode);
			
			Node serverNode = doc.createElement("server");
			serverNode.setTextContent(valiData.getServerUrl());
			newValidationResult.appendChild(serverNode);

			Node aliasNode = doc.createElement("alias");
			aliasNode.setTextContent(valiData.getServerAlias());
			newValidationResult.appendChild(aliasNode);
			
			Node usernameNode = doc.createElement("username");
			usernameNode.setTextContent(valiData.getServerUsername());
			newValidationResult.appendChild(usernameNode);

			Node passwordNode = doc.createElement("password");
			passwordNode.setTextContent(valiData.getServerPassword());
			newValidationResult.appendChild(passwordNode);

			Node resultSetNode = doc.createElement("resultSet");
			for (int i = 0; i < valiData.getParameters().length; i++) {
				Node resultNode = doc.createElement("result");
				Node parameterNode = doc.createElement("parameter");
				Node returnValueNode = doc.createElement("returnValue");
				parameterNode.setTextContent(valiData.getParameters()[i]);
				returnValueNode.setTextContent(valiData.getValidationResults()[i]);
				
				resultNode.appendChild(parameterNode);
				resultNode.appendChild(returnValueNode);
				resultSetNode.appendChild(resultNode);
			}
			
			newValidationResult.appendChild(resultSetNode);
			docRoot.appendChild(newValidationResult);
			NodeList test = docRoot.getChildNodes();
			System.out.println("Childcount :"+test.getLength());
			for (int i = 0; i < test.getLength(); i++) {
				System.out.println("Child "+i+": "+test.item(i).getNodeName());
			}
			
			Transformer t = TransformerFactory.newInstance().newTransformer();
		    t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		    t.setOutputProperty(OutputKeys.INDENT, "yes");
		    StreamResult sr = new StreamResult(f);
		    t.transform(new DOMSource(docRoot), sr);
		    		
		}
		catch ( Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public ValidationResultBean[] getResults(int howMany) {

		Document doc;
		NodeList results = null;
		
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				new InputSource(
					new StringReader(
						this.read(validationResults, "UTF-8")
					)
				)
			);
		
			results = (NodeList) xpath.evaluate("validationResults/validationResult", doc, XPathConstants.NODESET);
			if (results.getLength() < howMany || howMany == 0) {
				howMany = results.getLength();
			}
			ValidationResultBean[] resultList = new ValidationResultBean[howMany];
			for (int i = 0; i < howMany; i++) {
				Node result = results.item(results.getLength() - 1 - i); // von hinten durchgehen
				
				ValidationResultBean res = new ValidationResultBean();
				res.setTestOffset(results.getLength() - i);
				res.setTimestamp( ((Node) xpath.evaluate("timestamp", result, XPathConstants.NODE)).getTextContent() );
				res.setServerAlias( ((Node) xpath.evaluate("alias", result, XPathConstants.NODE)).getTextContent());
				
				NodeList tests = (NodeList) xpath.evaluate("resultSet/result", result, XPathConstants.NODESET);
				String[] validationResults = new String[tests.getLength()];
				String[] parameters = new String[tests.getLength()];
				for (int j = 0; j < tests.getLength(); j++) {
					validationResults[j] = ((Node) xpath.evaluate("returnValue", tests.item(j), XPathConstants.NODE)).getTextContent();
					parameters[j] = ((Node) xpath.evaluate("parameter", tests.item(j), XPathConstants.NODE)).getTextContent();
				}
				res.setValidationResults(validationResults);
				res.setParameters(parameters);
				
				resultList[i] = res;
			}
			return resultList;
		}
		catch (FileNotFoundException e) {
			ValidationResultBean[] leereListe = {};
			return leereListe;
		}
		catch (Exception e) {
			e.printStackTrace();
			ValidationResultBean[] leereListe = {};
			return leereListe;
		}
		
	}
	
	public String[] getServerList() {
		String[] returnvalue = new String[serverList.getLength()];
		//System.out.println("Serverlistenlänge: "+ serverList.getLength());
		for (int i = 0; i < serverList.getLength(); i++) {
			Node server = serverList.item(i);
			//System.out.println(server.getTextContent());
			try {
				returnvalue[i] = ((Node) xpath.evaluate("alias", server, XPathConstants.NODE)).getTextContent();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return returnvalue;
	}
	
	public void rerunTest(int testOffset, int subTestOffset) {
		// testOffset = 0 --> ungültiger Testindex
		if (testOffset == 0) {
			return;
		}
		
		ValidationResultBean valiData = new ValidationResultBean();
		Document doc = null;
		Node result = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				new InputSource(
					new StringReader(
						this.read(validationResults, "UTF-8")
					)
				)
			);
		
			result = (Node) xpath.evaluate("validationResults/validationResult["+testOffset+"]", doc, XPathConstants.NODE);
				
			valiData.setTestOffset(testOffset);
			valiData.setTimestamp( ((Node) xpath.evaluate("timestamp", result, XPathConstants.NODE)).getTextContent() );
			valiData.setServerAlias( ((Node) xpath.evaluate("alias", result, XPathConstants.NODE)).getTextContent());
			valiData.setServerUrl( ((Node) xpath.evaluate("server", result, XPathConstants.NODE)).getTextContent());
			valiData.setServerUsername( ((Node) xpath.evaluate("username", result, XPathConstants.NODE)).getTextContent());
			valiData.setServerPassword( ((Node) xpath.evaluate("password", result, XPathConstants.NODE)).getTextContent());
			
			
			
			NodeList tests;
			if (subTestOffset == 0) {
				tests = (NodeList) xpath.evaluate("resultSet/result", result, XPathConstants.NODESET);
			}
			else {
				// Die subTestOffset Variable kommt bereits bei 1 startend an (wird im RepositoryValidatorBean-Construktor behandelt)
				tests = (NodeList) xpath.evaluate("resultSet/result["+subTestOffset+"]", result, XPathConstants.NODESET);
			}
			String[] validationResults = new String[tests.getLength()];
			String[] parameters = new String[tests.getLength()];
			for (int j = 0; j < tests.getLength(); j++) {
				//validationResults[j] = ((Node) xpath.evaluate("returnValue", tests.item(j), XPathConstants.NODE)).getTextContent();
				parameters[j] = ((Node) xpath.evaluate("parameter", tests.item(j), XPathConstants.NODE)).getTextContent();
			}
			valiData.setValidationResults(validationResults);
			valiData.setParameters(parameters);
			validateServer(valiData);
		}
		catch(Exception e) {
			System.out.println("Fehler beim Rerun: "+e.getMessage());
			e.printStackTrace();
		}
		
	}

	public void validateServer(ValidationResultBean valiData) {
		valiData.setValidationResults(createValidationResultList(valiData));
		formatResultsAsXML(valiData);
	}
	public void validateServer(String serverAlias) {
		ValidationResultBean valiData = getValidationDataFromXML(serverAlias);
		validateServer(valiData);
		
	}
	public void validateServer(String serverAlias, String parameter) {
		ValidationResultBean valiData = getValidationDataFromXML(serverAlias);
		String[] params = {parameter};
		valiData.setParameters( params );
		validateServer(valiData);
		
	}
	public void validateServer(String serverAlias, String serverUrl, String serverUsername, String serverPassword, String parameter) {
		ValidationResultBean valiData = new ValidationResultBean();
		valiData.setServerAlias(serverAlias);
		valiData.setServerUrl(serverUrl);
		valiData.setServerUsername(serverUsername);
		valiData.setServerPassword(serverPassword);
		String[] params = {parameter}; 
		valiData.setParameters(params);
		validateServer(valiData);
	}

	public void deleteTest(int testOffset) {
		// testOffset = 0 --> lösche alle Datensätze
		// testOffset > 0 --> lösche Datensatz
		System.out.println("deleteTest("+testOffset+")");
		try {
			if (testOffset == 0) {
				File f = new File(validationResults);
				if (f.exists()) {
					f.delete();
					System.out.println("Datei gelöscht");
				}
				return;
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		Document doc = null;
		Node result = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				new InputSource(
					new StringReader(
						this.read(validationResults, "UTF-8")
					)
				)
			);
		
			result = (Node) xpath.evaluate("validationResults/validationResult["+testOffset+"]", doc, XPathConstants.NODE);
			Node docRoot = doc.getFirstChild();	
			docRoot.removeChild(result);
			Transformer t = TransformerFactory.newInstance().newTransformer();
		    t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		    t.setOutputProperty(OutputKeys.INDENT, "yes");
		    File f = new File(validationResults); 
		    StreamResult sr = new StreamResult(f);
		    t.transform(new DOMSource(docRoot), sr);
		}
		catch(Exception e) {
			System.out.println("Fehler beim Löschen: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}
}
