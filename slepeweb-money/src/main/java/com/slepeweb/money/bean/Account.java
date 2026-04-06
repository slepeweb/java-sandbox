package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.money.Util;

public class Account extends Payee {
	
	public static final String SAVINGS = "savings";
	public static final String CREDIT = "credit";
	public static final String CURRENT = "current";
	public static final String PENSION = "pension";
	public static final String SERVICE = "service";
	
	private static long DFLT_ACCOUNT = 20l;
	
	private long openingBalance = 0L;
	private boolean closed;
	private String note, type, sortCode, accountNo, rollNo;
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
			setSortCode(a.getSortCode()).
			setAccountNo(a.getAccountNo()).
			setRollNo(a.getRollNo()).
			setReconciled(a.getReconciled());
		}
	}
	
	// TODO: the idea is to configure the default using a spring bean
	protected void setDefault(long id) {
		DFLT_ACCOUNT = id;
	}
	
	public boolean isSavings() {
		return isType(SAVINGS);
	}
	
	public boolean isCredit() {
		return isType(CREDIT);
	}
	
	public boolean isCurrent() {
		return isType(CURRENT);
	}
	
	private boolean isType(String s) {
		return getType() != null && getType().equals(s);
	}
	
	public static long getDefault() {
		return DFLT_ACCOUNT;
	}
	
	public String getSummary() {
		List<String> summary = new ArrayList<String>();
		add2Summary(summary, getSortCode(), getAccountNo(), getRollNo(), getNote());
		return StringUtils.join(summary, " | ");
	}
	
	protected void add2Summary(List<String> summary, String... values) {
		for (String s : values) {
			if (StringUtils.isNotBlank(s)) {
				summary.add(s);
			}			
		}
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

	public String getSortCode() {
		return sortCode;
	}

	public Account setSortCode(String sortCode) {
		this.sortCode = sortCode;
		return this;
}

	public String getAccountNo() {
		return accountNo;
	}

	public Account setAccountNo(String accountNo) {
		this.accountNo = accountNo;
		return this;
	}
	
	public String getRollNo() {
		return rollNo;
	}

	public Account setRollNo(String rollNo) {
		this.rollNo = rollNo;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (accountNo == null) {
			if (other.accountNo != null)
				return false;
		} else if (!accountNo.equals(other.accountNo))
			return false;
		if (balance != other.balance)
			return false;
		if (closed != other.closed)
			return false;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		if (openingBalance != other.openingBalance)
			return false;
		if (reconciled != other.reconciled)
			return false;
		if (rollNo == null) {
			if (other.rollNo != null)
				return false;
		} else if (!rollNo.equals(other.rollNo))
			return false;
		if (sortCode == null) {
			if (other.sortCode != null)
				return false;
		} else if (!sortCode.equals(other.sortCode))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((accountNo == null) ? 0 : accountNo.hashCode());
		result = prime * result + (int) (balance ^ (balance >>> 32));
		result = prime * result + (closed ? 1231 : 1237);
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		result = prime * result + (int) (openingBalance ^ (openingBalance >>> 32));
		result = prime * result + (int) (reconciled ^ (reconciled >>> 32));
		result = prime * result + ((rollNo == null) ? 0 : rollNo.hashCode());
		result = prime * result + ((sortCode == null) ? 0 : sortCode.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

}
