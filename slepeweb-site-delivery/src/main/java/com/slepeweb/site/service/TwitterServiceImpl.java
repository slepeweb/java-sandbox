package com.slepeweb.site.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import com.slepeweb.site.bean.Tweet;
import com.slepeweb.site.bean.TwitterAccount;
import com.slepeweb.site.model.TwitterComponent;

@Service("twitterService")
public class TwitterServiceImpl implements TwitterService {
	private static Logger LOG = Logger.getLogger(TwitterServiceImpl.class);
	private static Pattern LINK_PATTERN = Pattern.compile("(https?:\\S*)", Pattern.CASE_INSENSITIVE);
	private static Pattern HASH_PATTERN = Pattern.compile("#(\\S*)");
	private static Pattern AT_PATTERN = Pattern.compile("@(\\S*)");
	private static final long CACHE_TTL = 300 * 1000; // 5 mins

	private Map<Long, TwitterComponent> cache = new HashMap<Long, TwitterComponent>();
	
	public TwitterComponent getSyndicatedTweets(TwitterComponent c) {
		TwitterComponent cached = this.cache.get(c.getId());
		long now = System.currentTimeMillis();
		if (cached != null && (now - cached.getCreated()) < CACHE_TTL) {
			return cached;
		}
		
		LOG.info(String.format("Getting syndicated twitter feeds at %1$tH:%1$tM:%1$tS", System.currentTimeMillis()));

		if (c.getAccounts() != null) {
			Twitter twitter = new TwitterFactory().getInstance();
			Iterator<Status> iter;
			Status status;
			String msg;
			List<Tweet> allTweets = new ArrayList<Tweet>();
			List<Tweet> tweets;
			int max;
	
			for (TwitterAccount account : c.getAccounts()) {
				tweets = new ArrayList<Tweet>();
				
				try {
					iter = twitter.getUserTimeline(account.getName()).iterator();
					max = account.getNumTweets();
					
					while (iter.hasNext() && tweets.size() < max) {
						status = iter.next();
						msg = status.getText();
						if (! msg.startsWith("RT ")) {
							tweets.add(new Tweet(status).setAccount(account));
						}
					}
				}
				catch (Exception e) {
					LOG.error(String.format("Failed to retrieve tweets for %s", account), e);
				}
				
				allTweets.addAll(tweets);
			}
				
			Collections.sort(allTweets, new Comparator<Tweet>() {
				@Override
				public int compare(Tweet o1, Tweet o2) {
					return o2.getCreatedAt().compareTo(o1.getCreatedAt());
				}
			});
			
			if (c.getMaxOverall() > 0 && c.getMaxOverall() < allTweets.size()) {
				allTweets = allTweets.subList(0, c.getMaxOverall());
			}
			
			processLinks(allTweets);
			c.setTweets(allTweets);
			c.setCreated(now);
			this.cache.put(c.getId(), c);
			return c;
		}
		
		return null;
	}

	private void processLinks(List<Tweet> tweets) {
		String text;
		Matcher m;
		
		for (Tweet tweet : tweets) {
			text = tweet.getText();
			
			// match any links
			m = LINK_PATTERN.matcher(text);			
			text = disableUnsuitableLinks(m);
			
			// match any hashTags
			m = HASH_PATTERN.matcher(text);
			text = m.replaceAll("<span class=\"hashtag\">#$1</span>");
	
			m = AT_PATTERN.matcher(text);
			text = m.replaceAll("<span class=\"hashtag\">@$1</span>");
	
			tweet.setText(text);
		}
	}
	
	private String disableUnsuitableLinks(Matcher m) {
		StringBuffer sb = new StringBuffer();
		
		// Retain all links, regardless of response headers such as "X-Frame-Options"
		while (m.find()) {
			retainLink(m, sb);
		}
		
		m.appendTail(sb);
		return sb.toString();
	}
	
	private void retainLink(Matcher m, StringBuffer sb) {
		String url = null;
		try {
			url = URLEncoder.encode(m.group(1), "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		m.appendReplacement(sb, String.format("<a href=\"/proxy?u=%s\" class=\"iframe group3\">%s</a>", url, m.group(1)));
	}
	
	@SuppressWarnings("unused")
	private void disableLink(Matcher m, StringBuffer sb) {
		m.appendReplacement(sb, String.format("<span class=\"link\">%s</span>", m.group(1)));
	}
	
	// Turns out that redirects were not the problem. Keep this code for the moment.
	@SuppressWarnings("unused")
	private static String getRedirectedHref(String shortHref) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpClientContext context = HttpClientContext.create();
		CloseableHttpResponse response = null;
		String fullHref = shortHref;
			    		
		try {
			HttpHead request = new HttpHead(shortHref);
			response = client.execute(request, context);
		    EntityUtils.consume(response.getEntity());
		    List<URI> urls = context.getRedirectLocations();
		    if (urls != null && urls.size() > 1) {
		    	fullHref = urls.get(urls.size() - 1).toString();
		    }
		} 
		catch (ClientProtocolException e) {
			LOG.error(String.format("Badly-formed url [%s]", shortHref), e);
		}
		catch (IOException e) {
			LOG.error(String.format("Failed to get resource [%s]", shortHref), e);
		}
		finally {
			if (response != null) {
				try {
					response.close();
				}
				catch (IOException e) {
					LOG.error(String.format("Failed to close http connection [%s]", shortHref), e);
				}
			}
		}
		
		return fullHref;
	}
	
}
