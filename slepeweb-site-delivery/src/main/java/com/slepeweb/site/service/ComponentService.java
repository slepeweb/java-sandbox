package com.slepeweb.site.service;

import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.site.model.Component;

public interface ComponentService {
	List<Component> getComponents(Item i);
	List<Component> getComponents(Item i, String linkName);
}
