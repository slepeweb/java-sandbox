<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<div class="in-this-section">
	<h4>In this section</h4>
		<c:forEach items="${_item.bindings}" var="i">
			<div><a href="${i.child.path}">${i.child.fields.title}</a></div>
		</c:forEach>
</div>
