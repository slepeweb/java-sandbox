package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

public class Dashboard {
	private List<DashboardAccountGroup> groups = new ArrayList<DashboardAccountGroup>();
	
	public DashboardAccountGroup find(String type) {
		for (DashboardAccountGroup g : this.groups) {
			if (g.getType().equals(type)) {
				return g;
			}
		}
		
		return null;
	}
	
	public DashboardAccountGroup addIfMissing(String type) {
		DashboardAccountGroup g = find(type);
		if (g == null) {
			g = new DashboardAccountGroup(type);
			this.groups.add(g);
		}
		return g;
	}

	public List<DashboardAccountGroup> getGroups() {
		return groups;
	}
}
