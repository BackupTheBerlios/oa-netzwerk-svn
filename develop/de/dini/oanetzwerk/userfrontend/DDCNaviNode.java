package de.dini.oanetzwerk.userfrontend;

import java.util.ArrayList;
import java.util.List;

public class DDCNaviNode {

	private String strDDCValue;
	private String strNameDE;
	private long longItemCount;
	private long longItemSubCount;
	private List<DDCNaviNode> listSubnodes;
	private BrowseBean parentBrowseBean; 
	
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

	public long getLongItemSubCount() {
		return longItemSubCount;
	}

	public void setLongItemSubCount(long longItemSubCount) {
		this.longItemSubCount = longItemSubCount;
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
	
	public BrowseBean getParentBrowseBean() {
		return parentBrowseBean;
	}

	public void setParentBrowseBean(BrowseBean parentBrowseBean) {
		this.parentBrowseBean = parentBrowseBean;
	}

	
	///////////////////////////////////////////////////////////////////
	
	public boolean isInPath() {
		for(String item : parentBrowseBean.getPathDDCCategories()) if(item.equals(this.strDDCValue)) return true;
		return false;
	}

	public String actionSelectDDCCategoryLink() {
		this.parentBrowseBean.addDDCCategoryToPath(this.strDDCValue);
		return "ddc_category_selected";
	}
	

}
