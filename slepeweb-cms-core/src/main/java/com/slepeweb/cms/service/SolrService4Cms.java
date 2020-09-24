package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.common.solr.bean.SolrResponse;

public interface SolrService4Cms {
	
	int indexSection(Item parentItem);
	boolean save(Item i);
	boolean remove(Item i);
	boolean remove(Site s);
	boolean removeSection(Item i);
	Object getDocument(Item i);
	Object getDocument(Item i, String language);
	SolrResponse<SolrDocument4Cms> query(Object p);
}
