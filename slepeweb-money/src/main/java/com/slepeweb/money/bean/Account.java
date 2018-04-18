package com.slepeweb.money.bean;

public class Account extends Payee {
	
	public void assimilate(Object obj) {
		if (obj instanceof Account) {
			Account f = (Account) obj;
			setName(f.getName());
		}
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
