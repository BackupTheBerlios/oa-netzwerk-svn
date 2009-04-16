package de.dini.oanetzwerk.codec;

import java.util.HashMap;
import java.util.Iterator;

public class RestEntrySet {

	private HashMap<String,String> entryHashMap;
	
	public RestEntrySet() {
		entryHashMap = new HashMap<String, String>();
	}

	public HashMap<String, String> getEntryHashMap() {
		return entryHashMap;
	}

	public void setEntryHashMap(HashMap<String, String> restDataEntries) {
		this.entryHashMap = restDataEntries;
	}
	
	public void addEntry(String key, String value) {
		this.entryHashMap.put(key,value);
	}
	
	public String getValue(String key) {
		return this.entryHashMap.get(key);
	}
	
	public Iterator<String> getKeyIterator() {
		return entryHashMap.keySet().iterator();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = this.getKeyIterator();
		if(!it.hasNext()) sb.append("empty RestEntrySet, no key-value pairs");
		while(it.hasNext()) {
			String key = it.next();
			sb.append("key=");
			sb.append(key);
			sb.append(" value=");
			sb.append(this.getValue(key));
			if(it.hasNext()) sb.append(" | ");
		}
		return sb.toString();
	}
	
}
