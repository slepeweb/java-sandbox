package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

public class SplitTransactionFormComponent {
	
	private Category category;
	private List<String> allMajors = new ArrayList<String>();
	private List<String> allMinors = new ArrayList<String>();
	private Long amount;
	private String memo = "";
	
	public Category getCategory() {
		return category;
	}

	public SplitTransactionFormComponent setCategory(Category category) {
		this.category = category;
		return this;
	}

	public List<String> getAllMajors() {
		return allMajors;
	}

	public List<String> getAllMinors() {
		return allMinors;
	}

	public SplitTransactionFormComponent setAllMajors(List<String> major) {
		this.allMajors = major;
		return this;
	}

	public SplitTransactionFormComponent setAllMinors(List<String> minor) {
		this.allMinors = minor;
		return this;
	}

	public String getAmountInPounds() {
		return Transaction.DF.format(amount / 100.0);
	}
	
	public Long getAmount() {
		return amount;
	}
	
	public SplitTransactionFormComponent setAmount(Long value) {
		this.amount = value;
		return this;
	}

	public String getMemo() {
		return this.memo;
	}

	public SplitTransactionFormComponent setMemo(String memo) {
		this.memo = memo;
		return this;
	}
}
