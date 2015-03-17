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
import com.slepeweb.site.bean.DatedLinkTarget;
import com.slepeweb.site.constant.FieldName;
import com.slepeweb.site.ntc.bean.CompetitionIndex;
import com.slepeweb.site.ntc.service.CompetitionService;

@Service("eventsService")
public class EventsServiceImpl implements EventsService {
	private static Logger LOG = Logger.getLogger(EventsServiceImpl.class);
	
	@Autowired private CompetitionService competitionService;

	@Cacheable(value="serviceCache")
	public List<DatedLinkTarget> getCombinedEvents(Item eventsIndexItem) {
		LOG.info(String.format("Getting events at %1$tH:%1$tM:%1$tS", System.currentTimeMillis()));

		List<Item> events = eventsIndexItem.getBoundItems();
		List<DatedLinkTarget> links = new ArrayList<DatedLinkTarget>(events.size());
		DatedLinkTarget lt;
		CompetitionIndex index = this.competitionService.getCompetitionIndex(eventsIndexItem.getSite());
		
		if (eventsIndexItem != null) {			
			for (Item event : events) {
				lt = new DatedLinkTarget().setDate(event.getDateFieldValue(FieldName.START_DATE));
				lt.setTitle(event.getFieldValue(FieldName.TITLE)).setHref(event.getPath());
				links.add(lt);
			}
			
			links.addAll(index.getFutureMatchesAsLinks());
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
