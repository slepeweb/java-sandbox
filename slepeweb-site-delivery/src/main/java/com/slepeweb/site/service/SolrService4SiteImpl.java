package com.slepeweb.site.service;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.common.solr.bean.SolrPager;
import com.slepeweb.common.solr.bean.SolrResponse;
import com.slepeweb.common.solr.service.SolrService4SiteBase;
import com.slepeweb.site.bean.SolrParams4Site;

import jakarta.annotation.PostConstruct;

@Service
public class SolrService4SiteImpl extends SolrService4SiteBase implements SolrService4Site {
	
	private static Logger LOG = Logger.getLogger(SolrService4SiteImpl.class);
		
	@PostConstruct
	public void init() throws Exception {
		setServerUrl("http://localhost:8983/solr/cms");
	}
	
	public SolrResponse<SolrDocument4Cms> query(Object p) {
		if (p instanceof SolrParams4Site) {
			SolrParams4Site params = (SolrParams4Site) p;
			SolrResponse<SolrDocument4Cms> response = new SolrResponse<SolrDocument4Cms>();
			
			if (StringUtils.isBlank(params.getSearchText())) {
				response.setError(true);
				response.setMessage("Please enter terms to search");
			}
			else {
				SolrQuery q = new SolrQuery();
				q.setQuery(params.getSearchText());
				q.add("defType", "dismax");
				q.add("qf", "title^10 tags^10 teaser^4 bodytext");
				q.setStart(params.getStart());
				q.setRows(params.getPageSize());
				LOG.info(String.format("Solr query: [%s]", q.getQuery()));
				
				try {
					QueryResponse qr = getClient().query(q);
					response.setResults(qr.getBeans(SolrDocument4Cms.class));
					response.setTotalHits(qr.getResults().getNumFound());
					LOG.debug(String.format("Query returned %d results out of %s", 
							response.getResults().size(), qr.getHeader().toString()));
					
					response.setPager(new SolrPager<SolrDocument4Cms>(
							response.getTotalHits(), 
							params.getPageSize(), 
							params.getPageNum()));	
					
					return response;
					
				} catch (Exception e) {
					response.setError(true);
					response.setMessage("Search system error");
				} 
			}
			
			response.setTotalHits(0);
			response.setResults(new ArrayList<SolrDocument4Cms>(0));
			return response;
		}
		
		return null;
	}
	
}
