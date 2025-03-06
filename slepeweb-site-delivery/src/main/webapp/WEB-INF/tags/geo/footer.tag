<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<footer>
	<span>&copy; <span id="render-item-id">Slepe</span> Web Solutions Ltd. All rights reserved.</span><br />
	<c:set var="_now" value="<%= new java.util.Date() %>" />
	<span class="smaller">Last updated: ${site:formatUKDate(_item.dateUpdated, 'MMMM d, yyyy, h:mm a')} 
		<i id="open-editor" class="fa-solid fa-pencil" data-id="${_item.origId}"></i></span>
</footer>
