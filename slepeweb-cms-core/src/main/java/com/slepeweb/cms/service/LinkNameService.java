package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.LinkName;


public interface LinkNameService {
	void deleteLinkName(LinkName ln);
	LinkName save(LinkName lt);
	List<LinkName> getLinkNames(Long siteId, Long linkTypeId);
	LinkName getLinkName(Long siteId, Long linkTypeId, String name);
}
