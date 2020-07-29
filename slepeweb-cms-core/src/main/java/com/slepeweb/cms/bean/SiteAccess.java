package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.List;

public class SiteAccess {

	public final static String LOGIN_PATH = "/login";
	public final static String NOT_AUTHORISED_PATH = "/notauthorised";

	private List<AccessRule> rules = new ArrayList<AccessRule>();
	
	public boolean grantAccess(Item i, User u) {
		
		// The first matching rule applies - rules should be ordered from most specific to least.
		for (AccessRule rule : this.rules) {
			if (rule.matchesConstraints(i)) {
				if (! rule.givesAccess()) {
					// User does not have access UNLESS he has specified roles
					return rule.matchesRoles(u);
				}
				
				return true;
			}
		}
			
		return false; 
	}


	public List<AccessRule> getRules() {
		return rules;
	}

	public void setRules(List<AccessRule> list) {
		this.rules = list;
	}
}
