package de.dini.oanetzwerk.servicemodule.aggregator;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

// kann als Interface nicht im JAXBContext registriert werden

@XmlJavaTypeAdapter(Classification.Adapter.class)
public interface ClassificationInterface {
	public String getValue();
	public void setValue(String value);
}
