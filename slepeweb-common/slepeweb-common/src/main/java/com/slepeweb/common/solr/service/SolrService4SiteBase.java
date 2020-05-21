package com.slepeweb.common.solr.service;

import com.slepeweb.common.solr.bean.SolrResponse;

public abstract class SolrService4SiteBase extends SolrServiceBase {
	
	public abstract SolrResponse<?> query(Object p);
}
