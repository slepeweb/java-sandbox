package com.slepeweb.commerce.bean;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.CmsBean;
import com.slepeweb.cms.except.ResourceException;

public class Variant extends CmsBean {

	private static final long serialVersionUID = 1L;
	
	private Long origItemId, alphaAxisValueId, betaAxisValueId;
	private String sku;
	private Integer stock, price;
	
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
		return String.format("Variant '%s' (%d @ %f.2)", getSku(), getStock(), getPriceInPounds());
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
		return getOrigItemId() != null && StringUtils.isNotBlank(getSku());
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
	
	public String getSku() {
		return sku;
	}
	
	public Variant setSku(String sku) {
		this.sku = sku;
		return this;
	}
	
	public Integer getStock() {
		return stock;
	}
	
	public Variant setStock(Integer stock) {
		this.stock = stock;
		return this;
	}
	
	public Integer getPrice() {
		return this.price == null ? 0 : this.price;
	}
	
	public float getPriceInPounds() {
		return this.price != null ? this.price / 100F : -1.0F;
	}

	public Variant setPrice(Integer price) {
		this.price = price;
		return this;
	}
	
	public Long getAlphaAxisValueId() {
		return this.alphaAxisValueId == null ? -1L : this.alphaAxisValueId;
	}
	
	public Variant setAlphaAxisValueId(Long alphaValue) {
		this.alphaAxisValueId = alphaValue;
		return this;
	}
	
	public Long getBetaAxisValueId() {
		return this.betaAxisValueId == null ? -1L : this.betaAxisValueId;
	}
	
	public Variant setBetaAxisValueId(Long betaValue) {
		this.betaAxisValueId = betaValue;
		return this;
	}

}
