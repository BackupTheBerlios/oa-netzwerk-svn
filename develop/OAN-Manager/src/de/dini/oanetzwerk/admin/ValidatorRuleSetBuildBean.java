package de.dini.oanetzwerk.admin;

import gr.uoa.di.validator.api.Validator;
import gr.uoa.di.validator.api.standalone.APIStandalone;
import gr.uoa.di.validator.jobs.JobListener;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import de.dini.oanetzwerk.admin.utils.AbstractBean;


@ManagedBean(name="vali_rset2")
@SessionScoped
public class ValidatorRuleSetBuildBean extends AbstractBean implements Serializable, JobListener{
	
	private String ruleSetName = "";
	private String[] ruleSetList = {};

	private static final long serialVersionUID = 1L;
	
	FacesContext ctx = FacesContext.getCurrentInstance();
	
	@PostConstruct
	public void init() {
		rulesOfRuleSet();
	}
	
	public void stop(){
		String[] a = {};
		setRuleSetList(a);
		setRuleSetName("");
	}
	
	public void rulesOfRuleSet(){
		try{
			Validator v = APIStandalone.getValidator();
			HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
			String r = request.getParameter("rid");
			int rN = Integer.parseInt(r);
			List<String> idl = v.getRuleSetRuleIds(rN);
			setRuleSetName(v.getRuleSetName(rN));
			String[] rst = new String[idl.size()];
			
			int i = 0;
			for(String s : idl){
				rst[i] = s;
				i++;
			}
			setRuleSetList(rst);			
			} catch (Exception e){
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!nicht geklappt!");
				e.printStackTrace();
			}	
	}
	
	public int getRid(){
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		String r = request.getParameter("rid");
		int rN = Integer.parseInt(r);
		return rN;
	}
	
	
	
	public boolean inRuleSet(String s){
		try{
		switch(ruleSetList.length){
		case 0: 
			return false;
		case 1: 
			if(ruleSetList[0].equalsIgnoreCase(s)){
			return true;
		} else{
			return false;
		}
		default: 
			int i = 0;
			boolean aus = false;
			while(i<=ruleSetList.length-1){
				if(ruleSetList[i].equalsIgnoreCase(s)){
					aus = true;
				}
				i++;
			}
			return aus;
		}
		} catch(Exception e){
			System.out.println("!!!!!!!!!222!!!!!!!!!!!!!!!!!!!!nicht geklappt!");
			e.printStackTrace();
			return true;		
		}
	}
	
	public void ruleSetAdd(){
		try{
			printList(ruleSetList);
			Validator v = APIStandalone.getValidator();	
			v.createRuleSet(getRuleSetName(), getRuleSetList());
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
		if(a.length == 0 | a.length == 1){
			String[] a1 = {};
			return a1;
		} else{
		String[] a1 = new String[a.length-1];
		int i = 0;
		while(i <= a.length -1){
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
			ruleSetList = addToList(ruleSetList, s);
			printList(ruleSetList);
	}
	
	public void delete(String s){
		ruleSetList = deleteFromList(ruleSetList, s);
		printList(ruleSetList);
	}

	public String getRuleSetName() {
		return ruleSetName;
	}

	public void setRuleSetName(String ruleSetName) {
		this.ruleSetName = ruleSetName;
	}

	public String[] getRuleSetList() {
		return ruleSetList;
	}

	public void setRuleSetList(String[] ruleSetList) {
		this.ruleSetList = ruleSetList;
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
