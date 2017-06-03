package com.slepeweb.commerce.bean;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.CmsBean;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.utils.SpringContext;
import com.slepeweb.commerce.service.ProductService;

public class Product extends CmsBean {
	private static final long serialVersionUID = 1L;
	//private static Logger LOG = Logger.getLogger(Product.class);
	
	private String partNum;
	private Integer stock, price;
	private Long origItemId, alphaAxisId, betaAxisId;
	private transient ProductService productService;
	
	private ProductService getProductService() {
		if (this.productService == null) {
			this.productService = (ProductService) SpringContext.getApplicationContext().getBean("productService");
		}
		return this.productService;
	}
	
	public void assimilate(Object obj) {
		if (obj instanceof Product) {
			Product p = (Product) obj;
			setStock(p.getStock());
			setPrice(p.getPrice());
			setAlphaAxisId(p.getAlphaAxisId());
			setBetaAxisId(p.getBetaAxisId());
		}
	}
	
	public boolean isDefined4Insert() {
		return StringUtils.isNotBlank(getPartNum());
	}
	
	@Override
	public String toString() {
		return String.format("Product '%s' (%d @ %f.2)", getPartNum(), getStock(), getPrice());
	}
	
	@Override
	protected void delete() {
		// TODO: not implemented
	}

	@Override
	public Long getId() {
		// Not required for Product table
		return null;
	}

	public Product save() throws MissingDataException, DuplicateItemException {
		return getProductService().save(this);
	}
	
	public Long getOrigItemId() {
		return origItemId;
	}

	public Product setOrigItemId(Long itemId) {
		this.origItemId = itemId;
		return this;
	}
	
	public String getPartNum() {
		return partNum;
	}

	public Product setPartNum(String partNum) {
		this.partNum = partNum;
		return this;
	}

	public Integer getStock() {
		return stock;
	}

	public Product setStock(Integer stock) {
		this.stock = stock;
		return this;
	}

	public Integer getPrice() {
		return price;
	}

	public float getPriceInPounds() {
		return this.price != null ? this.price / 100F : -1.0F;
	}

	public Product setPrice(Integer price) {
		this.price = price;
		return this;
	}

	public Long getAlphaAxisId() {
		return alphaAxisId;
	}

	public Product setAlphaAxisId(Long alphaAxis) {
		this.alphaAxisId = alphaAxis;
		return this;
	}

	public Long getBetaAxisId() {
		return betaAxisId;
	}

	public Product setBetaAxisId(Long betaAxis) {
		this.betaAxisId = betaAxis;
		return this;
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
