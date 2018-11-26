package com.slepeweb.money.bean;

import com.slepeweb.money.Util;

public class Account extends Payee {
	
	private long openingBalance = 0L;
	private boolean closed;
	private String note, type;
	private long balance;
	
	public void assimilate(Object obj) {
		if (obj instanceof Account) {
			super.assimilate(obj);
			
			Account a = (Account) obj;
			setOpeningBalance(a.getOpeningBalance()).
			setClosed(a.isClosed()).
			setNote(a.getNote());
		}
	}
		
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && accountEquals((Account) obj);
	}
	
	private boolean accountEquals(Account a) {
		if (closed != a.isClosed()) {
			return false;
		}
		
		if (openingBalance != a.getOpeningBalance()) {
			return false;
		}
		
		if (note == null) {
			if (a.getNote() != null)
				return false;
		} else if (!note.equals(a.getNote()))
			return false;
				
		/*
		 * MSAccess doesn't know about account types, so this is commented out,
		 * otherwise imports will always update accounts
		 * 
		if (type == null) {
			if (a.getType() != null)
				return false;
		} else if (!type.equals(a.getType()))
			return false;
		*/
		
		return true;
	}
	
	public long getOpeningBalance() {
		return openingBalance;
	}

	public Account setOpeningBalance(long balance) {
		this.openingBalance = balance;
		return this;
	}

	public boolean isClosed() {
		return closed;
	}

	public Account setClosed(boolean closed) {
		this.closed = closed;
		return this;
	}

	public String getNote() {
		return note;
	}

	public Account setNote(String note) {
		this.note = note;
		return this;
	}

	@Override
	public boolean isAccount() {
		return true;
	}
	
	@Override
	public Account setId(long id) {
		super.setId(id);
		return this;
	}
	
	@Override
	public Account setOrigId(long id) {
		super.setOrigId(id);
		return this;
	}
	
	@Override
	public Account setName(String s) {
		super.setName(s);
		return this;
	}

	public String getBalanceStr() {
		return Util.formatPounds(getBalance());
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	public String getType() {
		return type;
	}

	public Account setType(String type) {
		this.type = type;
		return this;
	}
}
