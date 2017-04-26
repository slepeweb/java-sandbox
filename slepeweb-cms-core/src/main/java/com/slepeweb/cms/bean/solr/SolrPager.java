package com.slepeweb.cms.bean.solr;

import java.util.ArrayList;
import java.util.List;

public class SolrPager {

	private static int MAX = 5;
	private List<SolrPageLink> pages = new ArrayList<SolrPageLink>();
	private SolrPageLink previous, next;
	
	public SolrPager(int numPages, int currentPage, String queryStringBase) {
		if (numPages > MAX) {
			numPages = MAX;
		}
		
		SolrPageLink link;
		for (int i = 1; i <= numPages; i++) {
			link = new SolrPageLink(String.valueOf(i), makeHref(queryStringBase, i));
			link.setSelected(i == currentPage);
			if (link.isSelected()) {
				link.setHref(null);
			}
			getPages().add(link);
		}
		
		this.previous = new SolrPageLink("Previous", makeHref(queryStringBase, currentPage - 1));
		this.previous.setSelected(currentPage <= 1);
		this.next = new SolrPageLink("Next", makeHref(queryStringBase, currentPage + 1));
		this.next.setSelected(currentPage >= numPages);
	}
	
	private String makeHref(String base, int pageNum) {
		return String.format("%s&page=%d", base, pageNum);
	}
	
	public SolrPageLink getPrevious() {
		return previous;
	}
	
	public void setPrevious(SolrPageLink previous) {
		this.previous = previous;
	}
	
	public SolrPageLink getNext() {
		return next;
	}
	
	public void setNext(SolrPageLink next) {
		this.next = next;
	}
	
	public List<SolrPageLink> getPages() {
		return pages;
	}
	
	public int getNumPages() {
		return this.pages.size();
	}
}
