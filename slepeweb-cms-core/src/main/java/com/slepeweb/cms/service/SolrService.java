package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.solr.SolrDocument;
import com.slepeweb.cms.bean.solr.SolrParams;
import com.slepeweb.cms.bean.solr.SolrResponse;

public interface SolrService {
	
	void indexSection(Item parentItem);
	boolean save(Item i);
	boolean remove(Item i);
	boolean remove(Site s);
	SolrResponse query(SolrParams p);
	SolrDocument getDocument(Item i);
}
