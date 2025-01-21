package com.slepeweb.money.bean;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.slepeweb.money.Util;

@JsonIgnoreProperties({"ready", "defined4Insert", "inDatabase", "legacy"})
public class SplitInput extends CategoryInput {
	
	private String memo;
	private long amount;
	
	public SplitInput() {}
	
	public SplitInput(SplitTransaction st) {
		assimilate(st.getCategory());
		setAmount(st.getAmount()).
		setMemo(st.getMemo());
	}
	
	public boolean isDebit() {
		return getAmount() <= 0L;
	}
		
	public String getAmountInPounds() {
		return Transaction.DF.format(amount / 100.0);
	}
	
	public long getAmountValue() {
		return isDebit() ? getAmount() * -1L : getAmount();
	}
	
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
