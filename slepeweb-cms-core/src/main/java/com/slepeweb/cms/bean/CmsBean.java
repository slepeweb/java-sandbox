package com.slepeweb.cms.bean;

import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.FieldForTypeService;
import com.slepeweb.cms.service.FieldService;
import com.slepeweb.cms.service.FieldValueService;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.ItemTypeService;
import com.slepeweb.cms.service.LinkService;
import com.slepeweb.cms.service.SiteService;
import com.slepeweb.cms.utils.LogUtil;

public abstract class CmsBean {
	
	protected transient CmsService cmsService;
	
	protected abstract CmsBean save();	
	protected abstract void delete();
	protected abstract boolean isDefined4Insert();	
	
	// The properties that should be assimilated are those which are specified in the update sql
	protected abstract void assimilate(Object obj);	
	
	protected String compose(String template, Object ... params) {
		return LogUtil.compose(template, params);
	}
	
	protected void setService(CmsService cms) {
		this.cmsService = cms;
	}
	
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
	
	public FieldForTypeService getFieldForTypeService() {
		return this.cmsService.getFieldForTypeService();
	}
	
	public FieldValueService getFieldValueService() {
		return this.cmsService.getFieldValueService();
	}
	
	public ItemService getItemService() {
		return this.cmsService.getItemService();
	}
	
	public LinkService getLinkService() {
		return this.cmsService.getLinkService();
	}
}