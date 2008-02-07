package de.dini.oanetzwerk.servicemodule.aggregator;

public abstract class  Classification {
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
}