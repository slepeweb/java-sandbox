package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;

public interface SolrService4Cms {
	
	void indexSection(Item parentItem);
	boolean save(Item i);
	boolean remove(Item i);
	boolean remove(Site s);
	Object getDocument(Item i);
	Object getDocument(Item i, String language);
}
