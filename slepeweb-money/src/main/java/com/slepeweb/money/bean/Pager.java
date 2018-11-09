package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

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
public class Pager<T> {

	private static int BLOCK = 7;
	private int selected, pageSize, maxPages;
	private List<T> results;
	
	public Pager(List<T> list, int pageSize, int selected) {
		this.results = list;
		this.pageSize = pageSize;
		this.selected = selected;
		this.maxPages = Integer.valueOf((this.results.size() - 1) / this.pageSize) + 1;
		
		if (this.selected < 1) {
			this.selected = 1;
		}
		
		if (this.selected > this.maxPages) {
			this.selected = this.maxPages;
		}
	}
	
	public List<Option> getNavigation() {
		List<Option> options = new ArrayList<Option>();
		Option o;
		int p = getSelected() + Integer.valueOf(BLOCK / 2);
		
		for (int i = 0; i < BLOCK; i++) {
			if (p >= 1 && p <= getMaxPages()) {
				o = new Option(p, String.valueOf(transposeSelected(p)));
				
				if (p == getSelected()) {
					o.setSelected(true);
					o.setName("Page " + o.getName());
				}
				
				options.add(o);
			}
			
			p--;
		}
		
		return options;
	}
	
	private int transposeSelected(int p) {
		return getMaxPages() - p + 1;
	}
	
	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}
	
	/*
	 * 'Next' means forwards in time, but reducing page selection
	 */
	public int getNextSelection() {
		if (isNext()) {
			return getSelected() - 1;
		}
		
		return getSelected();
	}
	
	/*
	 * 'Previous' means backwards in time, and increasing page selection
	 */
	public int getPreviousSelection() {
		if (isPrevious()) {
			return getSelected() + 1;
		}
		
		return getSelected();
	}
	
	public int getNextBlock() {
		if (isNext()) {
			int p = getSelected() - BLOCK;
			if (p < 1) {
				p = 1;
			}
			return p;
		}
		
		return getSelected();
	}
	
	public int getPreviousBlock() {
		if (isPrevious()) {
			int p = getSelected() + BLOCK;
			if (p > getMaxPages()) {
				p = getMaxPages();
			}
			return p;
		}
		
		return getSelected();
	}
	
	public boolean isPrevious() {
		return getSelected() < getMaxPages(); 
	}
	
	public boolean isNext() {
		return getSelected() > 1; 
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
		return results;
	}

	public int getTotalHits() {
		return getResults().size();
	}

	/*
	 * Remember, the search results are in reverse date order.
	 */
	public List<T> getPage() {
		int start = (getSelected() - 1) * getPageSize();
		int end = start + getPageSize();
		int totalResults = getResults().size();
		
		if (end > totalResults) {
			end = totalResults;
		}
		
		return getResults().subList(start, end);
	}
}
