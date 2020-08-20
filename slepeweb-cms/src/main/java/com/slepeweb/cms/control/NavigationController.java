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
	
	@RequestMapping(value="/leftnav/lazy/one", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public List<Navigation.Node> doLazyNavOneLevel(
			@RequestParam(value="key", required=false) Long origId,
			@RequestParam(value="site", required=false) Long siteId) {	
		
		List<Navigation.Node> level0 = new ArrayList<Navigation.Node>();
		
		if (origId == null) {
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
		
		return dive(this.itemService.getEditableVersion(origId), 1).getChildren();		
	}
	
	@RequestMapping(value="/leftnav/lazy/thread", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public List<Navigation.Node> doLazyNavThread(
			@RequestParam(value="key", required=false) Long origId,
			@RequestParam(value="site", required=false) Long siteId) {	
		
		if (origId == null) {
			return doLazyNavOneLevel(origId, siteId);
		}
		
		Item item = this.itemService.getEditableVersion(origId);
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
	@RequestMapping(value="/breadcrumbs/{origId}", method=RequestMethod.GET, produces="application/json")	
	@ResponseBody
	public String[] breadcrumbs(@PathVariable long origId) {	
		Item i = this.itemService.getEditableVersion(origId);
		List<Long> trail = new ArrayList<Long>();
		String[] result = null;
		
		if (i != null) {
			trail.add(i.getOrigId());
			
			while ((i = i.getParent()) != null) {
				trail.add(i.getOrigId());
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
		Navigation.Node pNode = Node.toNode(parentItem), cNode;		
		List<Link> bindings = parentItem.getBindings();
		pNode.setFolder(bindings.size() > 0);
		
		if (numLevels > 0) {
			for (Link l : bindings) {
				cNode = dive(l.getChild(), numLevels - 1);
				cNode.setShortcut(l.getChild().isShortcut());
				cNode.setExtraClasses(Node.getCmsIconClass(l.getChild()));
				pNode.addChild(cNode);
			}
		}
		
		return pNode;
	}
	
	private Navigation.Node dive(Item parentItem, final Vector<String> pathComponents) {
		Navigation.Node pNode = Node.toNode(parentItem);		
		Navigation.Node cNode;
		List<Link> bindings = parentItem.getBindings();
		pNode.setFolder(bindings.size() > 0);
		Item child;
		
		for (Link l : bindings) {
			child = l.getChild();
			
			if (
					! child.isShortcut() && 
					pathComponents.size() > 0 && 
					child.getSimpleName().equals(pathComponents.get(0))) {
				
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
				cNode = Node.toNode(child);
				cNode.setFolder(child.getBoundItems().size() > 0);
			}
			
			cNode.setShortcut(child.isShortcut());
			pNode.addChild(cNode);
		}
		
		return pNode;
	}	
}
