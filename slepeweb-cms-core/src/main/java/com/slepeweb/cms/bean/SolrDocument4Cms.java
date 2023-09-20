package com.slepeweb.cms.bean;

import org.apache.solr.client.solrj.beans.Field;

import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.utils.CmsUtil;

public class SolrDocument4Cms {

	@Field("key") private String key;
	@Field("id") private String id;
	@Field("origId") private String origId;
	@Field("ownerId") private Long ownerId;
	@Field("language") private String language = "en";
	@Field("siteid") private String siteId;
	@Field("type") private String type;
	@Field("template") private String template;
	@Field("title") private String title;
	@Field("subtitle") private String subtitle;
	@Field("teaser") private String teaser;
	@Field("bodytext") private String bodytext;
	@Field("tags") private String tags;
	@Field("path") private String path;
	@Field("editable") private boolean editable;
	@Field("viewable") private boolean viewable;
	@Field("extraStr1") private String extraStr1;
	@Field("extraStr2") private String extraStr2;
	@Field("extraStr3") private String extraStr3;
	
	private boolean accessible = true;
	
	public SolrDocument4Cms() {}
	
	public SolrDocument4Cms(Item i) {
		this(i, i.getSite().getLanguage());
	}
	
	public SolrDocument4Cms(Item i, String language) {
		/* 
		 * 10/1/2022: The document key used to be based upon the item original id, but
		 * has now been changed to the id. This means that the solr index will store
		 * multiple versions of the same item, and queries need to choose whether
		 * editable or viewable items are required.
		 */
		this.
			setId(String.valueOf(i.getId())).
			setOrigId(String.valueOf(i.getOrigId())).
			setLanguage(language).
			setKey(getId(), language).
			setSiteId(String.valueOf(i.getSite().getId())).
			setPath(i.getPath()).
			setType(i.getType().getName()).
			
			setTitle(CmsUtil.getFieldValue(i, FieldName.TITLE, language, false, null)).
			setSubtitle(CmsUtil.getFieldValue(i, FieldName.SUBTITLE, language, false, null)).
			setTeaser(CmsUtil.getFieldValue(i, FieldName.TEASER, language, false, null)).
			setBodytext(CmsUtil.getFieldValue(i, FieldName.BODYTEXT, language, true, null)).
			
			setTags(i.getTagsAsString()).
			setEditable(i.isEditable()).
			setViewable(i.isPublished());
		
		if (i.getTemplate() != null) {
			setTemplate(i.getTemplate().getController());
		}
		
		if (i.getSite().isMultilingual()) {
			setPath(String.format("/%s%s", language, i.getPath()));
		}		
	}
	
	
	@Override
	public String toString() {
		return String.format("(%s) [%s]", getTitle(), getPath());
	}
	
	public String getId() {
		return id;
	}
	
	public SolrDocument4Cms setId(String id) {
		this.id = id;
		return this;
	}
	
	public String getOrigId() {
		return origId;
	}
	
	public SolrDocument4Cms setOrigId(String id) {
		this.origId = id;
		return this;
	}
	
	public Long getOwnerId() {
		return ownerId;
	}

	public SolrDocument4Cms setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
		return this;
	}

	public String getTitle() {
		return title;
	}
	
	public SolrDocument4Cms setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getTeaser() {
		return teaser;
	}

	public SolrDocument4Cms setTeaser(String teaser) {
		this.teaser = teaser;
		return this;
	}

	public String getPath() {
		return path;
	}

	public SolrDocument4Cms setPath(String path) {
		this.path = path;
		return this;
	}

	public String getBodytext() {
		return bodytext;
	}

	public SolrDocument4Cms setBodytext(String bodytext) {
		this.bodytext = bodytext;
		return this;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public SolrDocument4Cms setSubtitle(String subtitle) {
		this.subtitle = subtitle;
		return this;
	}

	public String getSiteId() {
		return siteId;
	}

	public SolrDocument4Cms setSiteId(String siteId) {
		this.siteId = siteId;
		return this;
	}

	public String getType() {
		return type;
	}

	public SolrDocument4Cms setType(String type) {
		this.type = type;
		return this;
	}

	public String getTemplate() {
		return template;
	}

	public SolrDocument4Cms setTemplate(String template) {
		this.template = template;
		return this;
	}

	public String getLanguage() {
		return language;
	}

	public SolrDocument4Cms setLanguage(String language) {
		this.language = language;
		return this;
	}

	public boolean isEditable() {
		return editable;
	}

	public SolrDocument4Cms setEditable(boolean editable) {
		this.editable = editable;
		return this;
	}

	public boolean isViewable() {
		return viewable;
	}

	public SolrDocument4Cms setViewable(boolean viewable) {
		this.viewable = viewable;
		return this;
	}

	public String getKey() {
		return key;
	}

	public String getTags() {
		return tags;
	}

	public SolrDocument4Cms setTags(String tags) {
		this.tags = tags;
		return this;
	}

	public SolrDocument4Cms setKey(String key) {
		this.key = key;
		String[] parts = key.split("-");
		if (parts.length == 2) {
			this.id = parts[0];
			this.language = parts[1];
		}
		return this;
	}
	
	public SolrDocument4Cms setKey(String id, String language) {
		this.key = String.format("%s-%s", id, language);
		this.id = id;
		this.language = language;
		return this;
	}

	public String getExtraStr1() {
		return extraStr1;
	}

	public SolrDocument4Cms setExtraStr1(String s) {
		this.extraStr1 = s;
		return this;
	}

	public String getExtraStr2() {
		return extraStr2;
	}

	public void setExtraStr2(String extraStr2) {
		this.extraStr2 = extraStr2;
	}

	public String getExtraStr3() {
		return extraStr3;
	}

	public void setExtraStr3(String extraStr3) {
		this.extraStr3 = extraStr3;
	}

	public boolean isAccessible() {
		return accessible;
	}

	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}
}
