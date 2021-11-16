package com.slepeweb.cms.bean;

public class LinkNameOption {
	private String name, guidance;
	
	public LinkNameOption(String a, String b) {
		this.name = a;
		this.guidance = b;
	}

	public String getName() {
		return name;
	}

	public String getGuidance() {
		return guidance;
	}
}
