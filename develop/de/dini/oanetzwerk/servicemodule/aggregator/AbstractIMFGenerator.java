package de.dini.oanetzwerk.servicemodule.aggregator;

abstract class AbstractIMFGenerator {

	protected String xmlData;
	protected InternalMetadata im;
	
	public AbstractIMFGenerator() {
		this.im = new InternalMetadata();
		this.xmlData = null;
	}
	
	public abstract InternalMetadata generateIMF(String data);
	
}
