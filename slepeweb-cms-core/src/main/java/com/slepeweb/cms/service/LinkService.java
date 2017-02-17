package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.except.MissingDataException;


public interface LinkService {
	void deleteLinks(Long parentId, Long childId);
	void deleteLinks(Long parentId, String linkType, String name);
	List<Link> getLinks(Long parentId);
	List<Link> getBindings(Long parentId);
	List<Link> getBindings2TrashedItems(Long parentId);
	List<Link> getInlines(Long parentId);
	List<Link> getRelations(Long parentId);
	List<Link> getComponents(Long parentId);
	Link getLink(Long parentId, Long childId);
	Link save(Link l) throws MissingDataException;
	int getCount();
	int getCount(Long parentId);
	Link getParent(Long childId);
}
