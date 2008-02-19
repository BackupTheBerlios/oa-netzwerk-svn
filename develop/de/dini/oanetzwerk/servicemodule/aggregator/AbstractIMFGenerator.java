package de.dini.oanetzwerk.servicemodule.aggregator;

import de.dini.oanetzwerk.utils.imf.InternalMetadata;

abstract class AbstractIMFGenerator {

	protected String xmlData;
	protected InternalMetadata im;
	
	public AbstractIMFGenerator() {
		this.im = new InternalMetadata();
		this.xmlData = null;
	}
	
	public abstract InternalMetadata generateIMF(String data);
	
}
