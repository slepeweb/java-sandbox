package com.slepeweb.money.bean;

import com.slepeweb.money.Util;

public class Account extends Payee {
	
	private static long DFLT_ACCOUNT = 20l;
	
	private long openingBalance = 0L;
	private boolean closed;
	private String note, type;
	private long reconciled;
	private long balance;
	
	public void assimilate(Object obj) {
		if (obj instanceof Account) {
			super.assimilate(obj);
			
			Account a = (Account) obj;
			setBalance(a.getBalance()).
			setOpeningBalance(a.getOpeningBalance()).
			setClosed(a.isClosed()).
			setNote(a.getNote()).
			setType(a.getType()).
			setReconciled(a.getReconciled());
		}
	}
	
	// TODO: the idea is to configure the default using a spring bean
	protected void setDefault(long id) {
		DFLT_ACCOUNT = id;
	}
	
	public static long getDefault() {
		return DFLT_ACCOUNT;
	}
		
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && accountEquals((Account) obj);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (openingBalance ^ (openingBalance >>> 32));
		result = prime * result + (int) (balance ^ (balance >>> 32));
		result = prime * result + (closed ? 1231 : 1237);
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + (int) (reconciled ^ (reconciled >>> 32));
		return result;
	}

	private boolean accountEquals(Account a) {
		if (balance != a.getBalance()) {
			return false;
		}
		
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

		if (type == null) {
			if (a.getType() != null)
				return false;
		} else if (!type.equals(a.getType()))
			return false;
		
		if (reconciled != a.getReconciled()) {
			return false;
		}
		
		return true;
	}
	
	public long credit(long amount) {
		this.balance += amount;
		return this.balance;
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

	public Account setBalance(long balance) {
		this.balance = balance;
		return this;
	}

	public String getType() {
		return type;
	}

	public Account setType(String type) {
		this.type = type;
		return this;
	}

	public long getReconciled() {
		return reconciled;
	}

	public Account setReconciled(long reconciled) {
		this.reconciled = reconciled;
		return this;
	}
}
