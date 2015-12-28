package com.slepeweb.site.control;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.SiteService;
import com.slepeweb.cms.service.TagService;
import com.slepeweb.site.model.LinkTarget;
import com.slepeweb.site.service.NavigationService;

@Controller
@RequestMapping("/rest")
public class SiteRestController extends BaseController {
	//private static Logger LOG = Logger.getLogger(SiteRestController.class);
	public static final String SLEPEWEB_SITENAME = "Slepeweb";
	public static final String NOCRAWL = "nocrawl";
	
	@Autowired private SiteService siteService;
	@Autowired private NavigationService navigationService;
	@Autowired private TagService tagService;
	
	@RequestMapping(value="/sitemap/sws.txt", method=RequestMethod.GET, produces="text/plain")
	@ResponseBody
	public String swsSitemap() {	
		
		Site s = this.siteService.getSite(SLEPEWEB_SITENAME);
		if (s == null) {
			return "";
		}
		
		// Get all pages on the SWS site
		LinkTarget root = this.navigationService.drillDown(s.getItem("/"), 4, null);
		
		// Put the page URLs into a Set
		Set<String> set = new HashSet<String>(37);
		this.drillDown(root, set);
		
		// Remove the homepage url, since it forwards to /about
		set.remove("/");
		
		// Remove any pages tagged with 'nocrawl'
		for (Item i : this.tagService.getTaggedItems(s.getId(), NOCRAWL)) {
			set.remove(i.getPath());
		}
		
		// Put all remaining urls into a single string
		StringBuilder sb = new StringBuilder();
		String prefix = "http://www.slepeweb.com";
		Iterator<String> iter = set.iterator();
		while (iter.hasNext()) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(prefix).append(iter.next());
		}		

		return sb.toString();		
	}
	
	@RequestMapping(value="/robots/sws.txt", method=RequestMethod.GET, produces="text/plain")
	@ResponseBody
	public String swsRobots() {
		Site s = this.siteService.getSite(SLEPEWEB_SITENAME);
		if (s == null) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("Sitemap: http://www.slepeweb.com/rest/sitemap/sws.txt\n");
		sb.append("User-agent: *\n");
		sb.append("Disallow: /proxy\n");
		
		for (Item i : this.tagService.getTaggedItems(s.getId(), NOCRAWL)) {
			sb.append(String.format("Disallow: %s\n", i.getPath()));
		}
		
		return sb.toString();		
	}
	
	private void drillDown(LinkTarget node, Set<String> set) {
		set.add(node.getHref());
		for (LinkTarget lt : node.getChildren()) {
			drillDown(lt, set);
		}
	}
}
