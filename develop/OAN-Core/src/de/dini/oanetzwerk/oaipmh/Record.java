package de.dini.oanetzwerk.oaipmh;

import java.io.Serializable;

/**
 * @author Sammy David
 * @author Michael Kühn
 * 
 */

public class Record implements Serializable, Comparable<Record> {

	/**
	 * 
	 */

	private static final long serialVersionUID = -2411596695965731565L;

	/**
	 * 
	 */

	private DCHeader header;

	/**
	 * 
	 */

	private DCMetaData metaData;

	/**
	 * @return the header
	 */

	public final DCHeader getHeader() {

		if (this.header == null)
			this.header = new DCHeader();

		return this.header;
	}

	/**
	 * @return the metaData
	 */

	public final DCMetaData getMetaData() {

		if (this.metaData == null)
			this.metaData = new DCMetaData();

		return this.metaData;
	}

	@Override
	public int compareTo(Record record) {
		final int BEFORE = -1;
		final int EQUAL  = 0;
		final int AFTER  = 1;

		this.getHeader().getInternalIdentifier(); 
		
		if (this.getHeader().getInternalIdentifier() == record.getHeader().getInternalIdentifier())
			return EQUAL;

		if (this.getHeader().getInternalIdentifier() == null || record.getHeader().getInternalIdentifier() == null ) {
			return EQUAL;
		}
		
//		System.out.println("Comparing " + this.getHeader().getIdentifier() + ":" + this.getHeader().getInternalIdentifier() + "        " + record.getHeader().getIdentifier() + ":" + record.getHeader().getInternalIdentifier());
		if (this.getHeader().getInternalIdentifier().intValue() < record.getHeader().getInternalIdentifier().intValue())
			return BEFORE;
		if (this.getHeader().getInternalIdentifier().intValue() > record.getHeader().getInternalIdentifier().intValue())
			return AFTER;

		return 0;
	}
}