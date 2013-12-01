package com.slepeweb.sandbox.www.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.slepeweb.sandbox.orm.User;
import com.slepeweb.sandbox.www.model.Link;
import com.slepeweb.sandbox.www.model.Page;

@Service( "navigationService" )
public class NavigationServiceImpl implements NavigationService {

	public List<Link> getSandboxNavigation(Page page) {
		List<Link> links = new ArrayList<Link>();
		links.add(makeLink("Platform", "/sandbox/platform"));
		links.add(makeLink("Web services", "/sandbox/ws"));
		
		Link users = makeLink("User accounts", "/sandbox/user");
		links.add(users);
		
		List<Link> subLinks = new ArrayList<Link>();
		users.setChildren(subLinks);
		
		subLinks.add(makeLink("Introduction", "/sandbox/user/intro"));
		subLinks.add(makeLink("List users", "/sandbox/user/list"));
		subLinks.add(makeLink("Add a user", "/sandbox/user/add"));
		
		// Identify selected links
		for (Link topLink : links) {
			topLink.setSelected(page.getHref().startsWith(topLink.getHref()));
			for (Link subLink : topLink.getChildren()) {
				subLink.setSelected(page.getHref().startsWith(subLink.getHref()));
			}
		}
		
		return links;
	}

	public List<Link> getTopNavigation(Page page, User user) {
		List<Link> links = new ArrayList<Link>();
		links.add(makeLink("About", "/about"));
		links.add(makeLink("Profile", "/profile"));
		links.add(makeLink("Sandbox", "/sandbox"));
		links.add(makeLink("Contact", "/contact"));
		
		if (user == null || user.getAlias() == null) {
			try {
				links.add(makeLink("Login", "/login?nextPath=" + URLEncoder.encode(page.getHref(), "utf-8")));
			}
			catch (Exception e) {}
		}
//		else if (user.hasRole(Role.USER_ADMIN_ROLE)) {
//			links.add(makeLink("Users", "/user/list"));
//		}
		
		// Identify selected links
		for (Link topNavLink : links) {
			topNavLink.setSelected(page.getHref().startsWith(topNavLink.getHref()));
		}
		
		return links;
	}

	public List<Link> getBottomNavigation(Page page) {
		// TODO Auto-generated method stub
		return new ArrayList<Link>();
	}

	public List<Link> getBreadcrumbs(Page page) {
		// TODO Auto-generated method stub
		return new ArrayList<Link>();
	}

	private Link makeLink(String label, String href) {
		return new Link().setTitle(label).setHref(href);
	}
}
