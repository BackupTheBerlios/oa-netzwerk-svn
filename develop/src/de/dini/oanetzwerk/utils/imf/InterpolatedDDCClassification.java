package de.dini.oanetzwerk.utils.imf;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InterpolatedDDCClassification extends Classification  implements ClassificationInterface {
	
    private double probability;
	
	public InterpolatedDDCClassification(String value) {
		super(value);
		//setSplitValue();
	}
	
	public String toString() {
		String result = "Interpolated DDC Classification, value=" + this.value + " probability=" + this.probability;
		return result;
	}
	
	public InterpolatedDDCClassification() {
		
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
		
}
