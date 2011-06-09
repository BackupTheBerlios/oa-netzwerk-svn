package de.dini.oanetzwerk.admin;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

@ManagedBean(name = "language")
@SessionScoped
public class LanguageSwitcherBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LanguageSwitcherBean.class);
	
	private String localeCode;
	private Locale locale;
	
	private static Map<String, Object> language;
	
	static {
		language = new LinkedHashMap<String, Object>();
		language.put("Deutsch", Locale.GERMAN); 
		language.put("English", Locale.ENGLISH);
	}

	public Map<String, Object> getCountriesInMap() {
		return language;
	}

	public void countryLocaleCodeChanged(ValueChangeEvent e) {
		
		String newLocaleValue = e.getNewValue().toString();

		for (Map.Entry<String, Object> entry : language.entrySet()) {
			if (entry.getValue().toString().equals(newLocaleValue)) {
				FacesContext.getCurrentInstance().getViewRoot().setLocale((Locale) entry.getValue());
				locale = (Locale) entry.getValue();
			}
		}

	}

	public Locale getLocale() {
		logger.debug("locale: " + locale);
		return locale == null ? FacesContext.getCurrentInstance().getViewRoot().getLocale() : locale;
	}
	
	public String getLocaleCode() {
		return localeCode;
	}

	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}
}
