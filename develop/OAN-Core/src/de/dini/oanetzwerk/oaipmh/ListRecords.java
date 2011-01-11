package de.dini.oanetzwerk.oaipmh;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
 */

public class ListRecords extends AbstractOAIPMHVerb {

	private static Logger logger = Logger.getLogger(ListRecords.class);

	private static final String ERROR_MISSING_METADATAPREFIX = "Sorry! The 'metadataPrefix' parameter is missing!";

	private static final String ERROR_DATE_INVALID = "The format of the 'from'/'until' parameter is not valid!";

	private static final String ERROR_UNTIL_BEFORE_FROM = "The 'until' value must not be before the 'from' value!";

	private String metadataPrefix;

	private String from;

	private String until;

	private String set;

	private BigInteger completeListSize;

	private String resumptionToken = "";
	private BigInteger resumptionTokenCursor = BigInteger.valueOf(0);
	private boolean resumptionTokenExpired = false;
	private boolean resumptionTokenInvalid = false;
	private ResumptionToken token = null;

	public String processRequest(Map<String, String[]> parameter) {

		String errorMsg = checkForBadArguments(parameter);
		if (StringUtils.isNotEmpty(errorMsg)) {
			return errorMsg;
		}

		if (parameter.size() < 2) {

			return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, ERROR_MISSING_METADATAPREFIX).toString();
		}

