package de.dini.oanetzwerk.admin;

import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validator.constants.FieldNames;
import gr.uoa.di.validator.jobs.JobListener;
import gr.uoa.di.validatorweb.actions.rulesets.RuleSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import de.dini.oanetzwerk.admin.utils.AbstractBean;


@ManagedBean(name="vali_rset")
@SessionScoped
public class ValidatorRuleSetBean extends AbstractBean implements Serializable, JobListener{														

	private static final long serialVersionUID = 1L;
	
	private String ruleSetId;
	private String ruleSetName;
	private String rulesInAString;
	private String rNew;
	private String rOld;
	private String[] ruleList = {};
	private String[] ruleListShow = {};
	private List<Rule> rulesOfRuleset;
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	
	@PostConstruct
	public void init() {
		ruleSetRem();
		ruleSetShow();
	}
	
	//Zum Anzeigen einer Regel in der Regeländerungsseite
	public int[] ruleSetView(){
		try{
			Validator v = APIStandalone.getValidator();
			HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
			int id = new Integer(request.getParameter("rid"));
			List<String> ruleIds = v.getRuleSetRuleIds(id);
			int[] r = new int[ruleIds.size()];
			int i = 0;
			for(String s : ruleIds){
				r[i] = new Integer(s);
				i++;
			}
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			return r;
			} catch (Exception e){
				return null;
			}	
	}
	
	//Zum Anzeigen der vorhandenen Regelsätze
	public List<Rulest> getRulesets(){
		try{
			List<Rulest> rst = new ArrayList<Rulest>();
			Rulest rst2;
			Validator v = APIStandalone.getValidator();
			List<RuleSet> rulesets = v.getAllRuleSets();
			for(RuleSet ruleset : rulesets){
				rst2 = new Rulest();
				rst2.setId("" + ruleset.getId());
				rst2.setName(ruleset.getName());
				rst2.setRules(listWriter(ruleset.getRuleIds()));
				rst.add(rst2);
			}
			return rst;
			} catch (Exception e){
				return null;
			}	
	}
	//interne Methode, die eine Liste aus Strings zu einem einzigen verbindet
	public String listWriter(List<String> a){
		String ausgabe = "";
		//List<String> a2 = a.subList(2,a.size()-1);
		for(String b : a){
			ausgabe = ausgabe + ", " + b;
			
		}
		return ausgabe.substring(2, ausgabe.length());	
	}

	//verwendet, um die zu einem Regelsatz gehörigen Regeln anzuzeigen
	public void ruleSetShow(){
		try{
			List<Rule> a = new ArrayList<Rule>();
			Validator v = APIStandalone.getValidator();
			HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
			String r = request.getParameter("rid");
			int rN = Integer.parseInt(r);
			Rule rule;
			SgParameters s;
			List<String> idl = v.getRuleSetRuleIds(rN);
			
			for(String id: idl){
				s = v.getRule(id);
				rule = new Rule();
				rule.setId(s.getParamByName(FieldNames.RULE_ID));
				rule.setName(s.getParamByName(FieldNames.RULE_NAME));
				rule.setJobType(s.getParamByName(FieldNames.RULE_JOBTYPE));
				rule.setDescription(s.getParamByName(FieldNames.RULE_DESCRIPTION));
				a.add(rule);
			}
			setRulesOfRuleset(a);
			
			} catch (Exception e){
				e.printStackTrace();
			}	
	}
	
	//Zurücksetzen eines Regelsatzes	
	public void stop(){
		String[] a = {};
		setRuleList(a);
		setRuleSetName("");
	}
	
	//Sortieren der Regelliste und Hinzufügen des Regelsatzes zur Datenbank
	public void ruleSetAdd(){
		try{
			printList(ruleList);
			Validator v = APIStandalone.getValidator();	
			
			int[] ruleListInt = new int[ruleList.length];
			int i = 0;
			
			while(i<=ruleList.length - 1){
				ruleListInt[i] = new Integer(ruleList[i]);
				i++;
			}		
			java.util.Arrays.sort(ruleListInt);		
			i = 0;			
			while(i<=ruleList.length - 1){
				ruleList[i] = "" + ruleListInt[i];
				i++;				
			}	
			v.createRuleSet(getRuleSetName(), getRuleList());
			String[] a = {};
			setRuleList(a);
			setRuleSetName("");
		} catch (Exception e){
			System.out.println("Pech gehabt.");	
		}	
	}
	
