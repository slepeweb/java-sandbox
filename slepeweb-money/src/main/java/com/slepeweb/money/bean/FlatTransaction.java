package com.slepeweb.money.bean;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;

import com.slepeweb.money.Util;

public class FlatTransaction {

	@Field("id") private String id;
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
		
	/*
	 *  Solr seems to be determined to map a 'pdate' field to a java.util.Date object.
	 *  Extra methods required for solr to get/set the field.
	 */
	@Field("entered") private java.util.Date enteredAsUtilDate;
	private LocalDate entered;
	
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

	public String getEnteredStr() {
		return Util.formatSimple(getEntered());
	}
	
	/*
	 * Methods required by money app
	 */
	public FlatTransaction setEntered(LocalDate ld) {
		this.entered = ld;
		
		// Add 2 hours for date/times stored by solr
		ZonedDateTime zdt = ld.atStartOfDay(ZoneId.of("UTC"));
		zdt.plusHours(2);
		this.enteredAsUtilDate = Util.toUtilDate(zdt);
		return this;
	}
	
	public LocalDate getEntered() {
		return this.entered != null ? this.entered : Util.toLocalDate(this.enteredAsUtilDate);
	}
	
	/*
	 * Methods required by solr
	 */
	public FlatTransaction setEnteredAsUtilDate(java.util.Date d) {
		this.enteredAsUtilDate = d;
		this.entered = Util.toLocalDate(d);
		return this;
	}
	
	public java.util.Date getEnteredAsUtilDate() {
		 return this.enteredAsUtilDate;
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
