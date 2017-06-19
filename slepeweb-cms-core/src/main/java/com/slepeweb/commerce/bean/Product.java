package com.slepeweb.commerce.bean;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;

public class Product extends Item {
	private static final long serialVersionUID = 1L;
	//private static Logger LOG = Logger.getLogger(Product.class);
	
	private String partNum;
	private Long stock, price;
	private Long alphaAxisId, betaAxisId;
	private List<Variant> variants;
	
	@Override
	public boolean isProduct() {
		return true;
	}
	
	@Override
	public Product setOrigId(Long origId) {
		super.setOrigId(origId);
		return this;
	}
	
	@Override
	public void assimilate(Object obj) {
		if (obj instanceof Product) {
			Product p = (Product) obj;
			setPartNum(p.getPartNum());
			setStock(p.getStock());
			setPrice(p.getPrice());
			setAlphaAxisId(p.getAlphaAxisId());
			setBetaAxisId(p.getBetaAxisId());
		}
	}
	
	public void assimilateItem(Object obj) {
		if (obj instanceof Item) {
			Item i = (Item) obj;
			super.assimilate(i);
			setId(i.getId());
		}
	}
	
	@Override
	public boolean isDefined4Insert() {
		return super.isDefined4Insert() && StringUtils.isNotBlank(getPartNum());
	}
	
	@Override
	public String toString() {
		return String.format("Product '%s' (%d @ %f.2)", getPartNum(), getStock(), getPriceInPounds());
	}
	
	@Override
	public Product save() throws MissingDataException, DuplicateItemException {
		return getProductService().save(this);
	}
	
	// This deletes a specific version of an item, and NOT all versions
	@Override
	public void delete() {
		getProductService().deleteAllVersions(getOrigId());
	}
	
	public Product copy() {
		Object[] copyDetails = getCopyDetails();
		String name = (String) copyDetails[2];
		String simplename = (String) copyDetails[1];
		String partNum = getPartNum() + (String) copyDetails[3] + String.valueOf(copyDetails[0]);
		
		try {
			return getProductService().copy(this, name, simplename, partNum, (Integer) copyDetails[0]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}
	
	public String getPartNum() {
		return partNum;
	}

	public Product setPartNum(String partNum) {
		this.partNum = partNum;
		return this;
	}

	public Long getStock() {
		return stock;
	}

	public Product setStock(Long stock) {
		this.stock = stock;
		return this;
	}

	public Long getPrice() {
		return price;
	}

	public float getPriceInPounds() {
		return this.price != null ? this.price / 100F : -1.0F;
	}

	public Product setPrice(Long price) {
		this.price = price;
		return this;
	}

	public Long getAlphaAxisId() {
		return this.alphaAxisId == null ? -1L : this.alphaAxisId;
	}

	public Product setAlphaAxisId(Long alphaAxis) {
		this.alphaAxisId = alphaAxis;
		return this;
	}

	public Long getBetaAxisId() {
		return this.betaAxisId == null ? -1L : this.betaAxisId;
	}

	public Product setBetaAxisId(Long betaAxis) {
		this.betaAxisId = betaAxis;
		return this;
	}

	public Product setVariants(List<Variant> variants) {
		this.variants = variants;
		return this;
	}

	public List<Variant> getVariants() {
		if (this.variants == null) {
			this.variants = getVariantService().getMany(getOrigId(), null, null);
		}
		return this.variants;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((partNum == null) ? 0 : partNum.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (partNum == null) {
			if (other.partNum != null)
				return false;
		} else if (!partNum.equals(other.partNum))
			return false;
		return true;
	}

}
