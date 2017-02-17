package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.except.MissingDataException;


public interface SiteService {
	void deleteSite(Site s);
	Site getSite(String name);
	Site getSite(Long id);
	List<Site> getAllSites();
	Site save(Site s) throws MissingDataException;
}
