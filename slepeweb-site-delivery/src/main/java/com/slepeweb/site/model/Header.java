package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.site.constant.FieldName;

public class Header implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<String> stylesheets, javascripts;
	private List<LinkTarget> globalNavigation, topNavigation;
	private List<Item> breadcrumbItems;
	private Page page;
	
	public Header(Page page) {
		this.stylesheets = new ArrayList<String>();
		this.javascripts = new ArrayList<String>();
		this.page = page;
	}
	
	private void populateBreadcrumbs() {
		this.breadcrumbItems = new ArrayList<Item>();
		Item i = getPage().getItem();
		
		while (! i.getPath().equals("/")) {
			this.breadcrumbItems.add(i);
			i = i.getParent();
		}
		
		// Lastly, add the root item
		this.breadcrumbItems.add(i);
		Collections.reverse(this.breadcrumbItems);
	}
	
	private void populateTopNavigation(Item i) {
		this.topNavigation = new ArrayList<LinkTarget>();		
		Item root = i.getCmsService().getItemService().getItem(i.getSite().getId(), "/");
		LinkTarget lt;
		
		boolean swapLoginForLogout = false;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null) {
			Object obj = authentication.getPrincipal();
			if (obj instanceof User) {
				User user = (User) obj;
				for (GrantedAuthority auth : user.getAuthorities()) {
					if (auth.getAuthority().equals("SWS_GUEST")) {
						swapLoginForLogout = true;
						break;
					}
				}
			}
		}
		
		if (root != null) {
			Item child;
			for (Link l : root.getBindings()) {
				child = l.getChild();
				
				if (! child.getFieldValue(FieldName.HIDE_FROM_NAV, "").equalsIgnoreCase("yes")) {
					lt = new LinkTarget(child).
							setSelected(i.getPath().startsWith(child.getPath()));
					
					if (swapLoginForLogout && lt.getHref().equals("/login")) {
						lt.setHref("/j_spring_security_logout").setTitle("Logout");
					}
					this.topNavigation.add(lt);
				}
			}
		}
	}

	public List<String> getStylesheets() {
		return stylesheets;
	}

	public List<String> getJavascripts() {
		return javascripts;
	}

	public List<LinkTarget> getGlobalNavigation() {
		return globalNavigation;
	}
	
	public List<LinkTarget> getTopNavigation() {
		if (this.topNavigation == null) {
			populateTopNavigation(getPage().getItem());
		}
		return this.topNavigation;
	}
	
	public List<LinkTarget> getBreadcrumbs() {
		List<LinkTarget> breadcrumbs = new ArrayList<LinkTarget>(getBreadcrumbItems().size());
		for (Item i : getBreadcrumbItems()) {
			breadcrumbs.add(new LinkTarget(i));
		}
		return breadcrumbs;
	}
	
	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public List<Item> getBreadcrumbItems() {
		if (this.breadcrumbItems == null) {
			populateBreadcrumbs();
		}
		return breadcrumbItems;
	}
}
