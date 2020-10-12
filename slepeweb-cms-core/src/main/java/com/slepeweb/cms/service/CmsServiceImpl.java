package com.slepeweb.cms.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.component.SiteConfiguration;
import com.slepeweb.commerce.service.AxisService;
import com.slepeweb.commerce.service.AxisValueService;
import com.slepeweb.commerce.service.ProductService;
import com.slepeweb.commerce.service.VariantService;

// Bean defined in xml
public class CmsServiceImpl implements CmsService {
	
	@Autowired protected HostService hostService;	
	@Autowired protected SiteService siteService;	
	@Autowired protected ItemTypeService itemTypeService;
	@Autowired protected FieldService fieldService;
	@Autowired protected ItemService itemService;
	@Autowired protected LinkService linkService;
	@Autowired protected LinkNameService linkNameService;
	@Autowired protected LinkTypeService linkTypeService;
	@Autowired protected FieldForTypeService fieldForTypeService;
	@Autowired protected FieldValueService fieldValueService;
	@Autowired protected MediaService mediaService;
	@Autowired protected TemplateService templateService;
	@Autowired protected SiteConfigService siteConfigService;
	@Autowired protected LoglevelService loglevelService;
	@Autowired protected LoglevelUpdateService loglevelUpdateService;
	@Autowired protected TagService tagService;
	@Autowired protected ProductService productService;
	@Autowired protected AxisService axisService;
	@Autowired protected AxisValueService axisValueService;
	@Autowired protected VariantService variantService;
	@Autowired protected UserService userService;
	@Autowired protected AccessService accessService;
	@Autowired protected SiteAccessService siteAccessService;
	@Autowired protected SiteTypeService siteTypeService;
	@Autowired protected SiteConfiguration siteConfiguration;
	
	/* 
	 * In editorial context:
	 * 
	 * 1) 	Shortcuts are seen as separate items to the items they reference.
	 * 		This is essential to allow the content editor to relate a Shortcut to its reference.
	 * 		In (the opposite) site delivery context, a Shortcut item is effectively merged with its 
	 * 		reference item.
	 * 
	 * 2)	Access rules designated with type=='w' (ie writeable) are followed. Otherwise, readonly
	 * 		rules are followed (ie type=='r').
	 */
	private boolean editorialContext = true;
	
	public void setEditorialContext(boolean b) {
		this.editorialContext = b;
	}
	
	public boolean isEditorialContext() {
		return this.editorialContext;
	}
	
	public boolean isDeliveryContext() {
		return ! isEditorialContext();
	}
	
	/*
	 * In staging delivery context, the editable versions of items are delivered to web pages,
	 * as opposed to live delivery context.
	 */
	private boolean editableContentRequired = true;
	
	public void setEditableContentRequired(boolean b) {
		this.editableContentRequired = b;
	}

	public boolean isEditableContentRequired() {
		return editableContentRequired;
	}

	public boolean isViewableContentRequired() {
		return ! isEditableContentRequired();
	}
	
	/*
	 * This is the storefron context. WARNING: this code has had very little usage or testing for
	 * too long.
	 */
	private boolean commerceEnabled = false;

	public boolean isCommerceEnabled() {
		return commerceEnabled;
	}

	public void setCommerceEnabled(boolean b) {
		this.commerceEnabled = b;
	}
	
	private String staticSiteRoot;

	public String getStaticSiteRoot() {
		return staticSiteRoot;
	}

	public void setStaticSiteRoot(String staticSiteRoot) {
		this.staticSiteRoot = staticSiteRoot;
	}

	@PostConstruct
	public void initialiseCmsBeanFactory() {
		CmsBeanFactory.init(this);
	}
	
	public HostService getHostService() {
		return hostService;
	}
	
	public SiteService getSiteService() {
		return siteService;
	}
	
	public ItemTypeService getItemTypeService() {
		return itemTypeService;
	}
	
	public FieldService getFieldService() {
		return fieldService;
	}
	
	public ItemService getItemService() {
		return itemService;
	}
	
	public LinkService getLinkService() {
		return linkService;
	}

	public LinkNameService getLinkNameService() {
		return linkNameService;
	}

	public LinkTypeService getLinkTypeService() {
		return linkTypeService;
	}

	public FieldForTypeService getFieldForTypeService() {
		return fieldForTypeService;
	}

	public void setFieldForTypeService(FieldForTypeService fieldForTypeService) {
		this.fieldForTypeService = fieldForTypeService;
	}

	public FieldValueService getFieldValueService() {
		return fieldValueService;
	}

	public MediaService getMediaService() {
		return mediaService;
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	public TemplateService getTemplateService() {
		return templateService;
	}

	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	public SiteConfigService getSiteConfigService() {
		return siteConfigService;
	}

	public LoglevelService getLoglevelService() {
		return loglevelService;
	}

	public LoglevelUpdateService getLoglevelUpdateService() {
		return loglevelUpdateService;
	}

	public TagService getTagService() {
		return tagService;
	}

	public ProductService getProductService() {
		return productService;
	}

	public AxisService getAxisService() {
		return axisService;
	}

	public AxisValueService getAxisValueService() {
		return axisValueService;
	}

	public VariantService getVariantService() {
		return variantService;
	}

	public SiteConfiguration getSiteConfiguration() {
		return siteConfiguration;
	}

	public UserService getUserService() {
		return userService;
	}

	public AccessService getAccessService() {
		return accessService;
	}

	public SiteAccessService getSiteAccessService() {
		return siteAccessService;
	}

	public SiteTypeService getSiteTypeService() {
		return siteTypeService;
	}
}
