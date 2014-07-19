package com.slepeweb.cms.control;

import java.sql.Timestamp;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.ItemTypeService;
import com.slepeweb.cms.service.SiteService;

@Controller
public class MainController extends BaseController {
	private static Logger LOG = Logger.getLogger(MainController.class);

	@Autowired private CmsService cmsService;
	@Autowired private SiteService siteService;
	@Autowired private ItemTypeService itemTypeService;
	@Autowired private ItemService itemService;
	
	@RequestMapping("/test")
	public String doGeneric(HttpSession session, ModelMap model) {
		
		// Create a site or two ...
		Site s = this.siteService.getSite("YRP");		
		if (s == null) {
			s = new Site();
			s.setName("YRP");
			s.setHostname("www.yrp.com");		
			this.siteService.insertSite(s);
		}
		
		s = this.siteService.getSite("Slepeweb");
		if (s == null) {
			s = new Site();
			s.setName("Slepeweb");
			s.setHostname("www.slepeweb.com");		
			this.siteService.insertSite(s);
		}
		
		// Create types
		ItemType it;
		
		it = this.itemTypeService.getItemType("Root");
		if (it == null) {
			it = new ItemType();
			it.setName("Root");
			this.itemTypeService.insertItemType(it);
		}
		
		it = this.itemTypeService.getItemType("Section");
		if (it == null) {
			it = new ItemType();
			it.setName("Section");
			this.itemTypeService.insertItemType(it);
		}
		
		it = this.itemTypeService.getItemType("News");
		if (it == null) {
			it = new ItemType();
			it.setName("News");
			this.itemTypeService.insertItemType(it);
		}
		
		it = this.itemTypeService.getItemType("Article");
		if (it == null) {
			it = new ItemType();
			it.setName("Article");
			this.itemTypeService.insertItemType(it);
		}
		
		// Create items
		Item i;
		s = this.siteService.getSite("Slepeweb");
		
		it = this.itemTypeService.getItemType("Section");		
		i = this.itemService.getItem(s.getId(), "/news");
		if (i == null) {
			i = new Item();
			i.setName("News section");
			i.setSimpleName("news");
			i.setPath("/news");
			i.setDateCreated(new Timestamp(System.currentTimeMillis()));
			i.setDateUpdated(i.getDateCreated());
			i.setSite(s);
			i.setType(it);
			this.itemService.insertItem(i);
		}
		
		it = this.itemTypeService.getItemType("Article");		
		i = this.itemService.getItem(s.getId(), "/news/101");
		if (i == null) {
			i = new Item();
			i.setName("News item #1");
			i.setSimpleName("101");
			i.setPath("/news/101");
			i.setDateCreated(new Timestamp(System.currentTimeMillis()));
			i.setDateUpdated(i.getDateCreated());
			i.setSite(s);
			i.setType(it);
			this.itemService.insertItem(i);
		}
		
		i = this.itemService.getItem(s.getId(), "/news/102");
		if (i == null) {
			i = new Item();
			i.setName("News item #2");
			i.setSimpleName("102");
			i.setPath("/news/102");
			i.setDateCreated(new Timestamp(System.currentTimeMillis()));
			i.setDateUpdated(i.getDateCreated());
			i.setSite(s);
			i.setType(it);
			this.itemService.insertItem(i);
		}
		
		model.addAttribute("_page", "helo");
		return "test";
	}
}
