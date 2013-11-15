package com.slepeweb.sandbox.www.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.slepeweb.sandbox.www.model.Link;
import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.model.User;

@Service( "navigationService" )
public class NavigationServiceImpl implements NavigationService {

	public List<Link> getGlobalNavigation(Page page) {
		// TODO Auto-generated method stub
		return new ArrayList<Link>();
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
		
		for (Link l : links) {
			l.setSelected(l.getHref().startsWith(page.getHref()));
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
