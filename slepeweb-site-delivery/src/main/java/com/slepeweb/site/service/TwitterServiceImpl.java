package com.slepeweb.site.service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import com.slepeweb.site.bean.Tweet;
import com.slepeweb.site.bean.TwitterAccount;

@Service("twitterService")
public class TwitterServiceImpl implements TwitterService {
	private static Logger LOG = Logger.getLogger(TwitterServiceImpl.class);
	private static Pattern LINK_PATTERN = Pattern.compile("(http:\\S*)", Pattern.CASE_INSENSITIVE);
	private static Pattern HASH_PATTERN = Pattern.compile("#(\\S*)");
	private static Pattern AT_PATTERN = Pattern.compile("@(\\S*)");

	@Cacheable(value="serviceCache")
	public List<Tweet> getSyndicatedTweets(TwitterAccount[] accounts, int max) {
		LOG.info(String.format("Getting syndicated twitter feeds at $tH:$tM:$tS", System.currentTimeMillis()));

		if (accounts != null) {
			Twitter twitter = new TwitterFactory().getInstance();
			Iterator<Status> iter;
			Status status;
			String msg;
			List<Tweet> tweets = new ArrayList<Tweet>(23);
	
			for (TwitterAccount account : accounts) {
				try {
					iter = twitter.getUserTimeline(account.getName()).iterator();
					
					while (iter.hasNext()) {
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
			}
				
			Collections.sort(tweets, new Comparator<Tweet>() {
				@Override
				public int compare(Tweet o1, Tweet o2) {
					return o2.getCreatedAt().compareTo(o1.getCreatedAt());
				}
			});
			
			if (max > 0 && max < tweets.size()) {
				tweets = tweets.subList(0, max);
			}
			
			processLinks(tweets);			
			return tweets;
		}
		
		return new ArrayList<Tweet>();
	}

	private void processLinks(List<Tweet> tweets) {
		String text;
		Matcher m;
		
		for (Tweet tweet : tweets) {
			text = tweet.getText();
			
			// match any links
			m = LINK_PATTERN.matcher(text);
			text = m.replaceAll("<a href=\"$1\" target=\"_blank\">$1</a>");
			
			// match any hashTags
			m = HASH_PATTERN.matcher(text);
			//text = hashTagMatcher.replaceAll("<a href=\"http://twitter.com/#!/search?q=%23$1\">#$1</a>");
			text = m.replaceAll("<span class=\"hashtag\">#$1</span>");
	
			m = AT_PATTERN.matcher(text);
			text = m.replaceAll("<span class=\"hashtag\">@$1</span>");
	
			tweet.setText(text);
		}
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
