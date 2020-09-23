package com.slepeweb.money.bean;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.slepeweb.money.Util;

@JsonIgnoreProperties({"ready", "defined4Insert", "inDatabase", "legacy"})
public class SplitInput extends CategoryInput {
	
	private String memo;
	private long amount;
	
	public String getMemo() {
		return memo;
	}
	
	public SplitInput setMemo(String memo) {
		this.memo = memo;
		return this;
	}
	
	public long getAmount() {
		return amount;
	}
	
	public SplitInput setAmount(long amount) {
		this.amount = amount;
		return this;
	}
	
	public SplitInput setAmount(String amountStr) {
		if (StringUtils.isNotBlank(amountStr)) {
			this.amount = Util.parsePounds(amountStr);
		}

		return this;
	}
}
