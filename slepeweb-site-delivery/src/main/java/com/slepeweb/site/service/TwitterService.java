package com.slepeweb.site.service;

import java.util.List;

import com.slepeweb.site.bean.Tweet;
import com.slepeweb.site.bean.TwitterAccount;

public interface TwitterService {
	List<Tweet> getSyndicatedTweets(TwitterAccount[] accounts, int max, int maxOverall);
}
