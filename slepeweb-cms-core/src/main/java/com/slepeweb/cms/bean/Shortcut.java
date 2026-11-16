package com.slepeweb.cms.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class Shortcut extends Item {
	
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(Shortcut.class);
	
	private Item referred;
	private Long id4OrthogonalParent;
	private boolean replacedParentLinks2This;
		
	public Item getReferred() {
		return this.referred;
	}
	
	public Shortcut setReferred(Item referred) {
		this.referred = referred;
		return this;
	}
	

	@Override
	public boolean isShortcut() {
		return true;
	}
	
	public boolean isLinked() {
		return this.referred != null;
	}
	
	public Long getId4OrthogonalParent() {
		return id4OrthogonalParent;
	}

	public void setId4OrthogonalParent(Long id4OrthogonalParent) {
		this.id4OrthogonalParent = id4OrthogonalParent;
	}

	@Override
	public List<Link> getParentLinks(boolean includeBinding) {
		
		if (! this.getCmsService().isDeliveryContext() || ! includeBinding || this.replacedParentLinks2This) {
			return super.getParentLinks(includeBinding);
		}
		
		/*
		 * To reach this point:
		 * a) The context IS 'delivery'
		 * b) binding links ARE required
		 * c) We haven't already replaced the shortcut's parent binding
		 */
		
		List<Link> shortcutParentLinks = getLinkService().getParentLinks(this.id4OrthogonalParent);
		if (shortcutParentLinks.size() == 0 ) {
			LOG.info(String.format("Shortcut [%s] has no binding parent [%d]", this, this.id4OrthogonalParent));
			return super.getParentLinks(true);
		}
		
		// This includes binding, shortcut, component, relation and inline link types
		List<Link> allParentLinks = super.getParentLinks(true);
		
		// We don't want the parent binding of the target item, we want the one for the shortcut item
		Iterator<Link> iter = allParentLinks.iterator();
		Link l;
		
		// Remove the parent binding to the target ...
		while (iter.hasNext()) {
			l = iter.next();
			if (l.getType().equals(LinkType.binding)) {
				iter.remove();
				break;
			}
		}
		
		// ... and replace with the one to the shortcut
		allParentLinks.add(shortcutParentLinks.get(0));
		
		this.replacedParentLinks2This = true;
		return allParentLinks;
	}
	
}
