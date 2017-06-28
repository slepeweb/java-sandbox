package com.slepeweb.cms.test;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.commerce.bean.Product;

public abstract class BaseTest {
	protected static final String TEST_SITE_NAME = "Integration Testing";
	protected static final String HOMEPAGE_TYPE_NAME = TEST_SITE_NAME + " Homepage";
	protected static final String SECTION_TYPE_NAME = "ZSection";
	protected static final String NEWS_TYPE_NAME = "ZNews";
	protected static final String EVENT_TYPE_NAME = "ZEvent";
	protected static final String ARTICLE_TYPE_NAME = "ZArticle";
	protected static final String IMAGE_TYPE_NAME = "ZImage";
	protected static final String PRODUCT_TYPE_NAME = "Product";
	protected static final String TITLE_FIELD_NAME = "ztitle";
	protected static final String TEASER_FIELD_NAME = "zteaser";
	protected static final String BODY_FIELD_NAME = "zbodytext";
	protected static final String EMBARGO_FIELD_NAME = "zembargodate";
	protected static final String ALTTEXT_FIELD_NAME = "zalttext";
	protected static final String NEWS_TEMPLATE_NAME = "News Template";
	
	@Autowired protected CmsService cmsService;
	
	protected abstract TestResultSet execute();
	
	protected Site getTestSite() {
		return this.cmsService.getSiteService().getSite(TEST_SITE_NAME);
	}

	protected Site addSite(String name, String hostname, String homepageTypeName, String shortname) 
			throws MissingDataException, DuplicateItemException, ResourceException {
		
		return CmsBeanFactory.makeSite().
				setName(name).
				setShortname(shortname).
				save();	
	}
	
	protected ItemType addType(String name) {
		return addType(name, "application/cms");
	}
	
	protected ItemType addType(String name, String mimeType) {
		return CmsBeanFactory.makeItemType().
				setName(name).
				setMimeType(mimeType).
				save();
	}

	protected Field addField(String name, String variable, String help, FieldType type, int size, String dflt) {
		return CmsBeanFactory.makeField().
				setName(name).
				setVariable(variable).
				setHelp(help).
				setType(type).
				setSize(size).
				setDefaultValue(dflt).
				save();
	}
	
	protected Item addItem(Item parent, String name, String simplename, 
			Timestamp dateCreated, Timestamp dateUpdated, Site site, ItemType type, Template t) {
		
		Item i = CmsBeanFactory.makeItem(type.getName());
		i.setName(name).setSimpleName(simplename).setPath(getPath(parent, simplename)).
		setDateCreated(dateCreated).setDateUpdated(dateUpdated).
		setSite(site).setType(type).setTemplate(t);
	
		try {
			return parent != null ? parent.addChild(i) : null;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	protected Product addProduct(Item parent, String name, String simplename, 
			Timestamp dateCreated, Timestamp dateUpdated, Site site, ItemType type, Template t,
			String partNum, Long stock, Long price, Long alphaAxisId, Long betaAxisId) {
		
		Product p = CmsBeanFactory.makeProduct();
		p.setPartNum(partNum).setStock(stock).setPrice(price).setAlphaAxisId(alphaAxisId).setBetaAxisId(betaAxisId);
		p.setName(name).setSimpleName(simplename).setPath(getPath(parent, simplename)).
		setDateCreated(dateCreated).setDateUpdated(dateUpdated).
		setSite(site).setType(type).setTemplate(t);
		p.setParent(parent);
		
		try {
			p = p.save();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return p;		
	}
	
	private String getPath(Item parent, String simplename) {
		return 
				parent != null ? 
					parent.isSiteRoot() ? parent.getPath() + simplename : parent.getPath() + "/" + simplename : 
						"/" + simplename;
	}
	
	protected Template addTemplate(String name, String forward, Long siteId, Long typeId) {
		return CmsBeanFactory.makeTemplate().
				setName(name).
				setForward(forward).
				setSiteId(siteId).
				setItemTypeId(typeId).
				save();
	}
	
	protected LinkType addLinkType(String name) {
		LinkType lt = CmsBeanFactory.makeLinkType().
				setName(name).
				save();
		
		if (lt.getId() == null) {
			lt = this.cmsService.getLinkTypeService().getLinkType(name);
		}
		return lt;
	}
	
	protected LinkName addLinkName(Site s, LinkType lt, String name) {
		LinkName ln =  CmsBeanFactory.makeLinkName().
				setSiteId(s.getId()).
				setLinkTypeId(lt.getId()).
				setName(name).
				save();
		
		if (ln.getId() == null) {
			ln = this.cmsService.getLinkNameService().getLinkName(s.getId(), lt.getId(), name);
		}
		return ln;
	}
}
