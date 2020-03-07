package com.slepeweb.commerce.xls;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.common.util.StringUtil;

public class VariantXlsRow {
	private String update, partNum, stock, price, alpha, beta;

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
		StringBuilder sb = new StringBuilder(StringUtil.compress(getAlpha()));
		if (StringUtils.isNotBlank(getBeta())) {
			sb.append("-").append(StringUtil.compress(getBeta()));
		}
		return sb.toString();
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