		// minimum requirement: verb and at least metadataPrefix or
		// resumptionToken
		if (parameter.containsKey("resumptionToken")) {

			this.resumptionToken = parameter.get("resumptionToken")[0];

		} else if (!parameter.containsKey("metadataPrefix")) {

			return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, ERROR_MISSING_METADATAPREFIX).toString();
		}

		// check if unsupported metadata type is requested
		else if (!parameter.get("metadataPrefix")[0].equalsIgnoreCase("oai_dc")) {

			return new OAIPMHError(OAIPMHErrorcodeType.CANNOT_DISSEMINATE_FORMAT).toString();
		}

		// set oai_dc as metadataPrefix
		else {
			this.setMetadataPrefix(parameter.get("metadataPrefix")[0]);
		}

		// check if optional parameters have been supplied
		if (parameter.containsKey("from")) {
			this.setFrom(parameter.get("from")[0]);
		}
		if (parameter.containsKey("until")) {
			this.setUntil(parameter.get("until")[0]);
		}
		if (parameter.containsKey("set")) {
			this.setSet(parameter.get("set")[0]);

			if (StringUtils.isNotEmpty(this.getSet()) && !this.getSet().startsWith("ddc:") && !this.getSet().startsWith("dnb:")
					&& !this.getSet().startsWith("pub-type:") && !this.getSet().endsWith("_OAN")) {
				return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, "The specified set '" + this.getSet()
						+ "' is not supported by this service!").toString();
			}
		}

		// retrieve requested results

		ArrayList<RecordType> records = null;

		try {

			records = this.getRecords();

			// check for resumptionToken errors
			if (resumptionTokenExpired) {
				return new OAIPMHError(OAIPMHErrorcodeType.BAD_RESUMPTION_TOKEN, "The specified resumption token expired!").toString();
			} else if (resumptionTokenInvalid) {
				return new OAIPMHError(OAIPMHErrorcodeType.BAD_RESUMPTION_TOKEN, "The specified resumption token is not valid!").toString();
			}
		} catch (IllegalArgumentException e) {
			System.out.println("error: " + e.getMessage());
			return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, ERROR_UNTIL_BEFORE_FROM.equals(e.getMessage()) ? e.getMessage()
					: ERROR_DATE_INVALID).toString();
		}


		if (records.size() == 0) {
			return new OAIPMHError(OAIPMHErrorcodeType.NO_RECORDS_MATCH).toString();
		}

		ListRecordsType listRecord = new ListRecordsType();
		listRecord.getRecords().addAll(records);

		if (this.resumptionToken != null && token != null) {

			ResumptionTokenType resToType = new ResumptionTokenType();
			
			if (this.resumptionTokenCursor.intValue() + getMaxResults() >= this.completeListSize.intValue()) {
				
				resToType.setCompleteListSize(token.getCompleteListSize());
				resToType.setCursor(this.resumptionTokenCursor);
			} else {
				
				resToType.setExpirationDate(token.getExpirationDate());
				resToType.setValue(token.getId());
				resToType.setCompleteListSize(token.getCompleteListSize());
				resToType.setCursor(token.getResumptionTokenCursor());	
			}
			listRecord.setResumptionToken(resToType);
		}

		RequestType reqType = new RequestType();
		OAIPMHtype oaipmhMsg = new OAIPMHtype(reqType);
		reqType.setVerb(VerbType.LIST_RECORDS);

		// set request parameters for response
		if (StringUtils.isNotEmpty(this.getMetadataPrefix()))
			reqType.setMetadataPrefix(this.getMetadataPrefix());

		if (StringUtils.isNotEmpty(this.getFrom()))
			reqType.setFrom(this.getFrom());

		if (StringUtils.isNotEmpty(this.getUntil()))
			reqType.setUntil(this.getUntil());

		if (StringUtils.isNotEmpty(this.getSet()))
			reqType.setSet(this.getSet());

		if (parameter.containsKey("resumptionToken"))
			reqType.setResumptionToken(parameter.get("resumptionToken")[0]);

		oaipmhMsg.setListRecords(listRecord);

		Writer w = new StringWriter();

		// create xml response
		try {
			IBindingFactory oaipmhFactory = BindingDirectory.getFactory(OAIPMHtype.class);
			IMarshallingContext mctx = oaipmhFactory.createMarshallingContext();
			mctx.setIndent(2);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			mctx.setOutput(bos, "UTF-8");
			mctx.marshalDocument(oaipmhMsg, "UTF-8", false, w);

			return w.toString();
		} catch (JiBXException e) {

			logger.error(e.getLocalizedMessage() + e);
		}

		return w.toString();
	}

	/**
	 * @return
	 */
	private ArrayList<RecordType> getRecords() {

		LinkedList<Record> recordList;
		BigInteger idOffset = BigInteger.valueOf(0);
		DataConnection dataConnection = this.dataConnectionToolkit.createDataConnection();

		if (this.resumptionToken.equals("")) {

			// query for completeListSize
			this.completeListSize = BigInteger.valueOf(dataConnection.getRecordListSize(this.getFrom(), this.getUntil(), this.getSet()));

		} else {

			// load resumptiontoken if resumptiontoken parameter specified
			logger.debug("resumption token: " + this.resumptionToken);

			token = (ResumptionToken) ResumptionTokenManager.loadResumptionToken(this.resumptionToken);

			if (token == null) {
				// invalid resumptionToken
				this.resumptionTokenInvalid = true;
				return null;
			}

			if (token.getExpirationDate().before(new Date())) {
				// expired resumptionToken
				this.resumptionTokenExpired = true;
				return null;
			}

			idOffset = token.getIdOffset();

			HashMap<String, Object> map = token.getParameters();
			this.from = (String) map.get("from");
			this.until = (String) map.get("until");
			this.set = (String) map.get("set");
			this.metadataPrefix = (String) map.get("metadataPrefix");
			this.completeListSize = token.getCompleteListSize();
			this.resumptionTokenCursor = token.getResumptionTokenCursor().add(BigInteger.valueOf(getMaxResults()));
		}

		// actual query
		long start = System.currentTimeMillis();
		recordList = dataConnection.getRecordList(this.getFrom(), this.getUntil(), this.getSet(), idOffset, getMaxResults());
		logger.info("ListRecords DB-Query time ... " + Long.toString(System.currentTimeMillis() - start));
		logger.info("List size : " + recordList.size());

		if (recordList.size() == 0) {
			return new ArrayList<RecordType>();
		}
		if (recordList.size() >= getMaxResults()) {

			// create new resumption token

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("from", from);
			map.put("until", until);
			map.put("set", set);
			map.put("metadataPrefix", metadataPrefix);
			// System.out.println(recordList.get(recordList.size() -
			// 1).getHeader().getIdentifier());
			idOffset = BigInteger.valueOf(recordList.get(recordList.size() - 1).getHeader().getInternalIdentifier().intValue());

			this.resumptionToken = ResumptionTokenManager.createNewResumptionToken();

			// store resumption token
			token = ResumptionTokenManager.storeResumptionToken(this.resumptionToken, idOffset, completeListSize, resumptionTokenCursor,
					map);

		}
		// else {
		//			
		// this.resumptionToken = null;
		// }

		// create results

		ArrayList<RecordType> records = new ArrayList<RecordType>();

		RecordType record;
		HeaderType header;
		MetadataType metadata;
		OAIDCType oaidctype;

		for (Record recordItem : recordList) {

			record = new RecordType();
			header = new HeaderType();
			metadata = new MetadataType();
			oaidctype = new OAIDCType();

			header.setIdentifier(recordItem.getHeader().getIdentifier()); // ID_PREFIX + 
			header.setDatestamp(recordItem.getHeader().getDatestamp());

			for (String set : recordItem.getHeader().getSet())
				header.getSetSpecs().add(set);

			record.setHeader(header);

			if (recordItem.getMetaData().getTitle().size() != 0)
				for (String title : recordItem.getMetaData().getTitle())
					oaidctype.getTitle().add(title);

			if (recordItem.getMetaData().getCreator().size() != 0)
				for (String creator : recordItem.getMetaData().getCreator())
					oaidctype.getCreator().add(creator);

			if (recordItem.getMetaData().getSubject().size() != 0)
				for (String subject : recordItem.getMetaData().getSubject())
					oaidctype.getSubject().add(subject);

			if (recordItem.getMetaData().getDescription().size() != 0)
				for (String description : recordItem.getMetaData().getDescription())
					oaidctype.getDescription().add(description);

			if (recordItem.getMetaData().getPublisher().size() != 0)
				for (String publisher : recordItem.getMetaData().getPublisher())
					oaidctype.getPublisher().add(publisher);

			if (recordItem.getMetaData().getDate().size() != 0)
				for (String date : recordItem.getMetaData().getDate())
					oaidctype.getDate().add(date);

			if (recordItem.getMetaData().getType().size() != 0) {
				int i = 0;
				for (String type : recordItem.getMetaData().getType()) {
					if (DriverCompliance.isDriverComplianceEnabled()) {
						
						if (i == 0) {
							oaidctype.getType().add(DriverCompliance.getDriverDCTypeForString(type));
							oaidctype.getType().add(type);
						}
						i++;
					} else {
						oaidctype.getType().add(type);
					}
				}
			}
			if (recordItem.getMetaData().getFormat().size() != 0)
				for (String format : recordItem.getMetaData().getFormat())
					oaidctype.getFormat().add(format);

			if (recordItem.getMetaData().getIdentifier().size() != 0)
				for (String identifier : recordItem.getMetaData().getIdentifier())
					oaidctype.getIdentifier().add(identifier);

			if (recordItem.getMetaData().getLanguage().size() != 0)
				for (String language : recordItem.getMetaData().getLanguage())
					oaidctype.getLanguage().add(language);

			metadata.setAny(oaidctype);
			record.setMetadata(metadata);

			records.add(record);
		}

		return records;
	}

	/******************************* Getter & Setter *******************************/

	/**
	 * @return the metadataPrefix
	 */

	public final String getMetadataPrefix() {

		if (this.metadataPrefix == null)
			this.metadataPrefix = "";

		return this.metadataPrefix;
	}

	/**
	 * @param metadataPrefix
	 *            the metadataPrefix to set
	 */

	public final void setMetadataPrefix(String metadataPrefix) {

		this.metadataPrefix = metadataPrefix;
	}

	/**
	 * @return the from
	 */

	public final String getFrom() {

		if (this.from == null)
			this.from = "";

		return this.from;
	}

	/**
	 * @param from
	 *            the from to set
	 */

	public final void setFrom(String from) {

		this.from = from;
	}

	/**
	 * @return the until
	 */

	public final String getUntil() {

		if (this.until == null)
			this.until = "";

		return this.until;
	}

	/**
	 * @param until
	 *            the until to set
	 */

	public final void setUntil(String until) {

		this.until = until;
	}

	/**
	 * @return the set
	 */

	public final String getSet() {

		if (this.set == null)
			this.set = "";

		return this.set;
	}

	/**
	 * @param set
	 *            the set to set
	 */

	public final void setSet(String set) {

		this.set = set;
	}
}
