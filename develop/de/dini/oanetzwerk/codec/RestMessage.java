package de.dini.oanetzwerk.codec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RestMessage {

	private RestKeyword keyword;
	private String restURL;
	private List<RestEntrySet> listEntrySets;
	private RestStatusEnum status;
	private String statusDescription;
	
	public RestMessage() {
		listEntrySets = new ArrayList<RestEntrySet>();
		status = RestStatusEnum.OK;
		statusDescription = "";
		keyword = RestKeyword.UNKNOWN;
		restURL = "";
	}

	/**
	 * @param restKeyword
	 */
	
	public RestMessage (RestKeyword restKeyword) {

		this.listEntrySets = new ArrayList<RestEntrySet>();
		this.status = RestStatusEnum.OK;
		this.statusDescription = "";
		this.keyword = restKeyword;
		this.restURL = "";
	}

	public List<RestEntrySet> getListEntrySets() {
		return listEntrySets;
	}

	public void setListEntrySets(List<RestEntrySet> entrySets) {
		this.listEntrySets = entrySets;
	}

	public RestKeyword getKeyword() {
		return keyword;
	}

	public void setKeyword(RestKeyword keyword) {
		this.keyword = keyword;
	}

	public String getRestURL() {
		return restURL;
	}

	public void setRestURL(String restURL) {
		this.restURL = restURL;
	}

	public RestStatusEnum getStatus() {
		return status;
	}

	public void setStatus(RestStatusEnum status) {
		this.status = status;
	}
	
	public void addEntrySet(RestEntrySet entrySet) {
		this.listEntrySets.add(entrySet);
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("RestMessage[\n");
		sb.append("  status:");
		sb.append(this.status.toString());
		if(statusDescription != null && statusDescription.length() > 0) {
			sb.append("(description:");
			sb.append(this.statusDescription);
			sb.append(")");
		}		
		sb.append("\n  keyword:");
		sb.append(this.keyword.toString());
		if(restURL != null && restURL.length() > 0) {
			sb.append("(url:");
			sb.append(this.restURL);
			sb.append(")");
		}
		sb.append("\n  listEntrySets[\n");
		for(RestEntrySet entrySet : listEntrySets) {
			sb.append("    entrySet[\n");
			Iterator<String> it = entrySet.getKeyIterator();
			while(it.hasNext()) {
				String key = it.next();
				sb.append("      [key:");
				sb.append(key);
				sb.append(",value:");
				sb.append(entrySet.getValue(key));
				sb.append("]\n");
			}
			sb.append("    ]\n");
		}
		sb.append("  ]\n]");
		return sb.toString();		
	}
	
}
