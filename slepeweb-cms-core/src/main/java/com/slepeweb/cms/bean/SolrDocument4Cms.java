package com.slepeweb.cms.bean;

import org.apache.solr.client.solrj.beans.Field;

public class SolrDocument4Cms {

	@Field("key") private String key;
	@Field("id") private String id;
	@Field("language") private String language = "en";
	@Field("siteid") private String siteId;
	@Field("type") private String type;
	@Field("title") private String title;
	@Field("subtitle") private String subtitle;
	@Field("teaser") private String teaser;
	@Field("bodytext") private String bodytext;
	@Field("path") private String path;
	
	public SolrDocument4Cms() {}
	
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

	public String getLanguage() {
		return language;
	}

	public SolrDocument4Cms setLanguage(String language) {
		this.language = language;
		return this;
	}

	public String getKey() {
		return key;
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
	
	public SolrDocument4Cms setKey(String origId, String language) {
		this.key = String.format("%s-%s", origId, language);
		this.id = origId;
		this.language = language;
		return this;
	}
}
