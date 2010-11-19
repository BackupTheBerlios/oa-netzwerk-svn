package de.dini.oanetzwerk.oaipmh;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Sammy David
 *
 */
public class ResumptionToken implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id = "";

	private BigInteger idOffset;

	private Date expirationDate;
	
	private BigInteger completeListSize;

	private BigInteger resumptionTokenCursor;

	private HashMap<String, Object> parameters;

	public ResumptionToken(String id, BigInteger idOffset, Date expirationDate,
			BigInteger completeListSize, BigInteger resumptionTokenCursor,
			HashMap<String, Object> parameters) {
		super();
		this.id = id;
		this.idOffset = idOffset;
		this.expirationDate = expirationDate;
		this.completeListSize = completeListSize;
		this.resumptionTokenCursor = resumptionTokenCursor;
		this.parameters = parameters;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigInteger getIdOffset() {
		return idOffset;
	}

	public void setIdOffset(BigInteger idOffset) {
		this.idOffset = idOffset;
	}
	
	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public BigInteger getCompleteListSize() {
		return completeListSize;
	}

	public void setCompleteListSize(BigInteger completeListSize) {
		this.completeListSize = completeListSize;
	}

	public BigInteger getResumptionTokenCursor() {
		return resumptionTokenCursor;
	}

	public void setResumptionTokenCursor(BigInteger resumptionTokenCursor) {
		this.resumptionTokenCursor = resumptionTokenCursor;
	}

	public HashMap<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}

}
