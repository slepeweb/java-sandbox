package com.slepeweb.site.geo.bean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class PasswordList {
	
	private List<Group> groups = new ArrayList<Group>();
	private String error;
	
	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	public String getError() {
		return error;
	}
	
	public boolean isError() {
		return StringUtils.isNotBlank(this.error);
	}

	public static class Group {
		
		private String category;
		private List<Account> accounts;
		private int order;
		
		@Override
		public String toString() {
			return this.category;
		}
		
		public String getCategory() {
			return category;
		}
		
		public void setCategory(String name) {
			this.category = name;
		}
		
		public List<Account> getAccounts() {
			return accounts;
		}
		
		public void setAccounts(List<Account> list) {
			this.accounts = list;
			this.accounts.sort(new Comparator<Account>() {

				@Override
				public int compare(Account o1, Account o2) {
					// TODO Auto-generated method stub
					return o1.getCompany().toLowerCase().compareTo(o2.getCompany().toLowerCase());
				}
				
			});
		}

		public int getOrder() {
			return order;
		}

		public void setOrder(int order) {
			this.order = order;
		}
	}
	
	public static class Account {
		private String company, login, password, website, notes;
		private boolean important;
	
		@Override
		public String toString() {
			return this.company;
		}
		
		public String getCompany() {
			return company;
		}
	
		public void setCompany(String company) {
			this.company = company;
		}
	
		public String getLogin() {
			return login;
		}
	
		public void setLogin(String login) {
			this.login = login;
		}
	
		public String getPassword() {
			return password;
		}
	
		public void setPassword(String password) {
			this.password = password;
		}
	
		public String getWebsite() {
			return website;
		}
	
		public void setWebsite(String website) {
			this.website = website;
		}
	
		public String getNotes() {
			return notes;
		}
	
		public void setNotes(String notes) {
			this.notes = notes;
		}

		public boolean isImportant() {
			return important;
		}

		public void setImportant(boolean important) {
			this.important = important;
		}
	}
}
