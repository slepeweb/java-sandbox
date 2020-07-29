package com.slepeweb.cms.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.component.ServerConfig;
import com.slepeweb.cms.component.SiteConfiguration;
import com.slepeweb.commerce.service.AxisService;
import com.slepeweb.commerce.service.AxisValueService;
import com.slepeweb.commerce.service.ProductService;
import com.slepeweb.commerce.service.VariantService;

@Service
public class CmsServiceImpl extends BaseServiceImpl implements CmsService {
	
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
	@Autowired protected TagService tagService;
	@Autowired protected ProductService productService;
	@Autowired protected AxisService axisService;
	@Autowired protected AxisValueService axisValueService;
	@Autowired protected VariantService variantService;
	@Autowired protected ServerConfig serverConfig;
	@Autowired protected SiteConfiguration siteConfiguration;
	@Autowired protected UserService userService;
	@Autowired protected AccessService accessService;
	
	@PostConstruct
	public void initialiseCmsBeanFactory() {
		CmsBeanFactory.init(this);
	}
	
	public boolean isLiveServer() {
		return this.config.isLiveDelivery();
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

	public ServerConfig getServerConfig() {
		return serverConfig;
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
}