	public String[] addToList(String[] a, String b){
		System.out.println("neues element: " + b);
		
		String[] a1 = new String[a.length+1];
		int i = 0;
		while(i <= a.length -1){
			a1[i] = a[i];
			i++;
		}
		a1[i] = b;
		System.out.println("neue länge: " + a1.length );
		return a1;
	}
	
	public String[] deleteFromList(String[] a, String b){
		if(a.length == 0 || a.length == 1){
			String[] a1 = {};
			return a1;
		} else{
		String[] a1 = new String[a.length-1];
		int i = 0;
		while(i <= a.length-1){
			if(a[i].equalsIgnoreCase(b) == false){
				a1[i] = a[i];
			}
			i++;	
		}
		return a1;	
		}
	}
	
	public boolean inList(String[] a, String b){
		if(a.length == 0){
			return false;
		} else{
		int i = 0;
		boolean aus = false;
		System.out.println("break1");
		while(i <= a.length -1){
			System.out.println("break2");
			if(a[i].equalsIgnoreCase(b)){
				System.out.println("break3");
				aus = true;
			}
		i++;	
		}
		System.out.println("break4: " + aus);
		return aus;		
		}
	}
	
	public void printList(String[] a){
		if(a.length == 0){
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!leer");
		}
		int i = 0;
		while(i <= a.length-1){
			System.out.println(a[i]);
			System.out.println("!!!!!!!!!");
			i++;
		}	
	}
		
	public void add(String s){
			ruleList = addToList(ruleList, s);
			printList(ruleList);
	}
	
	public void delete(String s){
		ruleList = deleteFromList(ruleList, s);
		printList(ruleList);
	}
	
	
	//zur Darstellung in der Remove-Seite von Ruleset
	public void ruleSetRem(){
		try{
			Validator v = APIStandalone.getValidator();
			HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
			String ruleSetId = request.getParameter("rid");		
			setRuleSetId(ruleSetId);
			setRuleSetName(v.getRuleSetName(new Integer(ruleSetId)));		
     		setRulesInAString(listWriter(v.getRuleSetRuleIds(new Integer(ruleSetId))));
			
		} catch(Exception e){
		}	
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String getRuleSetName() {
		return ruleSetName;
	}

	public void setRuleSetName(String ruleSetName) {
		this.ruleSetName = ruleSetName;
	}

	public String getRuleSetId() {
		return ruleSetId;
	}

	public void setRuleSetId(String ruleSetId) {
		this.ruleSetId = ruleSetId;
	}

	public String getRNew() {
		return rNew;
	}

	public void setRNew(String rNew) {
		this.rNew = rNew;
	}

	public String getROld() {
		return rOld;
	}

	public void setROld(String rOld) {
		this.rOld = rOld;
	}
	
	public String[] getRuleList() {
		return ruleList;
	}

	public void setRuleList(String[] ruleList) {
		this.ruleList = ruleList;
	}
	
	public List<Rule> getRulesOfRuleset() {
		return rulesOfRuleset;
	}

	public void setRulesOfRuleset(List<Rule> rulesOfRuleset) {
		this.rulesOfRuleset = rulesOfRuleset;
	}
	
	public String getRulesInAString() {
		return rulesInAString;
	}

	public String[] getRuleListShow() {
		return ruleListShow;
	}

	public void setRuleListShow(String[] ruleListShow) {
		this.ruleListShow = ruleListShow;
	}

	public void setRulesInAString(String rulesInAString) {
		this.rulesInAString = rulesInAString;
	}

	public FacesContext getCtx() {
		return ctx;
	}

	public void setCtx(FacesContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void done(int arg0, double arg1) {
		System.out.println("Job with id " + arg0 + " finished successfully ");
	}

	@Override
	public void failed(int arg0, Exception arg1) {

		System.out.println("job failed with id " + arg0);
	}
	
}
