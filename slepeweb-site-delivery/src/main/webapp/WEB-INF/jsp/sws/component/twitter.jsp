<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- jsp/sws/component/twitter.jsp --></gen:debug>

<script>
$(function() {
	var tweets = {
			error: "... tweets not available right now.",
			updateDiv: function(html) {
				var div = $("#twitter-side");
				div.empty();
				div.append(html);	
			}
	};
	
	$.ajax({
		url : "/ws/tweets/${_comp.id}",
		dataType : "json",
		cache : false
	}).done(function(resp) {
		if (resp) {
			html = "";
			if (resp.heading) {
				html += "<h2>" + resp.heading + "</h2>";
			}
			if (resp.blurb) {
				html += "<div>" + resp.blurb + "</div>";
			}
			
			for (var i = 0; i < resp.tweets.length; i++) {
				html += '<div class="twitter-feed"><div class="left"><img src="';
				html += resp.tweets[i].account.iconPath;
				html += '" title="';
				html += resp.tweets[i].account.name;
				html += '" align="left" /></div><div class="right"><span>';
				html += resp.tweets[i].account.name;
				html += ', ';
				html += resp.tweets[i].timeAgo.quantity + resp.tweets[i].timeAgo.unit + " ago";
				html += ':</span><br />';
				html += resp.tweets[i].text;
				html += '</div></div>\n';
			}
			
			var obj = $(html);
			obj.find(".group3").colorbox({rel:'group3', transition:"none", current:'Tweet {current} of {total}'});
			obj.find(".iframe").colorbox({iframe:true, opacity:0.5, closeButton:true, width:"90%", height:"80%", top:"15%"});
			tweets.updateDiv(obj);
		}
		else {
			tweets.updateDiv(tweets.error);
		}	
	}).fail(function(jqXHR, status) {
		tweets.updateDiv(tweets.error);
	});		
});
</script>  

<div id="twitter-side">
	Please wait for latest tweets ...
</div>
