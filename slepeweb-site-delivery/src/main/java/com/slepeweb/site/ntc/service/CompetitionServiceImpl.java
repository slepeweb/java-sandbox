package com.slepeweb.site.ntc.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemFilter;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.site.constant.FieldName;
import com.slepeweb.site.ntc.bean.Competition;
import com.slepeweb.site.ntc.bean.CompetitionIndex;

@Service("competitionService")
public class CompetitionServiceImpl implements CompetitionService {
	private static Logger LOG = Logger.getLogger(CompetitionServiceImpl.class);
	
	//@Cacheable(value="serviceCache")
	public CompetitionIndex getCompetitionIndex(Site s) {
		LOG.info(String.format("Getting Competition at %1$tH:%1$tM:%1$tS", System.currentTimeMillis()));
		
		CompetitionIndex index = new CompetitionIndex();
		String path = "/matches";
		Item compRoot = s.getItem(path);
		
		if (compRoot == null) {
			LOG.error(String.format("Failed to identify competition root item [%s]", path));
			return index;
		}
		
		ItemFilter f = new ItemFilter().setTypes(new String[] {"Competition"});
		
		for (Item i : compRoot.getBoundItems(f)) {
			index.getCompetitions().add(new Competition().
					setItem(i)	.
					setName(i.getFieldValue(FieldName.TITLE)).
					setSquad(i.getFieldValue(FieldName.SQUAD)).
					setFixtures(i.getFieldValue(FieldName.FIXTURES)));
		}
		
		return index;
	}

}
