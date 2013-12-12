String.prototype.format = function() {
	var formatted = this;
	var args = (arguments[0] && Object.isArray(arguments[0])) ? arguments[0] : arguments;
	for (var i = 0; i < args.length; i++) {
		formatted = formatted.replace(new RegExp("\\{"+i+"\\}", "gi"), args[i]);
	}
	return formatted;
};					

var TwitterFeeder = Class.create({
	initialize: function(path, count, success, failure) {
		this.since = null;
		this.path = path;
		this.count = count;
		this.success = success;
		this.failure = failure;
		this.getTweets();
		this.startPolling();
	},
	
	getTweets: function(page) {
		var params = { "action": "friends", "count": this.count };
		if (this.since != null && !page) params.since_id = this.since;
		if (page) params.page = page;
		else page = 1;
		new Ajax.Request(this.path+"twitterproxy.jsp", { "method": "GET", "onSuccess": function(t) {
			this.success(t, page);
		}.bindAsEventListener(this), "onFailure": this.failure, "parameters": params });
	},
	
	doPoll: function(pe) {
		this.getTweets();
	},
	
	startPolling: function() {
		if (this.poll == null) this.poll = new PeriodicalExecuter(this.doPoll.bind(this), TwitterFeeder.Frequency);
	},
	
	stopPolling: function() {
		if (this.poll != null) {
			this.poll.stop();
			this.poll = null;
		}
	},
	
	updateSince: function(since) {
		if (!this.since || this.since < since) this.since = since;
	},
	
	update: function() {
		this.stopPolling();
		this.getTweets();
		this.startPolling();
	}
});

TwitterFeeder.Frequency = 120;

