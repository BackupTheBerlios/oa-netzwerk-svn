package de.dini.oanetzwerk.oaipmh;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

import de.dini.oanetzwerk.oaipmh.DataConnection;

/**
 * @author Sammy David
 * @author Michael Kühn
 * 
 */

public class ListMetadataFormats extends AbstractOAIPMHVerb  {


	public String processRequest(Map<String, String[]> parameter) {

		RequestType reqType = new RequestType();
		reqType.setVerb(VerbType.LIST_METADATA_FORMATS);

		String errorMsg = checkForBadArguments(parameter);
		if (StringUtils.isNotEmpty(errorMsg)) {
			return errorMsg;
		}
		
		if (parameter != null && parameter.size() == 2) {

			if (parameter.size() == 2 && parameter.containsKey("identifier")) {

				String identifier = parameter.get("identifier")[0];
				System.out.println("IDENT: " + identifier);
				DataConnection dataConnection = this.dataConnectionToolkit.createDataConnection();

				if (dataConnection.existsRepositoryIdentifier(identifier))
					reqType.setIdentifier(identifier);

				else {
					return new OAIPMHError(OAIPMHErrorcodeType.ID_DOES_NOT_EXIST).toString();
				}
			}
		}
		

		ListMetadataFormatsType metaDataFormatsList = new ListMetadataFormatsType();
		MetadataFormatType metaDataFormat = new MetadataFormatType();
		metaDataFormat.setMetadataPrefix("oai_dc");
		metaDataFormat.setSchema("http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
		metaDataFormat.setMetadataNamespace("http://www.openarchives.org/OAI/2.0/oai_dc");

		metaDataFormatsList.getMetadataFormats().add(metaDataFormat);

		OAIPMHtype oaipmhMsg = new OAIPMHtype(reqType);
		oaipmhMsg.setListMetadataFormats(metaDataFormatsList);

		Writer w = new StringWriter();

		try {
			// IBindingFactory identifierFactory =
			// BindingDirectory.getFactory(OaiIdentifierType.class);
			IBindingFactory oaipmhFactory = BindingDirectory.getFactory(OAIPMHtype.class);

			// marshal object back out to document in memory
			IMarshallingContext mctx = oaipmhFactory.createMarshallingContext();
			mctx.setIndent(2);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			mctx.setOutput(bos, "UTF-8");
			mctx.marshalDocument(oaipmhMsg, "UTF-8", null, w);

		} catch (JiBXException e) {

			System.err.println(e.getLocalizedMessage() + e);
		}

		System.out.println(w.toString());
		return w.toString();
	}
}
