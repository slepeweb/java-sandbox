package com.slepeweb.site.ntc.service;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemFilter;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.site.constant.ItemTypeName;
import com.slepeweb.site.constant.TagName;
import com.slepeweb.site.ntc.bean.Competition;
import com.slepeweb.site.ntc.bean.CompetitionIndex;

@Service("competitionService")
public class CompetitionServiceImpl implements CompetitionService {
	private static Logger LOG = Logger.getLogger(CompetitionServiceImpl.class);
	
	@Cacheable(value="serviceCache")
	public CompetitionIndex getCompetitionIndex(Site s) {
		LOG.info(String.format("Getting Competition at %1$tH:%1$tM:%1$tS", System.currentTimeMillis()));
		
		CompetitionIndex index = new CompetitionIndex();
		Item compRoot = s.getTaggedItem(TagName.MATCHES);
		
		if (compRoot == null) {
			LOG.error(String.format("Failed to identify competition root item [%s]", TagName.MATCHES));
			return index;
		}
		
		ItemFilter f = new ItemFilter().setTypes(new String[] {ItemTypeName.COMPETITION});
		
		for (Item i : compRoot.getBoundItems(f)) {
			index.getCompetitions().add(new Competition().setItem(i));
		}
		
		return index;
	}

}
