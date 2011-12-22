package de.dini.oanetzwerk.admin;

public class Rulest {

	private String id = "";
	private String name = "";
	private String rules = "";
	private boolean visible;
	private String[] ruleList;
	
	
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRules() {
		return rules;
	}
	public void setRules(String rules) {
		this.rules = rules;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public String[] getRuleList() {
		return ruleList;
	}
	public void setRuleList(String[] ruleList) {
		this.ruleList = ruleList;
	}

}
