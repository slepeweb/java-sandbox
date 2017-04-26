package com.slepeweb.site.ntc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemFilter;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.site.bean.DatedLinkTarget;
import com.slepeweb.site.ntc.bean.CompetitionIndex;

@Service("newsService")
public class NtcNewsServiceImpl implements NtcNewsService {
	private static Logger LOG = Logger.getLogger(NtcNewsServiceImpl.class);
	
	@Autowired private CompetitionService competitionService;

	@Cacheable(value="serviceCache")
	public List<DatedLinkTarget> getCombinedNews(Item newsIndexItem) {
		LOG.info(String.format("Getting events at %1$tH:%1$tM:%1$tS", System.currentTimeMillis()));

		ItemFilter f = new ItemFilter().setTypes(new String[] {ItemTypeName.NEWS, ItemTypeName.PDF});
		List<Item> newsList = newsIndexItem.getBoundItems(f);
		List<DatedLinkTarget> links = new ArrayList<DatedLinkTarget>(newsList.size());
		DatedLinkTarget lt;
		CompetitionIndex index = this.competitionService.getCompetitionIndex(newsIndexItem.getSite());
		
		if (newsIndexItem != null) {			
			for (Item news : newsList) {
				lt = new DatedLinkTarget().setDate(news.getDateFieldValue(FieldName.DATE_PUBLISHED));
				lt.setTitle(news.getFieldValue(FieldName.TITLE)).setHref(news.getPath());
				
				if (news.getType().getName().equals(ItemTypeName.PDF)) {
					lt.setStyle("iframe");
				}
				
				links.add(lt);
			}
			
			links.addAll(index.getRecentResultsAsLinks());
			Collections.sort(links, new Comparator<DatedLinkTarget>() {

				@Override
				public int compare(DatedLinkTarget o1, DatedLinkTarget o2) {
					return o2.getDate().compareTo(o1.getDate());
				}
				
			});
		}
		
		if (links.size() > 4) {
			links = links.subList(0, 4);
		}
		
		return links;
	}

}
