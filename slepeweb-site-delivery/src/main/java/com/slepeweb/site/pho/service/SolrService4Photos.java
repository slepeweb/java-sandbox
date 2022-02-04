package com.slepeweb.site.pho.service;

import com.slepeweb.common.solr.bean.SolrResponse;

public interface SolrService4Photos {
	SolrResponse<?> query(Object p);
}
