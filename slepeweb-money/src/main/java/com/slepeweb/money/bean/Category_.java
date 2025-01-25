package com.slepeweb.money.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * These objects support the rendering of category lists in various forms,
 * including split-transactions, scheduled split-transactions, search criteria and 
 * chart definitions (which are based on search criteria).
 */
@JsonIgnoreProperties({"visible", "lastVisible"})
public class Category_ {
	private String major, minor;
	private boolean visible, lastVisible, exclude;
	private String memo;
	private long amount;
	
	public Category_() {}
	
	public Category_(SplitTransaction st) {
		this.major = st.getCategory().getMajor();
		this.minor = st.getCategory().getMinor();
		this.amount = st.getAmountValue();
		this.memo = st.getMemo();
	}
	
	public Category_(SearchCategory c) {
		this.major = c.getMajor();
		this.minor = c.getMinor();
		this.exclude = c.isExclude();
	}
	
	public Category_(Category c) {
		this.major = c.getMajor();
		this.minor = c.getMinor();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getMajor());
		if (sb.length() > 0 && getMinor().length() > 0) {
			sb.append(" > ").append(getMinor());
		}
		
		if (sb.length() > 0) {
			return sb.toString();
		}
		
		return "[blank]";
	}

	public String getMajor() {
		return major;
	}

	public Category_ setMajor(String major) {
		this.major = major;
		return this;
	}

	public String getMinor() {
		return minor;
	}

	public Category_ setMinor(String minor) {
		this.minor = minor;
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	public Category_ setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public boolean isLastVisible() {
		return lastVisible;
	}

	public Category_ setLastVisible(boolean lastVisible) {
		this.lastVisible = lastVisible;
		return this;
	}

	public String getMemo() {
		return memo;
	}

	public Category_ setMemo(String memo) {
		this.memo = memo;
		return this;
	}

	public long getAmount() {
		return amount;
	}

	public Category_ setAmount(long amount) {
		this.amount = amount;
		return this;
	}

	public boolean isExclude() {
		return exclude;
	}

	public Category_ setExclude(boolean exclude) {
		this.exclude = exclude;
		return this;
	}
}
