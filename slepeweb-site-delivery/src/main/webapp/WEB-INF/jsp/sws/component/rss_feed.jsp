<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- jsp/sws/component/rss_feed.jsp --></gen:debug>

<h3>${_comp.heading}</h3>
<div>${_comp.blurb}</div>
<ul class="link-list">
<c:forEach items="${_comp.targets}" var="link" end="3">
	<li class="compact"><a href="${link.href}" class="iframe group2">${link.title}</a>
</c:forEach>
</ul>