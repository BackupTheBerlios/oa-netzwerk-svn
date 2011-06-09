package de.dini.oanetzwerk.validator;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<ValidationBean> v = new ArrayList<ValidationBean>();
		
		ValidationBean a = new ValidationBean();
		a.setDate("bla1");
		a.setOaiUrl("blo1");
		a.setState("bli1");
		
		ValidationBean b = new ValidationBean();
		b.setDate("bla2");
		b.setOaiUrl("blo2");
		b.setState("bli2");
		
		v.add(a);
		v.add(b);

		for(ValidationBean vali : v){
			System.out.println(vali.getDate());
		}
		
		
	}
	
	
	

}
