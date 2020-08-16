package com.slepeweb.cms.bean;

import com.slepeweb.cms.except.ResourceException;

public class SiteType extends CmsBean {
	private static final long serialVersionUID = 1L;

	private Long siteId;
	private ItemType type;
	
	public void assimilate(Object obj) {
		if (obj instanceof SiteType) {
			SiteType st = (SiteType) obj;
			setSiteId(st.getSiteId());
			setType(st.getType());
		}
	}
	
	public boolean isDefined4Insert() {
		return
			getSiteId() != null &&
			getType() != null;
	}
	
	public Long getId() {
		return NO_ID;
	}
	
	@Override
	public String toString() {
		return String.format("Item type for site (%s)", getType().getName());
	}
	
	public SiteType save() throws ResourceException {
		return getCmsService().getSiteTypeService().save(this);
	}

	public void delete() {
		getCmsService().getSiteTypeService().delete(getSiteId(), getType().getId());
	}
	
	public ItemType getType() {
		return type;
	}

	public SiteType setType(ItemType it) {
		this.type = it;
		return this;
	}

	public Long getSiteId() {
		return siteId;
	}

	public SiteType setSiteId(Long id) {
		this.siteId = id;
		return this;
	}

}
