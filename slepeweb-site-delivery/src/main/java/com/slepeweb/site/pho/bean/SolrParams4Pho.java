package com.slepeweb.site.pho.bean;

import org.apache.commons.lang3.StringUtils;

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

	public SolrParams4Pho setFrom(String year) {
		if (StringUtils.isNotBlank(year)) {
			this.from = String.format("%s/01/01", year);
		}
		return this;
	}

	public String getTo() {
		return to;
	}

	public SolrParams4Pho setTo(String year) {
		if (StringUtils.isNotBlank(year)) {
			this.to = String.format("%s/12/31", year);
		}
		return this;
	}
}
