package com.slepeweb.site.pho.bean;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.common.solr.bean.SolrConfig;
import com.slepeweb.site.bean.SolrParams4Site;

public class SolrParams4Pho extends SolrParams4Site {
	private String from, to;
	
	public SolrParams4Pho(Item i, SolrConfig config) {
		super(i, config);
	}

	public String getFrom() {
		return from;
	}

	public SolrParams4Pho setFrom(String from) {
		this.from = from;
		return this;
	}

	public String getTo() {
		return to;
	}

	public SolrParams4Pho setTo(String to) {
		this.to = to;
		return this;
	}
}
