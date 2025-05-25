package com.slepeweb.site.geo.service;

import com.slepeweb.common.solr.bean.SolrResponse;

public interface SolrService4Geo {
	SolrResponse<?> query(Object p);
}
