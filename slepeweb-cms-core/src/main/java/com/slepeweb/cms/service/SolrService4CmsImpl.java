package com.slepeweb.cms.service;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.common.solr.service.SolrService4CmsBase;

@Service
public class SolrService4CmsImpl extends SolrService4CmsBase implements SolrService4Cms {
	
	//private static Logger LOG = Logger.getLogger(SolrService4CmsImpl.class);
	private static final String SPACE = " ";
		
	@PostConstruct
	public void init() throws Exception {
		setServerUrl("http://localhost:8983/solr/cms");
	}
	
	public boolean save(Item i) {
		return super.save(i.getSite().getId(), i);
	}
	
	public boolean remove(Item i) {
		return super.remove(i.getSite().getId(), i.getOrigId());
	}
	
	public boolean remove(Site s) {
		return super.remove(s.getId());
	}
		
	public void indexSection(Item parentItem) {
		// The solrService composites content from this item and its main components
		save(parentItem);
		
		for (Link l : parentItem.getBindings()) {
			if (! l.getType().equals(LinkType.shortcut)) {
				indexSection(l.getChild());
			}
		}
	}	
	
	/*
	 * TODO: NOTE: This method is site-specific, since item field names will probably vary,
	 * although field purposes should apply to all sites, specifically 'title' and 'subtitle'.
	 */
	protected Object makeDoc(Object item) {
		
		if (item instanceof Item) {
			Item i = (Item) item;

			SolrDocument4Cms doc = new SolrDocument4Cms();
			doc.setId(String.valueOf(i.getOrigId())).
				setSiteId(String.valueOf(i.getSite().getId())).
				setType(i.getType().getName()).
				setTitle(i.getFieldValue(FieldName.TITLE)).
				setTeaser(i.getFieldValue(FieldName.TEASER)).
				setPath(i.getUrl());
			
			// A really annoying hack for the purpose of regression testing
			if (StringUtils.isNotBlank(i.getFieldValue("ztitle"))) {
				doc.setTitle(i.getFieldValue("ztitle"));
			}
			
			// ... and from its main components plus their children ...
			// TODO: NOTE: This bit is site-specific
			StringBuilder sbBody = new StringBuilder(i.getFieldValue(FieldName.BODYTEXT));
			StringBuilder sbSubtitle = new StringBuilder();
			
			for (Link l : i.getComponents()) {
				if (l.getName().equals(LinkName.MAIN)) {
					scrapeComponents(l.getChild(), sbBody, sbSubtitle);
				}
			}
			
			doc.setBodytext(sbBody.toString());
			doc.setSubtitle(sbSubtitle.toString());

			// More type-specific exceptions
			if (i.getType().getName().equals("Primary") || i.getType().getName().equals("Partner")) {
				StringBuilder sb = new StringBuilder(i.getFieldValue("firstname")).append(" ").
						append(i.getFieldValue("middlenames")).append(" ").
						append(i.getFieldValue("lastname"));
				
				String fullName = sb.toString().trim();
				if (StringUtils.isBlank(fullName)) {
					fullName = "(Un-named)";
				}
				doc.setTitle(fullName);
				doc.setBodytext(i.getFieldValue("overview"));
			}
			else if (i.getType().getName().equals("Document")) {
				doc.setTitle(i.getFieldValue("heading"));
			}
			
			return doc;
		}
		
		return null;
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
		Object o = super.getDocument(i.getSite().getId(), i.getOrigId(), SolrDocument4Cms.class);
		if (o instanceof SolrDocument4Cms) {
			return o;
		}
		return null;
	}
	
}
