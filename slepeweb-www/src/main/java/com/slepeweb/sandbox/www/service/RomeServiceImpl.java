package com.slepeweb.sandbox.www.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.slepeweb.sandbox.www.model.Link;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@Service( "romeService" )
public class RomeServiceImpl implements RomeService {

	public List<Link> getFeed(String url) {
		List<Link> links = new ArrayList<Link>();
		URL feedUrl = null;
		
		try {
			feedUrl = new URL(url);
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));
			@SuppressWarnings("unchecked")
			List<SyndEntryImpl> entries = feed.getEntries();
			Link l;
			
			for (SyndEntryImpl entry :  entries) {
				l = new Link().
						setTitle(entry.getTitle()).
						setTeaser(entry.getDescription().getValue()).
						setHref(entry.getUri());
				links.add(l);
			}
			
			return links;
		}
		catch (Exception e) {
			return links;
		}
		
	}

}
