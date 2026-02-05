package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

import com.slepeweb.common.bean.MoneyDashboard;
import com.slepeweb.money.Util;

public class Dashboard {
	private List<DashboardAccountGroup> groups;
	
	public Dashboard() {
		this.groups = new ArrayList<DashboardAccountGroup>();
		
		String[] types = {"current", "credit", "service", "savings", "pension"};
		for (String type : types) {
			this.groups.add(new DashboardAccountGroup(type));
		}
	}
	
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
	
	public long getTotal() {
		long t = 0;
		for (DashboardAccountGroup g : this.groups) {
			t += g.getTotal();
		}
		return t;
	}
	
	public MoneyDashboard adapt() {
		MoneyDashboard adapted = new MoneyDashboard().
				setTotal(Util.formatPounds(getTotal()));
		
		MoneyDashboard.Group mdg;
		MoneyDashboard.Account mda;
		
		for (DashboardAccountGroup dag : getGroups()) {
			mdg = adapted.findGroup(dag.getType());
			if (mdg == null) {
				mdg = new MoneyDashboard.Group().
						setName(dag.getType()).
						setTotal(Util.formatPounds(dag.getTotal()));
				
				adapted.getGroups().add(mdg);
			}
			
			for (Account a : dag.getAccounts()) {
				mda = new MoneyDashboard.Account().
						setName(a.getName()).
						setBalance(a.getBalanceStr()).
						setNotes(a.getNote());
				
				mdg.getAccounts().add(mda);
			}
		}
		
		return adapted;
	}
}
