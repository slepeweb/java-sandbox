package com.slepeweb.cms.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.bean.SolrParams4Cms;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.common.solr.bean.SolrPager;
import com.slepeweb.common.solr.bean.SolrResponse;
import com.slepeweb.common.solr.service.SolrService4CmsBase;

@Service
public class SolrService4CmsImpl extends SolrService4CmsBase implements SolrService4Cms {
	
	//private static Logger LOG = Logger.getLogger(SolrService4CmsImpl.class);
	private static final String SPACE = " ";
	private static final int MAX_WIDTH = 200;
		
	@PostConstruct
	public void init() throws Exception {
		setServerUrl("http://localhost:8983/solr/cms");
	}
	
	public boolean save(Item i) {
		return super.saveItem(i);
	}
	
	public boolean remove(Item i) {
		return super.removeItemByOrigId(i.getOrigId());
	}
	
	public boolean remove(Site s) {
		return super.removeItemBySiteId(s.getId());
	}
	
	public boolean removeSection(Item i) {
		Site site = i.getSite();
		boolean result = true;
		
		if (site.isMultilingual()) {
			for (String language : site.getAllLanguages()) {
				result = result && super.removeSectionByPath(site.getId(), String.format("/%s%s", language, i.getPath()));
			}
		}
		else {
			result = super.removeSectionByPath(site.getId(), i.getPath());
		}
		
		return result;
	}
		
	public int indexSection(Item parentItem) {
		// First, wipe section from solr
		removeSection(parentItem);
		
		// Now recursively crawl down section, and save each item found
		return indexSectionRecursive(parentItem);
		
	}
	
	private int indexSectionRecursive(Item parentItem) {
		// The solrService composites content from this item and its main components
		save(parentItem);
		int count = 1;
		
		for (Link l : parentItem.getBindings()) {
			if (! l.getType().equals(LinkType.shortcut)) {
				count += indexSectionRecursive(l.getChild());
			}
		}
		
		return count;
	}
	
	/*
	 * NOTE: This method is site-specific, since item field names will probably vary,
	 * although field purposes should apply to all sites, specifically 'title' and 'subtitle'.
	 */
	protected List<Object> makeDocuments(Object item) {
		
		if (item instanceof Item) {
			Item i = (Item) item;
			List<Object> docs = new ArrayList<Object>();
			SolrDocument4Cms doc;
			
			for (String language : i.getSite().getAllLanguages()) {
				
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				// Standard approach
				doc = new SolrDocument4Cms().
					setKey(String.valueOf(i.getOrigId()), language).
					setSiteId(String.valueOf(i.getSite().getId())).
					setType(i.getType().getName()).
					setTitle(getFieldValue(i, FieldName.TITLE, language, false, null)).
					setTeaser(getFieldValue(i, FieldName.TEASER, language, false, null)).
					setPath(i.getPath()).
					setEditable(i.isEditable()).
					setViewable(i.isPublished());
				
				if (i.getSite().isMultilingual()) {
					doc.setPath(String.format("/%s%s", language, i.getPath()));
				}
				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
				
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				// For test site
				if (i.getSite().getShortname().equals("z")) {
					if (StringUtils.isNotBlank(i.getFieldValue("ztitle"))) {
						doc.setTitle(i.getFieldValue("ztitle"));
					}
				}
				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
				
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				// For sws site: accumulating content from page components
				if (i.getSite().getShortname().equals("sws")) {
					StringBuilder sbBody = new StringBuilder(i.getFieldValue(FieldName.BODYTEXT));
					StringBuilder sbSubtitle = new StringBuilder();
					
					for (Link l : i.getComponents()) {
						if (l.getName().equals(LinkName.MAIN)) {
							scrapeComponents(l.getChild(), sbBody, sbSubtitle);
						}
					}
					
					doc.setBodytext(sbBody.toString());
					doc.setSubtitle(sbSubtitle.toString());
				}
				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				// For anc site
				if (i.getSite().getShortname().equals("anc")) {
					doc.setBodytext(getFieldValue(i, FieldName.OVERVIEW, language, true, null));
					
					if (i.getType().getName().equals("Boy") || i.getType().getName().equals("Girl")) {
						StringBuilder sb = new StringBuilder(i.getFieldValue("firstname")).append(" ").
								append(i.getFieldValue("middlenames")).append(" ").
								append(i.getFieldValue("lastname"));
						
						String fullName = sb.toString().trim();
						if (StringUtils.isBlank(fullName)) {
							fullName = "(Un-named)";
						}
						doc.setTitle(fullName);
						doc.setTeaser(StringUtils.abbreviate(getFieldValue(i, FieldName.OVERVIEW, language, false, null), MAX_WIDTH));
					}
					else if (i.getType().getName().equals("Document")) {
						doc.setTitle(getFieldValue(i, FieldName.HEADING, language, false, null));
						doc.setTeaser(StringUtils.abbreviate(getFieldValue(i, FieldName.OVERVIEW, language, false, null), MAX_WIDTH));
					}
				}
				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			
				docs.add(doc);
			}
			
			return docs;
		}
		
		return null;
	}
	
	private String getFieldValue(Item i, String variable, String language, boolean resolve, String dflt) {
		FieldValue fv = i.getFieldValueObj(variable, language);
		String result = dflt;
		
		if (fv != null) {
			result = resolve ? fv.getStringValueResolved() : fv.getStringValue();
		}
		
		return result;
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

	public Object getDocument(Item i) {
		return getDocument(i, i.getLanguage());
	}
	
	public Object getDocument(Item i, String language) {
		Object o = super.getDocument(i.getOrigId(), language, SolrDocument4Cms.class);
		if (o instanceof SolrDocument4Cms) {
			return o;
		}
		return null;
	}

	public SolrResponse<SolrDocument4Cms> query(Object p) {
		
		if (p instanceof SolrParams4Cms) {
			SolrParams4Cms params = (SolrParams4Cms) p;
			SolrResponse<SolrDocument4Cms> response = new SolrResponse<SolrDocument4Cms>();
			
			if (StringUtils.isBlank(params.getSearchText())) {
				response.setError(true);
				response.setMessage("Please enter terms to search");
			}
			else {
				SolrQuery q = new SolrQuery();
				q.setQuery(params.getSearchText());
				q.addFilterQuery(String.format("siteid:\"%d\"", params.getSiteId()));
				q.addFilterQuery(String.format("language:\"%s\"", params.getLanguage()));
				q.addFilterQuery(String.format("editable:\"%s\"", "true"));
				q.add("defType", "dismax");
				q.add("qf", "title^10 subtitle^4 bodytext");
				q.setStart(params.getStart());
				q.setRows(params.getPageSize());
				//LOG.info(String.format("Solr query: [%s]", q.getQuery()));
				
				try {
					QueryResponse qr = getClient().query(q);
					response.setResults(qr.getBeans(SolrDocument4Cms.class));
					response.setTotalHits(qr.getResults().getNumFound());
//					LOG.debug(String.format("Query returned %d results out of %s", 
//							response.getResults().size(), qr.getHeader().toString()));
					
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
