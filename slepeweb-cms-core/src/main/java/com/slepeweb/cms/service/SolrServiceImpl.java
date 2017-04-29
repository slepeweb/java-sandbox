package com.slepeweb.cms.service;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.SiteConfig;
import com.slepeweb.cms.bean.solr.SolrDocument;
import com.slepeweb.cms.bean.solr.SolrPager;
import com.slepeweb.cms.bean.solr.SolrParams;
import com.slepeweb.cms.bean.solr.SolrResponse;
import com.slepeweb.cms.constant.FieldName;

@Service
public class SolrServiceImpl implements SolrService {
	
	private static Logger LOG = Logger.getLogger(SolrServiceImpl.class);
	private static final String SPACE = " ";
	private static final String SOLR_ENABLED_KEY = "solr.enabled";
		
	@Value("${solr.server.url:http://localhost:8900/solr/cms}") 
	private String serverUrl;
	
	@Value("${solr.enabled:no}") 
	private String serverEnabled;
	
	@Autowired private SiteConfigService siteConfigService;
	
	private SolrClient client;

	public SolrClient getClient() {
		if (this.client == null) {
			this.client = new HttpSolrClient(this.serverUrl);
			LOG.info(String.format("Initialised solr server [%s]", this.serverUrl));
		}
		return this.client;
	}
	
	public SolrDocument getDocument(Item i) {
		if (isServerEnabled(i.getSite().getId())) {
			try {
				org.apache.solr.common.SolrDocument doc = getClient().getById(String.valueOf(i.getOrigId()));
				DocumentObjectBinder binder = new DocumentObjectBinder();
				return binder.getBean(SolrDocument.class, doc);
			}
			catch (Exception e) {
				LOG.error("Failed to retrieve Solr document", e);
			}
		}
		return null;
	}
	
	public boolean save(Item i) {
		if (isServerEnabled(i.getSite().getId())) {
			SolrDocument doc = makeDoc(i);
			try {
				/*UpdateResponse resp = */ getClient().addBean(doc);
				this.client.commit();
				LOG.debug("Item successfully indexed by Solr");
				return true;
			}
			catch (Exception e) {
				LOG.error("Solr failed to index item", e);
				return false;
			}
		}
		
		// Don't report failure if solr functionality is disabled in this deployment.
		return true;
	}
	
	public boolean remove(Item i) {
		if (isServerEnabled(i.getSite().getId())) {
			try {
				/*UpdateResponse resp = */ getClient().deleteById(String.valueOf(i.getOrigId()));
				this.client.commit();
				LOG.debug("Item successfully removed from Solr index");
				return true;
			}
			catch (Exception e) {
				LOG.error("Solr failed to remove item from Solr index", e);
				return false;
			}
		}
		
		// Don't report failure if solr functionality is disabled in this deployment.
		return true;
	}
	
	public SolrResponse query(SolrParams params) {
		Long siteId = params.getSearchResultsItem().getSite().getId();
		SolrResponse response = new SolrResponse(params);
		
		if (StringUtils.isBlank(params.getSearchText())) {
			response.setError(true);
			response.setMessage("Please enter terms to search");
		}
		else {
			if (isServerEnabled(siteId)) {
				SolrQuery q = new SolrQuery();
				q.setQuery(params.getSearchText());
				q.add("defType", "dismax");
				q.add("qf", "title^10 subtitle^4 bodytext");
				q.setStart(params.getStart());
				q.setRows(params.getPageSize());
				LOG.info(String.format("Solr query: [%s]", q.getQuery()));
				
				try {
					QueryResponse qr = getClient().query(q);
					response.setResults(qr.getBeans(SolrDocument.class));
					response.setTotalHits(qr.getResults().getNumFound());
					LOG.debug(String.format("Query returned %d results out of %s", 
							response.getResults().size(), qr.getHeader().toString()));
					
					response.setPager(new SolrPager(response.getNumPages(), params.getPageNum(), params.getHrefBase()));					
					return response;
					
				} catch (Exception e) {
					response.setError(true);
					response.setMessage("Search system error");
				} 
			}
		}
		
		response.setTotalHits(0);
		response.setResults(new ArrayList<SolrDocument>(0));
		return response;
	}
	
	private SolrDocument makeDoc(Item i) {
		SolrDocument doc = new SolrDocument();
		
		// From this item's fields ...
		doc.setId(String.valueOf(i.getOrigId())).
			setSiteId(String.valueOf(i.getSite().getId())).
			setType(i.getType().getName()).
			setTitle(i.getFieldValue(FieldName.TITLE)).
			setTeaser(i.getFieldValue(FieldName.TEASER)).
			setPath(i.getPath());
		
		// A really annoying hack for the purpose of regression testing
		if (StringUtils.isNotBlank(i.getFieldValue("ztitle"))) {
			doc.setTitle(i.getFieldValue("ztitle"));
		}
		
		// ... and from its main components plus their children ...
		StringBuilder sbBody = new StringBuilder(i.getFieldValue(FieldName.BODYTEXT));
		StringBuilder sbSubtitle = new StringBuilder();
		
		for (Link l : i.getComponents()) {
			if (l.getName().equals(LinkName.MAIN)) {
				scrapeComponents(l.getChild(), sbBody, sbSubtitle);
			}
		}
		
		doc.setBodytext(sbBody.toString());
		doc.setSubtitle(sbSubtitle.toString());
		
		return doc;
	}
	
	private void scrapeComponents(Item i, StringBuilder sbBody, StringBuilder sbSubtitle) {
		append(sbSubtitle, i.getFieldValue(FieldName.HEADING));
		append(sbBody, i.getFieldValue(FieldName.BLURB));
		
		for (Item j : i.getBoundItems()) {
			scrapeComponents(j, sbBody, sbSubtitle);
		}
	}
	
	private void append(StringBuilder sb, String s) {
		if (sb.length() > 0) {
			sb.append(SPACE);
		}
		sb.append(s);
	}

	public boolean isServerEnabled(Long siteId) {
		
		boolean enabled = isPositive(this.serverEnabled);
		SiteConfig config = this.siteConfigService.getSiteConfig(siteId, SOLR_ENABLED_KEY);
		if (config != null) {
			enabled = isPositive(config.getValue());
		}
		
		return enabled && getClient() != null;
	}
	
	private boolean isPositive(String str) {
		if (str != null) {
			String s = str.trim();
			return 
					s.equalsIgnoreCase("yes") ||
					s.equalsIgnoreCase("true") ||
					s.equalsIgnoreCase("1");
		}
		
		return false;
	}
}
