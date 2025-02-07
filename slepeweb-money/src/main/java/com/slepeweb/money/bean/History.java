package com.slepeweb.money.bean;

import java.sql.Timestamp;

import com.slepeweb.money.Util;

/*
 * This class records a) the last transaction that was updated, and b) the last account that was
 * selected, or c) the account associated with a). These objects will be barely populated, because
 * all we need to track are:
 * 
 * 		transaction:	'entered' & 'account' properties
 * 		account:		'id' property
 * 
 * Any attempt to retrieve more information than these objects will fail.
 */
public class History {
	
	private Account lastAccount;
	private Transaction lastTransaction;
	
	public History() {
		Account a = new Account().setName("");
		Timestamp now = Util.now();
		Transaction t = new Transaction().setEntered(now).setAccount(a);
		this.lastAccount = a;
		this.lastTransaction = t;
	}
	
	public Account getLastAccount() {
		return lastAccount;
	}
	
	public void setLastAccount(Account lastAccount) {
		this.lastAccount = lastAccount;
	}
	
	public Transaction getLastTransaction() {
		return lastTransaction;
	}
	
	public void setLastTransaction(Transaction lastTransaction) {
		this.lastTransaction = lastTransaction;
		this.lastAccount = lastTransaction.getAccount();
	}
}
