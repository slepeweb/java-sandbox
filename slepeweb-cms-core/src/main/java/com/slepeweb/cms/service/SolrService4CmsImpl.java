package com.slepeweb.cms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slepeweb.cms.bean.Dateish;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.bean.SolrParams4Cms;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.cms.utils.CmsUtil;
import com.slepeweb.common.solr.bean.SolrPager;
import com.slepeweb.common.solr.bean.SolrResponse;
import com.slepeweb.common.solr.service.SolrService4CmsBase;

import jakarta.annotation.PostConstruct;

@Service
public class SolrService4CmsImpl extends SolrService4CmsBase implements SolrService4Cms {
	
	//private static Logger LOG = Logger.getLogger(SolrService4CmsImpl.class);
	private static final String SPACE = " ";
	private static final int MAX_WIDTH = 200;
	private static Pattern ID_PATTERN = Pattern.compile("^\\$_(\\d+)$");
	
	@Autowired private ItemService itemService;
		
	@PostConstruct
	public void init() throws Exception {
		setServerUrl("http://localhost:8983/solr/cms");
	}
	
	public boolean save(Item i) {
		return super.saveItem(i);
	}
	
	public boolean remove(Item i) {
		return super.removeItems(i.getSolrKeys());
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
		int count = 0;
		if (! parentItem.getType().getName().equals(ItemTypeName.CONTENT_FOLDER)) {
			save(parentItem);
			count++;
		}
		
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
			String s;
			
			for (String language : i.getSite().getAllLanguages()) {
				
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				// Standard approach
				doc = new SolrDocument4Cms(i, language);
				
				if (! i.getSite().getShortname().equals("pho") && i.getType().isImage()) {
					doc.setTitle(CmsUtil.getFieldValue(i, FieldName.ALT_TEXT, language, false, null));
					doc.setTeaser(CmsUtil.getFieldValue(i, FieldName.CAPTION, language, false, null));
				}
				
				if (StringUtils.isBlank(doc.getTitle())) {
					doc.setTitle(i.getName());
				}
				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
				
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				// For test site
				if (i.getSite().getShortname().equals("z")) {
					if (StringUtils.isNotBlank(s = CmsUtil.getFieldValue(i, "ztitle", language, false, null))) {
						doc.setTitle(s);
					}
				}
				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
				
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				// For sws site: accumulating content from page components
				if (i.getSite().getShortname().equals("sws")) {
					StringBuilder sbBody = new StringBuilder(CmsUtil.getFieldValue(i, FieldName.BODYTEXT, language, true, null));
					StringBuilder sbSubtitle = new StringBuilder();
					
					for (Link l : i.getComponents()) {
						if (l.getName().equals(LinkName.std)) {
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
					doc.setBodytext(CmsUtil.getFieldValue(i, FieldName.OVERVIEW, language, true, null));
					
					if (i.getType().getName().equals("Boy") || i.getType().getName().equals("Girl")) {
						StringBuilder sb = new StringBuilder(i.getFieldValue("firstname")).append(" ").
								append(i.getFieldValue("middlenames")).append(" ").
								append(i.getFieldValue("lastname"));
						
						String fullName = sb.toString().trim();
						if (StringUtils.isBlank(fullName)) {
							fullName = "(Un-named)";
						}
						doc.setTitle(fullName);
						doc.setTeaser(StringUtils.abbreviate(CmsUtil.getFieldValue(i, FieldName.OVERVIEW, language, false, null), MAX_WIDTH));
					}
					else if (i.getType().getName().equals("Document")) {
						doc.setTitle(CmsUtil.getFieldValue(i, FieldName.HEADING, language, false, null));
						doc.setTeaser(StringUtils.abbreviate(CmsUtil.getFieldValue(i, FieldName.OVERVIEW, language, false, null), MAX_WIDTH));
					}
				}
				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
							
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				// For photos site
				if (i.getSite().getShortname().equals("pho")) {
					if (isPhoMedia(doc.getType())) {
						// Date-ish for media types
						s = CmsUtil.getFieldValue(i, FieldName.DATEISH, false, null);
						if (s != null) {	
							Dateish ish = new Dateish(s);
							doc.setExtraStr1(ish.toSortableString());
						}
						
						// Related media items, coded as json strings
						if (i.getRelatedItems().size() > 0) {
							List<SolrDocument4Cms> related = new ArrayList<SolrDocument4Cms>();
							for (Item r : i.getRelatedItems()) {
								if (isPhoMedia(r.getType().getName())) {
									for (Object o : makeDocuments(r)) {
										related.add((SolrDocument4Cms) o);
									}
								}
							}
							
							try {
								doc.setExtraStr2(new ObjectMapper().writeValueAsString(related));
							}
							catch (Exception e) {
								// TODO: Log error
							}
						}
					}
				}
				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
				
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				// For photos site
				if (i.getSite().getShortname().equals("geo")) {
					
				}
				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

				docs.add(doc);
			}
			
			return docs;
		}
		
		return null;
	}
	
	private boolean isPhoMedia(String name) {
		return name.startsWith(ItemTypeName.PHOTO_PREFIX) || name.startsWith(ItemTypeName.MOVIE_PREFIX);
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

	public SolrResponse<SolrDocument4Cms> query(SolrParams4Cms params) {
		
		SolrResponse<SolrDocument4Cms> response = new SolrResponse<SolrDocument4Cms>();
		String searchText = params.getSearchText();
		
		if (StringUtils.isBlank(searchText)) {
			return error(response, "Please enter terms to search");
		}
		
		Matcher m = ID_PATTERN.matcher(searchText);
		
		if (m.matches()) {
			Long origId = Long.valueOf(m.group(1));
			Item i = this.itemService.getEditableVersion(origId);
			
			if (i == null) {
				return error(response, String.format("Unable to find item with origId '%d'", origId));
			}
			
			if (! i.setUser(params.getUser()).isAccessible()) {
				return error(response, String.format("Item with id '%d' is not accessible by this user"));
			}
			
			List<SolrDocument4Cms> list = new ArrayList<SolrDocument4Cms>(1);
			list.add(new SolrDocument4Cms(i));

			if (! i.getSite().getId().equals(params.getSiteId())) {
				return error(response, String.format("Item with id '%d' belongs to a different site, and cannot be edited here.", i.getOrigId()));
			}
			
			return pack(response, String.format("Found the requested item with id '%d':", i.getOrigId()), list, 1, params.getPageSize(), 1);
		}
		
		SolrQuery q = new SolrQuery();
		q.setQuery(params.getSearchText());
		q.addFilterQuery(String.format("siteid:\"%d\"", params.getSiteId()));
		q.addFilterQuery(String.format("language:\"%s\"", params.getLanguage()));
		q.addFilterQuery(String.format("editable:\"%s\"", "true"));
		q.add("defType", "dismax");
		q.add("qf", "title^10 tags^8 teaser^4 bodytext");
		q.setStart(params.getStart());
		q.setRows(params.getPageSize());
		
		try {
			QueryResponse qr = getClient().query(q);
			return pack(response, "Please select an item from the list:", 
					qr.getBeans(SolrDocument4Cms.class), qr.getResults().getNumFound(), params.getPageSize(), params.getPageNum());
			
		} catch (Exception e) {
			return error(response, "Search system error");
		} 
	}
	
	private SolrResponse<SolrDocument4Cms> pack(SolrResponse<SolrDocument4Cms> response, String message, 
			List<SolrDocument4Cms> results, long totalHits, int pageSize, int pageNum) {
		
		response.setResults(results);
		response.setTotalHits(totalHits);
		response.setPager(new SolrPager<SolrDocument4Cms>(totalHits, pageSize, pageNum));
		response.setMessage(message);
		return response;
	}
	
	private SolrResponse<SolrDocument4Cms> error(SolrResponse<SolrDocument4Cms> response, String message) {
		response.setError(true);
		response.setMessage(message);
		response.setTotalHits(0);
		response.setResults(new ArrayList<SolrDocument4Cms>(0));
		return response;
	}
	
}
