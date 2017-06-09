package com.slepeweb.cms.service;

import com.slepeweb.commerce.service.ProductService;

public interface CmsService {
	boolean isLiveServer();
	HostService getHostService();
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
	ProductService getProductService();
}
