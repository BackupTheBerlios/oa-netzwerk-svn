package de.dini.oanetzwerk.admin;

import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validator.constants.FieldNames;
import gr.uoa.di.validator.jobs.JobListener;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import de.dini.oanetzwerk.admin.utils.AbstractBean;


@ManagedBean(name="vali_rule")
@RequestScoped
public class ValidatorRuleBuildBean extends AbstractBean implements Serializable, JobListener{
	
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

	
	FacesContext ctx = FacesContext.getCurrentInstance();

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 1L;	

	public void ruleBuild(){
		try{
			Validator v = APIStandalone.getValidator();
			List<String> i = v.getAllRuleIds();
			int a = i.size() +1;
			
			SgParameters params = new SgParameters();
			params.addParam(FieldNames.RULE_ID, "" + ruleId);
			params.addParam(FieldNames.RULE_NAME, ruleName);
			params.addParam(FieldNames.RULE_JOBTYPE, ruleJobType);
			
			if(ruleJobType.equals("OAI Usage Validation")){
				params.addParam(FieldNames.RULE_PROVIDER_INFORMATION, ruleVerb + ", "+ ruleField);
			}else{
				params.addParam(FieldNames.RULE_PROVIDER_INFORMATION, ruleField);
			}
			
			params.addParam(FieldNames.RULE_TYPE, ruleType);
		
			if(ruleType.equals("Cardinality")){
				params.addParam(FieldNames.RULE_CARDINALITY_LESSTHAN, ruleKey1);
				params.addParam(FieldNames.RULE_CARDINALITY_GREATERTHAN, ruleKey2);
			}
			if(ruleType.equals("Vocabulary")){
				params.addParam(FieldNames.RULE_VOCABULARY_WORDS, ruleKey1);
			}
			if(ruleType.equals("Regular Expression")){
				params.addParam(FieldNames.RULE_REGULAREXPRESSION_REGEXPR, ruleKey1);
			}
			if(ruleType.equals("Retrievable Resource")){
				params.addParam(FieldNames.RULE_RETRIEVERESOURCE_MAXDEPTH, ruleKey1);
				params.addParam(FieldNames.RULE_RETRIEVERESOURCE_MIMETYPES, ruleKey2);
			}
			if(ruleType.equals("Not Confused Fields")){
				params.addParam(FieldNames.RULE_NOTCONNFUSEDFIELDS_FIELD1, ruleKey1);
				params.addParam(FieldNames.RULE_NOTCONNFUSEDFIELDS_FIELD2, ruleKey2);
			}		

			if(ruleMandatory == true){
			params.addParam(FieldNames.RULE_MANDATORY, "" + 1);
			} else{
				params.addParam(FieldNames.RULE_MANDATORY, "" + 0);	
			}		
			switch(ruleSuccess){
			case 'a': params.addParam(FieldNames.RULE_SUCCESS, "a"); break;
			case '>': params.addParam(FieldNames.RULE_SUCCESS, ">" + ruleSuccessN); break;
			case '=': params.addParam(FieldNames.RULE_SUCCESS, "" + ruleSuccessN); break;
			}
			params.addParam(FieldNames.RULE_WEIGHT, "" + ruleWeight);
			if(ruleVisible == true){
				params.addParam(FieldNames.RULE_VISIBLE, "" + 1);
				} else{
					params.addParam(FieldNames.RULE_VISIBLE, "" + 0);
				}
			params.addParam(FieldNames.RULE_DESCRIPTION, ruleDescription);
			
			v.addNewRule(params);
			} catch (Exception e){
				e.printStackTrace();
			}
	}
	
	public void ruleDestroy(){
		try{
		ruleName = "";
		ruleDescription="";
		ruleType = "Vocabulary";
		ruleMandatory = true;
		ruleSuccess = '>';
		ruleSuccessN = 0;
		ruleWeight = 1;
		ruleVerb = "";
		ruleField = "";
		ruleVisible=true;
		ruleJobType = "OAI Usage Validation";
		ruleKey1 = "";
		ruleKey2 = "";
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
//	public void ruleDelete(){
//		try{
//			Validator v = APIStandalone.getValidator();
//			v.deleteRule("" + ruleId);
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//	

	public int getId(){
		try{
			Validator v = APIStandalone.getValidator();
			List<String> i = v.getAllRuleIds();
			int a = i.size() +1;
			setRuleId(a);
			return a;
			} catch (Exception e){
				e.printStackTrace();
				return 0;
			}		
	}
		
	public List<String> getRules(){
		try{
			Validator v = APIStandalone.getValidator();
			List<String> i = v.getAllRules();
			return i;		
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
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

	public boolean getRuleMandatory() {
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

	public boolean getRuleVisible() {
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

	
	
	@Override
	public void done(int arg0, double arg1) {
		System.out.println("Job with id " + arg0 + " finished successfully ");
	}

	@Override
	public void failed(int arg0, Exception arg1) {

		System.out.println("job failed with id " + arg0);
	}
	
	

}
