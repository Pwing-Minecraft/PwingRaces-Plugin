package net.pwing.races.race.attribute;


public class RaceAttribute {

	private String attribute;
	private double value;
	private String requirement;

	public RaceAttribute(String attribute, double value, String requirement) {
		this.attribute = attribute;
		this.value = value;
		this.requirement = requirement;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getRequirement() {
		return requirement;
	}

	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}
}
