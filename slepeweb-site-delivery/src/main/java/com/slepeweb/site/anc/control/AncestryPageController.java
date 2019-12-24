package com.slepeweb.site.anc.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.site.anc.bean.MenuItem;
import com.slepeweb.site.anc.bean.Person;
import com.slepeweb.site.anc.bean.svg.SvgSupport;
import com.slepeweb.site.control.BaseController;
import com.slepeweb.site.model.Page;

@Controller
@RequestMapping("/spring/anc")
public class AncestryPageController extends BaseController {
	
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
		model.addAttribute("_menu", createPersonMenu(i, subject));
		
		filterBreadcrumbs(page);		
		return page.getView();
	}
	
	private List<MenuItem> createPersonMenu(Item requestItem, Person p) {
		List<MenuItem> menu = new ArrayList<MenuItem>();

		MenuItem m = new MenuItem().setEnabled(true);
		m.setHref(p.getItem().getPath()).setTitle("Overview").setSelected(requestItem.getPath().equals(p.getItem().getPath()));
		menu.add(m);
		
		menu.add(createPersonMenuItem("History", p.getDocuments(), requestItem));
		menu.add(createPersonMenuItem("Gallery", p.getGallery(), requestItem));
		menu.add(createPersonMenuItem("Records", p.getRecords(), requestItem));
		
		return menu;
	}
	
	private List<MenuItem> createPersonSubMenu(Item requestItem, List<Item> members) {
		List<MenuItem> menu = new ArrayList<MenuItem>();
		MenuItem m;
		
		for (Item i : members) {
			m = new MenuItem().setEnabled(true);
			m.setHref(i.getPath()).setTitle(i.getFieldValue("heading")).setSelected(i.getPath().equals(requestItem.getPath()));
			menu.add(m);
		}

		return menu;
	}
	
	private MenuItem createPersonMenuItem(String label, List<Item> list, Item requestItem) {
		MenuItem m = new MenuItem().setEnabled(list.size() > 0);
		
		if (m.isEnabled()) {
			Item first = list.get(0);
			m.setHref(first.getPath()).setSelected(first.getPath().equals(requestItem.getPath()));
		}
		
		m.setTitle(label);
		return m;
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
		page.setTitle(i.getName());
		
		Person subject = new Person(i.getParent());
		model.addAttribute("_person", subject);
		model.addAttribute("_menu", createPersonMenu(i, subject));
		model.addAttribute("_subMenu", createPersonSubMenu(i, subject.getDocuments()));
		
		filterBreadcrumbs(page);
		return page.getView();
	}	
}
