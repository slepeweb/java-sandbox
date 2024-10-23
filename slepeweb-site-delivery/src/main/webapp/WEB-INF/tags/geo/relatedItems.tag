<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<div class="sidebar-wrapper">
	<div class="sidebar">
		<h4>Related items</h4>
			<c:forEach items="${_item.relatedItems}" var="i">
				<div><a class="block" href="${i.path}">${i.fields.title}</a></div>
			</c:forEach>
	</div>
</div>
