package com.slepeweb.site.anc.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemFilter;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.common.solr.bean.SolrConfig;
import com.slepeweb.site.anc.bean.MenuItem;
import com.slepeweb.site.anc.bean.Person;
import com.slepeweb.site.anc.bean.svg.AncestryDiagram;
import com.slepeweb.site.anc.bean.svg.SvgSupport;
import com.slepeweb.site.anc.service.AncCookieService;
import com.slepeweb.site.anc.service.SolrService4Ancestry;
import com.slepeweb.site.bean.SolrParams4Site;
import com.slepeweb.site.control.BaseController;
import com.slepeweb.site.model.Page;

@Controller
@RequestMapping("/spring/anc")
public class AncestryPageController extends BaseController {
	
	public static final String HISTORY_VIEW = "history";
	public static final String GALLERY_VIEW = "gallery";
	public static final String RECORD_VIEW = "record";
	
	@Autowired private ItemService itemService;
	@Autowired private SolrService4Ancestry solrService4Ancestry;
	@Autowired private AncCookieService ancCookieService;
	
	@Override
	public Page getStandardPage(Item i, String shortSitename, String viewNameSuffix, ModelMap model) {		
		Page p = super.getStandardPage(i, shortSitename, viewNameSuffix, model);
		
		String langPrefix = "";		
		if (i.getSite().isMultilingual()) {
			langPrefix = "/" + i.getLanguage();
		}	
		model.addAttribute("_languageUrlPrefix", langPrefix);
		
		return p;
	}
	
	@ModelAttribute(value="_history")
	public List<ItemIdentifier> breadcrumbTrail(HttpServletRequest req) {
		Item i = (Item) req.getAttribute(ITEM);
		List<ItemIdentifier> list = this.ancCookieService.getBreadcrumbsCookieValue(i.getSite(), req);
		return list;
	}
	
	@RequestMapping(value="/homepage")	
	public String homepage(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@ModelAttribute("_site") Site site, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "homepage", model);
		
		List<Person> tops = new ArrayList<Person>();
		for (Item c : i.getBoundItems(new ItemFilter().setTypes(new String[] {Person.BOY, Person.GIRL}))) {
			tops.add(new Person(c));
		}
		
