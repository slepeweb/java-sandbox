package com.slepeweb.cms.bean;

import com.slepeweb.cms.bean.guidance.IGuidance;

public class LinkNameOption {
	private String name;
	private IGuidance guidance;
	
	public LinkNameOption(String a, IGuidance b) {
		this.name = a;
		this.guidance = b;
	}

	public String getName() {
		return name;
	}

	public IGuidance getGuidance() {
		return guidance;
	}
}
