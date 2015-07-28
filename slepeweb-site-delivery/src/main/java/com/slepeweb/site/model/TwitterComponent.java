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
	private int maxPerAccount = 2, maxOverall;
	private long created;

	public TwitterComponent setup(Link l) {
		super.setup(l);
		setCreated(System.currentTimeMillis());
		
		/*
		 * Example data field value:
		 	2, 8
			andy_murray, /content/images/icon/murray
			rogerfederer, /content/images/icon/federer
			DjokerNole, /content/images/icon/djokovic
			RafaelNadal, /content/images/icon/nadal
			
			2 == this.maxPerAccount
			8 == this.maxOverall
		 */
		String data = l.getChild().getFieldValue("data");
		List<TwitterAccount> accountsList = new ArrayList<TwitterAccount>();
		String[] parts;

		if (StringUtils.isNotBlank(data)) {
			BufferedReader reader = new BufferedReader(new StringReader(data));
			
			try {
				String line = reader.readLine();
				
				if (line != null) {
					parts = line.split("[, ]+");
					
					if (parts.length == 2 && 
							StringUtils.isNumeric(parts[0]) && StringUtils.isNumeric(parts[1])) {
						this.maxPerAccount = Integer.parseInt(parts[0]);
						this.maxOverall = Integer.parseInt(parts[1]);
					}
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

	public int getMaxPerAccount() {
		return maxPerAccount;
	}

	public void setMaxPerAccount(int max) {
		this.maxPerAccount = max;
	}

	public int getMaxOverall() {
		return maxOverall;
	}

	public void setMaxOverall(int maxOverall) {
		this.maxOverall = maxOverall;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}	
}
