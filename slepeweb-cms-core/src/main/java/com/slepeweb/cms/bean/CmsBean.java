package com.slepeweb.cms.bean;

import java.io.Serializable;

import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.FieldForTypeService;
import com.slepeweb.cms.service.FieldService;
import com.slepeweb.cms.service.FieldValueService;
import com.slepeweb.cms.service.HostService;
import com.slepeweb.cms.service.ItemWorkerService;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.ItemTypeService;
import com.slepeweb.cms.service.LinkNameService;
import com.slepeweb.cms.service.LinkService;
import com.slepeweb.cms.service.LinkTypeService;
import com.slepeweb.cms.service.MediaFileService;
import com.slepeweb.cms.service.MediaService;
import com.slepeweb.cms.service.SiteService;
import com.slepeweb.cms.service.TagService;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.cms.utils.SpringContext;
import com.slepeweb.commerce.service.AxisService;
import com.slepeweb.commerce.service.AxisValueService;
import com.slepeweb.commerce.service.ProductService;
import com.slepeweb.commerce.service.VariantService;

public abstract class CmsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	protected static final Long NO_ID = -1L;
	protected transient CmsService cmsService;
	
	protected abstract CmsBean save() throws ResourceException;	
	protected abstract void delete();
	public abstract Long getId();
	protected abstract boolean isDefined4Insert() throws ResourceException;	
	
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
		if (this.cmsService == null) {
			this.cmsService = (CmsService) SpringContext.getApplicationContext().getBean("cmsService");
		}
		return this.cmsService;
	}
	
	public void setCmsService(CmsService cmsService) {
		this.cmsService = cmsService;
	}
	
	public HostService getHostService() {
		return this.cmsService.getHostService();
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
	
	public ItemWorkerService getItemWorkerService() {
		return this.cmsService.getItemWorkerService();
	}
	
	public LinkService getLinkService() {
		return this.cmsService.getLinkService();
	}
	
	public LinkNameService getLinkNameService() {
		return this.cmsService.getLinkNameService();
	}
	
	public LinkTypeService getLinkTypeService() {
		return this.cmsService.getLinkTypeService();
	}
	
	public MediaService getMediaService() {
		return this.cmsService.getMediaService();
	}
	
	public MediaFileService getMediaFileService() {
		return this.cmsService.getMediaFileService();
	}
	
	public TagService getTagService() {
		return this.cmsService.getTagService();
	}
	
	public ProductService getProductService() {
		return this.cmsService.getProductService();
	}
	
	public AxisService getAxisService() {
		return this.cmsService.getAxisService();
	}
	
	public AxisValueService getAxisValueService() {
		return this.cmsService.getAxisValueService();
	}
	
	public VariantService getVariantService() {
		return this.cmsService.getVariantService();
	}
}
