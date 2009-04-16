package de.dini.oanetzwerk.utils.imf;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OtherClassification extends Classification implements ClassificationInterface {
	
	public OtherClassification() {
		
	}
	
	public OtherClassification(String value) {
		super(value);
	}
	
	public String toString() {
		String result = "Other Classification, value=" + this.value;
		return result;
	}
	
	/*static class Adapter extends XmlAdapter<OtherClassification,ClassificationInterface> {
		public ClassificationInterface unmarshal(OtherClassification v) {
			System.out.println("unmarshal(" + v +")");
			return v;
		}
	    public OtherClassification marshal(ClassificationInterface v) {
			System.out.println("marshal(" + v +")");
	    	return (OtherClassification)v;
	    }
	}*/
	
}
