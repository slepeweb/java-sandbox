<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- tags/sessionExpiry.tag --></gen:debug>

<c:if test="${_item.site.secured}">
	<div id="session-expiry-warning" class="hidden">
		<h2>Session expiry</h2>
		<p>Your session is about to expire due to inactivity - you have <span></span> seconds remaining.</p>
		<p>To continue working, simply refresh this page, or navigate to a new page.</p>
			
		<audio id="bell" src="/resources/doorbell.mp3" type="audio/mp3"></audio>
	</div>
</c:if>