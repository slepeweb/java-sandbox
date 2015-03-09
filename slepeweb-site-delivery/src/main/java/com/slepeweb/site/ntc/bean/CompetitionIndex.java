package com.slepeweb.site.ntc.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.slepeweb.site.bean.DatedLinkTarget;

public class CompetitionIndex {

	private List<Competition> competitions = new ArrayList<Competition>();

	public List<DatedLinkTarget> getRecentResultsAsLinks() {
		return getFixturesAsLinks(getRecentResults(), true);
	}
	
	public List<DatedLinkTarget> getFutureMatchesAsLinks() {
		return getFixturesAsLinks(getFutureMatches(), false);
	}
	
	private List<DatedLinkTarget> getFixturesAsLinks(List<Fixture> fixtures, boolean forResults) {
		List<DatedLinkTarget> list = new ArrayList<DatedLinkTarget>();
		DatedLinkTarget lt;
		
		for (Fixture f : fixtures) {
			lt = new DatedLinkTarget().setDate(f.getDate());
			if (forResults) {
				lt.setTitle(f.getResultHeadline());
			}
			else {
				lt.setTitle(f.getTie());
			}
			
			lt.setHref(f.getCompetition().getItem().getPath());
			list.add(lt);
		}
		
		return list;
	}
	
	public List<Fixture> getRecentResults() {
		List<Fixture> recentResults = new ArrayList<Fixture>();
		for (Competition c : getCompetitions()) {
			recentResults.addAll(c.getRecentResults());
		}
		
		Collections.sort(recentResults, new Comparator<Fixture>() {
			@Override
			public int compare(Fixture f1, Fixture f2) {
				return f2.getDate().compareTo(f1.getDate());
			}
		});
		
		if (recentResults.size() > 6) {
			recentResults = recentResults.subList(0,  6);
		}
		
		return recentResults;
	}
	
	public List<Fixture> getFutureMatches() {
		List<Fixture> futureMatches = new ArrayList<Fixture>();
		for (Competition c : getCompetitions()) {
			futureMatches.addAll(c.getFutureMatches());
		}
		
		Collections.sort(futureMatches, new Comparator<Fixture>() {
			@Override
			public int compare(Fixture f1, Fixture f2) {
				return f1.getDate().compareTo(f2.getDate());
			}
		});
		
		if (futureMatches.size() > 2) {
			futureMatches = futureMatches.subList(0, 2);
		}
		
		return futureMatches;
	}
	
	public Competition findCompetition(String path) {
		for (Competition c : getCompetitions()) {
			if (c.getItem() != null && c.getItem().getPath().equals(path)) {
				return c;
			}
		}
		
		return null;
	}
	
	public List<Competition> getCompetitions() {
		return competitions;
	}

	public void setCompetitions(List<Competition> competitions) {
		this.competitions = competitions;
	}
	
}
