<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div id="session-expiry-warning" class="hidden">
	<h2>Session expiry</h2>
	<p>Your session is about to expire due to inactivity - you have <span></span> seconds remaining.</p>
	<p>To continue working, simply refresh this page, or navigate to a new item.</p>
	<audio id="session-bell" src="/cms/resources/doorbell.mp3"></audio>
</div>
