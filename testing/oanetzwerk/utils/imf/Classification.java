package de.dini.oanetzwerk.utils.imf;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public abstract class Classification implements ClassificationInterface {
	
	String value;
	
	public static boolean isDDC(String testvalue) {
		if (testvalue.toLowerCase().startsWith("ddc:")) return true;
		else 
			return false;
	}

	public static boolean isDNB(String testvalue) {
		if (testvalue.toLowerCase().startsWith("dnb:")) return true;
		else 
			return false;
	}
	
	public static boolean isDINISet(String testvalue) {
		if (testvalue.toLowerCase().startsWith("pub-type:")) return true;
		else 
			return false;
	}
	
	public static boolean isOther(String testvalue) {
		if (!(isDNB(testvalue)) && !(isDDC(testvalue)) && !(isDINISet(testvalue))) {
			return true;
		} else
			return false;
	}
	
	public void setSplitValue() {
		String[] temp = this.value.split(":");
		if (temp.length < 2) {
			// Fehler, keine korrekte DNB-Codierung bzw. kein Wert
			// ÜBergangsweise wird der gesamte Eintrag eingestellt
//			this.value = value;
		} else {
			this.value = value.split(":")[1];
		}
	}

	
	public Classification(String value) {
		this.value = value;
	}
	
	public Classification() {}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	static class Adapter extends XmlAdapter<Classification,ClassificationInterface> {
		public ClassificationInterface unmarshal(Classification v) {
			System.out.println("unmarshal(" + v +")");
			return v;
		}
	    public Classification marshal(ClassificationInterface v) {
			System.out.println("marshal(" + v +")");
	    	return (Classification)v;
	    }
	}
}