package com.slepeweb.cms.bean;

import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.FieldService;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.ItemTypeService;
import com.slepeweb.cms.service.LinkService;
import com.slepeweb.cms.service.SiteService;

public class CmsBean {
	
	protected transient CmsService cmsService;
	
	protected boolean isServiced() {
		return this.cmsService != null;
	}
	
	public CmsService getCmsService() {
		return cmsService;
	}
	
	public void setCmsService(CmsService cmsService) {
		this.cmsService = cmsService;
	}
	
	public SiteService getSiteService() {
		return this.cmsService.getSiteService();
	}
	
	public ItemTypeService getItemTypeService() {
		return this.cmsService.getItemTypeService();
	}
	
	public FieldService getFieldService() {
		return this.cmsService.getFieldService();
	}
	
	public ItemService getItemService() {
		return this.cmsService.getItemService();
	}
	
	public LinkService getLinkService() {
		return this.cmsService.getLinkService();
	}
}
