package com.slepeweb.cms.bean.solr;

public class SolrPageLink {
	private String label, href;
	private boolean selected;
	
	public SolrPageLink(String l, String h) {
		this.label = l;
		this.href = h;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getHref() {
		return href;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
}
