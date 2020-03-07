package com.slepeweb.site.anc.service;

import com.slepeweb.common.solr.bean.SolrResponse;

public interface SolrService4Ancestry {
	SolrResponse<?> query(Object p);
}
