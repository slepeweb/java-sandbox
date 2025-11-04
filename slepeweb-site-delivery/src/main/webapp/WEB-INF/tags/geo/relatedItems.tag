<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:if test="${fn:length(_item.relatedItems) > 0}">
	<div class="related-items">
		<h4>Related items</h4>
			<c:forEach items="${_item.relatedItems}" var="i">
				<div><a class="block" href="${i.path}">${i.fields.title}</a></div>
			</c:forEach>
	</div>
</c:if>