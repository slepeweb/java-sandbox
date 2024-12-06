package com.slepeweb.site.pho.service;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.cms.service.SiteAccessService;
import com.slepeweb.common.solr.bean.SolrPager;
import com.slepeweb.common.solr.bean.SolrResponse;
import com.slepeweb.common.solr.service.SolrService4SiteBase;
import com.slepeweb.site.pho.bean.SolrParams4Pho;

import jakarta.annotation.PostConstruct;

@Service
public class SolrService4PhotosImpl extends SolrService4SiteBase implements SolrService4Photos {
	
	private static Logger LOG = Logger.getLogger(SolrService4PhotosImpl.class);
	
	@Autowired private SiteAccessService siteAccessService;

	@PostConstruct
	public void init() throws Exception {
		setServerUrl("http://localhost:8983/solr/cms");
	}
	
	public SolrResponse<SolrDocument4Cms> query(Object p) {
		
		if (p instanceof SolrParams4Pho) {
			SolrParams4Pho params = (SolrParams4Pho) p;
			SolrResponse<SolrDocument4Cms> response = new SolrResponse<SolrDocument4Cms>();
			boolean searchTextProvided = StringUtils.isNotBlank(params.getSearchText());
			
			SolrQuery q = new SolrQuery();
			if (searchTextProvided) {
				q.setQuery(params.getSearchText());
				q.add("defType", "dismax");
				q.add("qf", "title^10 tags^8 teaser^4 bodytext");
			}
			else {
				q.setQuery("*:*");
			}
			
			q.addFilterQuery(String.format("siteid:\"%d\"", params.getSiteId()));
			q.addFilterQuery(String.format("viewable:\"%s\"", "true"));
			q.addFilterQuery(String.format("type:\"%s\" OR type:\"%s\"", ItemTypeName.PHOTO_JPG, ItemTypeName.MOVIE_MP4));
			
			if (StringUtils.isNotBlank(params.getFrom()) && StringUtils.isNotBlank(params.getTo())) {					
				q.addFilterQuery(String.format("extraStr1:[%s TO %s]", params.getFrom(), params.getTo()));
			}				
			else if (StringUtils.isNotBlank(params.getFrom())) {
				q.addFilterQuery(String.format("extraStr1:[%s TO NOW]", params.getFrom()));
			}
			else if (StringUtils.isNotBlank(params.getTo())) {
				q.addFilterQuery(String.format("extraStr1:[1870 TO %s]", params.getTo()));
			}
							
			// The photo date (year) is stored in the extra1 field
			q.addSort("extraStr1", ORDER.desc);
			
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
