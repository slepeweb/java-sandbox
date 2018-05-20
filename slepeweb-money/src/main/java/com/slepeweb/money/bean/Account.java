package com.slepeweb.money.bean;

public class Account extends Payee {
	
	private long balance = 0L;
	
	public void assimilate(Object obj) {
		if (obj instanceof Account) {
			Account a = (Account) obj;
			setName(a.getName());
			setBalance(a.getBalance());
		}
	}
		
	public long getBalance() {
		return balance;
	}

	public Account setBalance(long balance) {
		this.balance = balance;
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
