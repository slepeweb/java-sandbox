package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

public class DashboardAccountGroup {
	private String type;
	private List<Account> accounts;
	
	public DashboardAccountGroup(String type) {
		this.type = type;
		this.accounts = new ArrayList<Account>();
	}
	
	public long getTotal() {
		long t = 0;
		for (Account a : this.accounts) {
			t += a.getBalance();
		}
		return t;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String accountType) {
		this.type = accountType;
	}
	
	public List<Account> getAccounts() {
		return accounts;
	}
}
