package com.slepeweb.cms.service;

import com.slepeweb.cms.component.SiteConfiguration;
import com.slepeweb.commerce.service.AxisService;
import com.slepeweb.commerce.service.AxisValueService;
import com.slepeweb.commerce.service.ProductService;
import com.slepeweb.commerce.service.VariantService;

public interface CmsService {
	boolean isEditorialContext();
	boolean isDeliveryContext();
	boolean isCommerceEnabled();
	String getStaticSiteRoot();
	boolean isProductionDeployment();
	boolean isDevDeployment();
	
	HostService getHostService();
	SiteService getSiteService();
	ItemTypeService getItemTypeService();
	FieldService getFieldService();
	ItemService getItemService();
	ItemWorkerService getItemWorkerService();
	LinkService getLinkService();
	LinkNameService getLinkNameService();
	LinkTypeService getLinkTypeService();
	FieldForTypeService getFieldForTypeService();
	FieldValueService getFieldValueService();	
	MediaService getMediaService();	
	MediaFileService getMediaFileService();	
	TemplateService getTemplateService();
	SiteConfigService getSiteConfigService();
	LoglevelService getLoglevelService();
	LoglevelUpdateService getLoglevelUpdateService();
	TagService getTagService();
	ProductService getProductService();
	AxisService getAxisService();
	AxisValueService getAxisValueService();
	VariantService getVariantService();
	SiteConfiguration getSiteConfiguration();
	UserService getUserService();
	AccessService getAccessService();
	SiteAccessService getSiteAccessService();
	SiteTypeService getSiteTypeService();
	PasskeyService getPasskeyService();
}
