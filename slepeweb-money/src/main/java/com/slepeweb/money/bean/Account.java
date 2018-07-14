package com.slepeweb.money.bean;

public class Account extends Payee {
	
	private long openingBalance = 0L;
	private boolean closed;
	private String note;
	
	public void assimilate(Object obj) {
		if (obj instanceof Account) {
			Account a = (Account) obj;
			setName(a.getName());
			setOpeningBalance(a.getOpeningBalance()).
			setClosed(a.isClosed()).
			setNote(a.getNote());
		}
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
	public Account setName(String s) {
		super.setName(s);
		return this;
	}
}
