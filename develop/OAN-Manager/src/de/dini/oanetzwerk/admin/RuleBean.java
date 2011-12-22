package de.dini.oanetzwerk.admin;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.dini.oanetzwerk.admin.utils.AbstractBean;


@ManagedBean(name = "rule")
@RequestScoped
public class RuleBean extends AbstractBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Rule rule;
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Rule getRule() {
		return rule;
	}
	public void setRule(Rule rule) {
		this.rule = rule;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
