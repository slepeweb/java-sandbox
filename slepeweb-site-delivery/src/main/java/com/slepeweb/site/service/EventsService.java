package com.slepeweb.site.service;

import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.site.bean.DatedLinkTarget;

public interface EventsService {
	List<DatedLinkTarget> getCombinedEvents(Item i);
}
