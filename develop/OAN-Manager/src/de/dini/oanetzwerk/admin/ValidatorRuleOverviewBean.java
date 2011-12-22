package de.dini.oanetzwerk.admin;

import gr.uoa.di.validator.api.SgParameters;
import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validator.constants.FieldNames;
import gr.uoa.di.validator.jobs.JobListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.HttpServletRequest;

import de.dini.oanetzwerk.admin.utils.AbstractBean;


@ManagedBean(name="vali_ro")
@RequestScoped
public class ValidatorRuleOverviewBean extends AbstractBean implements Serializable, JobListener{
														
	private static final long serialVersionUID = 1L;

	public List<Rule> getRules(){
		try{
			List<Rule> rules = new ArrayList<Rule>();
			Validator v = APIStandalone.getValidator();
			Rule rule;
			SgParameters s;
			int i = 1;
			
			List<String> idl = v.getAllRuleIds();
			
			for(String id: idl){
				s = v.getRule(id);
				rule = new Rule();
				rule.setId(s.getParamByName(FieldNames.RULE_ID));
				rule.setName(s.getParamByName(FieldNames.RULE_NAME));
				rule.setJobType(s.getParamByName(FieldNames.RULE_JOBTYPE));
				rule.setDescription(s.getParamByName(FieldNames.RULE_DESCRIPTION));
				rules.add(rule);
			}
			return rules;
			
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}	
	}
	
	public void deleteRule(String s){
		try{			
			Validator v = APIStandalone.getValidator();
			v.deleteRule(s);
			
			} catch (Exception e){
				e.printStackTrace();
			}	
		
		
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
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
