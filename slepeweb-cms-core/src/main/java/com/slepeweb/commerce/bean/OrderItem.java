package com.slepeweb.commerce.bean;

public class OrderItem {
	private long origItemId;
	private String qualifier;
	private int quantity;
	private Product product;
	
	public OrderItem(int n, long o, String q) {
		this.origItemId = o;
		this.qualifier = q;
		this.quantity = n;
	}
	
	public long getOrigItemId() {
		return origItemId;
	}
	
	public void setOrigItemId(long origItemId) {
		this.origItemId = origItemId;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (origItemId ^ (origItemId >>> 32));
		result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
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
		OrderItem other = (OrderItem) obj;
		if (origItemId != other.origItemId)
			return false;
		if (qualifier == null) {
			if (other.qualifier != null)
				return false;
		} else if (!qualifier.equals(other.qualifier))
			return false;
		return true;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product p) {
		this.product = p;
	}
}
