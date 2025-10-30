package com.slepeweb.site.geo.bean;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.utils.CmsUtil;
import com.slepeweb.site.model.LinkTarget;

public class SectionMenu {
	private LinkTarget root;
	
	public SectionMenu(Item i) {
		if (i.isSiteRoot() || ! CmsUtil.isAppropriate4Navigation(i)) {
			return;
		}
		
		LinkTarget lt;
		Item p = i.getOrthogonalParent();
		this.root = new LinkTarget(p);
		
		for (Link sib : p.getBindings()) {
			if (CmsUtil.isAppropriate4Navigation(sib.getChild())) {
				lt = new LinkTarget(sib.getChild());
				this.root.getChildren().add(lt);
				lt.setSelected(sib.getChild().getId().equals(i.getId()));
				
				if (lt.isSelected()) {				
					for (Link l2 : sib.getChild().getBindings()) {
						if (CmsUtil.isAppropriate4Navigation(l2.getChild())) {
							lt.getChildren().add(new LinkTarget(l2.getChild()));
						}
					}
				}
			}
		}
	}
	
	public LinkTarget getRoot() {
		return root;
	}
}
