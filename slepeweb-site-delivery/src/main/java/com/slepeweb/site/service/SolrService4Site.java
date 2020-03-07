package com.slepeweb.site.service;

import com.slepeweb.common.solr.bean.SolrResponse;

public interface SolrService4Site {
	SolrResponse<?> query(Object p);
}
