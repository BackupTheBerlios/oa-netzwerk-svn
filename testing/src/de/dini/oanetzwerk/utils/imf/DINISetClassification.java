package de.dini.oanetzwerk.utils.imf;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DINISetClassification extends Classification  implements ClassificationInterface {
	
	public DINISetClassification(String value) {
		super(value);
		setSplitValue();
	}
	
	public String toString() {
		String result = "DINISet Classification, value=" + this.value;
		return result;
	}
	
	public DINISetClassification() {
		
	}
}
