package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.List;

public class Ownership {
	private List<User> siteContributors = new ArrayList<User>();
	private User owner, contributor;
	
	public User getOwner() {
		return owner;
	}

	public User getContributor() {
		return contributor;
	}

	private Site site;
	
	public Ownership(Item i, User u) {
		this.site = i.getSite();
		this.contributor = u;
		this.owner = i.getOwner();
		this.siteContributors = this.site.getContributors();
	}

	public List<User> getSiteContributors() {
		return siteContributors;
	}

	public boolean isUpdateable() {
		if (this.siteContributors.size() > 1) {
			return 
				this.owner.getId().equals(this.contributor.getId()) ||
				this.contributor.hasRole(this.site.getId(), User.ADMIN);
		}
		
		return false;
	}
	
}
