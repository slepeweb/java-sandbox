package com.slepeweb.site.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.site.model.LinkTarget;

public interface NavigationService {
	LinkTarget drillDown(Item parent, int numLevels, String currentItemPath);
}
