package com.slepeweb.money.bean.solr;

import java.util.ArrayList;
import java.util.List;

import com.slepeweb.money.bean.Option;

/**
 * Search results will be in reverse date order. A typical url for the most recent
 * page of results would be:
 * 
 * /money/transaction/list/by/category/227/1
 * 
 * where '1' indicates the first page of results in the list. As this figure increases,
 * we go further back in time. So, the pager navigation corresponding to 20 pages in total
 * would be:
 * 
 * 20, 19, 18 ... 2, 1
 * 
 * So, '1' corresponds to the most recent page of results, and '2' goes back in time
 * to the next page.
 * 
 */
public class SolrPager<T> {

	private static int BLOCK = 7;
	private int selectedPage, pageSize, maxPages;
	private List<T> searchResultsPage;
	private long totalHits;
	
	public SolrPager(long totalHits, List<T> searchResultsPage, int pageSize, int selected) {
		this.searchResultsPage = searchResultsPage;
		this.pageSize = pageSize;
		this.selectedPage = selected;
		this.totalHits = totalHits;
		
		long n = Long.valueOf((totalHits - 1) / this.pageSize) + 1;
		this.maxPages = (int) n;
		
		if (this.selectedPage < 1) {
			this.selectedPage = 1;
		}
		
		if (this.selectedPage > this.maxPages) {
			this.selectedPage = this.maxPages;
		}
	}
	
	public List<Option> getNavigation() {
		List<Option> options = new ArrayList<Option>();
		Option o;
		int p = getSelectedPage() + Integer.valueOf(BLOCK / 2);
		
		for (int i = 0; i < BLOCK; i++) {
			if (p >= 1 && p <= getMaxPages()) {
				o = new Option(p, String.valueOf(invertSelected(p)));
				
				if (p == getSelectedPage()) {
					o.setSelected(true);
					o.setName("Page " + o.getName());
				}
				
				options.add(o);
			}
			
			p--;
		}
		
		return options;
	}
	
	private int invertSelected(int p) {
		return getMaxPages() - p + 1;
	}
	
	public void setTotalHits(long totalHits) {
		this.totalHits = totalHits;
	}

	public int getSelectedPage() {
		return selectedPage;
	}

	public void setSelected(int selected) {
		this.selectedPage = selected;
	}
	
	/*
	 * 'Next' means forwards in time, but reducing page selection
	 */
	public int getNextSelection() {
		if (isNext()) {
			return getSelectedPage() - 1;
		}
		
		return getSelectedPage();
	}
	
	/*
	 * 'Previous' means backwards in time, and increasing page selection
	 */
	public int getPreviousSelection() {
		if (isPrevious()) {
			return getSelectedPage() + 1;
		}
		
		return getSelectedPage();
	}
	
	public int getNextBlock() {
		if (isNext()) {
			int p = getSelectedPage() - BLOCK;
			if (p < 1) {
				p = 1;
			}
			return p;
		}
		
		return getSelectedPage();
	}
	
	public int getPreviousBlock() {
		if (isPrevious()) {
			int p = getSelectedPage() + BLOCK;
			if (p > getMaxPages()) {
				p = getMaxPages();
			}
			return p;
		}
		
		return getSelectedPage();
	}
	
	public boolean isPrevious() {
		return getSelectedPage() < getMaxPages(); 
	}
	
	public boolean isNext() {
		return getSelectedPage() > 1; 
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getMaxPages() {
		return maxPages;
	}

	public void setMaxPages(int maxPages) {
		this.maxPages = maxPages;
	}

	public List<T> getResults() {
		return searchResultsPage;
	}

	public long getTotalHits() {
		return this.totalHits;
	}

	/*
	 * Remember, the search results are in reverse date order.
	 * 
		public List<T> getPage() {
		int start = (getSelectedPage() - 1) * getPageSize();
		int end = start + getPageSize();
		int totalResults = getResults().size();
		
		if (end > totalResults) {
			end = totalResults;
		}
		
		return getResults().subList(start, end);
	}
	 */
	
	public boolean isVisible() {
		return getMaxPages() > 1;
	}
}
