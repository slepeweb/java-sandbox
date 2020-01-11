package com.slepeweb.site.anc.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.site.anc.bean.MenuItem;
import com.slepeweb.site.anc.bean.Person;
import com.slepeweb.site.anc.bean.svg.SvgSupport;
import com.slepeweb.site.control.BaseController;
import com.slepeweb.site.model.Page;

@Controller
@RequestMapping("/spring/anc")
public class AncestryPageController extends BaseController {
	
	public static final String HISTORY_VIEW = "history";
	public static final String GALLERY_VIEW = "gallery";
	public static final String RECORD_VIEW = "record";
	
	@Autowired private ItemService itemService;
	
	@RequestMapping(value="/homepage")	
	public String homepage(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@ModelAttribute("_site") Site site, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "homepage", model);
		return page.getView();
	}

	@RequestMapping(value="/male")	
	public String male(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "person", model);
		page.setTitle(i.getName());
		
		Person subject = new Person(i);
		model.addAttribute("_person", subject);
		model.addAttribute("_support", new SvgSupport(subject));
		model.addAttribute("_menu", createPersonMenu(i, subject, null));
		
		filterBreadcrumbs(page);		
		return page.getView();
	}
	
	@RequestMapping(value="/female")	
	public String female(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
		
		return male(i, shortSitename, model);
	}
	
	@RequestMapping(value="/document")	
	public String history(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "personHistory", model);
		page.setTitle(i.getFieldValue("heading"));
		
		Person subject = new Person(i.getParent());
		model.addAttribute("_person", subject);
		model.addAttribute("_menu", createPersonMenu(i, subject, null));
		model.addAttribute("_subMenu", createPersonSubMenu(i, subject, subject.getDocuments(), null, null));
		
		filterBreadcrumbs(page);
		return page.getView();
	}	

	@RequestMapping(value="/male/gallery/{targetId}")	
	public String maleGallery(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@PathVariable long targetId,
			ModelMap model) {	
		
		// Use the same jsp as for records
		Person subject = new Person(i);
		return galleryAndRecordController(i, shortSitename, targetId, model, 
				subject, subject.getGallery(), RECORD_VIEW, GALLERY_VIEW);
	}	

	@RequestMapping(value="/male/record/{targetId}")	
	public String maleRecord(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@PathVariable long targetId,
			ModelMap model) {	
		
		Person subject = new Person(i);
		return galleryAndRecordController(i, shortSitename, targetId, model, 
				subject, subject.getRecords(), RECORD_VIEW, RECORD_VIEW);
	}	
	
	@RequestMapping(value="/female/gallery/{targetId}")	
	public String femaleGallery(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@PathVariable long targetId,
			ModelMap model) {	
		
		return maleGallery(i, shortSitename, targetId, model);
	}	

	@RequestMapping(value="/female/record/{targetId}")	
	public String femaleRecord(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@PathVariable long targetId,
			ModelMap model) {	
		
		return maleRecord(i, shortSitename, targetId, model);
	}	
	
	private String galleryAndRecordController(Item i, String shortSitename, long targetId, ModelMap model,
			Person subject, List<Item> items, String jspName, String menuName) {
		
		Page page = getStandardPage(i, shortSitename, jspName, model);
		
		model.addAttribute("_person", subject);
		model.addAttribute("_menu", createPersonMenu(i, subject, menuName));
		model.addAttribute("_subMenu", createPersonSubMenu(i, subject, items, menuName, targetId));
		model.addAttribute("_target", this.itemService.getItem(targetId).setLanguage(i.getLanguage()));
		
		filterBreadcrumbs(page);
		return page.getView();
	}

	private List<MenuItem> createPersonMenu(Item requestItem, Person p, String requestView) {
		List<MenuItem> menu = new ArrayList<MenuItem>();
		MenuItem m;
		Item target;

		// Overview
		m = new MenuItem();
		m.setHref(p.getItem().getUrl()).setTitle("Overview");
		m.setSelected(requestView == null && requestItem.getPath().equals(p.getItem().getPath()));
		menu.add(m);
		
		// History
		m = new MenuItem();
		m.setTitle("History");
		if (p.getDocuments().size() > 0) {
			target = p.getDocuments().get(0);
			m.setHref(target.getUrl());
		}
		else {
			m.setEnabled(false);
			m.setHref("");
		}
		m.setSelected(requestItem.getType().getName().equals("Document"));
		menu.add(m);
		
		// Gallery
		m = new MenuItem();
		m.setTitle("Gallery");
		if (p.getGallery().size() > 0) {
			target = p.getGallery().get(0);
			m.setHref(String.format("%s?view=%s/%d", p.getItem().getUrl(), GALLERY_VIEW, target.getId()));
		}
		else {
			m.setEnabled(false);
			m.setHref("");
		}
		m.setSelected(requestView != null && requestView.equals(GALLERY_VIEW));
		menu.add(m);
		
		// Records
		m = new MenuItem();
		m.setTitle("Records");
		if (p.getRecords().size() > 0) {
			target = p.getRecords().get(0);
			m.setHref(String.format("%s?view=%s/%d", p.getItem().getUrl(), RECORD_VIEW, target.getId()));
		}
		else {
			m.setEnabled(false);
			m.setHref("");
		}
		m.setSelected(requestView != null && requestView.equals(RECORD_VIEW));
		menu.add(m);
		
		return menu;
	}
	
	private List<MenuItem> createPersonSubMenu(Item requestItem, Person p, List<Item> members, String requestView, Long requestId) {
		List<MenuItem> menu = new ArrayList<MenuItem>();
		MenuItem m;
		String url, title;
		
		for (Item i : members) {
			m = new MenuItem();
			
			if (StringUtils.isBlank(requestView)) {
				// True for history
				url = i.getUrl();
				m.setSelected(url.equals(requestItem.getUrl()));
			}
			else {
				url = String.format("%s?view=%s/%d", p.getItem().getUrl(), requestView, i.getId());
				m.setSelected(i.getId() == requestId.longValue());
			}
			
			m.setHref(url);
			
			title = i.getFieldValue("heading");
			if (StringUtils.isBlank(title)) {
				title = i.getFieldValue("heading");
				if (StringUtils.isBlank(title)) {
					title = i.getName();
				}
			}
			
			m.setTitle(title);
			menu.add(m);
		}

		return menu;
	}
		
	private void filterBreadcrumbs(Page p) {
		Iterator<Item> iter = p.getHeader().getBreadcrumbItems().iterator();
		Item i;
		while (iter.hasNext()) {
			i = iter.next();
			if (! (i.getType().getName().equals("Male") || i.getType().getName().equals("Female"))) {
				iter.remove();
			}
		}
	}
	
}
