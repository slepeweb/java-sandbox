package com.slepeweb.money.bean.solr;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.slepeweb.money.Util;

@JsonIgnoreProperties({})
public class SolrParamsBase {

	private String payeeName;
	private Long accountId, transferAccountId, payeeId;
	private String transferDirection = "to";
	
	public String getPayeeName() {
		return payeeName;
	}

	public void setPayeeName(String payeeName) {
		if (StringUtils.isNotBlank(payeeName)) {
			this.payeeName = payeeName;
		}
	}

	public Long getPayeeId() {
		return payeeId;
	}

	public String getPayeeIdStr() {
		return getPayeeId() == null ? "" : String.valueOf(getPayeeId());
	}

	@JsonSetter("payeeId")
	public void setPayeeId(Long payeeId) {
		this.payeeId = payeeId;
	}

	public void setPayeeId(String payeeId) {
		this.payeeId = Util.toLong(payeeId, null);
	}

	public Long getAccountId() {
		return accountId;
	}

	public String getAccountIdStr() {
		return getAccountId() == null ? "" : String.valueOf(getAccountId());
	}

	@JsonSetter("accountId")
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = Util.toLong(accountId, null);		
	}

	public Long getTransferAccountId() {
		return transferAccountId;
	}

	public String getTransferAccountIdStr() {
		return getTransferAccountId() == null ? "-1" : String.valueOf(getTransferAccountId());
	}

	@JsonSetter("transferAccountId")
	public void setTransferAccountId(Long transferAccountId) {
		this.transferAccountId = transferAccountId;
	}
	
	public void setTransferAccountId(String accountId) {
		this.transferAccountId = Util.toLong(accountId, null);		
	}

	public boolean isTransfer() {
		return getTransferAccountId() != null && getTransferAccountId().longValue() > -1L;
	}

	public String getTransferDirection() {
		return transferDirection;
	}

	@JsonSetter("transferDirection")
	public void setTransferDirection(String dir) {
		this.transferDirection = dir;
	}

}
