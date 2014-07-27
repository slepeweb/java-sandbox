package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Link;


public interface LinkService {
	void deleteLinks(Long parentId, Long childId);
	void deleteLinks(Long parentId, String linkType, String name);
	List<Link> getLinks(Long parentId);
	Link getLink(Long parentId, Long childId);
	Link save(Link l);
}
