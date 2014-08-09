package com.slepeweb.cms.control;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.component.Navigation;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.SiteService;

@Controller
@RequestMapping("/rest/cms")
public class NavigationRestController extends BaseController {
	
	@Autowired private SiteService siteService;
	@Autowired private ItemService itemService;
	
	@RequestMapping(value="/leftnav", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public List<Navigation.Node> doFullNav() {	
		Site site = this.siteService.getSite("Integration Testing");
		Item homepage = site.getItem("/");
		Item contentFolder = site.getItem("/content");		
		Navigation nav = new Navigation();
		
		if (homepage != null) {
			nav.getNodes().add(dive(homepage));		
		}
		
		if (contentFolder != null) {
			nav.getNodes().add(dive(contentFolder));		
		}
		
		return nav.getNodes();
	}
	
	@RequestMapping(value="/lazyleftnav", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public List<Navigation.Node> doLazyNav(@RequestParam(value="key", required=false) String key) {	
		if (key == null) {
			List<Navigation.Node> level0 = new ArrayList<Navigation.Node>();
			Site site = this.siteService.getSite("Integration Testing");			
			level0.add(toNode(site.getItem("/")).setFolder(true).setLazy(true));
			level0.add(toNode(site.getItem("/content")).setFolder(true).setLazy(true));
			return level0;
		}
		
		return dive(this.itemService.getItem(Long.parseLong(key)), 1).getChildren();		
	}
	
	private Navigation.Node dive(Item parentItem) {
		return dive(parentItem, -1);
	}
	
	private Navigation.Node dive(Item parentItem, int numLevels) {
		Navigation.Node pNode = toNode(parentItem);
		if (numLevels > -1) pNode.setLazy(true);
		List<Item> bindings = parentItem.getBoundItems();
		pNode.setFolder(bindings.size() > 0);
		
		if (numLevels != 0) {
			Navigation.Node cNode;
			for (Item child : bindings) {
				cNode = dive(child, --numLevels);
				pNode.getChildren().add(cNode);
				if (numLevels == 1) cNode.setLazy(true);
			}
		}
		
		return pNode;
	}
	
	private Navigation.Node toNode(Item i) {
		return new Navigation.Node().setTitle(i.getName()).setKey(i.getId().toString());
	}
}
