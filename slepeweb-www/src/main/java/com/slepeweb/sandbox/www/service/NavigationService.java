package com.slepeweb.sandbox.www.service;

import java.util.List;

import com.slepeweb.sandbox.www.model.Link;
import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.model.User;

public interface NavigationService {
	List<Link> getGlobalNavigation(Page page);
	List<Link> getTopNavigation(Page page, User user);
	List<Link> getBottomNavigation(Page page);
	List<Link> getBreadcrumbs(Page page);
}