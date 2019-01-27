package com.slepeweb.money.bean;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Transaction;

public class Transfer extends Transaction {

	private Account mirrorAccount;
	
	public Transfer() {}
	
	public Transfer(Transaction t) {
		assimilate(t);
		setId(t.getId());
		setOrigId(t.getOrigId());
	}
	
	@Override
	public void assimilate(Object obj) {
		if (obj instanceof Transaction) {
			Transaction t = (Transaction) obj;
			super.assimilate(t);
		}
		
		if (obj instanceof Transfer) {
			Transfer t = (Transfer) obj;
			setMirrorAccount(t.getMirrorAccount());
		}
	}
		
	public Account getMirrorAccount() {
		return mirrorAccount;
	}

	public Transfer setMirrorAccount(Account mirrorAccount) {
		this.mirrorAccount = mirrorAccount;
		return this;
	}
}