var TweetParser = {
	parseText: function(text) {
		return text.replace(/((http[s]?|ftp):\/\/)((((([A-Za-z0-9]+|([A-Za-z0-9][[A-Za-z0-9]|-]+?[A-Za-z0-9]))[.])*([A-Za-z][\-A-Za-z0-9]*[A-Za-z0-9]))|((((25[0-5])|(2[0-4][0-9])|(1[0-9]{1,2})|([1-9][0-9]?))[.]){3}((25[0-5])|(2[0-4][0-9])|(1[0-9]{1,2})|([1-9][0-9]?))))([:][0-9]+)?)(([\/]([A-Za-z0-9][-A-Za-z0-9_\.\']*)?)+(([?][A-Za-z0-9]+([=]([A-Za-z0-9_]|([%][A-Fa-f0-9]{1,2}))+)?)([&][A-Za-z0-9]+([=]([A-Za-z0-9_]|([%][A-Fa-f0-9]{1,2}))+)?)*)?)?/g, function(match) {
			return "<a target='_blank' href='{0}'>{0}</a>".format(match);
		}).replace(/(\s\b|\B)@[A-Za-z0-9_]+/g, function(match) {
			return "<a target='_blank' href='http://www.twitter.com/{0}'>{1}</a>".format(match.substr(1), match);
		}).replace(/(\s\b|\B)#[A-Za-z0-9_]+/g, function(match) { 
			return "<a target='_blank' href='http://twitter.com/#search?q=%23{0}'>{1}</a>".format(match.substr(1), match);
		});
	},
	
	parseDate: function(string) {
		var date = new Date(string);
		if(isNaN(date)) {
			var dateA = string.split(" ");
			date = new Date("{0}, {2} {1} {5} {3} GMT{4}".format(dateA));
		}
		return date;
	},
	
	getTimeString: function(date) {
		var time = new Date().getTime() - date.getTime();
		if (time < 10000) return "less than 10 seconds ago";
		else if (time < 20000) return "less than 20 seconds ago";
		else if (time < 30000) return "half a minute ago";
		else if (time < 60000) return "less than a minute ago";
		else if (time < 120000)  return "1 minute ago";
		else if (time < 3600000) return Math.floor(time/60000) + " minutes ago";
		else if (time < 7200000) return "about 1 hour ago";
		else if (time < 86400000) return "about " + Math.floor(time / 3600000) + " hours ago";
		else return Math.floor(time/86400000) + " days ago";
	}
};

var Twitter = Class.create({
	initialize: function(tId, username, path, count) {
		this.element = $(tId);
		this.username = username;
		this.path = path;
		this.count = count;
		this.body = this.element.select(".ps_tweets")[0];
		this.notifications = this.element.select(".ps_twitter_notifications")[0];
		this.next = this.element.select(".ps_twitter_next")[0];
		this.previous = this.element.select(".ps_twitter_prev")[0].hide();
		this.header = this.element.select(".ps_twitter_status")[0];
		this.textarea = this.element.select(".ps_twitter_form textarea")[0];
		this.charCount = this.element.select(".ps_twitter_char_count")[0];
		this.button = this.element.select(".ps_twitter_update_btn")[0];
		
		this.page = 1; this.unseen = 0;
		this.dates = new Array();
		this.attachEvents();
		this.textarea.clear();				
		this.tweets = new Hash();
		this.feed = new TwitterFeeder(this.path, this.count, this.handleTweets.bind(this), this.handleFailure.bind(this));
	},
	
	attachEvents: function() {
		this.textarea.observe("keyup", this.validateInput.bindAsEventListener(this));
		this.textarea.observe("paste", this.validateInput.bindAsEventListener(this));
		this.next.observe("click", this.nextPage.bindAsEventListener(this));
		this.previous.observe("click", this.previousPage.bindAsEventListener(this));
		this.notifications.observe("click", function() {
			this.displayPage(1);
		}.bindAsEventListener(this));
	},
	
	onInput: function(e) {
		this.validateInput();
	},
	
	validateInput: function() {
		var value = this.textarea.getValue();
		if (this.replyingTo) {
			if (value.indexOf("@"+this.replyingTo.user.screen_name) != -1) {
				this.header.update("Reply to "+this.replyingTo.user.screen_name+":");
				this.button.setValue("reply");
			}
			else {
				this.header.update("What are you doing?");
				this.button.setValue("update");
			}
		}
		var length = value.length;
		
		if (length > 0 && length < 141) {
			this.activateButton();
		}
		else {
			this.deactivateButton();
		}
		
		if (length < 121) {
			this.charCount.removeClassName("below_20");
			this.charCount.removeClassName("below_10");
		}
		else if(length < 131) {
			this.charCount.addClassName("below_20");
			this.charCount.removeClassName("below_10");
		}
		else {
			this.charCount.removeClassName("below_20");
			this.charCount.addClassName("below_10");
		}
		
		this.charCount.update(140 - length);
		return true; 
	},
	
	activateButton: function() {
		if (!this.button.hasClassName("active")) {
			this.button.addClassName("active");
			this.button.observe("click", this.submitTweet.bindAsEventListener(this));
		}
	},
	
	deactivateButton: function() {
		this.button.removeClassName("active");
		this.button.stopObserving();
	},
	
	submitTweet: function(e) {
		this.deactivateButton();
		this.textarea.disable();
		
		var parameters = {
			"status": this.textarea.getValue(),
			"action": "update"
		};
		if (this.replyingTo != null) {
			parameters.in_reply_to_status_id = this.replyingTo.id;
		}
		
		var onSuccess = function() {
			this.textarea.enable();
			this.textarea.clear();
			this.validateInput();
			this.updateEarly();
		};
		
		new Ajax.Request(this.path+"twitterproxy.jsp", {
			"method": "get", 
			"parameters": parameters,
			"onSuccess": onSuccess.bind(this),
			"onFailure": this.submitTweet.bind(this, e) //try again
		});
		e.stop();
	},
	
	updateEarly: function() {
		this.feed.update();
	},
	
	replyToTweet: function(e, tweet) {
		this.textarea.setValue("@"+tweet.user.screen_name+" "+this.textarea.getValue());
		this.replyingTo = tweet;
		this.header.update("Reply to "+tweet.user.screen_name+":");
		this.validateInput();
	},
	
	deleteTweet: function(e, tweet, container) {
		var btn = e.element().addClassName("loading");
		if(confirm("Are you sure you want to delete this update?")) {
			var parameters = {
				"id": tweet.id,
				"action": "delete"
			};
			new Ajax.Request(this.path+"twitterproxy.jsp", {
				"method": "get", 
				"parameters": parameters,
				"onSuccess": function(t, btn, container) {
					btn.stopObserving();
					container.remove();
				}.bindAsEventListener(this, btn, container),
				"onFailure": function(t, btn) {
					//try again
					this.deleteTweet(e, tweet, container);
				}.bindAsEventListener(this, e, tweet, container)
			});
		}
		else {
			btn.removeClassName("loading");
		}
		e.stop();
	},
	
	favoriteTweet: function(e, tweet, container) {
		var btn = e.element().addClassName("loading");
		var parameters = {
			"id": tweet.id,
			"action": "favorite"
		};
		new Ajax.Request(this.path+"twitterproxy.jsp", {
			"method": "get", 
			"parameters": parameters,
			"onSuccess": function(t, btn, tweet, container) {
				container.addClassName("favorited");
				btn.stopObserving();
				btn.observe("click", this.unfavoriteTweet.bindAsEventListener(this, tweet, container));
				btn.removeClassName("loading");
			}.bindAsEventListener(this, btn, tweet, container),
			"onFailure": function(t) {
				//try again
				this.favoriteTweet(e, tweet, container);
			}.bindAsEventListener(this, e, tweet, container)
		});
		e.stop();
	},
	
	unfavoriteTweet: function(e, tweet, container) {
		var btn = e.element().addClassName("loading");
		var parameters = {
			"id": tweet.id,
			"action": "unfavorite"
		};
		new Ajax.Request(this.path+"twitterproxy.jsp", {
			"method": "get", 
			"parameters": parameters,
			"onSuccess": function(t, btn, tweet, container) {
				container.removeClassName("favorited");
				btn.stopObserving();
				btn.observe("click", this.favoriteTweet.bindAsEventListener(this, tweet, container));
				btn.removeClassName("loading");
			}.bindAsEventListener(this, btn, tweet, container),
			"onFailure": function(t) {
				//try again
				this.unfavoriteTweet(e, tweet, container);
			}.bi
		});
		e.stop();
	},
	
	nextPage: function(e) {
		e.stop();
		this.displayPage(++this.page);
		this.previous.style.display = "block";
	},
	
	previousPage: function(e) {
		e.stop();
		if (this.page > 1) {
			this.displayPage(--this.page);
			if (this.page < 2) {
				this.previous.hide();
			}
		}
	},
	
	displayPage: function(page) {
		this.page = page;
		this.body.update(); //clear the body
		this.dates = new Array();
		this.feed.getTweets(page);
		if (page==1) {
			this.notifications.hide();
			this.unseen = 0;
		}
	},
	
	handleTweets: function(t, page) {
		var tweets = $A(t.responseJSON);
		this.dates.each(function(obj) {
			obj.element.update(TweetParser.getTimeString(obj.date));
		});
		for(var i = 0; i < tweets.length; i++) {
			this.addTweet(tweets[i], page);
		}
		this.next.style.display = "block"; //show the 'next' button
	},
	
	handleFailure: function(t) {
		this.notifications.update("Too many requests, try again later").addClassName("ps_error").style.display = "block";
	},
	
	addToCurrentPage: function(tweet) {
		tweet.created_at_date = TweetParser.parseDate(tweet.created_at);
		var container = new Element("div", { "className": "ps_twitter_container" });
		if (tweet.in_reply_to_screen_name == this.username)  container.addClassName("reply");
		if (tweet.favorited) container.addClassName("favorited");
		container.tweetID = tweet.id;
		
		var avatar = new Element("img", { "className": "ps_twitter_avatar", "src": tweet.user.profile_image_url });
		var user = new Element("a", { "className": "ps_twitter_user", "href": "http://www.twitter.com/"+tweet.user.screen_name }).update(tweet.user.screen_name);
		var date = new Element("span").update(TweetParser.getTimeString(tweet.created_at_date));
		this.dates.push({ "element": date, "date": tweet.created_at_date});
		var details = new Element("div", { "className": "ps_twitter_details" }).insert(date).insert(" from " + tweet.source);
		var textContainer = new Element("div", { "className": "ps_twitter_text" });
		var controls = new Element("div", { "className": "ps_twitter_controls" });
		
		var faveBtn = new Element("div", { "title": "favourite this update", "className": "fave" });									
		if (tweet.favorited) faveBtn.observe("click", this.unfavoriteTweet.bindAsEventListener(this, tweet, container));
		else faveBtn.observe("click", this.favoriteTweet.bindAsEventListener(this, tweet, container));
		controls.insert(faveBtn);
		
		if(tweet.user.screen_name == this.username) {
			var deleteBtn = new Element("div", { "title": "delete this update", "className": "delete" });
			controls.insert(deleteBtn);
			deleteBtn.observe("click", this.deleteTweet.bindAsEventListener(this, tweet, container));
		}
		else {
			var replyBtn = new Element("div", { "title": "reply to {0}".format(tweet.user.screen_name), "className": "reply" });
			controls.insert(replyBtn);
			replyBtn.observe("click", this.replyToTweet.bindAsEventListener(this, tweet));
		}
											
		var text = TweetParser.parseText(tweet.text);
		
		textContainer.insert(user);
		textContainer.insert(" " + text);
		textContainer.insert(details);
		
		container.insert(avatar);
		container.insert(textContainer);
		container.insert(controls);
		
		var children = this.body.childElements();
		if (children.length > 0) {
			if (children.length > 17) {
				var child = children[children.length-1]; 
				child.remove();
				this.tweets.unset(child.tweetID);
			}
			for (var i = 0; i < children.length; i++) {
				var child = children[i];
				var cTweet = this.tweets.get(child.tweetID);
				if (cTweet) {
					if(cTweet.created_at_date.getTime() < tweet.created_at_date.getTime()) {
						child.insert({ "before": container });
						break;
					}
				}
			}
		}
		
		if(!container.parentNode || !container.parentNode.parentNode) {
			this.body.insert(container);
		}
	},
	
	addNotification: function(tweet) {
		this.unseen++;
		var message = this.unseen + " new update";
		if (this.unseen > 1) message+="s";
		this.notifications.update(message);
		this.notifications.removeClassName("ps_error");
		this.notifications.style.display = "block";
	},
	
	addTweet: function(tweet, page) {
		this.notifications.style.display = "none";
		this.feed.updateSince(tweet.id);
		this.tweets.set(tweet.id, tweet);
		if(page == this.page) {
			this.addToCurrentPage(tweet);
		}
		else {
			this.addNotification(tweet);
		}
	}
});