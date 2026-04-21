package com.slepeweb.site.pho.bean;

import org.apache.commons.lang3.StringUtils;

public class PhoMetadata {
	
	private static String DELIM = "|";
	private String itemPath, mediaFilePath, mediaType, title, teaser, tags, dateishStr;

	public String[] toArray() {
		return new String[] {
			getMediaFilePath(), getMediaType(), getTags(), getTitle(), getTeaser(), getDateishStr()
		};
	}
	
	public String getCsvRow() {
		return arrayToStr(toArray());
	}
	
	public static String[] getHeaderArray() {
		return new String[] {
			"File path", "Media type", "Tags", "Title", "Teaser", "Date-ish"
		};
	}
	
	public static String getCsvHeader() {
		return arrayToStr(getHeaderArray());
	}
	
	private static String arrayToStr(String[] arr) {
		return StringUtils.join(arr, DELIM);
	}
	
	public String getMediaFilePath() {
		return mediaFilePath;
	}

	public PhoMetadata setMediaFilePath(String filePath) {
		this.mediaFilePath = filePath;
		return this;
	}

	public String getItemPath() {
		return itemPath;
	}

	public PhoMetadata setItemPath(String itemPath) {
		this.itemPath = itemPath;
		return this;
	}

	public String getMediaType() {
		return mediaType;
	}

	public PhoMetadata setMediaType(String mediaType) {
		this.mediaType = mediaType;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public PhoMetadata setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getTeaser() {
		return teaser;
	}

	public PhoMetadata setTeaser(String teaser) {
		this.teaser = teaser;
		return this;
	}

	public String getTags() {
		return tags;
	}

	public PhoMetadata setTags(String tags) {
		this.tags = tags;
		return this;
	}

	public String getDateishStr() {
		return dateishStr;
	}

	public PhoMetadata setDateishStr(String dateish) {
		this.dateishStr = dateish;
		return this;
	}
 
}
