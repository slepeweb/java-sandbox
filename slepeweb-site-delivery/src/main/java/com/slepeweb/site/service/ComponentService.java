package com.slepeweb.site.service;

import java.util.List;

import com.slepeweb.cms.bean.Link;
import com.slepeweb.site.model.SimpleComponent;

public interface ComponentService {
	List<SimpleComponent> getComponents(List<Link> componentLinks);
	List<SimpleComponent> getComponents(List<Link> componentLinks, String linkName);
}
