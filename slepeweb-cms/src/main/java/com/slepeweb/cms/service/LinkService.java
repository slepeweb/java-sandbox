package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;


public interface LinkService {
	void insertLink(Link s);
	void deleteLinks(Long parentId, Long childId);
	void deleteLinks(Long parentId, String linkType, String name);
	List<Link> getLinks(Item parent, String linkType, String name);
}
