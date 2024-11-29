package com.slepeweb.cms.bean;

import java.sql.Timestamp;
import java.util.Map;

public class Item4Json {

	private long id, origId;
	private String name, simplename, path, hostname, type;
	private Map<String, Object> fieldValues;
	private Timestamp dateUpdated;
	private boolean image, video;
	
	public Item4Json(Item i) {
		this.id = i.getId();
		this.origId = i.getOrigId();
		this.name = i.getName();
		this.simplename = i.getSimpleName();
		this.path = i.getPath();
		this.hostname = i.getSite().getDeliveryHost().getPublicName();
		this.type = i.getType().getName();
		this.dateUpdated = i.getDateUpdated();		
		this.fieldValues = i.getFieldValueSet().getFields(i.getLanguage());
		String mimeType = i.getType().getMimeType();
		this.image = mimeType.startsWith("image");
		this.video = mimeType.startsWith("video");
	}

	public long getId() {
		return id;
	}

	public long getOrigId() {
		return origId;
	}

	public String getName() {
		return name;
	}

	public String getSimplename() {
		return simplename;
	}

	public String getPath() {
		return path;
	}

	public String getUrl() {
		return getHostname() == null ? path : String.format("//%s%s", getHostname(), getPath());
	}

	public String getType() {
		return type;
	}

	public Map<String, Object> getFieldValues() {
		return fieldValues;
	}

	public Timestamp getDateUpdated() {
		return dateUpdated;
	}

	public String getHostname() {
		return hostname;
	}

	public Item4Json setId(long id) {
		this.id = id;
		return this;
	}

	public Item4Json setOrigId(long origId) {
		this.origId = origId;
		return this;
	}

	public Item4Json setName(String name) {
		this.name = name;
		return this;
	}

	public Item4Json setSimplename(String simplename) {
		this.simplename = simplename;
		return this;
	}

	public Item4Json setPath(String path) {
		this.path = path;
		return this;
	}

	public Item4Json setHostname(String hostname) {
		this.hostname = hostname;
		return this;
	}

	public Item4Json setType(String type) {
		this.type = type;
		return this;
	}

	public Item4Json setFieldValues(Map<String, Object> fieldValues) {
		this.fieldValues = fieldValues;
		return this;
	}

	public Item4Json setDateUpdated(Timestamp dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

	public boolean isImage() {
		return image;
	}

	public boolean isVideo() {
		return video;
	}
}
