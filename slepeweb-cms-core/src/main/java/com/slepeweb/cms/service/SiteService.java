package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.except.ResourceException;


public interface SiteService {
	void deleteSite(Site s);
	Site getSite(String name);
	Site getSiteByShortname(String name);
	Site getSite(Long id);
	List<Site> getAllSites();
	List<Site> getAllSites(User u, String role);
	Site save(Site s) throws ResourceException;
	List<User> getContributors(long siteId);
}
