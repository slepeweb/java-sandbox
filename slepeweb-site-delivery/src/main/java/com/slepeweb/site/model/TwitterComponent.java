package com.slepeweb.site.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.slepeweb.cms.bean.Link;
import com.slepeweb.site.bean.Tweet;
import com.slepeweb.site.bean.TwitterAccount;

public class TwitterComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(TwitterComponent.class);

	private List<Tweet> tweets;
	private TwitterAccount[] accounts;
	private int max;

	public TwitterComponent setup(Link l) {
		super.setup(l);
		
		String data = l.getChild().getFieldValue("data");
		List<TwitterAccount> accountsList = new ArrayList<TwitterAccount>();
		String[] parts;

		if (StringUtils.isNotBlank(data)) {
			BufferedReader reader = new BufferedReader(new StringReader(data));
			
			try {
				String line = reader.readLine();
				if (StringUtils.isNumeric(line)) {
					this.max = Integer.parseInt(line);
				}
				
				while ((line = reader.readLine()) != null) {
					parts = line.split("[, ]+");
					if (parts.length == 2) {
						accountsList.add(new TwitterAccount().setName(parts[0]).setIconPath(parts[1]));
					}
				}
			}
			catch (IOException e) {
				LOG.error("Failed to read twitter data from component");
			}
			
			TwitterAccount[] accounts = new TwitterAccount[accountsList.size()];
			this.accounts = accountsList.toArray(accounts);
		}
		
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("TwitterComponent (%s): %s", getType(), getHeading());
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public TwitterAccount[] getAccounts() {
		return accounts;
	}

	public void setAccounts(TwitterAccount[] accounts) {
		this.accounts = accounts;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}	
}