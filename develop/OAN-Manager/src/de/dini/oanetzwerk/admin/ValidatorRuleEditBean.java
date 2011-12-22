package de.dini.oanetzwerk.admin;

import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validator.constants.FieldNames;
import gr.uoa.di.validator.jobs.JobListener;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import de.dini.oanetzwerk.admin.utils.AbstractBean;


@ManagedBean(name="vali_rule_edit")
@RequestScoped
public class ValidatorRuleEditBean extends AbstractBean implements Serializable, JobListener{
	
	FacesContext ctx = FacesContext.getCurrentInstance();

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 1L;
	
	private int ruleId;
	private String ruleName;
	private String ruleDescription;
	private String ruleType;
	private boolean ruleMandatory;
	private char ruleSuccess;
	private int ruleSuccessN;
	private int ruleWeight;
	private String ruleProviderInformation;
	private String ruleVerb;
	private String ruleField;
	private boolean ruleVisible;
	private String ruleJobType;
	
	private String ruleKey1;
	private String ruleKey2;
	
	@PostConstruct
	public void init(){
		ruleFill();
//		ruleShow();
	}
	
	//Methode zum Befüllen der rules_view
	public void ruleFill(){
		try{
			Validator v = APIStandalone.getValidator();
			HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
			String ruleId = request.getParameter("rid");
			
			SgParameters s = v.getRule(ruleId);
			
			//Regelid
			setRuleId(new Integer(s.getParamByName(FieldNames.RULE_ID)));
			//Regelname
			setRuleName(s.getParamByName(FieldNames.RULE_NAME));
			//Jobtyp
			setRuleJobType(s.getParamByName(FieldNames.RULE_JOBTYPE));
			//Feld und Verb
			String pr = s.getParamByName(FieldNames.RULE_PROVIDER_INFORMATION);
			int i = 0;
			int index = 0;
			char a;
			while(i <= pr.length() -1 ){
				a = pr.charAt(i);
				if(a == ','){
					index = i;
				}
				i++;
			}
			if(index == 0){
				setRuleField(s.getParamByName(FieldNames.RULE_PROVIDER_INFORMATION));
			} else{
				setRuleVerb(s.getParamByName(FieldNames.RULE_PROVIDER_INFORMATION).substring(0, index));
				setRuleField(s.getParamByName(FieldNames.RULE_PROVIDER_INFORMATION).substring(index+2));
			}
			//Regeltyp
			setRuleType(s.getParamByName(FieldNames.RULE_TYPE));
			//Regeltyp - Zusaetze
			if(s.getParamByName(FieldNames.RULE_TYPE).equalsIgnoreCase("Cardinality")){
				setRuleKey1(s.getParamByName(FieldNames.RULE_CARDINALITY_GREATERTHAN));
				setRuleKey2(s.getParamByName(FieldNames.RULE_CARDINALITY_LESSTHAN));	
			}
			if(s.getParamByName(FieldNames.RULE_TYPE).equalsIgnoreCase("Vocabulary")){
				setRuleKey1(s.getParamByName(FieldNames.RULE_VOCABULARY_WORDS));	
			}
			if(s.getParamByName(FieldNames.RULE_TYPE).equalsIgnoreCase("Regular Expression")){
				setRuleKey1(s.getParamByName(FieldNames.RULE_REGULAREXPRESSION_REGEXPR));	
			}
			if(s.getParamByName(FieldNames.RULE_TYPE).equalsIgnoreCase("Retrievable Resource")){
				setRuleKey1(s.getParamByName(FieldNames.RULE_RETRIEVERESOURCE_MAXDEPTH));
				setRuleKey2(s.getParamByName(FieldNames.RULE_RETRIEVERESOURCE_MIMETYPES));	
			}
			if(s.getParamByName(FieldNames.RULE_TYPE).equalsIgnoreCase("Not Confused Fields")){
				setRuleKey1(s.getParamByName(FieldNames.RULE_NOTCONNFUSEDFIELDS_FIELD1));
				setRuleKey2(s.getParamByName(FieldNames.RULE_NOTCONNFUSEDFIELDS_FIELD2));	
			}
			//Verpflichtend
			if(s.getParamByName(FieldNames.RULE_MANDATORY).equalsIgnoreCase("1")){		
				setRuleMandatory(true);
			} else{
				setRuleMandatory(false);
			}
			//Erfolg bei
			if(s.getParamByName(FieldNames.RULE_SUCCESS).charAt(0) == '>'){
				setRuleSuccess('>');
				setRuleSuccessN(new Integer(s.getParamByName(FieldNames.RULE_SUCCESS).substring(1)));
			}
			if(s.getParamByName(FieldNames.RULE_SUCCESS).charAt(0) == 'a'){
				setRuleSuccess('a');
			}
			if(s.getParamByName(FieldNames.RULE_SUCCESS).charAt(0) != 'a' & s.getParamByName(FieldNames.RULE_SUCCESS).charAt(0) != '>'){
				setRuleSuccess('=');
				setRuleSuccessN(new Integer(s.getParamByName(FieldNames.RULE_SUCCESS).substring(1)));
			}
			//Wichtung
			setRuleWeight(new Integer(s.getParamByName(FieldNames.RULE_WEIGHT)));
//			if(s.getParamByName(FieldNames.RULE_WEIGHT).charAt(0) == '1'){
//				setRuleWeight('1');
//			}
//			if(s.getParamByName(FieldNames.RULE_WEIGHT).charAt(0) == '2'){
//				setRuleWeight('2');
//			}
//			if(s.getParamByName(FieldNames.RULE_WEIGHT).charAt(0) == '3'){
//				setRuleWeight('3');
//			}
//			if(s.getParamByName(FieldNames.RULE_WEIGHT).charAt(0) == '4'){
//				setRuleWeight('4');
//			}
//			if(s.getParamByName(FieldNames.RULE_WEIGHT).charAt(0) == '5'){
//				setRuleWeight('5');
//			}
			//Sichtbarkeit
			if(s.getParamByName(FieldNames.RULE_VISIBLE).equalsIgnoreCase("1")){
				setRuleVisible(true);
			} else{
				setRuleVisible(false);
			}
			setRuleDescription(s.getParamByName(FieldNames.RULE_DESCRIPTION));
	
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void ruleEdit(){
		try{
			Validator v = APIStandalone.getValidator();
//			String[] str = {"1", "a", "0", "", "1", "OAI Usage Validation"};
			String[] str = {"41", "name", "descr", "Field Exists", "true", "a"};
			v.editRule("41", "Field Exists", "OAI Usage Validation", str);
//			v.editRule(41, "abcd", str);
			System.out.println("!!!!!!!!!!!!!");
			
//			String man;
//			String vis;
//			if(ruleMandatory == true){
//				man = "1";
//			} else{
//				man = "0";
//			}
//			if(ruleVisible == true){
//				vis = "1";
//			} else{
//				vis = "0";
//			}
//			String rpv = "";
//			if(ruleJobType.equalsIgnoreCase("OAI Usage Validation")){
//				rpv = ruleVerb + ", "+ ruleField;		
//			} else{
//				rpv = ruleField;
//			}
//			String[] lst = {"" + ruleId, ruleName, ruleDescription, ruleType, man, "" + ruleSuccess, "" + ruleWeight, rpv, vis, ruleJobType};
//			v.editRule("" + ruleId, ruleName, ruleJobType, lst);	
//			System.out.println("!!!!!!!!!!!Bla!!!!!!!!!!!");
		} catch (Exception e){
			e.printStackTrace();
			System.out.print("!!!!!!!!!!!!!!!!!!!Blo!!!!!!!!!!!!!!!");
		}
	}
	
	public FacesContext getCtx() {
		return ctx;
	}

	public void setCtx(FacesContext ctx) {
		this.ctx = ctx;
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleDescription() {
		return ruleDescription;
	}

	public void setRuleDescription(String ruleDescription) {
		this.ruleDescription = ruleDescription;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public boolean isRuleMandatory() {
		return ruleMandatory;
	}

	public void setRuleMandatory(boolean ruleMandatory) {
		this.ruleMandatory = ruleMandatory;
	}

	public char getRuleSuccess() {
		return ruleSuccess;
	}

	public void setRuleSuccess(char ruleSuccess) {
		this.ruleSuccess = ruleSuccess;
	}

	public int getRuleSuccessN() {
		return ruleSuccessN;
	}

	public void setRuleSuccessN(int ruleSuccessN) {
		this.ruleSuccessN = ruleSuccessN;
	}

	public int getRuleWeight() {
		return ruleWeight;
	}

	public void setRuleWeight(int ruleWeight) {
		this.ruleWeight = ruleWeight;
	}

	public String getRuleProviderInformation() {
		return ruleProviderInformation;
	}

	public void setRuleProviderInformation(String ruleProviderInformation) {
		this.ruleProviderInformation = ruleProviderInformation;
	}

	public String getRuleVerb() {
		return ruleVerb;
	}

	public void setRuleVerb(String ruleVerb) {
		this.ruleVerb = ruleVerb;
	}

	public String getRuleField() {
		return ruleField;
	}

	public void setRuleField(String ruleField) {
		this.ruleField = ruleField;
	}

	public boolean isRuleVisible() {
		return ruleVisible;
	}

	public void setRuleVisible(boolean ruleVisible) {
		this.ruleVisible = ruleVisible;
	}

	public String getRuleJobType() {
		return ruleJobType;
	}

	public void setRuleJobType(String ruleJobType) {
		this.ruleJobType = ruleJobType;
	}

	public String getRuleKey1() {
		return ruleKey1;
	}

	public void setRuleKey1(String ruleKey1) {
		this.ruleKey1 = ruleKey1;
	}
	
	public String getRuleKey2() {
		return ruleKey2;
	}

	public void setRuleKey2(String ruleKey2) {
		this.ruleKey2 = ruleKey2;
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
