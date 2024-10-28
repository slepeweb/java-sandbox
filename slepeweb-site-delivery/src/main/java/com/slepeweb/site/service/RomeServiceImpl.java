package com.slepeweb.site.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.slepeweb.site.bean.DatedLinkTarget;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@Service("romeService")
public class RomeServiceImpl implements RomeService {
	private static Logger LOG = Logger.getLogger(RomeServiceImpl.class);

	public List<DatedLinkTarget> getFeed(String url) {
		LOG.info(String.format("Getting RSS feed [%s] at %2$tH:%2$tM:%2$tS", url, System.currentTimeMillis()));

		List<DatedLinkTarget> links = new ArrayList<DatedLinkTarget>();
		URL feedUrl = null;

		try {
			feedUrl = new URL(url);
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));
			@SuppressWarnings("unchecked")
			List<SyndEntryImpl> entries = feed.getEntries();
			DatedLinkTarget l;

			for (SyndEntryImpl entry : entries) {
				l = new DatedLinkTarget().setDate(entry.getPublishedDate());
				l.setTitle(entry.getTitle()).
					setTeaser(entry.getDescription().getValue()).
					setHref(entry.getUri());
				links.add(l);
			}

			return links;
		} catch (Exception e) {
			return links;
		}

	}

}
