<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- jsp/ntc/component/twitter.jsp --></gen:debug>

<c:set var="_xclass" value="" />
<c:if test="${_comp.view eq 'rightside'}"><c:set var="_xclass" value="sidebar" /></c:if>

<div class="raised-box scroll twitter ${_xclass}">
	<h2>${_comp.heading}</h2>
	<c:if test="${not empty _comp.blurb}"><site:div>${_comp.blurb}</site:div></c:if>
	
	<ul class="link-list">
		<c:forEach items="${_comp.tweets}" var="_tweet">
			<li class="smaller">
				<img src="${_tweet.account.iconPath}" title="${_tweet.account.name}" align="left" />
				<span class="twitter-user">${_tweet.account.name}, ${_tweet.timeAgo}:</span> <br />
				${_tweet.text}</li>
		</c:forEach>
	</ul>
</div>