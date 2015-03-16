package com.slepeweb.cms.service;


public interface CmsService {
	boolean isLiveServer();
	SiteService getSiteService();
	ItemTypeService getItemTypeService();
	FieldService getFieldService();
	ItemService getItemService();
	LinkService getLinkService();
	LinkNameService getLinkNameService();
	LinkTypeService getLinkTypeService();
	FieldForTypeService getFieldForTypeService();
	FieldValueService getFieldValueService();	
	MediaService getMediaService();	
	TemplateService getTemplateService();
	SiteConfigService getSiteConfigService();
	LoglevelService getLoglevelService();
	TagService getTagService();
}
