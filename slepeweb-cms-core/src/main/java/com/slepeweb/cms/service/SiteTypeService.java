package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.SiteType;
import com.slepeweb.cms.except.ResourceException;


public interface SiteTypeService {
	void delete(Long siteId);
	void delete(Long siteId, Long itemtypeId);
	List<SiteType> get(Long siteId);
	SiteType get(Long siteId, Long itemtypeId);
	SiteType save(SiteType st) throws ResourceException;
}
