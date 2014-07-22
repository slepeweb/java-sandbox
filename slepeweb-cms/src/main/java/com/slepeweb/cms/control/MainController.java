package com.slepeweb.cms.control;

import java.sql.Timestamp;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.utils.LogUtil;

@Controller
public class MainController extends BaseController {
	private static Logger LOG = Logger.getLogger(MainController.class);

	@Autowired private CmsService cmsService;
	
	@RequestMapping("/test")
	public String doGeneric(HttpSession session, ModelMap model) {
		
		addType("Root");
		addType("Section");
		addType("News");
		addType("Article");
		
		ItemType sectionType = this.cmsService.getItemType("Section");
		ItemType newsType = this.cmsService.getItemType("News");
		
		addSite("YRP", "www.yrp.com");
		addSite("Slepeweb", "www.slepeweb.com");
		
		addField("Title", "title", "Page title - also used in links to this page", FieldType.text, 64);
		addField("Teaser", "teaser", "Used in links to this page", FieldType.text, 256);
		
		Field titleField = this.cmsService.getFieldService().getField("Title");
		Field teaserField = this.cmsService.getFieldService().getField("Teaser");
		addFieldForType(newsType, titleField, 1L, true);
		addFieldForType(newsType, teaserField, 2L, false);
		
		// Create items
		Site s = this.cmsService.getSite("Slepeweb");	
		Timestamp now = new Timestamp(System.currentTimeMillis());
		addItem("News section", "news", "/news", now, now, s, sectionType);
		addItem("News item #1", "101", "/news/101", now, now, s, newsType);
		addItem("News item #2", "102", "/news/102", now, now, s, newsType);
				
		model.addAttribute("_page", "helo");
		return "test";
	}
	
	private void addSite(String name, String hostname) {
		Site s = new Site().setName(name).setHostname(hostname);	
		if (this.cmsService.getSite(name) == null) {					
			this.cmsService.addSite(s);
		}
		else {
			LogUtil.warn(LOG, "Site already exists", s.getName());
		}
	}
	
	private void addType(String name) {
		ItemType it = new ItemType().setName(name);
		if (this.cmsService.getItemType(name) == null) {					
			this.cmsService.addItemType(it);
		}
		else {
			LogUtil.warn(LOG, "Item type already exists", it.getName());
		}
	}

	private void addField(String name, String variable, String help, FieldType type, int size) {
		Field f = new Field().setName(name).setVariable(variable).setHelp(help).setType(type).setSize(size);
		if (this.cmsService.getField(name) == null) {								
			this.cmsService.addField(f);
		}
		else {
			LogUtil.warn(LOG, "Field already exists", f.getName());
		}
	}
	
	private void addItem(String name, String simplename, String path, 
			Timestamp dateCreated, Timestamp dateUpdated, Site site, ItemType type) {
		
		Item i = new Item().setName(name).setSimpleName(simplename).setPath(path).
			setDateCreated(dateCreated).setDateUpdated(dateUpdated).setType(type);
		
		if (site.getItem(i.getPath()) == null) {								
			site.addItem(i);
		}
		else {
			LogUtil.warn(LOG, "Item already exists", i.getPath());
		}
	}
	
	private void addFieldForType(ItemType itemType, Field field, Long ordering, boolean mandatory) {
		FieldForType fft = new FieldForType().setType(itemType).setField(field).setOrdering(ordering).setMandatory(mandatory);
		if (this.cmsService.getFieldForTypeService().getFieldForType(fft.getField().getId(), fft.getType().getId()) == null) {								
			this.cmsService.getFieldForTypeService().insertFieldForType(fft);
		}
		else {
			LogUtil.warn(LOG, "Field for type already exists", fft.getType().getName());
		}
	}
	
}
