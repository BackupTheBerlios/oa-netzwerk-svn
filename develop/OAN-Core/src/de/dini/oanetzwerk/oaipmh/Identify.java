package de.dini.oanetzwerk.oaipmh;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

import de.dini.oanetzwerk.oaipmh.oaiidentifier.OaiIdentifierType;
import de.dini.oanetzwerk.server.database.SelectFromDB;

/**
 * 
 * @author Sammy David
 * @author Michael Kühn
 * 
 */

public class Identify extends AbstractOAIPMHVerb {

	private static Logger logger = Logger.getLogger(Identify.class);

	public String processRequest(Map<String, String[]> parameter) {
		logger.info("br0");
		String errorMsg = checkForBadArguments(parameter);
		if (StringUtils.isNotEmpty(errorMsg)) {
			return errorMsg;
		}
		
		List<String> adminEmails = new ArrayList<String>();
		adminEmails.add("oanet@oanet.cms.hu-berlin.de");

		IdentifyType identify = new IdentifyType();
		identify.setBaseURL("http://oanet.cms.hu-berlin.de/oaipmh/oaipmh");
		
		DataConnection dataConnection = this.dataConnectionToolkit.createDataConnection();
		identify.setEarliestDatestamp(dataConnection.getEarliestDataStamp());
		identify.setGranularity(GranularityType.YYYYMMDD);
		identify.setProtocolVersion(ProtocolVersionType._20);
		identify.setRepositoryName("OA Netzwerk OAI-PMH Export Interface");
		identify.setAdminEmails(adminEmails);
		identify.setDeletedRecord(DeletedRecordType.TRANSIENT);

//		OaiIdentifierType oaiIdent = new OaiIdentifierType();
//		oaiIdent.setDelimiter(":");
//		oaiIdent.setRepositoryIdentifier("oanet.de");
//		oaiIdent.setSampleIdentifier("oai:oanet.de:152");
//		oaiIdent.setScheme("oai");
//
//		DescriptionType descr = new DescriptionType();
//		descr.setObject(oaiIdent);

//		identify.getDescriptions().add(descr);
		RequestType reqType = new RequestType();
		reqType.setVerb(VerbType.IDENTIFY);
		OAIPMHtype oaipmhMsg = new OAIPMHtype(reqType);
		oaipmhMsg.setIdentify(identify);
		logger.info("br5");
		Writer writer = new StringWriter();

		try {
			// IBindingFactory identifierFactory =
			// BindingDirectory.getFactory(OaiIdentifierType.class);
			IBindingFactory oaipmhFactory = BindingDirectory.getFactory(OAIPMHtype.class);

			// marshal object back out to document in memory
			IMarshallingContext mctx = oaipmhFactory.createMarshallingContext();
			mctx.setIndent(2);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			mctx.setOutput(bos, "UTF-8");
			mctx.marshalDocument(oaipmhMsg, "UTF-8", null, writer);

		} catch (JiBXException e) {

			System.err.println(e.getLocalizedMessage() + e);
		}
		System.out.println(writer.toString());

		return writer.toString();
	}
}
