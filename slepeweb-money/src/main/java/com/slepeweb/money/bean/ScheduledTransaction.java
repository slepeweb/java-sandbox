package com.slepeweb.money.bean;

import org.apache.commons.lang3.StringUtils;

public class ScheduledTransaction extends Transaction {
	
	private int day;
	private String label = "";
	private Account mirror;
	
	public void assimilate(Object obj) {
		super.assimilate(obj);
		ScheduledTransaction source = (ScheduledTransaction) obj;
		setDay(source.getDay());
		setLabel(source.getLabel());
		setMirror(source.getMirror());
		
		assimilateSplits(source);
	}
	
	public void assimilateSplits(Transaction source) {
		getSplits().clear();
		for (SplitTransaction st : source.getSplits()) {
			getSplits().add(st.setTransactionId(getId()));
		}
	}
	
	@Override
	public boolean isDefined4Insert() {
		return  
			super.isDefined4Insert() &&
			StringUtils.isNotBlank(getLabel()) &&
			getDay() > 0;
	}
	
	@Override
	public String toString() {
		return getLabel();
	}
	
	public boolean isTransfer() {
		return getMirror() != null && getMirror().getId() > 0L;
	}
	
	public int getDay() {
		return day;
	}

	public ScheduledTransaction setDay(int day) {
		this.day = day;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public ScheduledTransaction setLabel(String label) {
		this.label = label;
		return this;
	}

	public Account getMirror() {
		return mirror;
	}

	public ScheduledTransaction setMirror(Account mirror) {
		this.mirror = mirror;
		return this;
	}	

}
