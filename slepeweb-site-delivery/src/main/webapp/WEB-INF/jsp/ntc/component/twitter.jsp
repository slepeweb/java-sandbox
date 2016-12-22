<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- jsp/ntc/component/twitter.jsp --></gen:debug>

<c:set var="_xclass" value="" />
<c:if test="${_comp.view eq 'rightside'}"><c:set var="_xclass" value="sidebar" /></c:if>

<div class="raised-box ${_xclass}">
	<div class="twitter scroll">
		<h2>${_comp.heading}</h2>
		<c:if test="${not empty _comp.blurb}">${_comp.blurb}</c:if>
		
		<c:forEach items="${_comp.tweets}" var="_tweet">
			<div class="row tweet">
				<div class="2u"><img src="${_tweet.account.iconPath}" title="${_tweet.account.name}" align="left" /></div>
					
				<div class="10u">
					<span class="twitter-user">${_tweet.account.name}, ${_tweet.timeAgo}:</span> <br />
					${_tweet.text}
				</div>
			</div>
		</c:forEach>
	</div>
</div>