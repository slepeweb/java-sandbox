<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<footer>
	<span>&copy; Slepe Web Solutions Ltd. All rights reserved.</span><br />
	<c:set var="_now" value="<%= new java.util.Date() %>" />
	<span class="smaller">${site:formatUKDate(_now, 'MMMM d, h:mm a')}</span>
</footer>
