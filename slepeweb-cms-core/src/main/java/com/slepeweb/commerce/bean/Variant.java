package com.slepeweb.commerce.bean;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.CmsBean;
import com.slepeweb.cms.except.ResourceException;

public class Variant extends CmsBean {

	private static final long serialVersionUID = 1L;
	
	private Long origItemId, alphaAxisValueId, betaAxisValueId;
	private String qualifier;
	private Long stock, price;
	private Product product;
	
	@Override
	public void assimilate(Object obj) {
		if (obj instanceof Variant) {
			Variant v = (Variant) obj;
			setStock(v.getStock());
			setPrice(v.getPrice());
			setAlphaAxisValueId(v.getAlphaAxisValueId());
			setBetaAxisValueId(v.getBetaAxisValueId());
		}
	}
	
	@Override
	public String toString() {
		return String.format("Variant '%s' (%d @ %f.2)", getQualifier(), getStock(), getPriceInPounds());
	}
	
	@Override
	public Variant save() throws ResourceException {
		return getVariantService().save(this);
	}

	@Override
	public void delete() {
		getVariantService().delete(this);
	}

	@Override
	public Long getId() {
		return getOrigItemId();
	}

	@Override
	public boolean isDefined4Insert() throws ResourceException {
		return getOrigItemId() != null && StringUtils.isNotBlank(getQualifier());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alphaAxisValueId == null) ? 0 : alphaAxisValueId.hashCode());
		result = prime * result + ((betaAxisValueId == null) ? 0 : betaAxisValueId.hashCode());
		result = prime * result + ((origItemId == null) ? 0 : origItemId.hashCode());
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
		Variant other = (Variant) obj;
		if (alphaAxisValueId == null) {
			if (other.alphaAxisValueId != null)
				return false;
		} else if (!alphaAxisValueId.equals(other.alphaAxisValueId))
			return false;
		if (betaAxisValueId == null) {
			if (other.betaAxisValueId != null)
				return false;
		} else if (!betaAxisValueId.equals(other.betaAxisValueId))
			return false;
		if (origItemId == null) {
			if (other.origItemId != null)
				return false;
		} else if (!origItemId.equals(other.origItemId))
			return false;
		return true;
	}

	
	public Long getOrigItemId() {
		return origItemId;
	}
	
	public Variant setOrigItemId(Long itemId) {
		this.origItemId = itemId;
		return this;
	}
	
	public String getQualifier() {
		return qualifier;
	}
	
	public Variant setQualifier(String q) {
		this.qualifier = q;
		return this;
	}
	
	public Long getStock() {
		return this.stock == null ? 0L : this.stock;
	}
	
	public Product getProduct() {
		if (this.product == null) {
			this.product = getCmsService().getProductService().get(getOrigItemId());
		}
		return this.product;
	}

	public Variant setStock(Long stock) {
		this.stock = stock;
		return this;
	}
	
	public Long getPrice() {
		return this.price == null ? 0 : this.price;
	}
	
	public float getPriceInPounds() {
		return this.price != null ? this.price / 100F : -1.0F;
	}

	public Variant setPrice(Long price) {
		this.price = price;
		return this;
	}
	
	public Long getAlphaAxisValueId() {
		return this.alphaAxisValueId == null ? -1L : this.alphaAxisValueId;
	}
	
	public AxisValue getAlphaAxisValue() {
		return getAxisValueService().get(getAlphaAxisValueId());
	}
	
	public Variant setAlphaAxisValueId(Long alphaValue) {
		this.alphaAxisValueId = alphaValue;
		return this;
	}
	
	public Long getBetaAxisValueId() {
		return this.betaAxisValueId == null ? -1L : this.betaAxisValueId;
	}
	
	public AxisValue getBetaAxisValue() {
		return getAxisValueService().get(getBetaAxisValueId());
	}
	
	public Variant setBetaAxisValueId(Long betaValue) {
		this.betaAxisValueId = betaValue;
		return this;
	}

}
