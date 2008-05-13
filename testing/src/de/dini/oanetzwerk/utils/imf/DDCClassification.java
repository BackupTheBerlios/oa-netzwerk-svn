package de.dini.oanetzwerk.utils.imf;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DDCClassification extends Classification  implements ClassificationInterface {
	public DDCClassification(String value) {
		super(value);
		setSplitValue();
	}
	
	public String toString() {
		String result = "DDC Classification, value=" + this.value;
		return result;
	}
	
	public DDCClassification() {
		
	}
}
