package de.dini.oanetzwerk.servicemodule.aggregator;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

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
