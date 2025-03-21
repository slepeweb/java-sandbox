<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<footer>
	<span>&copy; Slepe Web Solutions Ltd. All rights reserved.</span><br />
	<span>Last updated: ${site:formatUKDate(_item.dateUpdated, 'MMMM d, yyyy, h:mm a')} 
		<i id="open-editor" class="fa-solid fa-pencil" title="${_item.origId}"></i></span>
</footer>
