package com.slepeweb.commerce.xls;

public class VariantXlsRow {
	private String update, partNum, qualifier, stock, price, alpha, beta;

	@Override
	public String toString() {
		return String.format("%s (%s)", getPartNum(), getQualifier());
	}
	
	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getPartNum() {
		return partNum;
	}

	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String q) {
		this.qualifier = q;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getAlpha() {
		return alpha;
	}

	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}

	public String getBeta() {
		return beta;
	}

	public void setBeta(String beta) {
		this.beta = beta;
	}
	
}
