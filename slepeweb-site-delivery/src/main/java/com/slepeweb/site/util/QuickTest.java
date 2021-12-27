package com.slepeweb.site.util;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import com.slepeweb.site.bean.Tweet;
import com.slepeweb.site.bean.TwitterAccount;

public class QuickTest {
	
	public static void main(String[] args) {
		// http://t.co/uzH3Yw6kCc
		// http://t.co/LN3PZcCnir
		//trace(getRedirectedHref("http://t.co/uzH3Yw6kCc"));
		parseLinkData();
	}
	
	private static void parseLinkData() {
		String data = "1915::Izmir, Turkey";
		String[] parts = data.split("\\:\\:");
		trace(String.format(" date=%s", parts[0]));
		trace(String.format("place=%s", parts[1]));
		
		String tmplt = "01/01/1700";
//		trace(String.format("date=%s", StringUtils.overlay(tmplt, 
//				StringUtils.leftPad(parts[0].trim(), 0, tmplt.length() - 1)));

	}
	
	private static void getTweets() {
		Twitter twitter = new TwitterFactory().getInstance();
		String[] accounts = new String[] {
				"andy_murray",
				"rogerfederer",
				"DjokerNole",
				"RafaelNadal"
		};

		Iterator<Status> iter;
		Status status;
		String msg;
		List<Tweet> tweets = new ArrayList<Tweet>(accounts.length * 23);

		for (String account : accounts) {
			try {
				iter = twitter.getUserTimeline(account).iterator();
				
				while (iter.hasNext()) {
					status = iter.next();
					msg = status.getText();
					if (! msg.startsWith("RT ")) {
						tweets.add(new Tweet(status).setAccount(new TwitterAccount().setName(account)));
					}
				}
			}
			catch (Exception e) {
				trace(e.getMessage());
			}
		}
			
		Collections.sort(tweets, new Comparator<Tweet>() {
			public int compare(Tweet o1, Tweet o2) {
				return o2.getCreatedAt().compareTo(o1.getCreatedAt());
			}
		});
		
		for (Tweet t : tweets.subList(0, 8)) {
			trace(String.format("%s (%s): %s", t.getAccount().getName(), t.getTimeAgo(), t.getText()));
		}		
	}
	
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
			trace(String.format("Badly-formed url [%s]", shortHref), e);
		}
		catch (IOException e) {
			trace(String.format("Failed to get resource [%s]", shortHref), e);
		}
		finally {
			if (response != null) {
				try {
					response.close();
				}
				catch (IOException e) {
					trace(String.format("Failed to close http connection [%s]", shortHref), e);
				}
			}
		}
		
		return fullHref;
	}
	
	private static void trace(String s) {
		System.out.println(s);
	}
	
	private static void trace(String s, Exception e) {
		System.out.println(s + ": " + e.getMessage());
	}
}
