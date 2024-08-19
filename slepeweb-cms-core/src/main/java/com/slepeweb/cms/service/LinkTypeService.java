package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.LinkType;


public interface LinkTypeService {
	void deleteLinkType(LinkType lt);
	LinkType save(LinkType lt);
	LinkType getLinkType(String name);
	List<LinkType> getLinkTypes();
}
