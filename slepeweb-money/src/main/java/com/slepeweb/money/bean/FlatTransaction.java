package com.slepeweb.money.bean;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;

import com.slepeweb.money.Util;

public class FlatTransaction {

	@Field("id") private String id;
	
	// Solr seems to be determined to map a 'pdate' field to a java.util.Date object
	@Field("entered") private java.util.Date entered;
	
	@Field("amount") private Long amount;
	@Field("account") private String account;
	@Field("payee") private String payee;
	@Field("major") private String majorCategory = "";
	@Field("minor") private String minorCategory = "";
	@Field("memo") private String memo;
	
	/*
	 * type has the following possible values:
	 * 0 - transaction document has NO splits
	 * 1 - transaction document HAS splits
	 * 2 - transaction document represents a split
	 */
	@Field("type") private int type;
	
	public FlatTransaction() {}
	
	public FlatTransaction(Transaction t) {
		setId(String.valueOf(t.getId()));
		setEntered(t.getEntered());
		setAmount(t.getAmount());
		setAccount(t.getAccount().getName());
		setPayee(t.getPayee().getName());
		setMajorCategory(t.getCategory().getMajor());
		setMinorCategory(t.getCategory().getMinor());
		setMemo(t.getMemo());
		setType(0);
	}
	
	@Override
	public String toString() {
		return String.format("%s | %s: %s (%4$td/%4$tm/%4$tY)", 
				getAccount(), getPayee(), Util.formatPounds(getAmount()), getEntered());
	}
	
	public int getType() {
		return type;
	}

	public FlatTransaction setType(int type) {
		this.type = type;
		return this;
	}

	public String getId() {
		return id;
	}

	public FlatTransaction setId(String id) {
		this.id = id;
		return this;
	}

	public java.sql.Date getEntered() {
		return new java.sql.Date(this.entered.getTime());
	}
	
	public String getEnteredStr() {
		return Util.formatSimple(getEntered());
	}
	
	// Solr will call this method
	public FlatTransaction setEntered(java.util.Date entered) {
		this.entered = entered;
		return this;
	}
	
	public FlatTransaction setEntered(java.sql.Date entered) {
		this.entered = new java.util.Date(entered.getTime());
		return this;
	}
	
	public String getPayee() {
		return payee;
	}
	
	public FlatTransaction setPayee(String payee) {
		this.payee = payee;
		return this;
	}
	
	public String getMajorCategory() {
		return majorCategory;
	}

	public FlatTransaction setMajorCategory(String s) {
		this.majorCategory = s;
		return this;
	}

	public String getMinorCategory() {
		return minorCategory;
	}

	public FlatTransaction setMinorCategory(String s) {
		this.minorCategory = s;
		return this;
	}

	public String getCategory() {
		return getMajorCategory() + (StringUtils.isNotBlank(getMinorCategory()) ? " > " + getMinorCategory() : "");
	}
	
	public String getAccount() {
		return account;
	}
	
	public FlatTransaction setAccount(String account) {
		this.account = account;
		return this;
	}
	
	public String getMemo() {
		return memo;
	}
	
	public FlatTransaction setMemo(String memo) {
		this.memo = memo;
		return this;
	}
	
	public long getAmount() {
		return amount;
	}
	
	public String getAmountInPounds() {
		return Util.formatPounds(getAmount());
	}
	
	public FlatTransaction setAmount(long amount) {
		this.amount = amount;
		return this;
	}
}
