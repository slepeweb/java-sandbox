package com.slepeweb.common.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class MoneyDashboard {
	private List<Group> groups = new ArrayList<Group>();
	private String total, error;
	
	public Group findGroup(String name) {
		for (Group g : this.groups) {
			if (g.getName().equals(name)) {
				return g;
			}
		}		
		return null;
	}
	
	public List<Group> getGroups() {
		return groups;
	}

	public MoneyDashboard setGroups(List<Group> groups) {
		this.groups = groups;
		return this;
	}

	public String getTotal() {
		return total;
	}

	public MoneyDashboard setTotal(String total) {
		this.total = total;
		return this;
	}

	public String getError() {
		return error;
	}

	public MoneyDashboard setError(String error) {
		this.error = error;
		return this;
	}

	public boolean isError() {
		return StringUtils.isNotBlank(this.error);
	}
	
	public static class Group {
		private String name;
		private List<Account> accounts = new ArrayList<Account>();
		private String total;
		
		public Group() {}
		
		public Group(String s) {
			this.name = s;
		}
		
		public String getName() {
			return name;
		}
		
		public Group setName(String name) {
			this.name = name;
			return this;
		}
		
		public List<Account> getAccounts() {
			return accounts;
		}
		
		public Group setAccounts(List<Account> accounts) {
			this.accounts = accounts;
			return this;
		}
		
		public String getTotal() {
			return total;
		}
		
		public Group setTotal(String total) {
			this.total = total;
			return this;
		}
	}
	
	public static class Account {
		private String name, balance, notes;
		
		public Account() {}
		
		public Account(String name, String balance) {
			this.name = name;
			this.balance = balance;
		}

		public String getName() {
			return name;
		}

		public String getBalance() {
			return balance;
		}

		public Account setName(String name) {
			this.name = name;
			return this;
		}

		public Account setBalance(String balance) {
			this.balance = balance;
			return this;
		}

		public String getNotes() {
			return notes;
		}

		public Account setNotes(String notes) {
			this.notes = notes;
			return this;
		}
	}
}