		model.addAttribute("_rootEntries", tops);
		model.addAttribute("_breadcrumbs", personBreadcrumbs(page));		
		return page.getView();
	}

	@RequestMapping(value="/notfound")	
	public String notfound(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@ModelAttribute("_site") Site site, 
			ModelMap model) {	
		
		return error(i, shortSitename, site, model);
	}

	@RequestMapping(value="/error")	
	public String error(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@ModelAttribute("_site") Site site, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "error", model);
		return page.getView();
	}

	@RequestMapping(value="/boy")	
	public String boy(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "person", model);
		page.setTitle(i.getName());
		
		Person subject = new Person(i);
		model.addAttribute("_person", subject);
		
		List<SvgSupport> svgs = new ArrayList<SvgSupport>();
		svgs.add(new SvgSupport(subject, 0));

		if (subject.getRelationships().size() > 1) {
			for (int j = 1; j < subject.getRelationships().size(); j++) {
				svgs.add(new SvgSupport(subject, j));
			}
		}
		
		model.addAttribute("_svgList", svgs);		
		model.addAttribute("_menu", createPersonMenu(i, subject, null));	
		
		// Confusing - these _breadcrumbs form the hierarchy of ancestor boy/girl items
		model.addAttribute("_breadcrumbs", personBreadcrumbs(page));
		
		// Whereas these 'proper' breadcrumbs identify the boy/girl click history !!!
		// See also breadcrumbTrail() method, which sets the _history attribute for all other pages.
		model.addAttribute("_history", this.ancCookieService.updateBreadcrumbsCookie(i, req, res));
		
		return page.getView();
	}
	
	@RequestMapping(value="/girl")	
	public String girl(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		return boy(i, shortSitename, req, res, model);
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
		
		model.addAttribute("_breadcrumbs", personBreadcrumbs(page));
		return page.getView();
	}	

	@RequestMapping(value="/boy/gallery/{targetId}")	
	public String boyGallery(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@PathVariable long targetId,
			ModelMap model) {	
		
		Person subject = new Person(i);
		Page page = getStandardPage(i, shortSitename, GALLERY_VIEW, model);
		
		model.addAttribute("_person", subject);
		model.addAttribute("_menu", createPersonMenu(i, subject, GALLERY_VIEW));
		model.addAttribute("_gallery", subject.getGallery());
		
		model.addAttribute("_breadcrumbs", personBreadcrumbs(page));
		return page.getView();
	}	

	@RequestMapping(value="/boy/record/{targetId}")	
	public String boyRecord(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@PathVariable long targetId,
			ModelMap model) {	
		
		Person subject = new Person(i);
		return galleryAndRecordController(i, shortSitename, targetId, model, 
				subject, subject.getRecords(), RECORD_VIEW, RECORD_VIEW);
	}	
	
	@RequestMapping(value="/girl/gallery/{targetId}")	
	public String girlGallery(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@PathVariable long targetId,
			ModelMap model) {	
		
		return boyGallery(i, shortSitename, targetId, model);
	}	

	@RequestMapping(value="/girl/record/{targetId}")	
	public String girlRecord(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@PathVariable long targetId,
			ModelMap model) {	
		
		return boyRecord(i, shortSitename, targetId, model);
	}	
	
	@RequestMapping(value="/search", method=RequestMethod.POST)	
	public String search(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			HttpServletRequest request,
			ModelMap model) {	
				
		String searchText = iso2utf8(request.getParameter("searchtext"));
		String pageNum = request.getParameter("page");

		Page page = getStandardPage(i, shortSitename, "search", model);
		page.setTitle(i.getName());
		
		SolrParams4Site params = new SolrParams4Site(i, new SolrConfig());
		params.setPageSize(4).setPageNum(pageNum);
		params.setSearchText(searchText);
		model.addAttribute("_params", params);
		model.addAttribute("_search", this.solrService4Ancestry.query(params));
		return page.getView();
	}
	
	@RequestMapping(value="/homepage/diagram/{id}", method=RequestMethod.GET)	
	public String diagram(
			@PathVariable long id, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
				
		Item i = this.itemService.getItem(id);
		String view = "diagram";
		
		if (i != null && (i.getType().getName().equals(Person.BOY) || i.getType().getName().equals(Person.GIRL))) {
			AncestryDiagram a = new AncestryDiagram(new Person(i));
			Page page = getStandardPage(i, shortSitename, view, model);			
			model.addAttribute("_diagram", a.build(new Person(i)));
			return page.getView();
		}
		
		return composeJspPath(shortSitename, view);
	}
	
	private String galleryAndRecordController(Item i, String shortSitename, long targetId, ModelMap model,
			Person subject, List<Item> items, String jspName, String menuName) {
		
		Page page = getStandardPage(i, shortSitename, jspName, model);
		
		model.addAttribute("_person", subject);
		model.addAttribute("_menu", createPersonMenu(i, subject, menuName));
		model.addAttribute("_subMenu", createPersonSubMenu(i, subject, items, menuName, targetId));
		model.addAttribute("_target", this.itemService.getItem(targetId).setLanguage(i.getLanguage()));
		
		model.addAttribute("_breadcrumbs", personBreadcrumbs(page));
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
		
		// Scans
		m = new MenuItem();
		m.setTitle("Scans");
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
		
		// Notes
		m = new MenuItem();
		m.setTitle("Notes");
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
		
	private List<Person> personBreadcrumbs(Page p) {
		List<Person> trail = new ArrayList<Person>();

		for (Item i : p.getHeader().getBreadcrumbItems()) {
			if (i.getType().getName().equals(Person.BOY) || i.getType().getName().equals(Person.GIRL)) {
				trail.add(new Person(i));
			}
		}
		
		return trail;
	}
	
	/*
	 * I can't see a way to make the server process the form data as utf-8. All attempts to set the
	 * character encoding for dealing with non-english search terms as utf-8 have failed.
	 */
	private String iso2utf8(String s) {
		if (StringUtils.isNotBlank(s)) {
			try {
				return new String(s.getBytes("ISO-8859-1"));
			}
			catch (Exception e) {
			}
		}
		return s;
	}
}
