package de.dini.oanetzwerk.oaipmh;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

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
public class ListIdentifiers extends AbstractOAIPMHVerb {

	private static Logger logger = Logger.getLogger(ListIdentifiers.class);

	private static final String ID_PREFIX = "oai:oanet.de:";
	
	private static final String ERROR_DATE_INVALID = "The format of the 'from' and/or 'until' parameter is not valid!";

	private static final String ERROR_UNTIL_BEFORE_FROM = "The 'until' value must not be before the 'from' value!";

	private String metadataPrefix;

	private String from;

	private String until;

	private String set;

	private String resumptionToken = "";
	private BigInteger completeListSize = BigInteger.valueOf(0);
	private BigInteger resumptionTokenCursor = BigInteger.valueOf(0);
	private boolean resumptionTokenExpired = false;
	private boolean resumptionTokenInvalid = false;
	private ResumptionToken token = null;

	public String processRequest(Map<String, String[]> parameter) {

		if (parameter.size() < 2) {
			return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT).toString();
		}
		if (parameter.containsKey("resumptionToken")) {

			this.resumptionToken = parameter.get("resumptionToken")[0];
			if (parameter.size() > 2) {
				return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, 
				"The 'resumptionToken' parameter must not be combined with other parameters!").toString();
			}
		} else if (!parameter.containsKey("metadataPrefix")) {
	
			return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, 
					"The required argument 'metadataPrefix' is missing.").toString();
		} else if (parameter.get("metadataPrefix").length > 1) {
			return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, "Multiple 'metadataPrefix' declarations are not supported!").toString(); 
			
		} else if (!parameter.get("metadataPrefix")[0].equalsIgnoreCase("oai_dc")) {
			return new OAIPMHError(OAIPMHErrorcodeType.CANNOT_DISSEMINATE_FORMAT, 
					"The metadata format '" + parameter.get("metadataPrefix")[0] + "' given by metadataPrefix is not supported by the item or this repository!").toString();
		} else {
			this.setMetadataPrefix(parameter.get("metadataPrefix")[0]);
		}

		if (parameter.containsKey("from"))
			this.setFrom(parameter.get("from")[0]);

		if (parameter.containsKey("until"))
			this.setUntil(parameter.get("until")[0]);

		if (parameter.containsKey("set")) {
			this.setSet(parameter.get("set")[0]);
		
			if (StringUtils.isNotEmpty(this.getSet()) && !this.getSet().startsWith("ddc:") && !this.getSet().startsWith("dnb:") && !this.getSet().startsWith("pub-type:"))
			{
				return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, 
				"The specified set '" + this.getSet() + "' is not supported by this service!").toString();
			}
		}
		RequestType reqType = new RequestType();

		ListIdentifiersType listIdents = new ListIdentifiersType();
		ArrayList<HeaderType> headers = null;
		
		try {
			
			headers = this.getHeaders2();
			
			// check for resumptionToken errors
			if (resumptionTokenExpired) {
				return new OAIPMHError(OAIPMHErrorcodeType.BAD_RESUMPTION_TOKEN, 
				"The specified resumption token expired!").toString();
			} else if (resumptionTokenInvalid) {
				return new OAIPMHError(OAIPMHErrorcodeType.BAD_RESUMPTION_TOKEN, 
				"The specified resumption token is not valid!").toString();
			}
		} catch (IllegalArgumentException e) {
			System.out.println("error: " + e.getMessage());
			return new OAIPMHError(OAIPMHErrorcodeType.BAD_ARGUMENT, ERROR_UNTIL_BEFORE_FROM.equals(e.getMessage()) 
					? e.getMessage() : ERROR_DATE_INVALID).toString();
		}

		
		if (headers.size() == 0)
			return new OAIPMHError(OAIPMHErrorcodeType.NO_RECORDS_MATCH).toString();

		listIdents.getHeaders().addAll(headers);

		if (this.resumptionToken != null && token != null) {

			ResumptionTokenType resToType = new ResumptionTokenType();

			resToType.setExpirationDate(token.getExpirationDate());
			resToType.setValue(token.getId());
			resToType.setCompleteListSize(token.getCompleteListSize());
			resToType.setCursor(token.getResumptionTokenCursor());

			listIdents.setResumptionToken(resToType);
		}

		OAIPMHtype oaipmhMsg = new OAIPMHtype(reqType);
		reqType.setVerb(VerbType.LIST_IDENTIFIERS);

		if (!this.getMetadataPrefix().equals(""))
			reqType.setMetadataPrefix(this.getMetadataPrefix());

		if (!this.getFrom().equals(""))
			reqType.setFrom(this.getFrom());

		if (!this.getUntil().equals(""))
			reqType.setUntil(this.getUntil());

		if (!this.getSet().equals(""))
			reqType.setSet(this.getSet());

		if (parameter.containsKey("resumptionToken"))
			reqType.setResumptionToken(parameter.get("resumptionToken")[0]);

		// if (parameter.containsKey ("metadataPrefix"))
		// reqType.setMetadataPrefix (parameter.get ("metadataPrefix") [0]);

		oaipmhMsg.setListIdentifiers(listIdents);

		Writer w = new StringWriter();

		try {
			IBindingFactory oaipmhFactory = BindingDirectory.getFactory(OAIPMHtype.class);
			IMarshallingContext mctx = oaipmhFactory.createMarshallingContext();
			mctx.setIndent(2);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			mctx.setOutput(bos, "UTF-8");
			mctx.marshalDocument(oaipmhMsg, "UTF-8", false, w);

			return w.toString();
		} catch (JiBXException e) {

			System.err.println(e.getLocalizedMessage() + e);
		}

		return w.toString();
	}


	private ArrayList<HeaderType> getHeaders2(){

		LinkedList<Record> recordList;

		BigInteger idOffset = BigInteger.valueOf(0);

		if (this.resumptionToken.equals("")) {
			
			//query for completeListSize

		} else {

			logger.info("Resumption token: " + this.resumptionToken);

			ResumptionToken token = (ResumptionToken) ResumptionTokenManager.loadResumptionToken(this.resumptionToken);
			
			if (token == null || token.getExpirationDate() == null) {
				// invalid resumptionToken
				this.resumptionTokenInvalid = true;
				return null;
			}	
			

//			logger.info("ResumptionToken: now: " + new Date());
//			logger.info("ResumptionToken: now: " + token.getExpirationDate());
			
			if (token.getExpirationDate().before(new Date())) {
				// expired resumptionToken
				this.resumptionTokenExpired = true;
				return null;
			}
			
			idOffset = token.getIdOffset();
			this.resumptionTokenCursor = token.getResumptionTokenCursor().add(BigInteger.valueOf(1));
			this.completeListSize = token.getCompleteListSize();
			
			HashMap<String, Object> map = token.getParameters();
			this.from 	= (String) map.get("from");
			this.until	= (String) map.get("until");
			this.set	= (String) map.get("set");
			this.metadataPrefix	= (String) map.get("metadataPrefix");
//			System.out.println(from);
//			System.out.println(until);
//			System.out.println(set);
//			System.out.println(metadataPrefix);
		}

		
		DataConnection dataConnection = this.dataConnectionToolkit.createDataConnection();

		// actual query
		long start = System.currentTimeMillis();
		recordList = dataConnection.getIdentifierList(this.getFrom(), this.getUntil(), this.getSet(), idOffset, getMaxResults());
		logger.info("ListIdentifiers DB-Query time ... " + Long.toString(System.currentTimeMillis() - start));
		logger.info("List size : " + recordList.size());

		if (recordList.size() == 0) {
			return new ArrayList<HeaderType>();
		}
		if (recordList.size() >= getMaxResults()) {

			// create new resumption token
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("from", from);
			map.put("until", until);
			map.put("set", set);
			map.put("metadataPrefix", metadataPrefix);
//			System.out.println(recordList.get(recordList.size() - 1).getHeader().getIdentifier());
			idOffset = BigInteger.valueOf(Integer.parseInt(
					recordList.get(recordList.size() - 1).getHeader().getIdentifier()));
			
			this.resumptionToken = ResumptionTokenManager.createNewResumptionToken();
			
			// store resumption token
			token = ResumptionTokenManager.storeResumptionToken(this.resumptionToken, idOffset, completeListSize, resumptionTokenCursor, map);

		} else {
			
			this.resumptionToken = null;
		}
		
		// create results

		this.completeListSize = BigInteger.valueOf(recordList.size());
		ArrayList<HeaderType> headers = new ArrayList<HeaderType>();

		HeaderType header;
		
		for (Record record : recordList) {

			header = new HeaderType();
			header.setIdentifier(ID_PREFIX + record.getHeader().getIdentifier());
			header.setDatestamp(record.getHeader().getDatestamp());

			for (String set : record.getHeader().getSet()) {

				header.getSetSpecs().add(set);
			}

			headers.add(header);
		}

		return headers;
	}

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
