package com.slepeweb.cms.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.component.Navigation;
import com.slepeweb.cms.component.Navigation.Node;
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
		Site site = this.siteService.getSite("Integration Testing"); // TODO: Only used for testing ???
		Item homepage = site.getItem("/");
		Item contentFolder = site.getItem(Item.CONTENT_ROOT_PATH);		
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
	public List<Navigation.Node> doLazyNavOneLevel(
			@RequestParam(value="key", required=false) Long itemId,
			@RequestParam(value="site", required=false) Long siteId) {	
		
		List<Navigation.Node> level0 = new ArrayList<Navigation.Node>();
		
		if (itemId == null) {
			Site site = this.siteService.getSite(siteId);
			if (site != null) {				
				level0.add(dive(site.getItem("/")));
				level0.add(dive(site.getItem(Item.CONTENT_ROOT_PATH)));
				return level0;
			}
			else {
				// User must choose site from header dropdown
				return new ArrayList<Navigation.Node>();
			}
		}
		
		return dive(this.itemService.getItem(itemId), 1).getChildren();		
	}
	
	@RequestMapping(value="/leftnav/lazy/thread", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public List<Navigation.Node> doLazyNavThread(
			@RequestParam(value="key", required=false) Long itemId,
			@RequestParam(value="site", required=false) Long siteId) {	
		
		if (itemId == null) {
			return doLazyNavOneLevel(itemId, siteId);
		}
		
		Item item = this.itemService.getItem(itemId);
		String[] parts = item.getPath().substring(1).split("/");
		final Vector<String> pathComponents = new Vector<String>(parts.length);
		for (String s : parts) {
			pathComponents.add(s);
		}
		
		List<Navigation.Node> level0 = new ArrayList<Navigation.Node>();
		Site site = item.getSite();
		
		// pathComponents is relative to the pseudo root item, which in this case is '/'
		level0.add(dive(site.getItem("/"), pathComponents));
		
		// The pseudo root for items in the 'Content' section is /content, so this
		// component should NOT be in the pathComponents list, otherwise the main navigation
		// will not open up correctly.
		if (pathComponents.size() > 0 && pathComponents.get(0).equals("content")) {
			pathComponents.remove(0);
		}
		
		level0.add(dive(site.getItem(Item.CONTENT_ROOT_PATH), pathComponents));
		
		return level0;		
	}
	
	/*
	 * This method returns an item path as a sequence of item keys, eg. '/250/265/288', where
	 * '250' is the key for the root item/homepage, and '288' corresponds to the method argument 'itemId'.
	 * 
	 * The returned path is based on (primary) binding links, NOT shortcuts (ie secondaries).
	 */
	@RequestMapping(value="/breadcrumbs/{itemId}", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public String[] breadcrumbs(@PathVariable long itemId) {	
		Item i = this.itemService.getItem(itemId);
		List<Long> trail = new ArrayList<Long>();
		String[] result = null;
		
		if (i != null) {
			trail.add(i.getId());
			
			while ((i = i.getParent()) != null) {
				trail.add(i.getId());
			}
			
			Collections.reverse(trail);
			StringBuilder sb = new StringBuilder();
			for (Long l : trail) {
				sb.append("/").append(l);
			}
			
			result = new String[] {sb.toString()};
		}
		
		return result;
	}

	private Navigation.Node dive(Item parentItem) {
		return dive(parentItem, 1);
	}
	
	private Navigation.Node dive(Item parentItem, int numLevels) {
		Navigation.Node pNode = Node.toNode(parentItem, false), cNode;		
		List<Link> bindings = parentItem.getBindings();
		pNode.setFolder(bindings.size() > 0);
		
		if (numLevels > 0) {
			for (Link l : bindings) {
				cNode = dive(l.getChild(), numLevels - 1);
				cNode.setShortcut(l.getType().equals(LinkType.shortcut));
				cNode.setExtraClasses(Node.getCmsIconClass(l.getChild(), cNode.isShortcut()));
				pNode.addChild(cNode);
			}
		}
		
		return pNode;
	}
	
	private Navigation.Node dive(Item parentItem, final Vector<String> pathComponents) {
		Navigation.Node pNode = Node.toNode(parentItem, false);		
		Navigation.Node cNode;
		List<Link> bindings = parentItem.getBindings();
		pNode.setFolder(bindings.size() > 0);
		Item child;
		boolean shortcut;
		
		for (Link l : bindings) {
			child = l.getChild();
			shortcut = l.getType().equals(LinkType.shortcut);
			
			if (! shortcut && pathComponents.size() > 0 && child.getSimpleName().equals(pathComponents.get(0))) {
				@SuppressWarnings("unchecked")
				Vector<String> workingPath = (Vector<String>) pathComponents.clone();
				workingPath.remove(0);
				
				// There are more nodes below this one - continue diving
				cNode = dive(child, workingPath);
				if (pathComponents.size() == 1) {
					//cNode.setSelected(true);
				}
			}
			else {
				// We've reached the end of this trail
				cNode = Node.toNode(child, shortcut);
				cNode.setFolder(child.getBoundItems().size() > 0);
			}
			
			cNode.setShortcut(shortcut);
			pNode.addChild(cNode);
		}
		
		return pNode;
	}	
}
