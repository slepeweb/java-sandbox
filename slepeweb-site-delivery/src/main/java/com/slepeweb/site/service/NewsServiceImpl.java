package com.slepeweb.site.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.site.bean.DatedLinkTarget;
import com.slepeweb.site.constant.FieldName;
import com.slepeweb.site.ntc.bean.CompetitionIndex;
import com.slepeweb.site.ntc.service.CompetitionService;

@Service("newsService")
public class NewsServiceImpl implements NewsService {
	private static Logger LOG = Logger.getLogger(NewsServiceImpl.class);
	
	@Autowired private CompetitionService competitionService;

	//@Cacheable(value="serviceCache")
	public List<DatedLinkTarget> getCombinedNews(Item newsIndexItem) {
		LOG.info(String.format("Getting events at %1$tH:%1$tM:%1$tS", System.currentTimeMillis()));

		List<Item> newsList = newsIndexItem.getBoundItems();
		List<DatedLinkTarget> links = new ArrayList<DatedLinkTarget>(newsList.size());
		DatedLinkTarget lt;
		CompetitionIndex index = this.competitionService.getCompetitionIndex(newsIndexItem.getSite());
		
		if (newsIndexItem != null) {			
			for (Item news : newsList) {
				lt = new DatedLinkTarget().setDate(news.getDateFieldValue(FieldName.DATE_PUBLISHED));
				lt.setTitle(news.getFieldValue(FieldName.TITLE)).setHref(news.getPath());
				links.add(lt);
			}
			
			links.addAll(index.getRecentResultsAsLinks());
			Collections.sort(links, new Comparator<DatedLinkTarget>() {

				@Override
				public int compare(DatedLinkTarget o1, DatedLinkTarget o2) {
					return o1.getDate().compareTo(o2.getDate());
				}
				
			});
		}
		return links;
	}

}
