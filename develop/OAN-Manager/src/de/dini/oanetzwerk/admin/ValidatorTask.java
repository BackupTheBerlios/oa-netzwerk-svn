package de.dini.oanetzwerk.admin;

import gr.uoa.di.validator.api.Entry;

import java.util.List;

public class ValidatorTask {

	
	private Entry entry;
	private boolean showDetails;
	
	public ValidatorTask(Entry entry, boolean showDetails) {
		super();
		this.entry = entry;
		this.showDetails = showDetails;
	}

	public Entry getEntry() {
		return entry;
	}

	public void setEntry(Entry entry) {
		this.entry = entry;
	}
	
	public boolean isShowDetails() {
    	return showDetails;
    }

	public void setShowDetails(boolean showDetails) {
    	this.showDetails = showDetails;
    }

	public float getSuccessPercentage() {
		if (!entry.isHasErrors()) {
			return 100.0f;
		} 
		
		System.out.println(entry.getSuccesses());
		String[] successes = entry.getSuccesses().split("/");
		
		if (successes == null || successes.length < 2) {
			return 0.0f;
		}
		
		return (Float.parseFloat(successes[0]) * 100.0f) / Float.parseFloat(successes[1]); 
	}
	
	public String getColor() {
		float percentage = getSuccessPercentage();
		
		if (percentage > 85) {
			return "green";
		} else if (percentage < 20) {
			return "red";
		} else {
			return "yellow";
		}
	}

}
