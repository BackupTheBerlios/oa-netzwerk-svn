package de.dini.oanetzwerk.userfrontend;

import java.util.ArrayList;
import java.util.List;

public class DDCNaviNode {

	private String strDDCValue;
	private String strNameDE;
	private long longItemCount;
	private List<DDCNaviNode> listSubnodes;
	
	public DDCNaviNode() {
		listSubnodes = new ArrayList<DDCNaviNode>();
	}

	public String getStrDDCValue() {
		return strDDCValue;
	}

	public void setStrDDCValue(String strDDCValue) {
		this.strDDCValue = strDDCValue;
	}

	public long getLongItemCount() {
		return longItemCount;
	}

	public void setLongItemCount(long longItemCount) {
		this.longItemCount = longItemCount;
	}

	public List<DDCNaviNode> getListSubnodes() {
		return listSubnodes;
	}

	public void setListSubnodes(List<DDCNaviNode> listSubnodes) {
		this.listSubnodes = listSubnodes;
	}

	public String getStrNameDE() {
		return strNameDE;
	}

	public void setStrNameDE(String strNameDE) {
		this.strNameDE = strNameDE;
	}
	
}
