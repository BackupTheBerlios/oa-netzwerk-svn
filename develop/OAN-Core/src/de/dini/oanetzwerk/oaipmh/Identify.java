package de.dini.oanetzwerk.oaipmh;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
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
import de.dini.oanetzwerk.server.database.sybase.SelectFromDBSybase;

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
		

		IdentifyType identify = new IdentifyType();
		identify.setBaseURL(getOaipmhProperties().getProperty("baseUrl"));
		
		DataConnection dataConnection = this.dataConnectionToolkit.createDataConnection();
		identify.setEarliestDatestamp(dataConnection.getEarliestDataStamp());
		identify.setGranularity(GranularityType.YYYYMMDD);
		identify.setProtocolVersion(ProtocolVersionType._20);
		identify.setRepositoryName(getOaipmhProperties().getProperty("name"));
		
		// set admin emails
		String adminEmails = getOaipmhProperties().getProperty("adminEmails");
		List<String> emails = new ArrayList<String>();
		
		if (adminEmails != null && adminEmails.length() > 0) {
			if (adminEmails.contains(";")) {
				String[] temp = adminEmails.split(";");
				emails = Arrays.asList(temp);
			}
			else {
				emails.add(adminEmails);
			}
		}
		
		identify.setAdminEmails(emails);
		
		// set deleted record type
		String deletedRecords = getOaipmhProperties().getProperty("deletedRecords");
		identify.setDeletedRecord(DeletedRecordType.convert(deletedRecords));

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
