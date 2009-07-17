package de.dini.oanetzwerk.userfrontend;

import java.util.Comparator;

import de.dini.oanetzwerk.utils.imf.DuplicateProbability;

public class DuplicateProbabilityComparator implements Comparator<DuplicateProbability> {

	private boolean asc;
	
	public DuplicateProbabilityComparator(boolean ascending) {
		asc = ascending;
	}

	public int compare(DuplicateProbability o1, DuplicateProbability o2) {
		int value = 0;
		
		if(o1.getProbability() < o2.getProbability()) {
			if(asc) {
				value = -1;
			} else {
				value = 1;
			}
		} else if(o1.getProbability() > o2.getProbability()) {
			if(asc) {
				value = 1;
			} else {
				value = -1;
			}
		} else {
			if(o1.getReverseProbability() < o2.getReverseProbability()) {
				if(asc) {
					value = -1;
				} else {
					value = 1;
				}
			} else if(o1.getReverseProbability() > o2.getReverseProbability()) {
				if(asc) {
					value = 1;
				} else {
					value = -1;
				}
			}
		}
		
		return value;
	}
	
}
