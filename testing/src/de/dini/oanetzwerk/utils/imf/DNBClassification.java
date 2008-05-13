package de.dini.oanetzwerk.utils.imf;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DNBClassification extends Classification  implements ClassificationInterface {
	
	public DNBClassification(String value) {
		super(value);
		setSplitValue();
	}

	public String toString() {
		String result = "DNB Classification, value=" + this.value;
		return result;
	}

	public DNBClassification() {
		
	}
}
