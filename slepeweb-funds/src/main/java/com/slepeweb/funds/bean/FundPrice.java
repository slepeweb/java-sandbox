package com.slepeweb.funds.bean;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class FundPrice {
	
	public static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
	public static SimpleDateFormat SDF_SHORT = new SimpleDateFormat("dd/MM/yy");
	public static DecimalFormat DF = new DecimalFormat("#.00");
	public static DecimalFormat DF_TOTAL = new DecimalFormat("###,###");
	
	private Fund fund;
	private Timestamp entered;
	private float value;
	
	public void assimilate(Object obj) {
		if (obj instanceof FundPrice) {
			FundPrice fp = (FundPrice) obj;
			setFund(fp.getFund());
			setEntered(fp.getEntered());
			setValue(fp.getValue());
		}
	}
	
	public boolean isDefined4Insert() {
		return  
			getFund() != null &&
			getFund().getId() > 0L &&
			getEntered() != null &&
			getValue() > 0L;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %4.2fp (%3$td/%3$tm/%3$tY)", this.fund.getAlias(), getValue(), getEntered().getTime());
	}
	
	public Fund getFund() {
		return this.fund;
	}
	
	public FundPrice setFund(Fund f) {
		this.fund = f;
		return this;
	}
	
	public Timestamp getEntered() {
		return entered;
	}
	
	public FundPrice setEntered(Timestamp entered) {
		this.entered = entered;
		return this;
	}
	
	public float getValue() {
		return value;
	}
	
	public FundPrice setValue(float value) {
		this.value = value;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entered == null) ? 0 : entered.hashCode());
		result = prime * result + ((fund == null) ? 0 : fund.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FundPrice other = (FundPrice) obj;
		if (entered == null) {
			if (other.entered != null)
				return false;
		} else if (!entered.equals(other.entered))
			return false;
		if (fund == null) {
			if (other.fund != null)
				return false;
		} else if (!fund.equals(other.fund))
			return false;
		
		return true;
	}
}
