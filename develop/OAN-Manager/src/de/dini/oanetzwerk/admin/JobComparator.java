package de.dini.oanetzwerk.admin;



import gr.uoa.di.validatorweb.actions.browsejobs.Job;

import java.util.Comparator;

public class JobComparator implements Comparator<Job> {

	@Override
	public int compare(Job j1, Job j2) {
		if (j1.getId() == null && j2.getId() == null) {
			return 0;
		}

		int int1 = Integer.parseInt(j1.getId());
		int int2 = Integer.parseInt(j2.getId());
		
		return new Integer(int1).compareTo(int2);
	}
}
