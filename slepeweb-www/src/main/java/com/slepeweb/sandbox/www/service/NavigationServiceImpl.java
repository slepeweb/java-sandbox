package com.slepeweb.sandbox.www.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.slepeweb.sandbox.www.model.Link;
import com.slepeweb.sandbox.www.model.Page;

@Service( "navigationService" )
public class NavigationServiceImpl implements NavigationService {

	public List<Link> getGlobalNavigation(Page page) {
		// TODO Auto-generated method stub
		return new ArrayList<Link>();
	}

	public List<Link> getTopNavigation(Page page) {
		List<Link> links = new ArrayList<Link>();
		links.add(makeLink("Home", "/home", page));
		links.add(makeLink("Projects", "/projects", page));
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

	private Link makeLink(String label, String href, Page currentPage) {
		Link l = new Link().setTitle(label).setHref(href);
		l.setSelected(currentPage != null && currentPage.getPath().equals(href));
		return l;
	}
}
