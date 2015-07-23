<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- jsp/sws/component/twitter.jsp --></gen:debug>

<c:if test="${not empty _comp.heading}"><h2>${_comp.heading}</h2></c:if>
<c:if test="${not empty _comp.blurb}"><site:div>${_comp.blurb}</site:div></c:if>

<c:forEach items="${_comp.tweets}" var="_tweet">
	<div class="row twitter-side">
		<div class="1u">
			<img width="40px" src="${_tweet.account.iconPath}" title="${_tweet.account.name}" align="left" />
		</div>
			
		<div class="10u">
			<span>${_tweet.account.name}, ${_tweet.timeAgo}:</span><br />
			${_tweet.text}
		</div>
	</div>
</c:forEach>
