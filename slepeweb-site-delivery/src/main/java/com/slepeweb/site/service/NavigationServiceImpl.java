package com.slepeweb.site.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.StringWrapper;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.site.model.LinkTarget;

@Service("navigationService")
public class NavigationServiceImpl implements NavigationService {
	private static Logger LOG = Logger.getLogger(NavigationServiceImpl.class);
	
	public LinkTarget drillDown(Item parent, int numLevels, String currentItemPath) {
		if (! parent.getType().isMedia()) {
			LinkTarget parentTarget = createLinkTarget(parent, currentItemPath);
			LOG.debug(String.format("Created link: %s", parent.getPath()));
			
			if (parentTarget != null && numLevels-- > 0) {
				LinkTarget childTarget;
				
				if (! parent.getFieldValue(FieldName.HIDE_CHILDREN_FROM_NAV, new StringWrapper("")).equalsIgnoreCase("yes")) {
					
					for (Link l : parent.getBindings()) {
						childTarget = drillDown(l.getChild(), numLevels, currentItemPath);
						if (childTarget != null) {
							parentTarget.getChildren().add(childTarget);
						}
					}
				}
			}
	
			return parentTarget;
		}
		
		return null;
	}
	
	private LinkTarget createLinkTarget(Item child, String currentItemPath) {		
		if (! child.getFieldValue(FieldName.HIDE_FROM_NAV, new StringWrapper("")).equalsIgnoreCase("yes")) {
			LinkTarget lt = new LinkTarget(child);
			if (currentItemPath != null) {
				lt.setSelected(currentItemPath.startsWith(child.getPath()));
			}
			return lt;
		}
		
		return null;
	}

}
