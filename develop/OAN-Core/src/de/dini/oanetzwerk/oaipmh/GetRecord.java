package de.dini.oanetzwerk.oaipmh;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

import de.dini.oanetzwerk.oaipmh.oaidc.OAIDCType;

/**
 * @author Sammy David
 * @author Michael Kühn
 * 
 */

public class GetRecord extends AbstractOAIPMHVerb {

	private static Logger logger = Logger.getLogger(GetRecord.class);


	public String processRequest(Map<String, String[]> parameter) {

		String errorMsg = checkForBadArguments(parameter);
		if (StringUtils.isNotEmpty(errorMsg)) {
			return errorMsg;
		}
		
		if (parameter.size() != 3 || !parameter.containsKey("identifier") || !parameter.containsKey("metadataPrefix"))
			return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, "One or more required arguments are missing!").toString();

		if (!parameter.get("metadataPrefix")[0].equalsIgnoreCase("oai_dc"))
			return new OAIPMHError(OAIPMHErrorcodeType.CANNOT_DISSEMINATE_FORMAT, 
					"The metadata format '" + parameter.get("metadataPrefix")[0] + "' given by metadataPrefix is not supported by the item or this repository!").toString();

		DataConnection dataConnection = this.dataConnectionToolkit.createDataConnection();

		if (parameter.get("identifier")[0] == null ) { // || !parameter.get("identifier")[0].startsWith("oai:oanet.de")
			return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, "The specified identifier is not valid!").toString();
		}
		
		if (!dataConnection.existsRepositoryIdentifier(parameter.get("identifier")[0]))
			return new OAIPMHError(OAIPMHErrorcodeType.ID_DOES_NOT_EXIST).toString();

		GetRecordType getRecord = new GetRecordType();
		RecordType record = new RecordType();
		HeaderType header = new HeaderType();

		record.setHeader(header);
		
		
		/* TODO: 
		 * - language mapping to iso codes
		 * - duplicate subjects 
		 * - english subjects
		 * 
		 */
		MetadataType metadata = new MetadataType();
		
		OAIDCType oaidctype = new OAIDCType();
		
		String id = parameter.get("identifier")[0]; 
		System.out.println("ID1 " + id);
		final String identifier = dataConnection.getInternalIdentifier(id).toString();
		System.out.println("ID2 " + identifier);

		header.setIdentifier(id);
		header.setDatestamp(dataConnection.getDateStamp(identifier));

		for (String classification : dataConnection.getClassifications(identifier)) {
			header.getSetSpecs().add(classification);
		}
		
		for (String title : dataConnection.getTitles(identifier))
			oaidctype.getTitle().add(title);

		for (String creator : dataConnection.getCreators(identifier))
			oaidctype.getCreator().add(creator);

		for (String subject : dataConnection.getSubjects(identifier))
			oaidctype.getSubject().add(subject);

		for (String description : dataConnection.getDescriptions(identifier))
			oaidctype.getDescription().add(description);

		for (String publisher : dataConnection.getPublishers(identifier))
			oaidctype.getPublisher().add(publisher);

		for (String date : dataConnection.getDates(identifier))
			oaidctype.getDate().add(date);

		int i = 0;
		for (String type : dataConnection.getTypes(identifier)) {
			System.out.println("Type:  " + type);
			if (i == 0)
			{
				oaidctype.getType().add(DriverCompliance.getTypeForString(type));
				oaidctype.getType().add(type);
			}
			i++;
		}
		
		
		
		for (String format : dataConnection.getFormats(identifier))
			oaidctype.getFormat().add(format);

		for (String identif : dataConnection.getIdentifiers(identifier))
			oaidctype.getIdentifier().add(identif);

		
		
		for (String language : dataConnection.getLanguages(identifier)) {
			oaidctype.getLanguage().add(language);
//			System.out.println("LANG:" + language);
		}

		metadata.setAny(oaidctype);
		record.setMetadata(metadata);
		getRecord.setRecord(record);

		RequestType reqType = new RequestType();
		reqType.setVerb(VerbType.GET_RECORD);
		reqType.setIdentifier(id);
		reqType.setMetadataPrefix(parameter.get("metadataPrefix")[0]);
		OAIPMHtype oaipmhMsg = new OAIPMHtype(reqType);
		oaipmhMsg.setGetRecord(getRecord);

		Writer w = new StringWriter();

		try {
			IBindingFactory oaipmhFactory = BindingDirectory.getFactory(OAIPMHtype.class);
			IMarshallingContext mctx = oaipmhFactory.createMarshallingContext();
			mctx.setIndent(2);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			mctx.setOutput(bos, "UTF-8");
			mctx.marshalDocument(oaipmhMsg, "UTF-8", null, w);

			return w.toString();
		} catch (JiBXException e) {

			logger.warn(e.getMessage() + e);
			System.err.println(e.getLocalizedMessage() + e);
		}

		return null;
	}

}
