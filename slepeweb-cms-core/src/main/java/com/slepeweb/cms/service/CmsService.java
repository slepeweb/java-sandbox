package com.slepeweb.cms.service;


public interface CmsService {
	SiteService getSiteService();
	ItemTypeService getItemTypeService();
	FieldService getFieldService();
	ItemService getItemService();
	LinkService getLinkService();
	FieldForTypeService getFieldForTypeService();
	FieldValueService getFieldValueService();	
	MediaService getMediaService();	
	TemplateService getTemplateService();
	SiteConfigService getSiteConfigService();
}
