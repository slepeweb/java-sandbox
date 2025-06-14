package com.slepeweb.site.geo.bean;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.site.model.LinkTarget;

public class SectionMenu {
	private LinkTarget root;
	
	public SectionMenu(Item i) {
		if (i.isSiteRoot() || i.isHiddenFromNav()) {
			return;
		}
		
		LinkTarget lt, lt2;
		Item p = i.getOrthogonalParent();
		this.root = new LinkTarget(p);
		
		for (Link sib : p.getBindings()) {
			if (! sib.getChild().isHiddenFromNav()) {
				lt = new LinkTarget(sib.getChild());
				this.root.getChildren().add(lt);
				lt.setSelected(sib.getChild().getId().equals(i.getId()));
				
				if (lt.isSelected()) {				
					for (Link l2 : sib.getChild().getBindings()) {
						lt2 = new LinkTarget(l2.getChild());
						lt.getChildren().add(lt2);
					}
				}
			}
		}
	}
	
	public LinkTarget getRoot() {
		return root;
	}
}
