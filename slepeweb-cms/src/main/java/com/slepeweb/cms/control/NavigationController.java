package com.slepeweb.cms.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
@RequestMapping("/rest")
public class NavigationController extends BaseController {
	
	@Autowired private SiteService siteService;
	@Autowired private ItemService itemService;
	
	@RequestMapping(value="/leftnav/full", method=RequestMethod.GET, produces="application/json")	
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
	
	@RequestMapping(value="/leftnav/lazy/one", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public List<Navigation.Node> doLazyNavOneLevel(@RequestParam(value="key", required=false) String key) {	
		List<Navigation.Node> level0 = new ArrayList<Navigation.Node>();
		
		if (key == null) {
			Site site = this.siteService.getSite("Integration Testing");
			level0.add(dive(site.getItem("/")));
			level0.add(dive(site.getItem("/content")));
			return level0;
		}
		
		return dive(this.itemService.getItem(Long.parseLong(key)), 1).getChildren();		
	}
	
	@RequestMapping(value="/leftnav/lazy/thread", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public List<Navigation.Node> doLazyNavThread(@RequestParam(value="key", required=false) String key) {	
		if (key == null) {
			return doLazyNavOneLevel(key);
		}
		
		Item item = this.itemService.getItem(Long.parseLong(key));
		String[] parts = item.getPath().substring(1).split("/");
		final Vector<String> pathComponents = new Vector<String>(parts.length);
		for (String s : parts) {
			pathComponents.add(s);
		}
		
		List<Navigation.Node> level0 = new ArrayList<Navigation.Node>();
		Site site = this.siteService.getSite("Integration Testing");
		level0.add(dive(site.getItem("/"), pathComponents));
		level0.add(dive(site.getItem("/content"), pathComponents));
		return level0;		
	}
	
	private Navigation.Node dive(Item parentItem) {
		return dive(parentItem, 1);
	}
	
	private Navigation.Node dive(Item parentItem, int numLevels) {
		Navigation.Node pNode = toNode(parentItem);		
		List<Item> bindings = parentItem.getBoundItems();
		pNode.setFolder(bindings.size() > 0);
		
		if (numLevels > 0) {
			for (Item child : bindings) {
				pNode.addChild(dive(child, numLevels - 1));
			}
		}
		
		return pNode;
	}
	
	private Navigation.Node dive(Item parentItem, final Vector<String> pathComponents) {
		Navigation.Node pNode = toNode(parentItem);		
		Navigation.Node cNode;
		List<Item> bindings = parentItem.getBoundItems();
		pNode.setFolder(bindings.size() > 0);
		
		for (Item child : bindings) {				
			if (pathComponents.size() > 0 && child.getSimpleName().equals(pathComponents.get(0))) {
				@SuppressWarnings("unchecked")
				Vector<String> workingPath = (Vector<String>) pathComponents.clone();
				workingPath.remove(0);
				cNode = dive(child, workingPath);
				if (pathComponents.size() == 1) {
					//cNode.setSelected(true);
				}
			}
			else {
				cNode = toNode(child);
				cNode.setFolder(child.getBoundItems().size() > 0);
			}
			
			pNode.addChild(cNode);
		}
		
		return pNode;
	}
	
	private Navigation.Node toNode(Item i) {
		return new Navigation.Node().setTitle(i.getName()).setKey(i.getId().toString());
	}
}
