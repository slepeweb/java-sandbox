package com.slepeweb.site.geo.service;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.service.SiteAccessService;
import com.slepeweb.common.solr.bean.SolrPager;
import com.slepeweb.common.solr.bean.SolrResponse;
import com.slepeweb.common.solr.service.SolrService4SiteBase;
import com.slepeweb.site.bean.SolrParams4Site;

import jakarta.annotation.PostConstruct;

@Service
public class SolrService4GeoImpl extends SolrService4SiteBase implements SolrService4Geo {
	
	private static Logger LOG = Logger.getLogger(SolrService4GeoImpl.class);
	
	@Autowired private SiteAccessService siteAccessService;

	@PostConstruct
	public void init() throws Exception {
		setServerUrl("http://localhost:8983/solr/cms");
	}
	
	@Override
	public SolrResponse<SolrDocument4Cms> query(Object p) {
		
		if (p instanceof SolrParams4Site) {
			SolrParams4Site params = (SolrParams4Site) p;
			SolrResponse<SolrDocument4Cms> response = new SolrResponse<SolrDocument4Cms>();
			boolean searchTextProvided = StringUtils.isNotBlank(params.getSearchText());
			
			SolrQuery q = new SolrQuery();
			if (searchTextProvided) {
				q.setQuery(params.getSearchText());
				q.add("defType", "dismax");
				q.add("qf", "tags^10 title^8 teaser^4 bodytext");
			}
			else {
				q.setQuery("*:*");
			}
			
			q.addFilterQuery(String.format("siteid:\"%d\"", params.getSiteId()));
			q.addFilterQuery(String.format("viewable:\"%s\"", "true"));
			q.setStart(params.getStart());
			q.setRows(params.getPageSize());
			LOG.info(String.format("Solr query: [%s]", q.toQueryString()));
			
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
				
				flagInaccessibleDocuments(response, params.getUser());
				return response;
				
			} catch (Exception e) {
				response.setError(true);
				response.setMessage("Search system error");
			} 
			
			response.setTotalHits(0);
			response.setResults(new ArrayList<SolrDocument4Cms>(0));
			return response;
		}
		
		return null;
	}	
	
	private void flagInaccessibleDocuments(SolrResponse<SolrDocument4Cms> resp, Object o) {
		if (o instanceof User) {
			User u = (User) o;
			
			for (SolrDocument4Cms doc : resp.getResults()) {
				this.siteAccessService.isAccessible(doc, u);
			}
		}
	}
}
